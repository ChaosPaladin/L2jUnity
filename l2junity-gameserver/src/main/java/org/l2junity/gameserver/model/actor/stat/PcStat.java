/*
 * Copyright (C) 2004-2015 L2J Unity
 * 
 * This file is part of L2J Unity.
 * 
 * L2J Unity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Unity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.gameserver.model.actor.stat;

import java.util.concurrent.atomic.AtomicInteger;

import org.l2junity.Config;
import org.l2junity.gameserver.data.xml.impl.ExperienceData;
import org.l2junity.gameserver.enums.PartySmallWindowUpdateType;
import org.l2junity.gameserver.enums.UserInfoType;
import org.l2junity.gameserver.model.Party;
import org.l2junity.gameserver.model.actor.Summon;
import org.l2junity.gameserver.model.actor.instance.L2PetInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.EventDispatcher;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.model.stats.Stats;
import org.l2junity.gameserver.model.zone.ZoneId;
import org.l2junity.gameserver.network.client.send.AcquireSkillList;
import org.l2junity.gameserver.network.client.send.ExVitalityPointInfo;
import org.l2junity.gameserver.network.client.send.ExVoteSystemInfo;
import org.l2junity.gameserver.network.client.send.PartySmallWindowUpdate;
import org.l2junity.gameserver.network.client.send.PledgeShowMemberListUpdate;
import org.l2junity.gameserver.network.client.send.SocialAction;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.UserInfo;
import org.l2junity.gameserver.network.client.send.ability.ExAcquireAPSkillList;
import org.l2junity.gameserver.network.client.send.friend.L2FriendStatus;
import org.l2junity.gameserver.network.client.send.onedayreward.ExOneDayReceiveRewardList;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.util.Util;

public class PcStat extends PlayableStat
{
	private long _startingXp;
	/** Player's maximum talisman count. */
	private final AtomicInteger _talismanSlots = new AtomicInteger();
	private boolean _cloakSlot = false;
	private int _vitalityPoints = 0;
	
	public static final int MAX_VITALITY_POINTS = 140000;
	public static final int MIN_VITALITY_POINTS = 0;
	
	public PcStat(PlayerInstance activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public boolean addExp(long value)
	{
		PlayerInstance activeChar = getActiveChar();
		
		// Allowed to gain exp?
		if (!getActiveChar().getAccessLevel().canGainExp())
		{
			return false;
		}
		
		if (!super.addExp(value))
		{
			return false;
		}
		
		// Set new karma
		if (!activeChar.isCursedWeaponEquipped() && (activeChar.getReputation() < 0) && (activeChar.isGM() || !activeChar.isInsideZone(ZoneId.PVP)))
		{
			final int karmaLost = Formulas.calculateKarmaLost(activeChar, value);
			if (karmaLost > 0)
			{
				activeChar.setReputation(Math.min((activeChar.getReputation() + karmaLost), 0));
			}
		}
		
		// EXP status update currently not used in retail
		activeChar.sendPacket(new UserInfo(activeChar));
		return true;
	}
	
	public void addExpAndSp(long addToExp, long addToSp, boolean useBonuses)
	{
		PlayerInstance activeChar = getActiveChar();
		
		// Allowed to gain exp/sp?
		if (!activeChar.getAccessLevel().canGainExp())
		{
			return;
		}
		
		long baseExp = addToExp;
		long baseSp = addToSp;
		
		double bonusExp = 1.;
		double bonusSp = 1.;
		
		if (useBonuses)
		{
			bonusExp = getExpBonusMultiplier();
			bonusSp = getSpBonusMultiplier();
		}
		
		addToExp *= bonusExp;
		addToSp *= bonusSp;
		
		float ratioTakenByPlayer = 0;
		
		// if this player has a pet and it is in his range he takes from the owner's Exp, give the pet Exp now
		final Summon sPet = activeChar.getPet();
		if ((sPet != null) && Util.checkIfInShortRange(Config.ALT_PARTY_RANGE, activeChar, sPet, false))
		{
			L2PetInstance pet = (L2PetInstance) sPet;
			ratioTakenByPlayer = pet.getPetLevelData().getOwnerExpTaken() / 100f;
			
			// only give exp/sp to the pet by taking from the owner if the pet has a non-zero, positive ratio
			// allow possible customizations that would have the pet earning more than 100% of the owner's exp/sp
			if (ratioTakenByPlayer > 1)
			{
				ratioTakenByPlayer = 1;
			}
			
			if (!pet.isDead())
			{
				pet.addExpAndSp((long) (addToExp * (1 - ratioTakenByPlayer)), (int) (addToSp * (1 - ratioTakenByPlayer)));
			}
			
			// now adjust the max ratio to avoid the owner earning negative exp/sp
			addToExp = (long) (addToExp * ratioTakenByPlayer);
			addToSp = (int) (addToSp * ratioTakenByPlayer);
		}
		
		if (!addExp(addToExp))
		{
			addToExp = 0;
		}
		
		if (!addSp(addToSp))
		{
			addToSp = 0;
		}
		
		SystemMessage sm = null;
		if ((addToExp == 0) && (addToSp != 0))
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_SP);
			sm.addLong(addToSp);
		}
		else if ((addToSp == 0) && (addToExp != 0))
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_XP);
			sm.addLong(addToExp);
		}
		else
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_XP_BONUS_S2_AND_S3_SP_BONUS_S4);
			sm.addLong(addToExp);
			sm.addLong(addToExp - baseExp);
			sm.addLong(addToSp);
			sm.addLong(addToSp - baseSp);
		}
		activeChar.sendPacket(sm);
	}
	
	@Override
	public boolean removeExpAndSp(long addToExp, long addToSp)
	{
		return removeExpAndSp(addToExp, addToSp, true);
	}
	
	public boolean removeExpAndSp(long addToExp, long addToSp, boolean sendMessage)
	{
		int level = getLevel();
		if (!super.removeExpAndSp(addToExp, addToSp))
		{
			return false;
		}
		
		if (sendMessage)
		{
			// Send a Server->Client System Message to the L2PcInstance
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_XP_HAS_DECREASED_BY_S1);
			sm.addLong(addToExp);
			getActiveChar().sendPacket(sm);
			sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
			sm.addLong(addToSp);
			getActiveChar().sendPacket(sm);
			if (getLevel() < level)
			{
				getActiveChar().broadcastStatusUpdate();
			}
		}
		return true;
	}
	
	@Override
	public final boolean addLevel(byte value)
	{
		if ((getLevel() + value) > (ExperienceData.getInstance().getMaxLevel() - 1))
		{
			return false;
		}
		
		boolean levelIncreased = super.addLevel(value);
		if (levelIncreased)
		{
			getActiveChar().setCurrentCp(getMaxCp());
			getActiveChar().broadcastPacket(new SocialAction(getActiveChar().getObjectId(), SocialAction.LEVEL_UP));
			getActiveChar().sendPacket(SystemMessageId.YOUR_LEVEL_HAS_INCREASED);
			getActiveChar().notifyFriends(L2FriendStatus.MODE_LEVEL);
		}
		
		// Notify to scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLevelChanged(getActiveChar(), getLevel() - value, getLevel()), getActiveChar());
		
		// Give AutoGet skills and all normal skills if Auto-Learn is activated.
		getActiveChar().rewardSkills();
		
		if (getActiveChar().getClan() != null)
		{
			getActiveChar().getClan().updateClanMember(getActiveChar());
			getActiveChar().getClan().broadcastToOnlineMembers(new PledgeShowMemberListUpdate(getActiveChar()));
		}
		if (getActiveChar().isInParty())
		{
			getActiveChar().getParty().recalculatePartyLevel(); // Recalculate the party level
		}
		
		// Maybe add some skills when player levels up in transformation.
		getActiveChar().getTransformation().ifPresent(transform -> transform.onLevelUp(getActiveChar()));
		
		// Synchronize level with pet if possible.
		final Summon sPet = getActiveChar().getPet();
		if (sPet != null)
		{
			final L2PetInstance pet = (L2PetInstance) sPet;
			if (pet.getPetData().isSynchLevel() && (pet.getLevel() != getLevel()))
			{
				pet.getStat().setLevel(getLevel());
				pet.getStat().getExpForLevel(getActiveChar().getLevel());
				pet.setCurrentHp(pet.getMaxHp());
				pet.setCurrentMp(pet.getMaxMp());
				pet.broadcastPacket(new SocialAction(getActiveChar().getObjectId(), SocialAction.LEVEL_UP));
				pet.updateAndBroadcastStatus(1);
			}
		}
		
		getActiveChar().broadcastStatusUpdate();
		// Update the overloaded status of the L2PcInstance
		getActiveChar().refreshOverloaded(true);
		// Update the expertise status of the L2PcInstance
		getActiveChar().refreshExpertisePenalty();
		// Send a Server->Client packet UserInfo to the L2PcInstance
		getActiveChar().sendPacket(new UserInfo(getActiveChar()));
		// Send acquirable skill list
		getActiveChar().sendPacket(new AcquireSkillList(getActiveChar()));
		getActiveChar().sendPacket(new ExVoteSystemInfo(getActiveChar()));
		if ((getLevel() >= 99) && getActiveChar().isNoble())
		{
			getActiveChar().sendPacket(new ExAcquireAPSkillList(getActiveChar()));
		}
		getActiveChar().sendPacket(new ExOneDayReceiveRewardList(getActiveChar()));
		return levelIncreased;
	}
	
	@Override
	public boolean addSp(long value)
	{
		if (!super.addSp(value))
		{
			return false;
		}
		UserInfo ui = new UserInfo(getActiveChar(), false);
		ui.addComponentType(UserInfoType.CURRENT_HPMPCP_EXP_SP);
		getActiveChar().sendPacket(ui);
		
		return true;
	}
	
	@Override
	public final long getExpForLevel(int level)
	{
		return ExperienceData.getInstance().getExpForLevel(level);
	}
	
	@Override
	public final PlayerInstance getActiveChar()
	{
		return (PlayerInstance) super.getActiveChar();
	}
	
	@Override
	public final long getExp()
	{
		if (getActiveChar().isSubClassActive())
		{
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getExp();
		}
		
		return super.getExp();
	}
	
	public final long getBaseExp()
	{
		return super.getExp();
	}
	
	@Override
	public final void setExp(long value)
	{
		if (getActiveChar().isSubClassActive())
		{
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setExp(value);
		}
		else
		{
			super.setExp(value);
		}
	}
	
	public void setStartingExp(long value)
	{
		if (Config.BOTREPORT_ENABLE)
		{
			_startingXp = value;
		}
	}
	
	public long getStartingExp()
	{
		return _startingXp;
	}
	
	/**
	 * Gets the maximum talisman count.
	 * @return the maximum talisman count
	 */
	public int getTalismanSlots()
	{
		return _talismanSlots.get();
	}
	
	public void addTalismanSlots(int count)
	{
		_talismanSlots.addAndGet(count);
	}
	
	public boolean canEquipCloak()
	{
		return _cloakSlot;
	}
	
	public void setCloakSlotStatus(boolean cloakSlot)
	{
		_cloakSlot = cloakSlot;
	}
	
	@Override
	public final byte getLevel()
	{
		if (getActiveChar().isSubClassActive())
		{
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getLevel();
		}
		
		return super.getLevel();
	}
	
	public final byte getBaseLevel()
	{
		return super.getLevel();
	}
	
	@Override
	public final void setLevel(byte value)
	{
		if (value > (ExperienceData.getInstance().getMaxLevel() - 1))
		{
			value = (byte) (ExperienceData.getInstance().getMaxLevel() - 1);
		}
		
		if (getActiveChar().isSubClassActive())
		{
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setLevel(value);
		}
		else
		{
			super.setLevel(value);
		}
	}
	
	@Override
	public final long getSp()
	{
		if (getActiveChar().isSubClassActive())
		{
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getSp();
		}
		
		return super.getSp();
	}
	
	public final long getBaseSp()
	{
		return super.getSp();
	}
	
	@Override
	public final void setSp(long value)
	{
		if (getActiveChar().isSubClassActive())
		{
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setSp(value);
		}
		else
		{
			super.setSp(value);
		}
	}
	
	/*
	 * Return current vitality points in integer format
	 */
	public int getVitalityPoints()
	{
		if (getActiveChar().isSubClassActive())
		{
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getVitalityPoints();
		}
		return _vitalityPoints;
	}
	
	public int getBaseVitalityPoints()
	{
		return _vitalityPoints;
	}
	
	public double getVitalityExpBonus()
	{
		return (getVitalityPoints() > 0) ? Config.RATE_VITALITY_EXP_MULTIPLIER : 1.0;
	}
	
	public void setVitalityPoints(int value)
	{
		if (getActiveChar().isSubClassActive())
		{
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setVitalityPoints(value);
			return;
		}
		_vitalityPoints = value;
	}
	
	/*
	 * Set current vitality points to this value if quiet = true - does not send system messages
	 */
	public void setVitalityPoints(int points, boolean quiet)
	{
		points = Math.min(Math.max(points, MIN_VITALITY_POINTS), MAX_VITALITY_POINTS);
		if (points == getVitalityPoints())
		{
			return;
		}
		
		if (points < getVitalityPoints())
		{
			getActiveChar().sendPacket(SystemMessageId.YOUR_VITALITY_HAS_DECREASED);
		}
		else
		{
			getActiveChar().sendPacket(SystemMessageId.YOUR_VITALITY_HAS_INCREASED);
		}
		
		setVitalityPoints(points);
		
		if (points == 0)
		{
			getActiveChar().sendPacket(SystemMessageId.YOUR_VITALITY_IS_FULLY_EXHAUSTED);
		}
		else if (points == MAX_VITALITY_POINTS)
		{
			getActiveChar().sendPacket(SystemMessageId.YOUR_VITALITY_IS_AT_MAXIMUM);
		}
		
		final PlayerInstance player = getActiveChar();
		player.sendPacket(new ExVitalityPointInfo(getVitalityPoints()));
		player.broadcastUserInfo(UserInfoType.VITA_FAME);
		final Party party = player.getParty();
		if (party != null)
		{
			final PartySmallWindowUpdate partyWindow = new PartySmallWindowUpdate(player, false);
			partyWindow.addComponentType(PartySmallWindowUpdateType.VITALITY_POINTS);
			party.broadcastToPartyMembers(player, partyWindow);
		}
	}
	
	public synchronized void updateVitalityPoints(int points, boolean useRates, boolean quiet)
	{
		if ((points == 0) || !Config.ENABLE_VITALITY)
		{
			return;
		}
		
		if (useRates)
		{
			if (getActiveChar().isLucky())
			{
				return;
			}
			
			if (points < 0) // vitality consumed
			{
				int stat = (int) getValue(Stats.VITALITY_CONSUME_RATE, 1);
				
				if (stat == 0)
				{
					return;
				}
				if (stat < 0)
				{
					points = -points;
				}
			}
			
			if (points > 0)
			{
				// vitality increased
				points *= Config.RATE_VITALITY_GAIN;
			}
			else
			{
				// vitality decreased
				points *= Config.RATE_VITALITY_LOST;
			}
		}
		
		if (points > 0)
		{
			points = Math.min(getVitalityPoints() + points, MAX_VITALITY_POINTS);
		}
		else
		{
			points = Math.max(getVitalityPoints() + points, MIN_VITALITY_POINTS);
		}
		
		if (Math.abs(points - getVitalityPoints()) <= 1e-6)
		{
			return;
		}
		
		setVitalityPoints(points);
	}
	
	public double getExpBonusMultiplier()
	{
		double bonus = 1.0;
		double vitality = 1.0;
		double bonusExp = 1.0;
		
		// Bonus from Vitality System
		vitality = getVitalityExpBonus();
		
		// Bonus exp from skills
		bonusExp = 1 + (getValue(Stats.BONUS_EXP, 0) / 100);
		
		if (vitality > 1.0)
		{
			bonus += (vitality - 1);
		}
		
		if (bonusExp > 1)
		{
			bonus += (bonusExp - 1);
		}
		
		// Check for abnormal bonuses
		bonus = Math.max(bonus, 1);
		if (Config.MAX_BONUS_EXP > 0)
		{
			bonus = Math.min(bonus, Config.MAX_BONUS_EXP);
		}
		
		return bonus;
	}
	
	public double getSpBonusMultiplier()
	{
		double bonus = 1.0;
		double vitality = 1.0;
		double bonusSp = 1.0;
		
		// Bonus from Vitality System
		vitality = getVitalityExpBonus();
		
		// Bonus sp from skills
		bonusSp = 1 + (getValue(Stats.BONUS_SP, 0) / 100);
		
		if (vitality > 1.0)
		{
			bonus += (vitality - 1);
		}
		
		if (bonusSp > 1)
		{
			bonus += (bonusSp - 1);
		}
		
		// Check for abnormal bonuses
		bonus = Math.max(bonus, 1);
		if (Config.MAX_BONUS_SP > 0)
		{
			bonus = Math.min(bonus, Config.MAX_BONUS_SP);
		}
		
		return bonus;
	}
	
	/**
	 * Gets the maximum brooch jewel count.
	 * @return the maximum brooch jewel count
	 */
	public int getBroochJewelSlots()
	{
		return (int) getValue(Stats.BROOCH_JEWELS, 0);
	}
	
	@Override
	protected void onRecalculateStats(boolean broadcast)
	{
		super.onRecalculateStats(broadcast);
		
		final PlayerInstance player = getActiveChar();
		
		// Upon master stats recalculation trigger pet/servitor recalculation too
		if (player.hasPet())
		{
			player.getPet().getStat().recalculateStats(broadcast);
		}
		if (player.hasServitors())
		{
			player.getServitors().values().forEach(servitor -> servitor.getStat().recalculateStats(broadcast));
		}
	}
}

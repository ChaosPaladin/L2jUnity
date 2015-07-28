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
package quests.Q10751_WindsOfFateEncounters;

import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.enums.HtmlActionScope;
import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.base.ClassId;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.annotations.RegisterEvent;
import org.l2junity.gameserver.model.events.annotations.RegisterType;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import org.l2junity.gameserver.model.holders.NpcLogListHolder;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.PlaySound;
import org.l2junity.gameserver.network.client.send.SocialAction;
import org.l2junity.gameserver.network.client.send.TutorialCloseHtml;
import org.l2junity.gameserver.network.client.send.TutorialShowHtml;
import org.l2junity.gameserver.network.client.send.TutorialShowQuestionMark;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

/**
 * Winds of Fate: Encounters (10751)
 * @author malyelfik
 */
public final class Q10751_WindsOfFateEncounters extends Quest
{
	// NPC
	private static final int NAVARI = 33931;
	private static final int AYANTHE = 33942;
	private static final int KATALIN = 33943;
	private static final int RAYMOND = 30289;
	private static final int MYSTERIOUS_WIZARD = 33980;
	private static final int TELESHA = 33981;
	// Monsters
	private static final int[] MONSTERS =
	{
		27528, // Skeleton Warrior
		27529, // Skeleton Archer
	};
	// Items
	private static final int WIND_SPIRIT_REALMS_RELIC = 39535;
	private static final int NAVARI_SUPPORT_BOX_FIGHTER = 40266;
	private static final int NAVARI_SUPPORT_BOX_MAGE = 40267;
	// Location
	private static final Location TELEPORT_LOC = new Location(-80565, 251763, -3080);
	// Misc
	private static final int MIN_LEVEL = 38;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10751_WindsOfFateEncounters()
	{
		super(10751, Q10751_WindsOfFateEncounters.class.getSimpleName(), "Winds of Fate: Encounters");
		addStartNpc(NAVARI);
		addFirstTalkId(TELESHA, MYSTERIOUS_WIZARD);
		addTalkId(NAVARI, AYANTHE, KATALIN, RAYMOND, TELESHA, MYSTERIOUS_WIZARD);
		addKillId(MONSTERS);
		
		addCondRace(Race.ERTHEIA, "");
		addCondMinLevel(MIN_LEVEL, "33931-00.htm");
		registerQuestItems(WIND_SPIRIT_REALMS_RELIC);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "30289-02.html":
			case "30289-06.html":
			case "33942-05.html":
			case "33942-06.html":
			case "33942-07.html":
			case "33942-08.html":
			case "33942-09.html":
			case "33942-10.html":
			case "33943-05.html":
			case "33943-06.html":
			case "33943-07.html":
			case "33943-08.html":
			case "33943-09.html":
			case "33943-10.html":
				break;
			case "33931-02.htm":
			{
				qs.startQuest();
				if (player.isMageClass())
				{
					qs.setCond(3, true);
				}
				else
				{
					qs.setCond(2, true);
					htmltext = "33931-03.htm";
				}
				break;
			}
			case "33943-02.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(4, true);
				}
				break;
			}
			case "33942-02.html":
			{
				if (qs.isCond(3))
				{
					qs.setCond(5, true);
				}
				break;
			}
			case "30289-03.html":
			{
				if (qs.isCond(4) || qs.isCond(5))
				{
					giveItems(player, WIND_SPIRIT_REALMS_RELIC, 1);
					qs.setCond(6, true);
				}
				break;
			}
			case "SPAWN_WIZZARD":
			{
				if (qs.isCond(6) && (npc != null) && (npc.getId() == TELESHA))
				{
					final Npc wizzard = addSpawn(MYSTERIOUS_WIZARD, npc, true, 30000);
					wizzard.setSummoner(player);
					wizzard.setTitle(player.getAppearance().getVisibleName());
					wizzard.broadcastInfo();
					showOnScreenMsg(player, NpcStringId.TALK_TO_THE_MYSTERIOUS_WIZARD2, ExShowScreenMessage.TOP_CENTER, 10000);
					npc.deleteMe();
				}
				htmltext = null;
				break;
			}
			case "33980-02.html":
			{
				if (qs.isCond(6))
				{
					giveItems(player, WIND_SPIRIT_REALMS_RELIC, 1);
					qs.setCond(7, true);
					showOnScreenMsg(player, NpcStringId.RETURN_TO_RAYMOND_OF_THE_TOWN_OF_GLUDIO, ExShowScreenMessage.TOP_CENTER, 8000);
				}
				break;
			}
			case "30289-07.html":
			{
				if (qs.isCond(7))
				{
					if (!player.isMageClass())
					{
						qs.setCond(8, true);
					}
					else
					{
						qs.setCond(9, true);
						htmltext = "30289-08.html";
					}
				}
				break;
			}
			case "33942-11.html":
			{
				final ClassId newClass = ClassId.CLOUD_BREAKER;
				if (qs.isCond(9) && newClass.childOf(player.getClassId()))
				{
					player.setBaseClass(newClass);
					player.setClassId(newClass.getId());
					player.broadcastUserInfo();
					player.sendPacket(new SocialAction(player.getObjectId(), 23));
					giveAdena(player, 11000, false);
					giveItems(player, NAVARI_SUPPORT_BOX_MAGE, 1);
					addExpAndSp(player, 2700000, 648);
					qs.exitQuest(false, true);
				}
				break;
			}
			case "33943-11.html":
			{
				final ClassId newClass = ClassId.MARAUDER;
				if (qs.isCond(8) && newClass.childOf(player.getClassId()))
				{
					player.setBaseClass(newClass);
					player.setClassId(newClass.getId());
					player.broadcastUserInfo();
					player.sendPacket(new SocialAction(player.getObjectId(), 23));
					giveAdena(player, 11000, false);
					giveItems(player, NAVARI_SUPPORT_BOX_FIGHTER, 1);
					addExpAndSp(player, 2700000, 648);
					qs.exitQuest(false, true);
				}
				break;
			}
			default:
				htmltext = null;
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		if (npc.getId() == TELESHA)
		{
			htmltext = "33981-01.html";
		}
		else
		{
			final QuestState qs = getQuestState(player, false);
			if (qs == null)
			{
				return htmltext;
			}
			if (qs.isCond(6))
			{
				htmltext = "33980-01.html";
			}
			else if (qs.isCond(7))
			{
				htmltext = "33980-03.html";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (npc.getId())
		{
			case NAVARI:
			{
				switch (qs.getState())
				{
					case State.CREATED:
						htmltext = "33931-01.htm";
						break;
					case State.STARTED:
					{
						switch (qs.getCond())
						{
							case 2:
								htmltext = "33931-04.html";
								break;
							case 3:
								htmltext = "33931-05.html";
								break;
						}
						break;
					}
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			}
			case KATALIN:
			{
				if (!player.isMageClass())
				{
					if (qs.isStarted())
					{
						switch (qs.getCond())
						{
							case 2:
								htmltext = "33943-01.html";
								break;
							case 4:
								htmltext = "33943-03.html";
								break;
							case 8:
								htmltext = "33943-04.html";
								break;
						}
					}
					else if (qs.isCompleted())
					{
						htmltext = getAlreadyCompletedMsg(player);
					}
				}
				break;
			}
			case AYANTHE:
			{
				if (player.isMageClass())
				{
					if (qs.isStarted())
					{
						switch (qs.getCond())
						{
							case 3:
								htmltext = "33942-01.html";
								break;
							case 5:
								htmltext = "33942-03.html";
								break;
							case 9:
								htmltext = "33942-04.html";
								break;
						}
					}
					else if (qs.isCompleted())
					{
						htmltext = getAlreadyCompletedMsg(player);
					}
				}
				break;
			}
			case RAYMOND:
			{
				if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 4:
						case 5:
							htmltext = "30289-01.html";
							break;
						case 6:
							htmltext = "30289-04.html";
							break;
						case 7:
							htmltext = "30289-05.html";
							break;
						case 8:
							htmltext = "30289-09.html";
							break;
						case 9:
							htmltext = "30289-10.html";
							break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(6))
		{
			int killCount = qs.getInt(KILL_COUNT_VAR);
			if (killCount <= 5)
			{
				qs.set(KILL_COUNT_VAR, ++killCount);
				sendNpcLogList(killer);
			}
			
			if ((killCount >= 5) && !World.getInstance().getVisibleObjects(npc, Npc.class, 1000).stream().anyMatch(n -> ((n.getId() == TELESHA) && (n.getSummoner() == killer))))
			{
				final Npc telsha = addSpawn(TELESHA, npc, false, 30000);
				telsha.setSummoner(killer);
				telsha.setTitle(killer.getAppearance().getVisibleName());
				telsha.broadcastInfo();
				showOnScreenMsg(killer, NpcStringId.CHECK_ON_TELESHA, ExShowScreenMessage.TOP_CENTER, 10000);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(6))
		{
			final int killCount = qs.getInt(KILL_COUNT_VAR);
			if (killCount > 0)
			{
				final Set<NpcLogListHolder> holder = new HashSet<>(1);
				holder.add(new NpcLogListHolder(NpcStringId.KILL_SKELETONS, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event)
	{
		if (event.getMarkId() == getId())
		{
			final PlayerInstance player = event.getActiveChar();
			final QuestState qs = getQuestState(player, false);
			if (qs == null)
			{
				player.sendPacket(new PlaySound(3, "Npcdialog1.serenia_quest_12", 0, 0, 0, 0, 0));
				player.sendPacket(new TutorialShowHtml(getHtm(player.getHtmlPrefix(), "popup.html")));
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_BYPASS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerBypass(OnPlayerBypass event)
	{
		final String command = event.getCommand();
		final PlayerInstance player = event.getActiveChar();
		final QuestState st = getQuestState(player, false);
		
		if (st == null)
		{
			if (command.equals("Q10751_teleport"))
			{
				player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
				if (!player.isInCombat())
				{
					player.teleToLocation(TELEPORT_LOC);
				}
				else
				{
					showOnScreenMsg(player, NpcStringId.YOU_CANNOT_TELEPORT_IN_COMBAT, ExShowScreenMessage.TOP_CENTER, 5000);
					player.sendPacket(new TutorialShowQuestionMark(getId()));
				}
				player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
			}
			else if (command.equals("Q10751_close"))
			{
				player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
				player.sendPacket(new TutorialShowQuestionMark(getId()));
				player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		final PlayerInstance player = event.getActiveChar();
		final QuestState st = getQuestState(player, false);
		final int oldLevel = event.getOldLevel();
		final int newLevel = event.getNewLevel();
		
		if ((st == null) && (player.getRace().equals(Race.ERTHEIA)) && (oldLevel < newLevel) && (newLevel >= MIN_LEVEL))
		{
			showOnScreenMsg(player, NpcStringId.QUEEN_NAVARI_HAS_SENT_A_LETTER_NCLICK_THE_QUESTION_MARK_ICON_TO_READ, ExShowScreenMessage.TOP_CENTER, 10000);
			player.sendPacket(new TutorialShowQuestionMark(getId()));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		final PlayerInstance player = event.getActiveChar();
		final QuestState st = getQuestState(player, false);
		
		if ((st == null) && player.getRace().equals(Race.ERTHEIA) && (player.getLevel() >= MIN_LEVEL))
		{
			showOnScreenMsg(player, NpcStringId.QUEEN_NAVARI_HAS_SENT_A_LETTER_NCLICK_THE_QUESTION_MARK_ICON_TO_READ, ExShowScreenMessage.TOP_CENTER, 10000);
			player.sendPacket(new TutorialShowQuestionMark(getId()));
		}
	}
	
	public static void main(String[] args)
	{
		new Q10751_WindsOfFateEncounters();
	}
}
/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q10393_KekropusLetterAClueCompleted;

import org.l2junity.gameserver.enums.HtmlActionScope;
import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.annotations.RegisterEvent;
import org.l2junity.gameserver.model.events.annotations.RegisterType;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.TutorialCloseHtml;
import org.l2junity.gameserver.network.client.send.TutorialShowHtml;
import org.l2junity.gameserver.network.client.send.TutorialShowQuestionMark;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

/**
 * Kekropus' Letter: A Clue Completed (10393)
 * @author St3eT
 */
public final class Q10393_KekropusLetterAClueCompleted extends Quest
{
	// NPCs
	private static final int FLUTER = 30677;
	private static final int KELIOS = 33862;
	private static final int INVISIBLE_NPC = 19543;
	// Items
	private static final int SOE_TOWN_OF_OREN = 37113; // Scroll of Escape: Town of Oren
	private static final int SOE_OUTLAW_FOREST = 37026; // Scroll of Escape: Outlaw Forest
	private static final int EAC = 952; // Scroll: Enchant Armor (C-grade)
	private static final int STEEL_COIN = 37045; // Steel Door Guild Coin
	// Location
	private static final Location TELEPORT_LOC = new Location(83676, 55510, -1512);
	// Misc
	private static final int MIN_LEVEL = 46;
	private static final int MAX_LEVEL = 51;
	
	public Q10393_KekropusLetterAClueCompleted()
	{
		super(10393, Q10393_KekropusLetterAClueCompleted.class.getSimpleName(), "Kekropus' Letter: A Clue Completed");
		addTalkId(FLUTER, KELIOS);
		addSeeCreatureId(INVISIBLE_NPC);
		registerQuestItems(SOE_TOWN_OF_OREN, SOE_OUTLAW_FOREST);
		addCondNotRace(Race.ERTHEIA, "");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState st = getQuestState(player, false);
		
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30677-02.html":
			{
				htmltext = event;
				break;
			}
			case "30677-03.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
					giveItems(player, SOE_OUTLAW_FOREST, 1);
					st.setQuestLocation(NpcStringId.OUTLAW_FOREST_LV_46);
					htmltext = event;
				}
				break;
			}
			case "33862-02.html":
			{
				if (st.isCond(2))
				{
					st.exitQuest(false, true);
					giveItems(player, EAC, 4);
					giveItems(player, STEEL_COIN, 15);
					addExpAndSp(player, 483840, 116);
					showOnScreenMsg(player, NpcStringId.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_52, ExShowScreenMessage.TOP_CENTER, 6000);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		if (st == null)
		{
			return htmltext;
		}
		
		if (st.getState() == State.STARTED)
		{
			if (st.isCond(1))
			{
				if (npc.getId() == FLUTER)
				{
					htmltext = "30677-01.html";
				}
			}
			else if (st.isCond(2))
			{
				htmltext = npc.getId() == FLUTER ? "30677-04.html" : "33862-01.html";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			final PlayerInstance player = creature.getActingPlayer();
			final QuestState st = getQuestState(player, false);
			
			if ((st != null) && st.isCond(2))
			{
				showOnScreenMsg(player, NpcStringId.OUTLAW_FOREST_IS_A_GOOD_HUNTING_ZONE_FOR_LV_46_OR_ABOVE, ExShowScreenMessage.TOP_CENTER, 6000);
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		final PlayerInstance player = event.getActiveChar();
		final int oldLevel = event.getOldLevel();
		final int newLevel = event.getNewLevel();
		
		if ((oldLevel < newLevel) && (newLevel == MIN_LEVEL) && (player.getRace() != Race.ERTHEIA))
		{
			player.sendPacket(new TutorialShowQuestionMark(getId()));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event)
	{
		if (event.getMarkId() == getId())
		{
			final PlayerInstance player = event.getActiveChar();
			final QuestState st = getQuestState(player, true);
			
			st.startQuest();
			st.setQuestLocation(NpcStringId.TOWN_OF_OREN);
			giveItems(player, SOE_TOWN_OF_OREN, 1);
			player.sendPacket(new TutorialShowHtml(getHtm(player.getHtmlPrefix(), "popup.html")));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_BYPASS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerBypass(OnPlayerBypass event)
	{
		final String command = event.getCommand();
		final PlayerInstance player = event.getActiveChar();
		final QuestState st = getQuestState(player, false);
		
		if (command.equals("Q10393_teleport") && (st != null) && st.isCond(1) && hasQuestItems(player, SOE_TOWN_OF_OREN))
		{
			player.teleToLocation(TELEPORT_LOC);
			takeItems(player, SOE_TOWN_OF_OREN, -1);
			player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
			player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		final PlayerInstance player = event.getActiveChar();
		final QuestState st = getQuestState(player, false);
		
		if ((player.getLevel() >= MIN_LEVEL) && (player.getLevel() <= MAX_LEVEL) && (st == null))
		{
			player.sendPacket(new TutorialShowQuestionMark(getId()));
		}
	}
}
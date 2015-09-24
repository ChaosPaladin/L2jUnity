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
package quests.Q00017_LightAndDarkness;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

import quests.Q00015_SweetWhispers.Q00015_SweetWhispers;

/**
 * Light And Darkness (17)
 * @author nonom
 */
public class Q00017_LightAndDarkness extends Quest
{
	// NPCs
	private static final int HIERARCH = 31517;
	private static final int SAINT_ALTAR_1 = 31508;
	private static final int SAINT_ALTAR_2 = 31509;
	private static final int SAINT_ALTAR_3 = 31510;
	private static final int SAINT_ALTAR_4 = 31511;
	// Item
	private static final int BLOOD_OF_SAINT = 7168;
	// Misc
	private static final int MIN_LEVEL = 61;
	
	public Q00017_LightAndDarkness()
	{
		super(17, Q00017_LightAndDarkness.class.getSimpleName(), "Light and Darkness");
		addStartNpc(HIERARCH);
		addTalkId(HIERARCH, SAINT_ALTAR_1, SAINT_ALTAR_2, SAINT_ALTAR_3, SAINT_ALTAR_4);
		registerQuestItems(BLOOD_OF_SAINT);
		addCondMaxLevel(MIN_LEVEL, "31517-02a.html");
		addCondCompletedQuest(Q00015_SweetWhispers.class.getSimpleName(), "");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "31517-02.html":
				{
					st.startQuest();
					giveItems(player, BLOOD_OF_SAINT, 4);
				}
				break;
			case "31508-02.html":
			case "31509-02.html":
			case "31510-02.html":
			case "31511-02.html":
				final int cond = st.getCond();
				final int npcId = Integer.parseInt(event.replace("-02.html", ""));
				if ((cond == (npcId - 31507)) && hasQuestItems(player, BLOOD_OF_SAINT))
				{
					htmltext = npcId + "-01.html";
					takeItems(player, BLOOD_OF_SAINT, 1);
					st.setCond(cond + 1, true);
				}
				break;
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
		
		switch (st.getState())
		{
			case State.CREATED:
				htmltext = "31517-00.htm";
				break;
			case State.STARTED:
				final long blood = getQuestItemsCount(player, BLOOD_OF_SAINT);
				final int npcId = npc.getId();
				switch (npcId)
				{
					case HIERARCH:
						if (st.getCond() < 5)
						{
							htmltext = (blood >= 5) ? "31517-05.html" : "31517-04.html";
						}
						else
						{
							addExpAndSp(player, 1_469_840, 352);
							st.exitQuest(false, true);
							htmltext = "31517-03.html";
						}
						break;
					case SAINT_ALTAR_1:
					case SAINT_ALTAR_2:
					case SAINT_ALTAR_3:
					case SAINT_ALTAR_4:
						if ((npcId - 31507) == st.getCond())
						{
							htmltext = npcId + ((blood > 0) ? "-00.html" : "-02.html");
						}
						else if (st.getCond() > (npcId - 31507))
						{
							htmltext = npcId + "-03.html";
						}
						break;
				}
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg(player);
				break;
		}
		return htmltext;
	}
}

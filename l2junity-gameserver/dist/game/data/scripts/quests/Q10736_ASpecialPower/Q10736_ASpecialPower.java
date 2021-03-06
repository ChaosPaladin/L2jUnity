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
package quests.Q10736_ASpecialPower;

import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.base.ClassId;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.l2junity.gameserver.model.holders.NpcLogListHolder;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

import quests.Q10734_DoOrDie.Q10734_DoOrDie;

/**
 * A Special Power (10736)
 * @author Sdw
 */
public final class Q10736_ASpecialPower extends Quest
{
	// NPC
	private static final int KATALIN = 33943;
	// Monsters
	private static final int FLOATO = 27526;
	private static final int FLOATO2 = 27531;
	private static final int RATEL = 27527;
	// Items
	private static final ItemHolder SOULSHOTS_REWARD = new ItemHolder(1835, 500);
	// Misc
	private static final int MIN_LEVEL = 4;
	private static final int MAX_LEVEL = 20;
	public static final int KILL_COUNT_VAR = 0;
	
	public Q10736_ASpecialPower()
	{
		super(10735);
		addStartNpc(KATALIN);
		addTalkId(KATALIN);
		
		addCondRace(Race.ERTHEIA, "");
		addCondClassId(ClassId.ERTHEIA_FIGHTER, "");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33943-00.htm");
		addCondCompletedQuest(Q10734_DoOrDie.class.getSimpleName(), "33943-00.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && event.equals("33943-02.htm"))
		{
			qs.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player, boolean isSimulated)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
				htmltext = "33943-01.htm";
				break;
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "33943-03.html";
						break;
					}
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					{
						htmltext = "33943-04.html";
						break;
					}
					case 7:
					{
						if (!isSimulated)
						{
							giveAdena(player, 900, true);
							rewardItems(player, SOULSHOTS_REWARD);
							addExpAndSp(player, 3154, 0);
							qs.exitQuest(false, true);
						}
						htmltext = "33943-05.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg(player);
				break;
		}
		return htmltext;
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final Set<NpcLogListHolder> holder = new HashSet<>();
		final QuestState qs = getQuestState(player, false);
		if (qs != null)
		{
			int npcId = -1;
			switch (qs.getCond())
			{
				case 2:
					npcId = FLOATO;
					break;
				case 4:
					npcId = FLOATO2;
					break;
				case 6:
					npcId = RATEL;
					break;
			}
			if (npcId != -1)
			{
				holder.add(new NpcLogListHolder(npcId, false, qs.getMemoStateEx(KILL_COUNT_VAR)));
			}
		}
		return holder;
	}
}
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
package quests.Q10780_AWeakenedBarrier;

import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.NpcLogListHolder;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

/**
 * A Weakened Barrier (10780)
 * @author malyelfik
 */
public final class Q10780_AWeakenedBarrier extends Quest
{
	// NPCs
	private static final int ANDY = 33845;
	private static final int BACON = 33846;
	// Monsters
	private static final int[] MONSTERS =
	{
		20555, // Giant Fungus
		20558, // Rotting tree
		23305, // Corroded Skeleton
		23306, // Rotten Corpse
		23307, // Corpse Spider
		23308, // Explosive Spider
	};
	// Items
	private static final int ENCHANT_ARMOR_B = 948;
	private static final int STEEL_DOOR_GUILD_COIN = 37045;
	// Misc
	private static final int MIN_LEVEL = 52;
	private static final int MAX_LEVEL = 58;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10780_AWeakenedBarrier()
	{
		super(10780, Q10780_AWeakenedBarrier.class.getSimpleName(), "A Weakened Barrier");
		addStartNpc(ANDY);
		addTalkId(ANDY, BACON);
		addKillId(MONSTERS);
		
		addCondRace(Race.ERTHEIA, "33845-01.htm");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33845-02.htm");
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
			case "33845-04.htm":
			case "33845-05.htm":
				break;
			case "33845-06.htm":
			{
				qs.startQuest();
				break;
			}
			case "33846-03.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, ENCHANT_ARMOR_B, 5);
					giveItems(player, STEEL_DOOR_GUILD_COIN, 36);
					addExpAndSp(player, 3811500, 914);
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
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == ANDY)
		{
			switch (qs.getState())
			{
				case State.CREATED:
					htmltext = "33845-03.htm";
					break;
				case State.STARTED:
					if (qs.isCond(1))
					{
						htmltext = "33845-07.html";
					}
					break;
				case State.COMPLETED:
					htmltext = getAlreadyCompletedMsg(player);
					break;
			}
		}
		else if (qs.isStarted())
		{
			htmltext = (qs.isCond(1)) ? "33846-01.html" : "33846-02.html";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			int count = qs.getInt(KILL_COUNT_VAR);
			if (count < 20)
			{
				qs.set(KILL_COUNT_VAR, ++count);
				if (count >= 20)
				{
					qs.setCond(2, true);
				}
				else
				{
					sendNpcLogList(killer);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final int killCount = qs.getInt(KILL_COUNT_VAR);
			if (killCount > 0)
			{
				final Set<NpcLogListHolder> holder = new HashSet<>(1);
				holder.add(new NpcLogListHolder(NpcStringId.KILL_MONSTERS_NEAR_THE_SEA_OF_SPORES, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
	
	public static void main(String[] args)
	{
		new Q10780_AWeakenedBarrier();
	}
}
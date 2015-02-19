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
package quests.Q00453_NotStrongEnoughAlone;

import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.enums.QuestSound;
import org.l2junity.gameserver.enums.QuestType;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.NpcLogListHolder;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.util.Util;

import quests.Q10282_ToTheSeedOfAnnihilation.Q10282_ToTheSeedOfAnnihilation;

/**
 * Not Strong Enough Alone (453)
 * @author malyelfik
 */
public final class Q00453_NotStrongEnoughAlone extends Quest
{
	// NPCs
	private static final int KLEMIS = 32734;
	private static final int[] MONSTER1 =
	{
		22746,
		22747,
		22748,
		22749,
		22750,
		22751,
		22752,
		22753
	};
	private static final int[] MONSTER2 =
	{
		22754,
		22755,
		22756,
		22757,
		22758,
		22759
	};
	private static final int[] MONSTER3 =
	{
		22760,
		22761,
		22762,
		22763,
		22764,
		22765
	};
	// Reward
	private static final int POUCH = 34861; // Ingredient and Hardener Pouch (R-grade)
	private static final int EWR = 17526; // Scroll: Enchant Weapon (R-Grade)
	private static final int EAR = 17527; // Scroll: Enchant Armor (R-Grade)
	// @formatter:off
	private static final int[] ATT_STONES =
	{
		9546, 9547, 9548, 9549, 9550, 9551,
	};
	private static final int[] ATT_CRYSTALS =
	{
		9552, 9553, 9554, 9555, 5956, 9557,
	};
	// @formatter:on
	
	// Misc
	private static final int MIN_LV = 85;
	
	public Q00453_NotStrongEnoughAlone()
	{
		super(453, Q00453_NotStrongEnoughAlone.class.getSimpleName(), "Not Strong Enought Alone");
		addStartNpc(KLEMIS);
		addTalkId(KLEMIS);
		addKillId(MONSTER1);
		addKillId(MONSTER2);
		addKillId(MONSTER3);
		addCondCompletedQuest(Q10282_ToTheSeedOfAnnihilation.class.getSimpleName(), "32734-03.html");
		addCondMinLevel(MIN_LV, "32734-03.html");
	}
	
	private void increaseKill(PlayerInstance player, Npc npc)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return;
		}
		
		int npcId = npc.getId();
		
		if (Util.checkIfInRange(1500, npc, player, false))
		{
			if (Util.contains(MONSTER1, npcId) && st.isCond(2))
			{
				if (npcId == MONSTER1[4])
				{
					npcId = MONSTER1[0];
				}
				else if (npcId == MONSTER1[5])
				{
					npcId = MONSTER1[1];
				}
				else if (npcId == MONSTER1[6])
				{
					npcId = MONSTER1[2];
				}
				else if (npcId == MONSTER1[7])
				{
					npcId = MONSTER1[3];
				}
				
				final int currValue = st.getInt("count_" + npcId);
				if (currValue < 15)
				{
					st.set("count_" + npcId, currValue + 1);
					st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				checkProgress(st, 15, MONSTER1[0], MONSTER1[1], MONSTER1[2], MONSTER1[3]);
			}
			else if (Util.contains(MONSTER2, npcId) && st.isCond(3))
			{
				if (npcId == MONSTER2[3])
				{
					npcId = MONSTER2[0];
				}
				else if (npcId == MONSTER2[4])
				{
					npcId = MONSTER2[1];
				}
				else if (npcId == MONSTER2[5])
				{
					npcId = MONSTER2[2];
				}
				
				final int currValue = st.getInt("count_" + npcId);
				if (currValue < 20)
				{
					st.set("count_" + npcId, currValue + 1);
					st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				checkProgress(st, 20, MONSTER2[0], MONSTER2[1], MONSTER2[2]);
			}
			else if (Util.contains(MONSTER3, npcId) && st.isCond(4))
			{
				if (npcId == MONSTER3[3])
				{
					npcId = MONSTER3[0];
				}
				else if (npcId == MONSTER3[4])
				{
					npcId = MONSTER3[1];
				}
				else if (npcId == MONSTER3[5])
				{
					npcId = MONSTER3[2];
				}
				
				final int currValue = st.getInt("count_" + npcId);
				if (currValue < 20)
				{
					st.set("count_" + npcId, currValue + 1);
					st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				checkProgress(st, 20, MONSTER3[0], MONSTER3[1], MONSTER3[2]);
			}
		}
		sendNpcLogList(player);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		switch (event)
		{
			case "32734-06.htm":
			{
				st.startQuest();
				break;
			}
			case "32734-07.html":
			{
				st.setCond(2, true);
				sendNpcLogList(player);
				break;
			}
			case "32734-08.html":
			{
				st.setCond(3, true);
				sendNpcLogList(player);
				break;
			}
			case "32734-09.html":
			{
				st.setCond(4, true);
				sendNpcLogList(player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		if (player.getParty() != null)
		{
			for (PlayerInstance member : player.getParty().getMembers())
			{
				increaseKill(member, npc);
			}
		}
		else
		{
			increaseKill(player, npc);
		}
		return null;
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
			{
				htmltext = "32734-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "32734-10.html";
						break;
					}
					case 2:
					{
						htmltext = "32734-11.html";
						break;
					}
					case 3:
					{
						htmltext = "32734-12.html";
						break;
					}
					case 4:
					{
						htmltext = "32734-13.html";
						break;
					}
					case 5:
					{
						final int random = getRandom(100);
						if (random < 10)
						{
							giveItems(player, POUCH, getRandom(1, 4));
						}
						else if (random < 20)
						{
							giveItems(player, (getRandom(100) < 25 ? EWR : EAR), 1);
						}
						else
						{
							giveItems(player, (getRandom(100) < 15 ? ATT_CRYSTALS[getRandom(ATT_CRYSTALS.length)] : ATT_STONES[getRandom(ATT_STONES.length)]), 1);
						}
						
						st.exitQuest(QuestType.DAILY, true);
						htmltext = "32734-14.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (st.isNowAvailable())
				{
					st.setState(State.CREATED);
					htmltext = "32734-01.htm";
				}
				else
				{
					htmltext = "32734-02.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	private static void checkProgress(QuestState st, int count, int... mobs)
	{
		for (int mob : mobs)
		{
			if (st.getInt("count_" + mob) < count)
			{
				return;
			}
		}
		st.setCond(5, true);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance activeChar)
	{
		final QuestState qs = getQuestState(activeChar, false);
		final Set<NpcLogListHolder> npcLogList = new HashSet<>(3);
		
		if (qs != null)
		{
			switch (qs.getCond())
			{
				case 2:
				{
					npcLogList.add(new NpcLogListHolder(MONSTER1[0], false, qs.getInt("count_" + MONSTER1[0])));
					npcLogList.add(new NpcLogListHolder(MONSTER1[1], false, qs.getInt("count_" + MONSTER1[1])));
					npcLogList.add(new NpcLogListHolder(MONSTER1[2], false, qs.getInt("count_" + MONSTER1[2])));
					npcLogList.add(new NpcLogListHolder(MONSTER1[3], false, qs.getInt("count_" + MONSTER1[3])));
					break;
				}
				case 3:
				{
					npcLogList.add(new NpcLogListHolder(MONSTER2[0], false, qs.getInt("count_" + MONSTER2[0])));
					npcLogList.add(new NpcLogListHolder(MONSTER2[1], false, qs.getInt("count_" + MONSTER2[1])));
					npcLogList.add(new NpcLogListHolder(MONSTER2[2], false, qs.getInt("count_" + MONSTER2[2])));
					break;
				}
				case 4:
				{
					npcLogList.add(new NpcLogListHolder(MONSTER3[0], false, qs.getInt("count_" + MONSTER3[0])));
					npcLogList.add(new NpcLogListHolder(MONSTER3[1], false, qs.getInt("count_" + MONSTER3[1])));
					npcLogList.add(new NpcLogListHolder(MONSTER3[2], false, qs.getInt("count_" + MONSTER3[2])));
					break;
				}
			}
			return npcLogList;
		}
		return super.getNpcLogList(activeChar);
	}
}
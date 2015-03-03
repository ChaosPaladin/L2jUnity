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
package quests.Q10363_RequestOfTheSeeker;

import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.annotations.RegisterEvent;
import org.l2junity.gameserver.model.events.annotations.RegisterType;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerSocialAction;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;
import org.l2junity.gameserver.util.Util;

import quests.Q10362_CertificationOfTheSeeker.Q10362_CertificationOfTheSeeker;

/**
 * Request of the Seeker (10363)
 * @author Gladicek
 */
public final class Q10363_RequestOfTheSeeker extends Quest
{
	// Npcs
	private static final int NAGEL = 33450;
	private static final int CELIN = 33451;
	// Items
	private static final int WOODEN_HELMET = 43;
	private static final int HEALING_POTION = 1060;
	// Misc
	private static final int MIN_LEVEL = 12;
	private static final int MAX_LEVEL = 20;
	private static final int SOCIAL_SORROW = 13;
	// Mobs
	private static final int STALKER = 22992;
	private static final int CRAWLER = 22991;
	private static final int[] CORPSES =
	{
		32961,
		32962,
		32963,
		32964,
	};
	
	public Q10363_RequestOfTheSeeker()
	{
		super(10363, Q10363_RequestOfTheSeeker.class.getSimpleName(), "Request of the Seeker");
		addStartNpc(NAGEL);
		addTalkId(NAGEL, CELIN);
		addSpawnId(CORPSES);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33450-07.htm");
		addCondCompletedQuest(Q10362_CertificationOfTheSeeker.class.getSimpleName(), "33450-07.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "33450-02.htm":
			case "33451-02.htm":
			{
				htmltext = event;
				break;
			}
			case "33450-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33450-06.htm":
			{
				if (qs.isCond(6))
				{
					showOnScreenMsg(player, NpcStringId.USE_THE_YE_SAGIRA_TELEPORT_DEVICE_TO_GO_TO_EXPLORATION_AREA_3, ExShowScreenMessage.TOP_CENTER, 4500);
					qs.setCond(7, true);
					htmltext = event;
					break;
				}
				break;
			}
			
			case "33451-03.htm":
			{
				if (qs.isCond(7))
				{
					giveItems(player, WOODEN_HELMET, 1);
					giveAdena(player, 480, true);
					giveItems(player, HEALING_POTION, 100);
					addExpAndSp(player, 70200, 16);
					qs.exitQuest(false, true);
					htmltext = event;
					break;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = null;
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == NAGEL)
				{
					htmltext = "33450-01.htm";
					break;
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == NAGEL)
				{
					switch (qs.getCond())
					{
						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
						{
							htmltext = "33450-04.htm";
							break;
						}
						case 6:
						{
							htmltext = "33450-05.htm";
							break;
						}
						case 7:
						{
							htmltext = "33450-06.htm";
							break;
						}
					}
				}
				else if (npc.getId() == CELIN)
				{
					if (qs.isCond(7))
					{
						htmltext = "33451-01.htm";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = npc.getId() == NAGEL ? "33450-07.htm" : "33451-04.htm";
				break;
			}
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_SOCIAL_ACTION)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerSocialAction(OnPlayerSocialAction event)
	{
		final PlayerInstance player = event.getActiveChar();
		final QuestState qs = getQuestState(player, false);
		
		final WorldObject target = player.getTarget();
		
		if ((target != null) && target.isNpc() && Util.contains(CORPSES, target.getId()))
		{
			final Npc npc = (Npc) player.getTarget();
			
			if (!player.isInsideRadius(npc, 120, true, true))
			{
				showOnScreenMsg(player, NpcStringId.YOU_ARE_TOO_FAR_FROM_THE_CORPSE, ExShowScreenMessage.TOP_CENTER, 4500);
				npc.deleteMe();
			}
			else if (event.getSocialActionId() != SOCIAL_SORROW)
			{
				addSpawn((getRandomBoolean() ? CRAWLER : STALKER), npc, false, 0, true);
				npc.deleteMe();
			}
			else if ((qs == null) || qs.isCompleted())
			{
				showOnScreenMsg(player, NpcStringId.GRUDGE_OF_YE_SAGIRA_VICTIMS_HAVE_BEEN_RELIEVED_WITH_YOUR_TEARS, ExShowScreenMessage.TOP_CENTER, 4500);
				npc.deleteMe();
			}
			else
			{
				NpcStringId npcStringId = null;
				
				switch (qs.getCond())
				{
					case 1:
					{
						npcStringId = NpcStringId.YOU_VE_SHOWN_YOUR_CONDOLENCES_TO_ONE_CORPSE;
						qs.setCond(2, true);
						break;
					}
					case 2:
					{
						npcStringId = NpcStringId.YOU_VE_SHOWN_YOUR_CONDOLENCES_TO_A_SECOND_CORPSE;
						qs.setCond(3, true);
						break;
					}
					case 3:
					{
						npcStringId = NpcStringId.YOU_VE_SHOWN_YOUR_CONDOLENCES_TO_A_THIRD_CORPSE;
						qs.setCond(4, true);
						break;
					}
					case 4:
					{
						npcStringId = NpcStringId.YOU_VE_SHOWN_YOUR_CONDOLENCES_TO_A_FOURTH_CORPSE;
						qs.setCond(5, true);
						break;
					}
					case 5:
					{
						npcStringId = NpcStringId.YOU_VE_SHOWN_YOUR_CONDOLENCES_TO_A_FIFTH_CORPSE;
						qs.setCond(6, true);
						break;
					}
					case 6:
					case 7:
					{
						npcStringId = NpcStringId.GRUDGE_OF_YE_SAGIRA_VICTIMS_HAVE_BEEN_RELIEVED_WITH_YOUR_TEARS;
						break;
					}
				}
				npc.deleteMe();
				
				if (npcStringId != null)
				{
					showOnScreenMsg(player, npcStringId, ExShowScreenMessage.TOP_CENTER, 4500);
				}
			}
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.setRandomAnimationEnabled(false);
		return super.onSpawn(npc);
	}
}
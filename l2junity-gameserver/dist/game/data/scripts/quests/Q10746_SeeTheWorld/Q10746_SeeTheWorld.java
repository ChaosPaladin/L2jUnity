/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q10746_SeeTheWorld;

import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

import quests.Q10745_TheSecretIngredients.Q10745_TheSecretIngredients;

/**
 * See the World (10746)
 * @author Sdw
 */
public class Q10746_SeeTheWorld extends Quest
{
	// NPCs
	private static final int KARLA = 33933;
	private static final int ASTIEL = 33948;
	private static final int LEVIAN = 30037;
	// Items
	private static final ItemHolder EMISSARY_SUPPORT_BOX_WARRIOR = new ItemHolder(40264, 1);
	private static final ItemHolder EMISSARY_SUPPORT_BOX_MAGE = new ItemHolder(40265, 1);
	// Misc
	private static final Location GLUDIN_VILLAGE = new Location(-80806, 149975, -3048);
	private static final int MIN_LEVEL = 19;
	private static final int MAX_LEVEL = 25;
	
	public Q10746_SeeTheWorld()
	{
		super(10746, Q10746_SeeTheWorld.class.getSimpleName(), "See The World");
		addStartNpc(KARLA);
		addTalkId(KARLA, ASTIEL, LEVIAN);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "fixme.htm");
		addCondCompletedQuest(Q10745_TheSecretIngredients.class.getSimpleName(), "fixme.htm");
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
			case "33933-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			
			case "33948-02.htm":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
					player.teleToLocation(GLUDIN_VILLAGE);
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
		final QuestState qs = getQuestState(player, true);
		String htmltext = qs.isCompleted() ? getAlreadyCompletedMsg(player) : getNoQuestMsg(player);
		
		switch (npc.getId())
		{
			case KARLA:
			{
				if (qs.isCreated())
				{
					htmltext = "33933-01.htm";
				}
				else if (qs.isStarted())
				{
					htmltext = "33933-02.htm";
				}
				break;
			}
			
			case ASTIEL:
			{
				if (qs.isCond(1))
				{
					htmltext = "33948-01.htm";
					break;
				}
				break;
			}
			
			case LEVIAN:
			{
				if (qs.isCond(2))
				{
					giveAdena(player, 43000, true);
					addExpAndSp(player, 53422, 5);
					if (player.isMageClass())
					{
						giveItems(player, EMISSARY_SUPPORT_BOX_MAGE);
					}
					else
					{
						giveItems(player, EMISSARY_SUPPORT_BOX_WARRIOR);
					}
					showOnScreenMsg(player, NpcStringId.CHECK_YOUR_EQUIPMENT_IN_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 4500);
					qs.exitQuest(false, true);
					htmltext = "30037-01.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
}

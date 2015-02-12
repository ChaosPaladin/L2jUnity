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
package ai.npc.Pantheon;

import org.l2junity.gameserver.enums.ChatType;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.L2PcInstance;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.network.NpcStringId;
import org.l2junity.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.Q10320_LetsGoToTheCentralSquare.Q10320_LetsGoToTheCentralSquare;
import ai.npc.AbstractNpcAI;

/**
 * Pantheon AI.
 * @author Gladicek
 */
public final class Pantheon extends AbstractNpcAI
{
	// NPC
	private static final int PANTHEON = 32972;
	// Location
	private static final Location MUSEUM = new Location(-114711, 243911, -7968);
	// Misc
	private static final int MIN_LEVEL = 20;
	
	private Pantheon()
	{
		super(Pantheon.class.getSimpleName(), "ai/npc");
		addSpawnId(PANTHEON);
		addStartNpc(PANTHEON);
		addFirstTalkId(PANTHEON);
		addTalkId(PANTHEON);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "32972-1.html":
			{
				htmltext = event;
				break;
			}
			case "teleport_museum":
			{
				if (player.getLevel() >= MIN_LEVEL)
				{
					player.teleToLocation(MUSEUM);
					break;
				}
				htmltext = "32972-noteleport.html";
				break;
			}
			case "TEXT_SPAM":
			{
				if (npc != null)
				{
					broadcastNpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.IS_IT_BETTER_TO_END_DESTINY_OR_START_DESTINY);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(Q10320_LetsGoToTheCentralSquare.class.getSimpleName());
		if (st == null)
		{
			showOnScreenMsg(player, NpcStringId.BEGIN_TUTORIAL_QUESTS, ExShowScreenMessage.TOP_CENTER, 4500);
		}
		return super.onFirstTalk(npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		startQuestTimer("TEXT_SPAM", 10000, npc, null, true);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new Pantheon();
	}
}
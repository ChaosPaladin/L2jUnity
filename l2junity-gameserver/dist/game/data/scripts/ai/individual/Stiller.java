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
package ai.individual;

import org.l2junity.gameserver.enums.ChatType;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.NpcStringId;

import ai.npc.AbstractNpcAI;

/**
 * Stiller AI.
 * @author Gladicek
 */
public final class Stiller extends AbstractNpcAI
{
	// NPCs
	private static final int STILLER = 33125;
	// Misc
	private static final NpcStringId[] STILLER_SHOUT =
	{
		NpcStringId.HEY_DID_YOU_SPEAK_WITH_PANTHEON,
		NpcStringId.EVERYONE_NEEDS_TO_MEET_PANTHEON_FIRST_BEFORE_HUNTING
	};
	
	private Stiller()
	{
		super(Stiller.class.getSimpleName(), "ai/individual");
		addSpawnId(STILLER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("SPAM_TEXT") && (npc != null))
		{
			npc.broadcastSay(ChatType.NPC_GENERAL, STILLER_SHOUT[getRandom(2)], 1000);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		startQuestTimer("SPAM_TEXT", 10000, npc, null, true);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new Stiller();
	}
}
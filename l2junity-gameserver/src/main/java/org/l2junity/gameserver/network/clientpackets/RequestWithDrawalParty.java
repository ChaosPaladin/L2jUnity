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
package org.l2junity.gameserver.network.clientpackets;

import org.l2junity.gameserver.model.Party;
import org.l2junity.gameserver.model.Party.MessageType;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.matching.MatchingRoom;

/**
 * This class ...
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestWithDrawalParty extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		final PlayerInstance player = getActiveChar();
		if (player == null)
		{
			return;
		}
		
		final Party party = player.getParty();
		if (party != null)
		{
			party.removePartyMember(player, MessageType.LEFT);
			
			final MatchingRoom room = player.getMatchingRoom();
			if (room != null)
			{
				room.deleteMember(player, false);
			}
		}
	}
	
	@Override
	public String getType()
	{
		return getClass().getSimpleName();
	}
}

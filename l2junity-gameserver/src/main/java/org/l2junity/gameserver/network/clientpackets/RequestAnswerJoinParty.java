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
import org.l2junity.gameserver.model.PartyMatchRoom;
import org.l2junity.gameserver.model.PartyMatchRoomList;
import org.l2junity.gameserver.model.Party.messageType;
import org.l2junity.gameserver.model.actor.instance.L2PcInstance;
import org.l2junity.gameserver.model.actor.request.PartyRequest;
import org.l2junity.gameserver.network.SystemMessageId;
import org.l2junity.gameserver.network.serverpackets.ExManagePartyRoomMember;
import org.l2junity.gameserver.network.serverpackets.JoinParty;
import org.l2junity.gameserver.network.serverpackets.SystemMessage;

public final class RequestAnswerJoinParty extends L2GameClientPacket
{
	private static final String _C__43_REQUESTANSWERPARTY = "[C] 43 RequestAnswerJoinParty";
	
	private int _response;
	
	@Override
	protected void readImpl()
	{
		_response = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		final PartyRequest request = player.getRequest(PartyRequest.class);
		if ((request == null) || request.isProcessing())
		{
			return;
		}
		request.setProcessing(true);
		
		final L2PcInstance requestor = request.getActiveChar();
		if (requestor == null)
		{
			return;
		}
		final Party party = requestor.getParty();
		requestor.sendPacket(new JoinParty(_response));
		
		if (_response == 1)
		{
			if (party != null)
			{
				if (party.getMemberCount() >= 9)
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_PARTY_IS_FULL);
					player.sendPacket(sm);
					requestor.sendPacket(sm);
					return;
				}
			}
			player.joinParty(party);
			
			if (requestor.isInPartyMatchRoom() && player.isInPartyMatchRoom())
			{
				final PartyMatchRoomList list = PartyMatchRoomList.getInstance();
				if ((list != null) && (list.getPlayerRoomId(requestor) == list.getPlayerRoomId(player)))
				{
					final PartyMatchRoom room = list.getPlayerRoom(requestor);
					if (room != null)
					{
						final ExManagePartyRoomMember packet = new ExManagePartyRoomMember(player, room, 1);
						for (L2PcInstance member : room.getPartyMembers())
						{
							if (member != null)
							{
								member.sendPacket(packet);
							}
						}
					}
				}
			}
			else if (requestor.isInPartyMatchRoom() && !player.isInPartyMatchRoom())
			{
				final PartyMatchRoomList list = PartyMatchRoomList.getInstance();
				if (list != null)
				{
					final PartyMatchRoom room = list.getPlayerRoom(requestor);
					if (room != null)
					{
						room.addMember(player);
						ExManagePartyRoomMember packet = new ExManagePartyRoomMember(player, room, 1);
						for (L2PcInstance member : room.getPartyMembers())
						{
							if (member != null)
							{
								member.sendPacket(packet);
							}
						}
						player.setPartyRoom(room.getId());
						// player.setPartyMatching(1);
						player.broadcastUserInfo();
					}
				}
			}
		}
		else if (_response == -1)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_SET_TO_REFUSE_PARTY_REQUESTS_AND_CANNOT_RECEIVE_A_PARTY_REQUEST);
			sm.addPcName(player);
			requestor.sendPacket(sm);
			
			// activate garbage collection if there are no other members in party (happens when we were creating new one)
			if ((party != null) && (party.getMemberCount() == 1))
			{
				requestor.getParty().removePartyMember(requestor, messageType.None);
			}
		}
		else
		{
			// requestor.sendPacket(SystemMessageId.THE_PLAYER_DECLINED_TO_JOIN_YOUR_PARTY); FIXME: Done in client?
			
			// activate garbage collection if there are no other members in party (happens when we were creating new one)
			if ((party != null) && (party.getMemberCount() == 1))
			{
				requestor.getParty().removePartyMember(requestor, messageType.None);
			}
		}
		
		if (party != null)
		{
			party.setPendingInvitation(false); // if party is null, there is no need of decreasing
		}
		
		request.setProcessing(false);
		player.removeRequest(request.getClass());
	}
	
	@Override
	public String getType()
	{
		return _C__43_REQUESTANSWERPARTY;
	}
}

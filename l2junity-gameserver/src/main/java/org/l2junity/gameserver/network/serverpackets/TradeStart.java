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
package org.l2junity.gameserver.network.serverpackets;

import java.util.Collection;

import org.l2junity.Config;
import org.l2junity.gameserver.instancemanager.MentorManager;
import org.l2junity.gameserver.model.PcCondOverride;
import org.l2junity.gameserver.model.actor.instance.L2PcInstance;
import org.l2junity.gameserver.model.items.instance.ItemInstance;

public final class TradeStart extends AbstractItemPacket
{
	private final L2PcInstance _activeChar;
	private final L2PcInstance _partner;
	private final Collection<ItemInstance> _itemList;
	private int _mask = 0;
	
	public TradeStart(L2PcInstance player)
	{
		_activeChar = player;
		_partner = player.getActiveTradeList().getPartner();
		_itemList = _activeChar.getInventory().getAvailableItems(true, (_activeChar.canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && Config.GM_TRADE_RESTRICTED_ITEMS), false);
		
		if (_partner != null)
		{
			if (player.getFriendList().contains(_partner.getObjectId()))
			{
				_mask |= 0x01;
			}
			if ((player.getClanId() > 0) && (_partner.getClanId() == _partner.getClanId()))
			{
				_mask |= 0x02;
			}
			if ((MentorManager.getInstance().getMentee(player.getObjectId(), _partner.getObjectId()) != null) || (MentorManager.getInstance().getMentee(_partner.getObjectId(), player.getObjectId()) != null))
			{
				_mask |= 0x04;
			}
			if ((player.getAllyId() > 0) && (player.getAllyId() == _partner.getAllyId()))
			{
				_mask |= 0x08;
			}
			
			// Does not shows level
			if (_partner.isGM())
			{
				_mask |= 0x10;
			}
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		if ((_activeChar.getActiveTradeList() == null) || (_partner == null))
		{
			return;
		}
		
		writeC(0x14);
		writeD(_partner.getObjectId());
		writeC(_mask); // some kind of mask
		if ((_mask & 0x10) == 0)
		{
			writeC(_partner.getLevel());
		}
		writeH(_itemList.size());
		for (ItemInstance item : _itemList)
		{
			writeItem(item);
		}
	}
}

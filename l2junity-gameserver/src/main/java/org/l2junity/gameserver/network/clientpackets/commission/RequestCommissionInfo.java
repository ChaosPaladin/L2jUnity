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
package org.l2junity.gameserver.network.clientpackets.commission;

import org.l2junity.gameserver.instancemanager.CommissionManager;
import org.l2junity.gameserver.model.actor.instance.L2PcInstance;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.network.clientpackets.L2GameClientPacket;
import org.l2junity.gameserver.network.serverpackets.commission.ExCloseCommission;
import org.l2junity.gameserver.network.serverpackets.commission.ExResponseCommissionInfo;

/**
 * @author NosBit
 */
public class RequestCommissionInfo extends L2GameClientPacket
{
	private int _itemObjectId;
	
	@Override
	protected void readImpl()
	{
		_itemObjectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (!CommissionManager.isPlayerAllowedToInteract(player))
		{
			player.sendPacket(ExCloseCommission.STATIC_PACKET);
			return;
		}
		
		final ItemInstance itemInstance = player.getInventory().getItemByObjectId(_itemObjectId);
		if (itemInstance != null)
		{
			player.sendPacket(player.getLastCommissionInfos().getOrDefault(itemInstance.getId(), ExResponseCommissionInfo.EMPTY));
		}
		else
		{
			player.sendPacket(ExResponseCommissionInfo.EMPTY);
		}
		
	}
	
	@Override
	public String getType()
	{
		return getClass().getSimpleName();
	}
	
}

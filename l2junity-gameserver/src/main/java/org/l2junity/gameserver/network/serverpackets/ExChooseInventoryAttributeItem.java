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

import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.model.Elementals;
import org.l2junity.gameserver.model.actor.instance.L2PcInstance;
import org.l2junity.gameserver.model.items.instance.ItemInstance;

/**
 * @author Kerberos
 */
public class ExChooseInventoryAttributeItem extends L2GameServerPacket
{
	private final int _itemId;
	private final long _count;
	private final byte _atribute;
	private final int _level;
	private final Set<Integer> _items = new HashSet<>();
	
	public ExChooseInventoryAttributeItem(L2PcInstance activeChar, ItemInstance stone)
	{
		_itemId = stone.getDisplayId();
		_count = stone.getCount();
		_atribute = Elementals.getItemElement(_itemId);
		if (_atribute == Elementals.NONE)
		{
			throw new IllegalArgumentException("Undefined Atribute item: " + stone);
		}
		_level = Elementals.getMaxElementLevel(_itemId);
		
		// Register only items that can be put an attribute stone/crystal
		for (ItemInstance item : activeChar.getInventory().getItems())
		{
			if (item.isElementable())
			{
				_items.add(item.getObjectId());
			}
		}
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x63);
		writeD(_itemId);
		writeQ(_count);
		writeD(_atribute == Elementals.FIRE ? 1 : 0); // Fire
		writeD(_atribute == Elementals.WATER ? 1 : 0); // Water
		writeD(_atribute == Elementals.WIND ? 1 : 0); // Wind
		writeD(_atribute == Elementals.EARTH ? 1 : 0); // Earth
		writeD(_atribute == Elementals.HOLY ? 1 : 0); // Holy
		writeD(_atribute == Elementals.DARK ? 1 : 0); // Unholy
		writeD(_level); // Item max attribute level
		writeD(_items.size());
		_items.forEach(this::writeD);
	}
}

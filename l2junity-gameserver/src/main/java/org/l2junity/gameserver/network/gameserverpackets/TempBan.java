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
package org.l2junity.gameserver.network.gameserverpackets;

import org.l2junity.util.network.BaseSendablePacket;

/**
 * @author mrTJO
 */
public class TempBan extends BaseSendablePacket
{
	public TempBan(String accountName, String ip, long time, String reason)
	{
		writeC(0x0A);
		writeS(accountName);
		writeS(ip);
		writeQ(System.currentTimeMillis() + (time * 60000));
		if (reason != null)
		{
			writeC(0x01);
			writeS(reason);
		}
		else
		{
			writeC(0x00);
		}
	}
	
	public TempBan(String accountName, String ip, long time)
	{
		this(accountName, ip, time, null);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}

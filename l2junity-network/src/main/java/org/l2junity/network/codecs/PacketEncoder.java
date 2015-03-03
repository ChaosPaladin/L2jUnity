/*
 * Copyright (C) 2004-2014 L2J Server
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
package org.l2junity.network.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2junity.network.IOutgoingPacket;
import org.l2junity.network.PacketWriter;

/**
 * @author Nos
 */
@Sharable
public class PacketEncoder extends MessageToByteEncoder<IOutgoingPacket>
{
	private static final Logger _log = Logger.getLogger(PacketEncoder.class.getName());
	
	private final ByteOrder _byteOrder;
	private final int _maxPacketSize;
	
	public PacketEncoder(ByteOrder byteOrder, int maxPacketSize)
	{
		super();
		_byteOrder = byteOrder;
		_maxPacketSize = maxPacketSize;
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, IOutgoingPacket packet, ByteBuf out)
	{
		if (out.order() != _byteOrder)
		{
			out = out.order(_byteOrder);
		}
		
		if (packet.write(new PacketWriter(out)))
		{
			if (out.writerIndex() > _maxPacketSize)
			{
				_log.log(Level.WARNING, "", new IllegalStateException("Packet (" + packet + ") size (" + out.writerIndex() + ") is bigger than the limit (" + _maxPacketSize + ")"));
				// Avoid sending the packet
				out.clear();
			}
		}
		else
		{
			// Avoid sending the packet
			out.clear();
		}
	}
}
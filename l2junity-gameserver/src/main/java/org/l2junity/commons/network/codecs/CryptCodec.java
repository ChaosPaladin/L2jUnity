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
package org.l2junity.commons.network.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

import org.l2junity.commons.network.ICrypt;

/**
 * @author Nos
 */
public class CryptCodec extends ByteToMessageCodec<ByteBuf>
{
	private final ICrypt _crypt;
	
	public CryptCodec(ICrypt crypt)
	{
		super();
		_crypt = crypt;
	}
	
	/*
	 * (non-Javadoc)
	 * @see io.netty.handler.codec.ByteToMessageCodec#encode(io.netty.channel.ChannelHandlerContext, java.lang.Object, io.netty.buffer.ByteBuf)
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out)
	{
		// Check if there are any data to encrypt.
		if (!msg.isReadable())
		{
			return;
		}
		
		msg.resetReaderIndex();
		_crypt.encrypt(msg);
		msg.resetReaderIndex();
		out.writeBytes(msg);
	}
	
	/*
	 * (non-Javadoc)
	 * @see io.netty.handler.codec.ByteToMessageCodec#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
	{
		in.resetReaderIndex();
		_crypt.decrypt(in);
		out.add(in.retain());
	}
}

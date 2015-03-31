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
package handlers.effecthandlers;

import org.l2junity.gameserver.GeoData;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.conditions.Condition;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.skills.BuffInfo;
import org.l2junity.gameserver.network.client.send.FlyToLocation;
import org.l2junity.gameserver.network.client.send.FlyToLocation.FlyType;
import org.l2junity.gameserver.util.Util;

/**
 * @author Nos
 */
public class FlyMove extends AbstractEffect
{
	private final FlyType _flyType;
	private final int _angle;
	private final boolean _absoluteAngle;
	private final int _range;
	private final boolean _effectedPos;
	private final int _speed;
	private final int _delay;
	private final int _animationSpeed;
	
	/**
	 * @param attachCond
	 * @param applyCond
	 * @param set
	 * @param params
	 */
	public FlyMove(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_flyType = params.getEnum("flyType", FlyType.class, FlyType.DUMMY);
		_angle = params.getInt("angle", 0);
		_absoluteAngle = params.getBoolean("absoluteAngle", false);
		_range = params.getInt("range", 20);
		_effectedPos = params.getBoolean("effectedPos", true);
		_speed = params.getInt("speed", 0);
		_delay = params.getInt("delay", 0);
		_animationSpeed = params.getInt("animationSpeed", 0);
	}
	
	@Override
	public void onStart(BuffInfo info)
	{
		final Creature effected = _effectedPos ? info.getEffected() : info.getEffector();
		
		double angle = _absoluteAngle ? _angle : Util.convertHeadingToDegree(Util.calculateHeadingFrom(info.getEffector(), effected));
		angle = (angle + _angle) % 360;
		if (angle < 0)
		{
			angle += 360;
		}
		
		final double radiansAngle = Math.toRadians(angle);
		final int posX = (int) (info.getEffected().getX() + (_range * Math.cos(radiansAngle)));
		final int posY = (int) (info.getEffected().getY() + (_range * Math.sin(radiansAngle)));
		final Location destination = GeoData.getInstance().moveCheck(info.getEffector().getX(), info.getEffector().getY(), info.getEffector().getZ(), posX, posY, info.getEffected().getZ(), info.getEffected().getInstanceId());
		
		effected.broadcastPacket(new FlyToLocation(effected, destination, _flyType, _speed, _delay, _animationSpeed));
		effected.setXYZ(destination);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
}
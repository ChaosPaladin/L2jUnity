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
package org.l2junity.gameserver.model.actor.instance;

import java.util.List;
import java.util.logging.Level;

import javolution.util.FastList;

import org.l2junity.gameserver.enums.InstanceType;
import org.l2junity.gameserver.model.L2Spawn;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Tower;
import org.l2junity.gameserver.model.actor.templates.L2NpcTemplate;

/**
 * Class for Control Tower instance.
 */
public class L2ControlTowerInstance extends Tower
{
	private volatile List<L2Spawn> _guards;
	
	public L2ControlTowerInstance(L2NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.L2ControlTowerInstance);
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (getCastle().getSiege().isInProgress())
		{
			getCastle().getSiege().killedCT(this);
			
			if ((_guards != null) && !_guards.isEmpty())
			{
				for (L2Spawn spawn : _guards)
				{
					if (spawn == null)
					{
						continue;
					}
					try
					{
						spawn.stopRespawn();
						// spawn.getLastSpawn().doDie(spawn.getLastSpawn());
					}
					catch (Exception e)
					{
						_log.log(Level.WARNING, "Error at L2ControlTowerInstance", e);
					}
				}
				_guards.clear();
			}
		}
		return super.doDie(killer);
	}
	
	public void registerGuard(L2Spawn guard)
	{
		getGuards().add(guard);
	}
	
	private final List<L2Spawn> getGuards()
	{
		if (_guards == null)
		{
			synchronized (this)
			{
				if (_guards == null)
				{
					_guards = new FastList<>();
				}
			}
		}
		return _guards;
	}
}

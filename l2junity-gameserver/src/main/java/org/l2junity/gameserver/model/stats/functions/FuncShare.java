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
package org.l2junity.gameserver.model.stats.functions;

import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Summon;
import org.l2junity.gameserver.model.actor.instance.L2PcInstance;
import org.l2junity.gameserver.model.conditions.Condition;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.Stats;

/**
 * @author UnAfraid
 */
public class FuncShare extends AbstractFunction
{
	public FuncShare(Stats stat, int order, Object owner, double value, Condition applayCond)
	{
		super(stat, order, owner, value, applayCond);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, Skill skill, double initVal)
	{
		if ((getApplayCond() == null) || getApplayCond().test(effector, effected, skill))
		{
			if ((effector != null) && effector.isServitor())
			{
				final Summon summon = (Summon) effector;
				final L2PcInstance player = summon.getOwner();
				if (player != null)
				{
					return initVal + (getBaseValue(getStat(), player) * getValue());
				}
			}
		}
		return initVal;
	}
	
	public static double getBaseValue(Stats stat, L2PcInstance player)
	{
		switch (stat)
		{
			case MAX_HP:
			{
				return player.getMaxHp();
			}
			case MAX_MP:
			{
				return player.getMaxMp();
			}
			case POWER_ATTACK:
			{
				return player.getPAtk(null);
			}
			case MAGIC_ATTACK:
			{
				return player.getMAtk(null, null);
			}
			case POWER_DEFENCE:
			{
				return player.getPDef(null);
			}
			case MAGIC_DEFENCE:
			{
				return player.getMDef(null, null);
			}
			case CRITICAL_RATE:
			{
				return player.getCriticalHit(null, null);
			}
			case POWER_ATTACK_SPEED:
			{
				return player.getPAtkSpd();
			}
			case MAGIC_ATTACK_SPEED:
			{
				return player.getMAtkSpd();
			}
			default:
			{
				return player.calcStat(stat, 0, null, null);
			}
		}
	}
}

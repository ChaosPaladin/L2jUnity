/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.conditions.Condition;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.effects.L2EffectType;
import org.l2junity.gameserver.model.skills.BuffInfo;
import org.l2junity.gameserver.network.client.SystemMessageId;

/**
 * Damage Over Time Percent effect implementation.
 * @author Adry_85
 */
public final class DamOverTimePercent extends AbstractEffect
{
	private final boolean _canKill;
	private final double _power;
	
	public DamOverTimePercent(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_canKill = params.getBoolean("canKill", false);
		_power = params.getDouble("power", 0);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.DMG_OVER_TIME_PERCENT;
	}
	
	@Override
	public boolean onActionTime(BuffInfo info)
	{
		if (info.getEffected().isDead())
		{
			return false;
		}
		
		double damage = info.getEffected().getCurrentHp() * _power * getTicksMultiplier();
		if (damage >= (info.getEffected().getCurrentHp() - 1))
		{
			if (info.getSkill().isToggle())
			{
				info.getEffected().sendPacket(SystemMessageId.YOUR_SKILL_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_HP);
				return false;
			}
			
			// For DOT skills that will not kill effected player.
			if (!_canKill)
			{
				// Fix for players dying by DOTs if HP < 1 since reduceCurrentHP method will kill them
				if (info.getEffected().getCurrentHp() <= 1)
				{
					return info.getSkill().isToggle();
				}
				
				damage = info.getEffected().getCurrentHp() - 1;
			}
		}
		info.getEffected().reduceCurrentHpByDOT(damage, info.getEffector(), info.getSkill());
		info.getEffected().notifyDamageReceived(damage, info.getEffector(), info.getSkill(), false, true);
		
		return info.getSkill().isToggle();
	}
}

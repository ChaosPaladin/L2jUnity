/*
 * Copyright (C) 2004-2015 L2J Unity
 * 
 * This file is part of L2J Unity.
 * 
 * L2J Unity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Unity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.gameserver.model.effects;

/**
 * @author UnAfraid
 */
public enum EffectFlag
{
	NONE,
	RESURRECTION_SPECIAL,
	NOBLESS_BLESSING,
	SILENT_MOVE,
	PROTECTION_BLESSING,
	RELAXING,
	BLOCK_CONTROL,
	CONFUSED,
	MUTED,
	PSYCHICAL_MUTED,
	PSYCHICAL_ATTACK_MUTED,
	DISARMED,
	ROOTED,
	BLOCK_ACTIONS,
	CONDITIONAL_BLOCK_ACTIONS,
	BETRAYED,
	HP_BLOCK,
	MP_BLOCK,
	DEBUFF_BLOCK,
	ABNORMAL_SHIELD,
	BLOCK_RESURRECTION,
	SERVITOR_SHARE,
	UNTARGETABLE,
	CANNOT_ESCAPE,
	DOUBLE_CAST,
	ATTACK_BEHIND,
	TARGETING_DISABLED,
	FACEOFF,
	PHYSICAL_SHIELD_ANGLE_ALL,
	CHEAPSHOT,
	IGNORE_DEATH;
	
	public int getMask()
	{
		return 1 << ordinal();
	}
}

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
package org.l2junity.gameserver.model.zone.type;

import java.util.Map.Entry;

import javolution.util.FastMap;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.ThreadPoolManager;
import org.l2junity.gameserver.datatables.SkillData;
import org.l2junity.gameserver.enums.InstanceType;
import org.l2junity.gameserver.instancemanager.ZoneManager;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.zone.AbstractZoneSettings;
import org.l2junity.gameserver.model.zone.ZoneType;
import org.l2junity.gameserver.model.zone.TaskZoneSettings;
import org.l2junity.gameserver.model.zone.ZoneId;
import org.l2junity.gameserver.network.client.send.EtcStatusUpdate;

/**
 * another type of damage zone with skills
 * @author kerberos
 */
public class EffectZone extends ZoneType
{
	private int _chance;
	private int _initialDelay;
	private int _reuse;
	protected boolean _bypassConditions;
	private boolean _isShowDangerIcon;
	protected volatile FastMap<Integer, Integer> _skills;
	
	public EffectZone(int id)
	{
		super(id);
		_chance = 100;
		_initialDelay = 0;
		_reuse = 30000;
		setTargetType(InstanceType.L2Playable); // default only playabale
		_bypassConditions = false;
		_isShowDangerIcon = true;
		AbstractZoneSettings settings = ZoneManager.getSettings(getName());
		if (settings == null)
		{
			settings = new TaskZoneSettings();
		}
		setSettings(settings);
	}
	
	@Override
	public TaskZoneSettings getSettings()
	{
		return (TaskZoneSettings) super.getSettings();
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("chance"))
		{
			_chance = Integer.parseInt(value);
		}
		else if (name.equals("initialDelay"))
		{
			_initialDelay = Integer.parseInt(value);
		}
		else if (name.equals("reuse"))
		{
			_reuse = Integer.parseInt(value);
		}
		else if (name.equals("bypassSkillConditions"))
		{
			_bypassConditions = Boolean.parseBoolean(value);
		}
		else if (name.equals("maxDynamicSkillCount"))
		{
			_skills = new FastMap<Integer, Integer>(Integer.parseInt(value)).shared();
		}
		else if (name.equals("skillIdLvl"))
		{
			String[] propertySplit = value.split(";");
			_skills = new FastMap<>(propertySplit.length);
			for (String skill : propertySplit)
			{
				String[] skillSplit = skill.split("-");
				if (skillSplit.length != 2)
				{
					_log.warning(getClass().getSimpleName() + ": invalid config property -> skillsIdLvl \"" + skill + "\"");
				}
				else
				{
					try
					{
						_skills.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!skill.isEmpty())
						{
							_log.warning(getClass().getSimpleName() + ": invalid config property -> skillsIdLvl \"" + skillSplit[0] + "\"" + skillSplit[1]);
						}
					}
				}
			}
		}
		else if (name.equals("showDangerIcon"))
		{
			_isShowDangerIcon = Boolean.parseBoolean(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		if (_skills != null)
		{
			if (getSettings().getTask() == null)
			{
				synchronized (this)
				{
					if (getSettings().getTask() == null)
					{
						getSettings().setTask(ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new ApplySkill(), _initialDelay, _reuse));
					}
				}
			}
		}
		if (character.isPlayer())
		{
			character.setInsideZone(ZoneId.ALTERED, true);
			if (_isShowDangerIcon)
			{
				character.setInsideZone(ZoneId.DANGER_AREA, true);
				character.sendPacket(new EtcStatusUpdate(character.getActingPlayer()));
			}
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		if (character.isPlayer())
		{
			character.setInsideZone(ZoneId.ALTERED, false);
			if (_isShowDangerIcon)
			{
				character.setInsideZone(ZoneId.DANGER_AREA, false);
				if (!character.isInsideZone(ZoneId.DANGER_AREA))
				{
					character.sendPacket(new EtcStatusUpdate(character.getActingPlayer()));
				}
			}
		}
		if (_characterList.isEmpty() && (getSettings().getTask() != null))
		{
			getSettings().clear();
		}
	}
	
	protected Skill getSkill(int skillId, int skillLvl)
	{
		return SkillData.getInstance().getSkill(skillId, skillLvl);
	}
	
	public int getChance()
	{
		return _chance;
	}
	
	public void addSkill(int skillId, int skillLvL)
	{
		if (skillLvL < 1) // remove skill
		{
			removeSkill(skillId);
			return;
		}
		
		if (_skills == null)
		{
			synchronized (this)
			{
				if (_skills == null)
				{
					_skills = new FastMap<Integer, Integer>(3).shared();
				}
			}
		}
		_skills.put(skillId, skillLvL);
	}
	
	public void removeSkill(int skillId)
	{
		if (_skills != null)
		{
			_skills.remove(skillId);
		}
	}
	
	public void clearSkills()
	{
		if (_skills != null)
		{
			_skills.clear();
		}
	}
	
	public int getSkillLevel(int skillId)
	{
		if ((_skills == null) || !_skills.containsKey(skillId))
		{
			return 0;
		}
		return _skills.get(skillId);
	}
	
	private final class ApplySkill implements Runnable
	{
		protected ApplySkill()
		{
			if (_skills == null)
			{
				throw new IllegalStateException("No skills defined.");
			}
		}
		
		@Override
		public void run()
		{
			if (isEnabled())
			{
				for (Creature temp : getCharactersInside())
				{
					if ((temp != null) && !temp.isDead())
					{
						if (Rnd.get(100) < getChance())
						{
							for (Entry<Integer, Integer> e : _skills.entrySet())
							{
								Skill skill = getSkill(e.getKey(), e.getValue());
								if ((skill != null) && (_bypassConditions || skill.checkCondition(temp, temp, false)))
								{
									if (!temp.isAffectedBySkill(e.getKey()))
									{
										skill.applyEffects(temp, temp);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
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
package ai.group_template;

import org.l2junity.gameserver.ai.CtrlIntention;
import org.l2junity.gameserver.enums.ChatType;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.actor.Attackable;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.L2QuestGuardInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.annotations.Id;
import org.l2junity.gameserver.model.events.annotations.RegisterEvent;
import org.l2junity.gameserver.model.events.annotations.RegisterType;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureKill;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

import ai.npc.AbstractNpcAI;

/**
 * Wastelands AI.
 * @author St3eT
 */
public final class Wastelands extends AbstractNpcAI
{
	// NPCs
	private static final int JOEL = 33516;
	private static final int SCHUAZEN = 33517;
	private static final int COMMANDER = 33434;
	private static final int GUARD = 33437;
	private static final int DECO_GUARD = 19140; // Antharas Decoration 5
	private static final int DECO_GUARD2 = 19163; // Wastelands Decoration 1
	private static final int REGENERATED_KANILOV = 27459;
	private static final int REGENERATED_POSLOF = 27460;
	private static final int SAKUM = 27453;
	private static final int COMMANDO = 19126;
	private static final int COMMANDO_CAPTAIN = 19127;
	// Locations
	private static final Location GUARD_POSLOF_LOC = new Location(-29474, 187083, -3912);
	private static final Location[] COMMANDO_SAKUM_LOC =
	{
		new Location(-36525, 192032, -3640),
		new Location(-36160, 191912, -3640),
		new Location(-36371, 191370, -3632),
		new Location(-36765, 191759, -3632),
	};
	private static final Location[] COMMANDO_CAPTAIN_SAKUM_LOC =
	{
		new Location(-36683, 191475, -3632),
		new Location(-36131, 191574, -3632),
	};
	
	private Wastelands()
	{
		super(Wastelands.class.getSimpleName(), "ai/individual");
		addSpawnId(COMMANDER, GUARD, DECO_GUARD, REGENERATED_KANILOV, REGENERATED_POSLOF, SAKUM);
		addSeeCreatureId(JOEL, SCHUAZEN, COMMANDO, COMMANDO_CAPTAIN);
		addKillId(REGENERATED_POSLOF, SAKUM);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "SOCIAL_SHOW":
			{
				npc.broadcastSocialAction(4);
				
				World.getInstance().getVisibleObjects(npc, Npc.class, 500).stream().filter(n -> n.getId() == GUARD).forEach(guard ->
				{
					startQuestTimer("SOCIAL_ACTION", getRandom(2500, 3500), guard, null);
				});
				break;
			}
			case "SOCIAL_ACTION":
			{
				npc.broadcastSocialAction(4);
				break;
			}
			case "START_ATTACK":
			{
				final Attackable guard = (Attackable) npc;
				final int attackId;
				
				switch (guard.getId())
				{
					case JOEL:
					{
						attackId = REGENERATED_KANILOV;
						break;
					}
					case SCHUAZEN:
					{
						attackId = REGENERATED_POSLOF;
						break;
					}
					case COMMANDO:
					case COMMANDO_CAPTAIN:
					{
						attackId = SAKUM;
						break;
					}
					default:
					{
						attackId = 0;
						break;
					}
				}
				
				if (attackId > 0)
				{
					//@formatter:off
					final Npc monster = World.getInstance().getVisibleObjects(guard, Npc.class, 1000)
						.stream()
						.filter(obj -> (obj.getId() == attackId))
						.findFirst()
						.orElse(null);
					//@formatter:on
					
					if (monster != null)
					{
						guard.reduceCurrentHp(1, monster, null); // TODO: Find better way for attack
						monster.reduceCurrentHp(1, guard, null);
						guard.setCanStopAttackByTime(false);
						
						if ((guard.getId() != COMMANDO) && (guard.getId() != COMMANDO_CAPTAIN))
						{
							guard.setIsInvul(true);
						}
						
						if (guard.getId() == SCHUAZEN)
						{
							//@formatter:off
							final L2QuestGuardInstance decoGuard = (L2QuestGuardInstance) World.getInstance().getVisibleObjects(guard, Npc.class, 500)
								.stream()
								.filter(obj -> (obj.getId() == DECO_GUARD2))
								.findFirst()
								.orElse(null);
							//@formatter:on
							
							if (decoGuard != null)
							{
								decoGuard.reduceCurrentHp(0, monster, null); // TODO: Find better way for attack
								monster.reduceCurrentHp(1, decoGuard, null);
								decoGuard.setCanStopAttackByTime(false);
								decoGuard.setIsInvul(true);
							}
						}
					}
					else
					{
						startQuestTimer("START_ATTACK", 250, guard, null);
					}
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer() && (npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK))
		{
			startQuestTimer("START_ATTACK", 250, npc, null);
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case COMMANDER:
			{
				startQuestTimer("SOCIAL_SHOW", 13000, npc, null, true);
				npc.setRandomAnimationEnabled(false);
				break;
			}
			case REGENERATED_KANILOV:
			case REGENERATED_POSLOF:
			{
				final int guardId = npc.getId() == REGENERATED_KANILOV ? JOEL : SCHUAZEN;
				//@formatter:off
				final L2QuestGuardInstance guard = (L2QuestGuardInstance) World.getInstance().getVisibleObjects(npc, Npc.class, 500)
					.stream()
					.filter(obj -> (obj.getId() == guardId))
					.findFirst()
					.orElse(null);
				//@formatter:on
				
				if (guard != null)
				{
					if (guard.getId() == SCHUAZEN)
					{
						addSpawn(DECO_GUARD2, GUARD_POSLOF_LOC);
					}
					guard.broadcastSay(ChatType.NPC_GENERAL, guard.getId() == JOEL ? NpcStringId.AH_REGENERATOR_POSLOF_APPEARED_AGAIN : NpcStringId.AH_REGENERATOR_KANILOV_APPEARED_AGAIN);
					notifyEvent("START_ATTACK", guard, null);
				}
				break;
			}
			case SAKUM:
			{
				manageCommando((Attackable) npc);
				break;
			}
			case GUARD:
			case DECO_GUARD:
			{
				npc.setRandomAnimationEnabled(false);
				break;
			}
		}
		
		if (npc.getId() == COMMANDER)
		{
			startQuestTimer("SOCIAL_SHOW", 13000, npc, null, true);
		}
		npc.setRandomAnimationEnabled(false);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		if (npc.getId() == REGENERATED_POSLOF)
		{
			World.getInstance().forEachVisibleObjectInRange(npc, Attackable.class, 1000, guard ->
			{
				if ((guard.getId() == DECO_GUARD2))
				{
					guard.deleteMe();
				}
			});
		}
		else if (npc.getId() == SAKUM)
		{
			World.getInstance().forEachVisibleObjectInRange(npc, Attackable.class, 1000, guard ->
			{
				if ((guard.getId() == COMMANDO) || (guard.getId() == COMMANDO_CAPTAIN))
				{
					guard.deleteMe();
				}
			});
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@RegisterEvent(EventType.ON_CREATURE_KILL)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(COMMANDO)
	@Id(COMMANDO_CAPTAIN)
	public void onCreatureKill(OnCreatureKill event)
	{
		final Attackable guard = (Attackable) event.getTarget();
		
		//@formatter:off
		final Attackable sakum = (Attackable) World.getInstance().getVisibleObjects(guard, Npc.class, 1000)
			.stream()
			.filter(obj -> (obj.getId() == SAKUM))
			.findFirst()
			.orElse(null);
		//@formatter:on
		
		if (sakum != null)
		{
			manageCommando(sakum);
		}
	}
	
	private void manageCommando(Attackable sakum)
	{
		int guardCount = sakum.getVariables().getInt("GUARD_COUNT", 0);
		guardCount--;
		
		if (guardCount <= 0)
		{
			if (sakum.getVariables().getBoolean("GUARD_CAPTAIN", false))
			{
				sakum.getVariables().set("GUARD_COUNT", COMMANDO_CAPTAIN_SAKUM_LOC.length);
				sakum.getVariables().set("GUARD_CAPTAIN", false);
				
				for (Location loc : COMMANDO_CAPTAIN_SAKUM_LOC)
				{
					final Attackable commander = (Attackable) addSpawn(COMMANDO_CAPTAIN, loc);
					commander.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HOW_DARE_YOU_ATTACK);
					
					commander.reduceCurrentHp(1, sakum, null); // TODO: Find better way for attack
					sakum.reduceCurrentHp(1, commander, null);
					commander.setCanStopAttackByTime(false);
					
					notifyEvent("START_ATTACK", commander, null);
				}
			}
			else
			{
				sakum.getVariables().set("GUARD_COUNT", COMMANDO_SAKUM_LOC.length);
				sakum.getVariables().set("GUARD_CAPTAIN", true);
				
				for (Location loc : COMMANDO_SAKUM_LOC)
				{
					final Attackable commander = (Attackable) addSpawn(COMMANDO, loc);
					
					commander.reduceCurrentHp(1, sakum, null); // TODO: Find better way for attack
					sakum.reduceCurrentHp(1, commander, null);
					commander.setCanStopAttackByTime(false);
					
					notifyEvent("START_ATTACK", commander, null);
				}
			}
		}
		else
		{
			sakum.getVariables().set("GUARD_COUNT", guardCount);
		}
	}
	
	public static void main(String[] args)
	{
		new Wastelands();
	}
}
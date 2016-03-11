/*
 * Copyright (C) 2004-2016 L2J Unity
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
package instances.MemoryOfDisaster;

import java.util.List;
import java.util.stream.Collectors;

import org.l2junity.commons.util.CommonUtil;
import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.ai.CtrlIntention;
import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.enums.ChatType;
import org.l2junity.gameserver.enums.Movie;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.base.ClassId;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.annotations.RegisterEvent;
import org.l2junity.gameserver.model.events.annotations.RegisterType;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureAttacked;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureKill;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureSee;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerCallToChangeClass;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.instancezone.Instance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.Earthquake;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.OnEventTrigger;
import org.l2junity.gameserver.network.client.send.ValidateLocation;
import org.l2junity.gameserver.network.client.send.awakening.ExCallToChangeClass;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

import instances.AbstractInstance;

/**
 * Memory Of Disaster instance zone.
 * @author Sdw
 */
public class MemoryOfDisaster extends AbstractInstance
{
	// NPCs
	private static final int INVISIBLE_NPC = 18919;
	private static final int BRONK = 19192;
	private static final int ROGIN = 19193;
	private static final int TOROCCO = 19198;
	private static final int TENTACLE = 19171;
	private static final int TEREDOR = 19172;
	private static final int SOLDIER = 19196;
	private static final int SOLDIER2 = 19197;
	private static final int SIEGE_GOLEM = 19189;
	private static final int TEREDOR_TRANSPARENT = 18998;
	private static final int SILVERA = 19194;
	private static final int WIRPHY = 19195;
	private static final int EARTH_WYRM_TRASKEN = 19217;
	private static final int[] DWARVES =
	{
		19191,
		19192,
		19193,
		19198,
		19199,
		19200,
		19201,
		19202,
		19203,
		19204,
		19205,
		19206,
		19207,
		19208,
		19209,
		19210,
		19211,
		19212,
		19213,
		19214,
		19215
	};
	// Locations
	private static final Location BATTLE_PORT = new Location(116063, -183167, -1460, 64960);
	private static final Location ROGIN_MOVE = new Location(116400, -183069, -1600);
	private static final Location AWAKENING_GUIDE_MOVE_1 = new Location(115830, -182103, -1400);
	private static final Location AWAKENING_GUIDE_MOVE_2 = new Location(115955, -181387, -1624);
	private static final Location AWAKENING_GUIDE_MOVE_3 = new Location(116830, -180257, -1176);
	private static final Location AWAKENING_GUIDE_MOVE_4 = new Location(115110, -178852, -896);
	private static final Location AWAKENING_GUIDE_MOVE_5 = new Location(115095, -176978, -808);
	private static final Location DWARVES_MOVE_1 = new Location(115830, -182103, -1400);
	private static final Location DWARVES_MOVE_2 = new Location(115955, -181387, -1624);
	private static final Location DWARVES_MOVE_3 = new Location(116830, -180257, -1176);
	private static final Location[] DWARVES_MOVE_RANDOM =
	{
		new Location(117147, -179248, -1120),
		new Location(115110, -178852, -896),
		new Location(115959, -178311, -1064)
	};
	private static final Location GOLEM_MOVE = new Location(116608, -179205, -1176);
	private static final Location PULLER_TELEPORT = new Location(115899, -181931, -1424, 0);
	private static final Location WIRPHY_MOVE = new Location(116639, -179990, -1160);
	private static final Location SILVERA_MOVE = new Location(116880, -179821, -1144);
	// Skills
	private static final SkillHolder SIEGE_GOLEM_SKILL_1 = new SkillHolder(16022, 1);
	private static final SkillHolder SIEGE_GOLEM_SKILL_2 = new SkillHolder(16024, 1);
	private static final SkillHolder PULLER_SKILL = new SkillHolder(16031, 1);
	private static final SkillHolder TEREDOR_TRANSPARENT_SKILL = new SkillHolder(16021, 1);
	private static final SkillHolder TRASKEN_SKILL_1 = new SkillHolder(14505, 1);
	// Items
	private static final int TRANSPARENT_1HS = 15280;
	// Misc
	private static final int FIRE_IN_DWARVEN_VILLAGE = 23120700;
	private static final int TEMPLATE_ID = 200;
	private static final NpcStringId[] SHOUT_BRONK_DEATH =
	{
		NpcStringId.BRONK,
		NpcStringId.CHIEF,
		NpcStringId.BRONK2,
		NpcStringId.NO_WAY3
	};
	private static final NpcStringId[] SHOUT_RUN =
	{
		NpcStringId.FOR_BRONK,
		NpcStringId.DWARVES_FOREVER,
		NpcStringId.SAVE_THE_DWARVEN_VILLAGE,
		NpcStringId.WHOAAAAAA,
		NpcStringId.FIGHT
	};
	private static final NpcStringId[] SHOUT_SILVERA_DEATH =
	{
		NpcStringId.SILVERA,
		NpcStringId.WE_CAN_T_TAKE_ANY_MORE_LOSSES,
		NpcStringId.TOO_LATE3,
		NpcStringId.NO_WAY4,
		NpcStringId.ANOTHER_ONE_OVER_THERE
	};
	
	public MemoryOfDisaster()
	{
		addInstanceCreatedId(TEMPLATE_ID);
		addSpawnId(INVISIBLE_NPC, TENTACLE, SOLDIER, SOLDIER2, TEREDOR, SIEGE_GOLEM, WIRPHY, SILVERA, TEREDOR_TRANSPARENT, EARTH_WYRM_TRASKEN);
		addMoveFinishedId(ROGIN, SOLDIER, WIRPHY, SILVERA);
		addMoveFinishedId(DWARVES);
		addSpellFinishedId(SIEGE_GOLEM, INVISIBLE_NPC, TEREDOR_TRANSPARENT);
		setCreatureKillId(this::onCreatureKill, BRONK, SILVERA);
		setCreatureAttackedId(this::onCreatureAttacked, BRONK, TENTACLE, SOLDIER, SOLDIER2, TEREDOR, SIEGE_GOLEM, WIRPHY, SILVERA);
		setCreatureSeeId(this::onCreatureSee, TENTACLE, SOLDIER, SOLDIER2, TEREDOR, INVISIBLE_NPC);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isMoDInstance(instance))
		{
			switch (npc.getId())
			{
				case INVISIBLE_NPC:
				{
					switch (npc.getParameters().getString("type", ""))
					{
						case "FIGHT":
						{
							addSpawn(npc, npc.getParameters().getInt("npcId"), npc.getLocation(), true, instance.getId());
							switch (Rnd.get(3))
							{
								case 0:
								{
									addSpawn(npc, SOLDIER, npc.getLocation(), true, instance.getId());
									break;
								}
								case 1:
								{
									addSpawn(npc, SOLDIER, npc.getLocation(), true, instance.getId());
									addSpawn(npc, SOLDIER2, npc.getLocation(), true, instance.getId());
									break;
								}
								case 2:
								{
									addSpawn(npc, SOLDIER, npc.getLocation(), true, instance.getId());
									addSpawn(npc, SOLDIER2, npc.getLocation(), true, instance.getId());
									addSpawn(npc, SOLDIER2, npc.getLocation(), true, instance.getId());
									break;
								}
							}
							break;
						}
						case "EVENT_B":
						{
							getTimers().addTimer("WARNING_TIME", 180000, npc, null);
							break;
						}
						case "EVENT_C":
						{
							// Rework me plz, I'm dying
							final Npc golem = addSpawn(npc, SIEGE_GOLEM, 116881, -180742, -1248, 1843, false, 0, false, instance.getId());
							golem.setIsInvul(true);
							break;
						}
						case "REINFORCE":
						{
							getTimers().addTimer("REINFORCE_SPAWN", 30000, npc, null);
							break;
						}
						default:
						{
							if (npc.getVariables().getString("type", "").equals("PULLER"))
							{
								addSkillCastDesire(npc, instance.getFirstPlayer(), PULLER_SKILL, 100000000);
							}
							break;
						}
					}
					break;
				}
				case SOLDIER:
				{
					switch (npc.getVariables().getString("type", ""))
					{
						case "AWAKENING_GUIDE":
						{
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, AWAKENING_GUIDE_MOVE_1);
							npc.setIsRunning(true);
							break;
						}
						default:
						{
							npc.initSeenCreatures();
							break;
						}
					}
					break;
				}
				case TENTACLE:
				case SOLDIER2:
				{
					npc.initSeenCreatures();
					break;
				}
				case TEREDOR:
				{
					npc.initSeenCreatures();
					if (npc.isScriptValue(2))
					{
						addSpawn(WIRPHY, 116361, -179760, -1128, 57533, false, 0, false, instance.getId());
						addSpawn(SILVERA, 116361, -179760, -1128, 57533, false, 0, false, instance.getId());
					}
					break;
				}
				case SIEGE_GOLEM:
				{
					npc.initSeenCreatures();
					Npc teredor = addSpawn(TEREDOR, 117100, -181088, -1272, 19956, false, 0, false, instance.getId());
					addAttackDesire(teredor, npc);
					teredor.setScriptValue(1);
					teredor = addSpawn(TEREDOR, 116925, -180420, -1200, 46585, false, 0, false, instance.getId());
					addAttackDesire(teredor, npc);
					teredor.setScriptValue(1);
					teredor = addSpawn(TEREDOR, 116656, -180461, -1240, 56363, false, 0, false, instance.getId());
					addAttackDesire(teredor, npc);
					teredor.setScriptValue(1);
					break;
				}
				case WIRPHY:
				{
					npc.setIsRunning(true);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, WIRPHY_MOVE);
					npc.getInstanceWorld().getAliveNpcs(TEREDOR).stream().filter(n -> n.isScriptValue(2)).forEach(n -> addAttackDesire(n, npc));
					break;
				}
				case SILVERA:
				{
					npc.setIsRunning(true);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, SILVERA_MOVE);
					break;
				}
				case TEREDOR_TRANSPARENT:
				{
					// TODO: Check why the first doesn't work :(
					addSkillCastDesire(npc, npc, TEREDOR_TRANSPARENT_SKILL, 1000000);
					npc.setTarget(npc);
					npc.doCast(TEREDOR_TRANSPARENT_SKILL.getSkill());
					break;
				}
				case EARTH_WYRM_TRASKEN:
				{
					npc.setLHandId(TRANSPARENT_1HS);
					npc.broadcastInfo();
					getTimers().addTimer("ENTER_EVENT", 5000, npc, null);
					break;
				}
			}
		}
		return super.onSpawn(npc);
	}
	
	public void onCreatureSee(OnCreatureSee event)
	{
		final Creature creature = event.getSeen();
		final Npc npc = (Npc) event.getSeer();
		final Instance world = npc.getInstanceWorld();
		if (isMoDInstance(world))
		{
			if (creature.isNpc())
			{
				switch (npc.getId())
				{
					case SOLDIER:
					case SOLDIER2:
					{
						if ((creature.getId() == TENTACLE) || ((creature.getId() == TEREDOR) && !((Npc) creature).isScriptValue(2)))
						{
							addAttackDesire(npc, creature);
						}
						break;
					}
					case TENTACLE:
					{
						if ((creature.getId() == SOLDIER) || (creature.getId() == SOLDIER2))
						{
							addAttackDesire(npc, creature);
						}
						break;
					}
					case TEREDOR:
					{
						if (!npc.isScriptValue(2))
						{
							if ((creature.getId() == SOLDIER) || (creature.getId() == SOLDIER2))
							{
								addAttackDesire(npc, creature);
							}
						}
						break;
					}
				}
			}
			else if (creature.isPlayer())
			{
				switch (npc.getId())
				{
					case INVISIBLE_NPC:
					{
						if (npc.getParameters().getString("type", "").equals("EVENT_C"))
						{
							final Npc siegeGolem = npc.getInstanceWorld().getNpc(SIEGE_GOLEM);
							if (siegeGolem.isScriptValue(0))
							{
								siegeGolem.setScriptValue(1);
								siegeGolem.abortAttack();
								siegeGolem.abortCast();
								addSkillCastDesire(siegeGolem, siegeGolem, SIEGE_GOLEM_SKILL_1, 1000000);
								world.getAliveNpcs(TEREDOR).stream().filter(n -> n.isScriptValue(1)).forEach(n -> getTimers().addTimer("TEREDOR_SUICIDE", 10000, n, null));
								getTimers().addTimer("CHASING_TRAJAN_TIME", 5000, npc, null);
								getTimers().addTimer("EARTHWORM_TIME", 15000, npc, null);
							}
						}
						else if (npc.getParameters().getString("type", "").equals("EVENT_B"))
						{
							getTimers().cancelTimers("WARNING_TIME");
						}
						else if (npc.getVariables().getString("type", "").equals("PULLER"))
						{
							showOnScreenMsg(creature.getActingPlayer(), NpcStringId.WATCH_THE_DWARVEN_VILLAGE_LAST_STAND, ExShowScreenMessage.TOP_CENTER, 5000);
						}
						break;
					}
				}
			}
		}
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "EARTHQUAKE":
			{
				player.sendPacket(new Earthquake(player.getLocation(), 50, 4));
				break;
			}
			case "END_OF_OPENING_SCENE":
			{
				player.teleToLocation(BATTLE_PORT);
				getTimers().addTimer("SPAWN_ROGIN", 10000, null, player);
				break;
			}
			case "SPAWN_ROGIN":
			{
				showOnScreenMsg(player, NpcStringId.WATCH_THE_DWARVEN_VILLAGE_LAST_STAND, ExShowScreenMessage.TOP_CENTER, 5000);
				player.getInstanceWorld().spawnGroup("ROGIN").forEach(n ->
				{
					n.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, ROGIN_MOVE);
					n.setIsRunning(true);
				});
				break;
			}
			case "ROGIN_TALK":
			{
				switch (npc.getVariables().getInt("talkId", 0))
				{
					case 0:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.CHIEF_REPORTING_IN);
						npc.getVariables().set("talkId", 1);
						getTimers().addTimer("ROGIN_TALK", 2000, npc, null);
						break;
					}
					case 1:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.ENEMIES_ARE_APPROACHING_FORM_THE_SOUTH);
						npc.getVariables().set("talkId", 2);
						getTimers().addTimer("ROGIN_TALK", 2000, npc, null);
						npc.getInstanceWorld().getNpc(TOROCCO).broadcastSay(ChatType.NPC_GENERAL, NpcStringId.ROGIN_I_M_HERE);
						npc.getInstanceWorld().getNpc(BRONK).broadcastSay(ChatType.NPC_GENERAL, NpcStringId.MM_I_SEE);
						// Set Bronk heading towards Rogin
						break;
					}
					case 2:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_ELDERS_HAVEN_T_BEEN_MOVED_TO_SAFETY);
						npc.getVariables().set("talkId", 3);
						getTimers().addTimer("ROGIN_TALK", 2000, npc, null);
						break;
					}
					case 3:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.MANY_RESIDENTS_STILL_HAVEN_T_LEFT_THEIR_HOMES);
						getTimers().addTimer("BRONK_TALK", 2000, npc.getInstanceWorld().getNpc(BRONK), null);
						break;
					}
				}
				break;
			}
			case "BRONK_TALK":
			{
				switch (npc.getVariables().getInt("talkId", 0))
				{
					case 0:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THANK_YOU_FOR_THE_REPORT_ROGIN);
						npc.getVariables().set("talkId", 1);
						getTimers().addTimer("BRONK_TALK", 2000, npc, null);
						npc.setHeading(17036);
						npc.broadcastPacket(new ValidateLocation(npc));
						break;
					}
					case 1:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.SOLDIERS_WE_RE_FIGHTING_A_BATTLE_THAT_CAN_T_BE_WON);
						npc.getVariables().set("talkId", 2);
						getTimers().addTimer("BRONK_TALK", 2000, npc, null);
						npc.setHeading(17036);
						npc.broadcastPacket(new ValidateLocation(npc));
						break;
					}
					case 2:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.BUT_WE_HAVE_TO_DEFEND_OUR_VILLAGE_SO_WE_RE_FIGHTING);
						npc.getVariables().set("talkId", 3);
						getTimers().addTimer("BRONK_TALK", 2000, npc, null);
						break;
					}
					case 3:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.FOR_THE_FINE_WINES_AND_TREASURES_OF_ADEN);
						npc.getVariables().set("talkId", 4);
						getTimers().addTimer("BRONK_TALK", 2000, npc, null);
						break;
					}
					case 4:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_M_PROUD_OF_EVERY_ONE_OF);
						npc.getVariables().set("talkId", 5);
						getTimers().addTimer("BRONK_TALK", 2000, npc, null);
						break;
					}
					case 5:
					{
						npc.getInstanceWorld().spawnGroup("TENTACLE").forEach(n ->
						{
							n.getVariables().set("isLeaderKiller", true);
							addAttackDesire(n, npc);
						});
						break;
					}
				}
				break;
			}
			case "REINFORCE_SPAWN":
			{
				final Npc soldier = addSpawn(SOLDIER, npc.getLocation(), false, 0, false, npc.getInstanceId());
				soldier.getVariables().set("type", "AWAKENING_GUIDE");
				getTimers().addTimer("REINFORCE_SPAWN", 40000, npc, null);
				break;
			}
			case "ATTACK_TIME":
			{
				final List<Npc> tentacles = npc.getInstanceWorld().getAliveNpcs(TENTACLE).stream().filter(n -> n.getVariables().getBoolean("isLeaderKiller", false)).collect(Collectors.toList());
				addAttackDesire(npc, tentacles.get(Rnd.get(tentacles.size())));
				break;
			}
			case "RUN_TIME":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, SHOUT_RUN[Rnd.get(SHOUT_RUN.length)]);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, DWARVES_MOVE_1);
				npc.setIsRunning(true);
				break;
			}
			case "TEREDOR_SUICIDE":
			{
				npc.doDie(null);
				break;
			}
			case "WARNING_TIME":
			{
				final Npc invisibleNpc = addSpawn(INVISIBLE_NPC, 117100, -181088, -1272, 19956, false, 0, false, npc.getInstanceId());
				invisibleNpc.getVariables().set("type", "PULLER");
				break;
			}
			case "SECOND_PULL":
			{
				addSkillCastDesire(npc, npc.getInstanceWorld().getFirstPlayer(), PULLER_SKILL, 100000000);
				getTimers().addTimer("DESPAWNER_PULLER", 3000, npc, null);
				break;
			}
			case "DESPAWNER_PULLER":
			{
				npc.deleteMe();
				break;
			}
			case "CHASING_TRAJAN_TIME":
			{
				final Npc teredor = addSpawn(npc, TEREDOR, 116016, -179503, -1040, 58208, false, 0, false, npc.getInstanceId());
				teredor.setScriptValue(2);
				break;
			}
			case "EARTHWORM_TIME":
			{
				addSpawn(npc, TEREDOR_TRANSPARENT, 116016, -179503, -1040, 58208, false, 0, false, npc.getInstanceId());
				break;
			}
			case "ENTER_EVENT":
			{
				addSkillCastDesire(npc, npc, TRASKEN_SKILL_1, 100000000);
				npc.getInstanceWorld().getFirstPlayer().sendPacket(new Earthquake(npc.getLocation(), 50, 4));
				getTimers().addTimer("TRASKEN_UNEQUIP", 2000, npc, null);
				break;
			}
			case "TRASKEN_UNEQUIP":
			{
				npc.setLHandId(0);
				npc.broadcastInfo();
				break;
			}
		}
	}
	
	@Override
	public void onInstanceCreated(Instance instance)
	{
		getTimers().addTimer("OPENING_SCENE", 500, e ->
		{
			instance.getPlayers().forEach(p ->
			{
				p.sendPacket(new OnEventTrigger(FIRE_IN_DWARVEN_VILLAGE, true));
				playMovie(p, Movie.SC_AWAKENING_OPENING);
				getTimers().addRepeatingTimer("EARTHQUAKE", 10000, null, p);
				getTimers().addTimer("END_OF_OPENING_SCENE", 32000, null, p);
			});
		});
	}
	
	@Override
	public void onMoveFinished(Npc npc)
	{
		if (CommonUtil.contains(DWARVES, npc.getId()))
		{
			if ((npc.getX() == DWARVES_MOVE_1.getX()) && (npc.getY() == DWARVES_MOVE_1.getY()))
			{
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, DWARVES_MOVE_2);
			}
			else if ((npc.getX() == DWARVES_MOVE_2.getX()) && (npc.getY() == DWARVES_MOVE_2.getY()))
			{
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, DWARVES_MOVE_3);
			}
			else if ((npc.getX() == DWARVES_MOVE_3.getX()) && (npc.getY() == DWARVES_MOVE_3.getY()))
			{
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, DWARVES_MOVE_RANDOM[Rnd.get(DWARVES_MOVE_RANDOM.length)]);
			}
		}
		switch (npc.getId())
		{
			case ROGIN:
			{
				if ((npc.getX() == ROGIN_MOVE.getX()) && (npc.getY() == ROGIN_MOVE.getY()))
				{
					getTimers().addTimer("ROGIN_TALK", 3000, npc, null);
				}
				break;
			}
			case SOLDIER:
			{
				switch (npc.getVariables().getString("type", ""))
				{
					case "AWAKENING_GUIDE":
					{
						if ((npc.getX() == AWAKENING_GUIDE_MOVE_1.getX()) && (npc.getY() == AWAKENING_GUIDE_MOVE_1.getY()))
						{
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, AWAKENING_GUIDE_MOVE_2);
						}
						else if ((npc.getX() == AWAKENING_GUIDE_MOVE_2.getX()) && (npc.getY() == AWAKENING_GUIDE_MOVE_2.getY()))
						{
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, AWAKENING_GUIDE_MOVE_3);
						}
						else if ((npc.getX() == AWAKENING_GUIDE_MOVE_3.getX()) && (npc.getY() == AWAKENING_GUIDE_MOVE_3.getY()))
						{
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, AWAKENING_GUIDE_MOVE_4);
						}
						else if ((npc.getX() == AWAKENING_GUIDE_MOVE_4.getX()) && (npc.getY() == AWAKENING_GUIDE_MOVE_4.getY()))
						{
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, AWAKENING_GUIDE_MOVE_5);
						}
						else if ((npc.getX() == AWAKENING_GUIDE_MOVE_5.getX()) && (npc.getY() == AWAKENING_GUIDE_MOVE_5.getY()))
						{
							getTimers().addTimer("DESPAWN_AWAKENING_GUIDE", 1000, e -> npc.deleteMe());
						}
						break;
					}
				}
				break;
			}
			case WIRPHY:
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HELP_ME3);
				break;
			}
			case SILVERA:
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_VE_GOT_A_MONSTER_ON_MY_TAIL);
				break;
			}
		}
	}
	
	private void onCreatureAttacked(OnCreatureAttacked event)
	{
		final Instance world = event.getTarget().getInstanceWorld();
		if (isMoDInstance(world))
		{
			if (!event.getAttacker().isPlayable())
			{
				final Npc npc = (Npc) event.getTarget();
				final Npc attacker = (Npc) event.getAttacker();
				if (CommonUtil.contains(DWARVES, npc.getId()))
				{
					final int attackCount = npc.getVariables().getInt("attackCount", 0) + 1;
					if (attackCount == 10)
					{
						npc.doDie(attacker);
					}
					else
					{
						npc.getVariables().set("attackCount", attackCount);
					}
				}
				switch (npc.getId())
				{
					case BRONK:
					{
						npc.doDie(attacker);
						break;
					}
					case SOLDIER:
					case SOLDIER2:
					{
						final int attackCount = npc.getVariables().getInt("attackCount", 0) + 1;
						if (attackCount == 10)
						{
							npc.doDie(attacker);
							addSpawn((Npc) npc.getSummoner(), SOLDIER, npc.getLocation(), true, world.getId());
						}
						else
						{
							npc.getVariables().set("attackCount", attackCount);
						}
						break;
					}
					case TENTACLE:
					{
						final int attackCount = npc.getVariables().getInt("attackCount", 0) + 1;
						final boolean isBronKiller = npc.getVariables().getBoolean("isLeaderKiller", false);
						final int killCount = isBronKiller ? 5 : 20;
						if (attackCount == killCount)
						{
							npc.doDie(attacker);
							if (!isBronKiller)
							{
								addSpawn((Npc) npc.getSummoner(), npc.getId(), npc.getLocation(), true, world.getId());
							}
						}
						else
						{
							npc.getVariables().set("attackCount", attackCount);
							addAttackDesire(npc, attacker);
						}
						break;
					}
					case TEREDOR:
					{
						if (npc.isScriptValue(0))
						{
							final int attackCount = npc.getVariables().getInt("attackCount", 0) + 1;
							if (attackCount == 20)
							{
								npc.doDie(attacker);
								addSpawn((Npc) npc.getSummoner(), npc.getId(), npc.getLocation(), true, world.getId());
							}
							else
							{
								npc.getVariables().set("attackCount", attackCount);
								addAttackDesire(npc, attacker);
							}
						}
						else if (npc.isScriptValue(2))
						{
							final int attackCount = npc.getVariables().getInt("attackCount", 0) + 1;
							if ((attackCount == 80) || (attacker.getId() == SIEGE_GOLEM))
							{
								npc.doDie(attacker);
							}
							addAttackDesire(npc, attacker);
						}
						break;
					}
					case SIEGE_GOLEM:
					{
						if (npc.isScriptValue(0))
						{
							addSkillCastDesire(npc, attacker, SIEGE_GOLEM_SKILL_2, 1000000);
						}
						break;
					}
					case WIRPHY:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.NO_WAY2);
						npc.doDie(null);
						// TODO : This shit doesn't attack silvera for some reason which prevents to trigger the dwarf text
						// Used some additional method such as World.getInstance().getVisibleObjects(attacker, Npc.class, n -> (n.getId() == SILVERA))
						// It found the npc but didn't worked either.
						addAttackDesire(attacker, world.getNpc(SILVERA));
						break;
					}
					case SILVERA:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.MY_GOD);
						npc.doDie(null);
						addAttackDesire(world.getNpc(SIEGE_GOLEM), attacker);
						break;
					}
				}
			}
		}
	}
	
	private void onCreatureKill(OnCreatureKill event)
	{
		final Npc npc = ((Npc) event.getTarget());
		if (npc.getId() == BRONK)
		{
			for (Npc dwarf : npc.getInstanceWorld().getNpcs(DWARVES))
			{
				if (dwarf.getId() == ROGIN)
				{
					dwarf.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.CHIEF2);
				}
				else
				{
					dwarf.broadcastSay(ChatType.NPC_GENERAL, SHOUT_BRONK_DEATH[Rnd.get(SHOUT_BRONK_DEATH.length)]);
				}
				getTimers().addTimer("ATTACK_TIME", 1000, dwarf, null);
				getTimers().addTimer("RUN_TIME", 10000, dwarf, null);
			}
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.UGH_IF_I_SEE_YOU_IN_THE_SPIRIT_WORLD_FIRST_ROUND_IS_ON_ME);
		}
		else if (npc.getId() == SILVERA)
		{
			npc.getInstanceWorld().getNpcs(DWARVES).forEach(n -> n.broadcastSay(ChatType.NPC_GENERAL, SHOUT_SILVERA_DEATH[Rnd.get(SHOUT_SILVERA_DEATH.length)]));
		}
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		if ((npc.getId() == SIEGE_GOLEM) && (skill.getId() == SIEGE_GOLEM_SKILL_1.getSkillId()))
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, GOLEM_MOVE);
			npc.setIsRunning(true);
		}
		else if ((npc.getId() == INVISIBLE_NPC) && npc.getVariables().getString("type", "").equals("PULLER") && (skill.getId() == PULLER_SKILL.getSkillId()) && npc.isScriptValue(0))
		{
			npc.teleToLocation(PULLER_TELEPORT);
			getTimers().addTimer("SECOND_PULL", 2000, npc, null);
		}
		else if ((npc.getId() == TEREDOR_TRANSPARENT) && (skill.getId() == TEREDOR_TRANSPARENT_SKILL.getSkillId()))
		{
			final Npc invisibleNpc = addSpawn(npc, INVISIBLE_NPC, npc.getLocation(), false, npc.getInstanceId());
			invisibleNpc.getVariables().set("type", "BOUNCER");
			addSpawn(npc, EARTH_WYRM_TRASKEN, npc.getLocation(), false, npc.getInstanceId());
			npc.deleteMe();
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_CALL_TO_CHANGE_CLASS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerCallToChangeClass(OnPlayerCallToChangeClass event)
	{
		enterInstance(event.getActiveChar(), null, TEMPLATE_ID);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		final PlayerInstance player = event.getActiveChar();
		if ((player.getLevel() > 84) && player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && !player.isSubClassActive() && (player.getClassId() != ClassId.JUDICATOR))
		{
			for (ClassId newClass : player.getClassId().getNextClassIds())
			{
				player.sendPacket(new ExCallToChangeClass(newClass.getId(), false));
				showOnScreenMsg(player, NpcStringId.FREE_THE_GIANT_FROM_HIS_IMPRISONMENT_AND_AWAKEN_YOUR_TRUE_POWER, ExShowScreenMessage.TOP_CENTER, 5000);
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		final PlayerInstance player = event.getActiveChar();
		if ((player.getLevel() > 84) && player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && !player.isSubClassActive() && (player.getClassId() != ClassId.JUDICATOR))
		{
			for (ClassId newClass : player.getClassId().getNextClassIds())
			{
				player.sendPacket(new ExCallToChangeClass(newClass.getId(), false));
				showOnScreenMsg(player, NpcStringId.FREE_THE_GIANT_FROM_HIS_IMPRISONMENT_AND_AWAKEN_YOUR_TRUE_POWER, ExShowScreenMessage.TOP_CENTER, 5000);
			}
		}
	}
	
	private boolean isMoDInstance(Instance instance)
	{
		return (instance != null) && (instance.getTemplateId() == TEMPLATE_ID);
	}
	
	public static void main(String[] args)
	{
		new MemoryOfDisaster();
	}
}
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
package handlers.bypasshandlers;

import java.util.logging.Level;

import org.l2junity.gameserver.handler.IBypassHandler;
import org.l2junity.gameserver.instancemanager.SiegeManager;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.L2ObservationInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.ActionFailed;
import org.l2junity.gameserver.network.client.send.ItemList;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;

public class Observation implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"observesiege",
		"observeoracle",
		"observe"
	};
	
	private static final int[][] LOCATIONS = new int[][]
	{
		//@formatter:off
		// Gludio
		{-18347, 114000, -2360, 500},
		{-18347, 113255, -2447, 500},
		// Dion
		{22321, 155785, -2604, 500},
		{22321, 156492, -2627, 500},
		// Giran
		{112000, 144864, -2445, 500},
		{112657, 144864, -2525, 500},
		// Innadril
		{116260, 244600, -775, 500},
		{116260, 245264, -721, 500},
		// Oren
		{78100, 36950, -2242, 500},
		{78744, 36950, -2244, 500},
		// Aden
		{147457, 9601, -233, 500},
		{147457, 8720, -252, 500},
		// Goddard
		{147542, -43543, -1328, 500},
		{147465, -45259, -1328, 500},
		// Rune
		{20598, -49113, -300, 500},
		{18702, -49150, -600, 500},
		// Schuttgart
		{77541, -147447, 353, 500},
		{77541, -149245, 353, 500},
		// Coliseum
		{148416, 46724, -3000, 80},
		{149500, 46724, -3000, 80},
		{150511, 46724, -3000, 80},
		// Dusk
		{-77200, 88500, -4800, 500},
		{-75320, 87135, -4800, 500},
		{-76840, 85770, -4800, 500},
		{-76840, 85770, -4800, 500},
		{-79950, 85165, -4800, 500},
		// Dawn
		{-79185, 112725, -4300, 500},
		{-76175, 113330, -4300, 500},
		{-74305, 111965, -4300, 500},
		{-75915, 110600, -4300, 500},
		{-78930, 110005, -4300, 500}
		//@formatter:on
	};
	
	@Override
	public boolean useBypass(String command, PlayerInstance activeChar, Creature target)
	{
		if (!(target instanceof L2ObservationInstance))
		{
			return false;
		}
		
		if (activeChar.hasSummon())
		{
			activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_OBSERVE_A_SIEGE_WITH_A_PET_OR_SERVITOR_SUMMONED);
			return false;
		}
		if (activeChar.isOnEvent())
		{
			activeChar.sendMessage("Cannot use while current Event");
			return false;
		}
		
		String _command = command.split(" ")[0].toLowerCase();
		final int param;
		try
		{
			param = Integer.parseInt(command.split(" ")[1]);
		}
		catch (NumberFormatException nfe)
		{
			_log.log(Level.WARNING, "Exception in " + getClass().getSimpleName(), nfe);
			return false;
		}
		
		if ((param < 0) || (param > (LOCATIONS.length - 1)))
		{
			return false;
		}
		final int[] locCost = LOCATIONS[param];
		
		Location loc = new Location(locCost[0], locCost[1], locCost[2]);
		final long cost = locCost[3];
		
		switch (_command)
		{
			case "observesiege":
			{
				if (SiegeManager.getInstance().getSiege(loc) != null)
				{
					doObserve(activeChar, (Npc) target, loc, cost);
				}
				else
				{
					activeChar.sendPacket(SystemMessageId.OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE);
				}
				return true;
			}
			case "observeoracle": // Oracle Dusk/Dawn
			{
				doObserve(activeChar, (Npc) target, loc, cost);
				return true;
			}
			case "observe": // Observe
			{
				doObserve(activeChar, (Npc) target, loc, cost);
				return true;
			}
		}
		return false;
	}
	
	private static final void doObserve(final PlayerInstance player, final Npc npc, final Location pos, final long cost)
	{
		if (player.reduceAdena("Broadcast", cost, npc, true))
		{
			// enter mode
			player.enterObserverMode(pos);
			player.sendPacket(new ItemList(player, false));
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}

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
package quests.Q10408_KekropusLetterTheSwampOfScreams;

import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

import quests.LetterQuest;

/**
 * Kekropus' Letter: The Swamp of Screams (10408)
 * @author St3eT
 */
public final class Q10408_KekropusLetterTheSwampOfScreams extends LetterQuest
{
	// NPCs
	private static final int MATHIAS = 31340;
	private static final int DOKARA = 33847;
	private static final int INVISIBLE_NPC = 19543;
	// Items
	private static final int SOE_SWAMP_OF_SCREAMS = 37030; // Scroll of Escape: Swamp of Screams
	private static final int SOE_TOWN_OF_RUNE = 37117; // Scroll of Escape: Town of Rune
	private static final int EWA = 729; // Scroll: Enchant Weapon (A-grade)
	private static final int STEEL_COIN = 37045; // Steel Door Guild Coin
	// Location
	private static final Location TELEPORT_LOC = new Location(42682, -47986, -792);
	// Misc
	private static final int MIN_LEVEL = 65;
	private static final int MAX_LEVEL = 69;
	
	public Q10408_KekropusLetterTheSwampOfScreams()
	{
		super(10408, Q10408_KekropusLetterTheSwampOfScreams.class.getSimpleName(), "Kekropus' Letter: The Swamp of Screams");
		addTalkId(MATHIAS, DOKARA);
		addSeeCreatureId(INVISIBLE_NPC);
		
		setIsErtheiaQuest(false);
		setLevel(MIN_LEVEL, MAX_LEVEL);
		setStartQuestSound("Npcdialog1.kekrops_quest_6");
		setStartLocation(SOE_TOWN_OF_RUNE, TELEPORT_LOC);
		registerQuestItems(SOE_TOWN_OF_RUNE, SOE_SWAMP_OF_SCREAMS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "31340-02.html":
			{
				htmltext = event;
				break;
			}
			case "31340-03.html":
			{
				if (st.isCond(1))
				{
					takeItems(player, SOE_TOWN_OF_RUNE, -1);
					giveItems(player, SOE_SWAMP_OF_SCREAMS, 1);
					st.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "33847-02.html":
			{
				if (st.isCond(2))
				{
					st.exitQuest(false, true);
					giveItems(player, EWA, 2);
					giveItems(player, STEEL_COIN, 91);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 942_690, 226);
					}
					showOnScreenMsg(player, NpcStringId.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_70, ExShowScreenMessage.TOP_CENTER, 6000);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, false);
		
		if (st == null)
		{
			return htmltext;
		}
		
		if (st.isStarted())
		{
			if ((npc.getId() == MATHIAS) && st.isCond(1))
			{
				htmltext = "31340-01.html";
			}
			else if (st.isCond(2))
			{
				htmltext = npc.getId() == MATHIAS ? "31340-04.html" : "33847-01.html";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			final PlayerInstance player = creature.getActingPlayer();
			final QuestState st = getQuestState(player, false);
			
			if ((st != null) && st.isCond(2))
			{
				showOnScreenMsg(player, NpcStringId.SWAMP_OF_SCREAMS_IA_A_GOOD_HUNTING_ZONE_FOR_LV_65_OR_ABOVE, ExShowScreenMessage.TOP_CENTER, 6000);
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public boolean canShowTutorialMark(PlayerInstance player)
	{
		return !player.isMageClass();
	}
}
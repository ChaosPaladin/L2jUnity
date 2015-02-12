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
package org.l2junity.gameserver.taskmanager.tasks;

import java.nio.file.Paths;
import java.util.logging.Level;

import org.l2junity.gameserver.scripting.ScriptEngineManager;
import org.l2junity.gameserver.taskmanager.Task;
import org.l2junity.gameserver.taskmanager.TaskManager.ExecutedTask;

/**
 * @author janiii
 */
public class TaskScript extends Task
{
	public static final String NAME = "script";
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		try
		{
			ScriptEngineManager.getInstance().executeScript(Paths.get("cron", task.getParams()[2]));
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Script execution failed!", e);
		}
	}
}

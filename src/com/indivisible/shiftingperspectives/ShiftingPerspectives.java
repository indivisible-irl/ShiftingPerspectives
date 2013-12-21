package com.indivisible.shiftingperspectives;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShiftingPerspectives extends JavaPlugin
{

	@Override
	public void onEnable()
	{
		//TODO verify WorldGuard is installed
		
	}
	
	@Override
	public void onDisable()
	{
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args)
	{
		// command root to be '/shift <args>'
		
		//ASK false if not catching command?
		return false;
	}

	

	
}

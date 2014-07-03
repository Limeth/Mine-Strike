package cz.minestrike.me.limeth.minestrike.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;

public abstract class SchemeCommandHandler
{
	private String command;
	private String commandPath;
	private String description;
	
	public SchemeCommandHandler(String command, String commandPath, String description)
	{
		this.command = command;
		this.commandPath = commandPath;
		this.description = description;
	}
	
	public abstract void execute(CommandSender sender, Scheme scheme, String[] args);
	
	public String getDisplay()
	{
		return ChatColor.ITALIC + "/" + commandPath + ChatColor.GRAY + " - " + description;
	}
	
	public String getCommand()
	{
		return command;
	}

	public void setCommand(String command)
	{
		this.command = command;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getCommandPath()
	{
		return commandPath;
	}

	public void setCommandPath(String commandPath)
	{
		this.commandPath = commandPath;
	}
}

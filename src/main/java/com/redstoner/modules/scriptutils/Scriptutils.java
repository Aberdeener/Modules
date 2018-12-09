package com.redstoner.modules.scriptutils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@Commands(CommandHolderType.File)
@Version(major = 4, minor = 2, revision = 0, compatible = 4)
public class Scriptutils implements Module
{
	/** Prints Bukkit restart message
	 * arg 0 timeout
	 * arg 1 $(whoami);
	 * arg 2: reason */
	@Command(hook = "script_restart")
	public void print_restart(CommandSender sender, String timeout, String name, String reason)
	{
		Utils.broadcast("", "§2§l=============================================", null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§9" + name + " is restarting the server.", null);
		Utils.broadcast("", "§a§lServer is going to restart in " + timeout + " seconds.", null);
		Utils.broadcast("", "§6§l" + reason, null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§2§l=============================================", null);
	}
	
	/** Prints the Bukkit shut down message
	 * arg 0 timeout
	 * arg 1 $(whoami);
	 * arg 2: reason */
	@Command(hook = "script_stop")
	public void print_stop(CommandSender sender, String timeout, String name, String reason)
	{
		Utils.broadcast("", "§2§l=============================================", null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§9" + name + " is shutting down the server.", null);
		Utils.broadcast("", "§a§lServer is going to shut down in " + timeout + " seconds.", null);
		Utils.broadcast("", "§6§l" + reason, null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§2§l=============================================", null);
	}
	
	/** Prints the shut down abort message */
	@Command(hook = "script_stop_abort")
	public void abort_stop(CommandSender sender)
	{
		Utils.broadcast("", "§4§oShut down has been aborted.", null);
	}
	
	/** Prints the restart abort message */
	@Command(hook = "script_restart_abort")
	public void abort_restart(CommandSender sender)
	{
		Utils.broadcast("", "§4§oRestart has been aborted.", null);
	}
	
	/** Prints the backup started message, saves all worlds and turns off world saving */
	@Command(hook = "script_backup_begin")
	public void print_backup_begin(CommandSender sender)
	{
		Utils.broadcast("", "§4 =§2 Starting backup now.", null);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off");
	}
	
	/** Prints the backup finished message and turns on world saving */
	@Command(hook = "script_backup_end")
	public void print_backup_end(CommandSender sender)
	{
		Utils.broadcast("", "§4 =§2 Backup completed.", null);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on");
	}
	
	/** Prints the backup error message and turns on world saving */
	@Command(hook = "script_backup_error")
	public void print_backup_error(CommandSender sender)
	{
		Utils.broadcast("", "§4 =§c§l Error while backing up!", null);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on");
	}
	
	/** Prints the database backup started message and admin-chat warning */
	@Command(hook = "script_backup_database_begin")
	public void print_backup_db_begin(CommandSender sender)
	{
		Utils.broadcast("", "§6 =§2 Starting database backup now.", null);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ac §aLogblock may be unavailable!");
	}
	
	/** Prints the database dumps compression started message */
	@Command(hook = "script_backup_database_dumps")
	public void print_backup_db_dumps(CommandSender sender)
	{
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ac §aDumps completed, logblock available again.");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ac §aNow compressing dumps, will take a while...");
	}
	
	/** Prints the database finished message and backup size in admin-chat
	 * arg 0 size of backup */
	@Command(hook = "script_backup_database_end")
	public void print_backup_db_end(CommandSender sender, String size)
	{
		Utils.broadcast("", "§6 =§2 Database backup completed.", null);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ac §abackup size: §2" + size + "MB§a.");
	}
	
	/** Prints the database backup error message */
	@Command(hook = "script_backup_database_error")
	public void print_backup_db_error(CommandSender sender)
	{
		Utils.broadcast("", "§6 =§c§l Error while backing up database!", null);
	}
	
	/** Prints the database backup abort message */
	@Command(hook = "script_backup_database_abort")
	public void print_backup_db_abort(CommandSender sender)
	{
		Utils.broadcast("", "§6 =§2 Database backup aborted.", null);
	}
	
	/** Saves all worlds, kicks players and shuts down the server
	 * arg 0: reason */
	@Command(hook = "script_shutdown")
	public void shutdown(CommandSender sender, String reason)
	{
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kickall " + reason);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
	}
}

package com.redstoner.modules.socialspy;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;

import com.nemez.cmdmgr.Command;
import com.nemez.cmdmgr.CommandManager;
import com.redstoner.annotations.Version;
import com.redstoner.misc.BroadcastFilter;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.CoreModule;
import com.redstoner.modules.datamanager.DataManager;

@Version(major = 3, minor = 1, revision = 0, compatible = 3)
public class Socialspy implements CoreModule
{
	@Override
	public void postEnable()
	{
		CommandManager.registerCommand(this.getClass().getResourceAsStream("Socialspy.cmd"), this, Main.plugin);
	}
	
	@Command(hook = "config_prefix_default")
	public boolean prefixDefault(CommandSender sender)
	{
		return prefix(sender, getDefaultPrefix());
	}
	
	@Command(hook = "config_prefix")
	public boolean prefix(CommandSender sender, String prefix)
	{
		Utils.sendMessage(sender, null, "Set your prefix to: " + prefix);
		DataManager.getDataManager().setData(sender, "prefix", prefix);
		return true;
	}
	
	@Command(hook = "config_format_default")
	public boolean configFormatDefault(CommandSender sender)
	{
		return configFormat(sender, getDefaultFormat());
	}
	
	@Command(hook = "config_format")
	public boolean configFormat(CommandSender sender, String format)
	{
		Utils.sendMessage(sender, null, "Set your format to: " + format);
		DataManager.getDataManager().setData(sender, "format", format);
		return true;
	}
	
	@Command(hook = "stripcolor_on")
	public boolean stripcolorOn(CommandSender sender)
	{
		Utils.sendMessage(sender, null, "Enabled stripping colors!");
		DataManager.getDataManager().setData(sender, "stripcolor", true);
		return true;
	}
	
	@Command(hook = "stripcolor_off")
	public boolean stripcolorOff(CommandSender sender)
	{
		Utils.sendMessage(sender, null, "Disabled stripping colors!");
		DataManager.getDataManager().setData(sender, "stripcolor", false);
		return true;
	}
	
	@Command(hook = "stripcolor")
	public boolean stripcolor(CommandSender sender)
	{
		boolean b = (boolean) DataManager.getDataManager().getOrDefault(sender, "stripcolor", true);
		Utils.sendMessage(sender, null, (b ? "Disabled" : "Enabled") + " stripping colors!");
		DataManager.getDataManager().setData(sender, "stripcolor", !b);
		return true;
	}
	
	@Command(hook = "on")
	public boolean spyOn(CommandSender sender)
	{
		Utils.sendMessage(sender, null, "Enabled socialspy!");
		DataManager.getDataManager().setData(sender, "enabled", true);
		return true;
	}
	
	@Command(hook = "off")
	public boolean spyOff(CommandSender sender)
	{
		Utils.sendMessage(sender, null, "Disabled socialspy!");
		DataManager.getDataManager().setData(sender, "enabled", false);
		return true;
	}
	
	@Command(hook = "toggle")
	public boolean spyToggle(CommandSender sender)
	{
		boolean b = (boolean) DataManager.getDataManager().getOrDefault(sender, "enabled", false);
		Utils.sendMessage(sender, null, (b ? "Disabled" : "Enabled") + " socialspy!");
		DataManager.getDataManager().setData(sender, "enabled", !b);
		return true;
	}
	
	@Command(hook = "format_help")
	public boolean formatInfo(CommandSender sender)
	{
		Utils.sendModuleHeader(sender);
		Utils.sendMessage(sender, "", " Format placeholders:");
		Utils.sendMessage(sender, "", " &c%s&eender &7(display name) | &c%S&eender &7(real name)", '&');
		Utils.sendMessage(sender, "", " &c%t&earget &7(display name) | &c%T&earget &7(real name)", '&');
		Utils.sendMessage(sender, "", " &p%s&erefix &7(see prefix option)", '&');
		Utils.sendMessage(sender, "", " &m%s&eessage", '&');
		Utils.sendMessage(sender, "", " &c%s&eommand", '&');
		Utils.sendMessage(sender, "", " Any other text will be put as literal text. Use %% to escape any %.", '&');
		Utils.sendMessage(sender, "", " The default format is: '§e" + getDefaultFormat());
		Utils.sendMessage(sender, "", " The default prefix is: '§e" + getDefaultPrefix());
		return true;
	}
	
	@Command(hook = "commands_list")
	public boolean commands_list(CommandSender sender)
	{
		Utils.sendModuleHeader(sender);
		JSONArray commands = (JSONArray) DataManager.getDataManager().getOrDefault(sender, "commands",
				getDefaultCommandList());
		if (commands == null || commands.size() == 0)
			Utils.sendErrorMessage(sender, "", "You are not listening to any commands!");
		else
		{
			Utils.sendMessage(sender, "", "You are listening to the following " + commands.size() + " commands:");
			Utils.sendMessage(sender, "", Arrays.toString(commands.toArray()).replace(", /", "&7, &e/")
					.replace("[", "[&e").replace("]", "&7]"), '&');
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private final JSONArray getDefaultCommandList()
	{
		JSONArray commands = new JSONArray();
		commands.add("/m");
		commands.add("/r");
		return commands;
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "commands_add")
	public boolean commands_add(CommandSender sender, String command)
	{
		JSONArray commands = (JSONArray) DataManager.getDataManager().getOrDefault(sender, "commands",
				getDefaultCommandList());
		commands.add(command);
		DataManager.getDataManager().setData(sender, "commands", commands);
		Utils.sendMessage(sender, null, "You are now spying on &e" + command, '&');
		return true;
	}
	
	@Command(hook = "commands_del")
	public boolean commands_del(CommandSender sender, String command)
	{
		JSONArray commands = (JSONArray) DataManager.getDataManager().getOrDefault(sender, "commands",
				getDefaultCommandList());
		commands.remove(command);
		DataManager.getDataManager().setData(sender, "commands", commands);
		Utils.sendMessage(sender, null, "You are no longer spying on &e" + command, '&');
		return true;
	}
	
	public static void spyBroadcast(CommandSender sender, CommandSender target, String message, String command,
			BroadcastFilter filter)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if ((boolean) DataManager.getDataManager().getOrDefault(p, "enabled", false))
				if (p.hasPermission("utils.socialspy"))
				{
					if (filter == null || filter.sendTo(p))
						Utils.sendMessage(p, "", formatMessage(p, sender, target, message, command));
				}
				else
					DataManager.getDataManager().setData(sender, "enabled", false);
		}
	}
	
	public static void spyBroadcast(CommandSender sender, String target, String message, String command,
			BroadcastFilter filter)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if ((boolean) DataManager.getDataManager().getOrDefault(p, "enabled", false))
				if (p.hasPermission("utils.socialspy"))
				{
					if (filter == null || filter.sendTo(p))
						Utils.sendMessage(p, "", formatMessage(p, sender, target, message, command));
				}
				else
					DataManager.getDataManager().setData(sender, "enabled", false);
		}
	}
	
	private static String formatMessage(CommandSender formatHolder, CommandSender sender, CommandSender target,
			String message, String command)
	{
		if ((boolean) DataManager.getDataManager().getOrDefault(formatHolder, "stripcolor", false))
			message = ChatColor.stripColor(message);
		String format = (String) DataManager.getDataManager().getOrDefault(formatHolder, "format", getDefaultFormat());
		// Replace escaped % with placeholder
		format = format.replace("%%", "§§");
		// Sender name
		format = format.replace("%s", Utils.getName(sender));
		format = format.replace("%S", sender.getName());
		// Target name
		format = format.replace("%t", Utils.getName(target));
		format = format.replace("%T", target.getName());
		// Prefix
		String prefix = (String) DataManager.getDataManager().getOrDefault(formatHolder, "prefix", getDefaultPrefix());
		format = format.replace("%p", prefix);
		// Apply colors to halfway replaced String
		format = ChatColor.translateAlternateColorCodes('&', format);
		// Insert command and message
		format = format.replace("%c", command);
		format = format.replace("%m", message);
		// Convert placeholder back
		format = format.replace("§§", "%%");
		return format;
	}
	
	private static String formatMessage(CommandSender formatHolder, CommandSender sender, String target, String message,
			String command)
	{
		if ((boolean) DataManager.getDataManager().getOrDefault(formatHolder, "stripcolor", false))
			message = ChatColor.stripColor(message);
		String format = (String) DataManager.getDataManager().getOrDefault(formatHolder, "format", getDefaultFormat());
		// Replace escaped % with placeholder
		format = format.replace("%%", "§§");
		// Sender name
		format = format.replace("%s", Utils.getName(sender));
		format = format.replace("%S", sender.getName());
		// Target name
		format = format.replace("%t", target);
		format = format.replace("%T", target);
		// Prefix
		String prefix = (String) DataManager.getDataManager().getOrDefault(formatHolder, "prefix", getDefaultPrefix());
		format = format.replace("%p", prefix);
		// Apply colors to halfway replaced String
		format = ChatColor.translateAlternateColorCodes('&', format);
		// Insert command and message
		format = format.replace("%c", command);
		format = format.replace("%m", message);
		// Convert placeholder back
		format = format.replace("§§", "%%");
		return format;
	}
	
	private static final String getDefaultFormat()
	{
		return "%s &7to %t %p: %m";
	}
	
	private static final String getDefaultPrefix()
	{
		return "&7";
	}
	
	@Command(hook = "migrate")
	public boolean migrate(CommandSender sender)
	{
		DataManager.getDataManager().migrateAll("Message");
		return true;
	}
}

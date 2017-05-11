package com.redstoner.modules.message;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;

import com.nemez.cmdmgr.Command;
import com.nemez.cmdmgr.Command.AsyncType;
import com.nemez.cmdmgr.CommandManager;
import com.redstoner.annotations.Version;
import com.redstoner.misc.BroadcastFilter;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;
import com.redstoner.modules.datamanager.DataManager;

import net.md_5.bungee.api.ChatColor;

@Version(major = 3, minor = 1, revision = 6, compatible = 3)
public class Message implements Module
{
	HashMap<CommandSender, CommandSender> replyTargets = new HashMap<CommandSender, CommandSender>();
	
	@Override
	public void postEnable()
	{
		CommandManager.registerCommand(getClass().getResourceAsStream("Message.cmd"), this, Main.plugin);
	}
	
	@Command(hook = "message", async = AsyncType.ALWAYS)
	public boolean message(CommandSender sender, String target, String message)
	{
		CommandSender p;
		if (target.equalsIgnoreCase("console"))
			p = Bukkit.getConsoleSender();
		else
			p = Bukkit.getPlayer(target);
		if (p == null)
		{
			Utils.sendErrorMessage(sender, null, "That player couldn't be found!");
			return true;
		}
		else
		{
			message = Utils.colorify(message, sender);
			spyBroadcast(sender, p, message, "/m", new BroadcastFilter()
			{
				@Override
				public boolean sendTo(CommandSender recipient)
				{
					return !(recipient.equals(sender) || recipient.equals(target));
				}
			});
			Utils.sendMessage(sender, "&6[&cme &6-> " + Utils.getName(p) + "&6] ", "§f" + message, '&');
			Utils.sendMessage(p, "&6[" + Utils.getName(sender) + " &6-> &cme&6] ", "§f" + message, '&');
			replyTargets.put(sender, p);
			replyTargets.put(p, sender);
		}
		return true;
	}
	
	@Command(hook = "reply", async = AsyncType.ALWAYS)
	public boolean reply(CommandSender sender, String message)
	{
		CommandSender target = replyTargets.get(sender);
		if (target == null || ((target instanceof OfflinePlayer) && !((OfflinePlayer) target).isOnline()))
		{
			Utils.sendErrorMessage(sender, null, "You don't have anyone to reply to!");
			return true;
		}
		else
		{
			message = Utils.colorify(message, sender);
			spyBroadcast(sender, target, message, "/m", new BroadcastFilter()
			{
				@Override
				public boolean sendTo(CommandSender recipient)
				{
					return !(recipient.equals(sender) || recipient.equals(target));
				}
			});
			Utils.sendMessage(sender, "&6[&cme &6-> " + Utils.getName(target) + "&6] ", "§f" + message, '&');
			Utils.sendMessage(target, "&6[" + Utils.getName(sender) + " &6-> &cme&6] ", "§f" + message, '&');
		}
		return true;
	}
	
	@Command(hook = "config_prefix_default")
	public boolean prefixDefault(CommandSender sender)
	{
		return prefix(sender, getDefaultPrefix());
	}
	
	@Command(hook = "config_prefix")
	public boolean prefix(CommandSender sender, String prefix)
	{
		Utils.sendMessage(sender, null, "Set your socialspy prefix to: " + prefix);
		DataManager.setData(sender, "prefix", prefix);
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
		Utils.sendMessage(sender, null, "Set your socialspy format to: " + format);
		DataManager.setData(sender, "format", format);
		return true;
	}
	
	@Command(hook = "stripcolor_on")
	public boolean stripcolorOn(CommandSender sender)
	{
		Utils.sendMessage(sender, null, "Enabled stripping colors!");
		DataManager.setData(sender, "stripcolor", true);
		return true;
	}
	
	@Command(hook = "stripcolor_off")
	public boolean stripcolorOff(CommandSender sender)
	{
		Utils.sendMessage(sender, null, "Disabled stripping colors!");
		DataManager.setData(sender, "stripcolor", false);
		return true;
	}
	
	@Command(hook = "stripcolor")
	public boolean stripcolor(CommandSender sender)
	{
		boolean b = (boolean) DataManager.getOrDefault(sender, "stripcolor", true);
		Utils.sendMessage(sender, null, (b ? "Disabled" : "Enabled") + " stripping colors!");
		DataManager.setData(sender, "stripcolor", !b);
		return true;
	}
	
	@Command(hook = "on")
	public boolean spyOn(CommandSender sender)
	{
		Utils.sendMessage(sender, null, "Enabled socialspy!");
		DataManager.setData(sender, "enabled", true);
		return true;
	}
	
	@Command(hook = "off")
	public boolean spyOff(CommandSender sender)
	{
		Utils.sendMessage(sender, null, "Disabled socialspy!");
		DataManager.setData(sender, "enabled", false);
		return true;
	}
	
	@Command(hook = "toggle")
	public boolean spyToggle(CommandSender sender)
	{
		boolean b = (boolean) DataManager.getOrDefault(sender, "enabled", false);
		Utils.sendMessage(sender, null, (b ? "Disabled" : "Enabled") + " socialspy!");
		DataManager.setData(sender, "enabled", !b);
		return true;
	}
	
	@Command(hook = "format_help")
	public boolean formatInfo(CommandSender sender)
	{
		Utils.sendModuleHeader(sender, "Socialspy");
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
		Utils.sendModuleHeader(sender, "Socialspy");
		JSONArray commands = (JSONArray) DataManager.getOrDefault(sender, "commands", getDefaultCommandList());
		if (commands == null || commands.size() == 0)
			Utils.sendErrorMessage(sender, "", "You are not listening to any commands!");
		else
		{
			Utils.sendMessage(sender, "", "You are listening to the following " + commands.size() + " commands:");
			Utils.sendMessage(sender, "", Arrays.toString(commands.toArray()));
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
		JSONArray commands = (JSONArray) DataManager.getOrDefault(sender, "commands", getDefaultCommandList());
		commands.add(command);
		DataManager.setData(sender, "commands", commands);
		Utils.sendMessage(sender, "&8[&2Socialspy&8]", "You are now spying on &e" + command, '&');
		return true;
	}
	
	@Command(hook = "commands_del")
	public boolean commands_del(CommandSender sender, String command)
	{
		JSONArray commands = (JSONArray) DataManager.getOrDefault(sender, "commands", getDefaultCommandList());
		commands.remove(command);
		DataManager.setData(sender, "commands", commands);
		Utils.sendMessage(sender, "&8[&2Socialspy&8]", "You are no longer spying on &e" + command, '&');
		return true;
	}
	
	public static void spyBroadcast(CommandSender sender, CommandSender target, String message, String command,
			BroadcastFilter filter)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if ((boolean) DataManager.getOrDefault(p, "enabled", false))
				if (p.hasPermission("utils.socialspy"))
				{
					if (filter == null || filter.sendTo(p))
						Utils.sendMessage(p, "", formatMessage(p, sender, target, message, command));
					Utils.sendMessage(p, "", formatMessage(p, sender, target, message, command));
				}
				else
					DataManager.setData(sender, "enabled", false);
		}
	}
	
	public static void spyBroadcast(CommandSender sender, String target, String message, String command,
			BroadcastFilter filter)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if ((boolean) DataManager.getOrDefault(p, "enabled", false))
				if (p.hasPermission("utils.socialspy"))
				{
					if (filter == null || filter.sendTo(p))
						Utils.sendMessage(p, "", formatMessage(p, sender, target, message, command));
				}
				else
					DataManager.setData(sender, "enabled", false);
		}
	}
	
	private static String formatMessage(CommandSender formatHolder, CommandSender sender, CommandSender target,
			String message, String command)
	{
		if ((boolean) DataManager.getOrDefault(formatHolder, "stripcolor", false))
			message = ChatColor.stripColor(message);
		String format = (String) DataManager.getOrDefault(formatHolder, "format", getDefaultFormat());
		// Replace escaped % with placeholder
		format = format.replace("%%", "§§");
		// Sender name
		format = format.replace("%s", Utils.getName(sender));
		format = format.replace("%S", sender.getName());
		// Target name
		format = format.replace("%t", Utils.getName(target));
		format = format.replace("%T", target.getName());
		// Prefix
		String prefix = (String) DataManager.getOrDefault(formatHolder, "prefix", getDefaultPrefix());
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
		if ((boolean) DataManager.getOrDefault(formatHolder, "stripcolor", false))
			message = ChatColor.stripColor(message);
		String format = (String) DataManager.getOrDefault(formatHolder, "format", getDefaultFormat());
		// Replace escaped % with placeholder
		format = format.replace("%%", "§§");
		// Sender name
		format = format.replace("%s", Utils.getName(sender));
		format = format.replace("%S", sender.getName());
		// Target name
		format = format.replace("%t", target);
		format = format.replace("%T", target);
		// Prefix
		String prefix = (String) DataManager.getOrDefault(formatHolder, "prefix", getDefaultPrefix());
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
}

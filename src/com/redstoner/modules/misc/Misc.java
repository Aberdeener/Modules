package com.redstoner.modules.misc;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

import net.nemez.chatapi.ChatAPI;
import net.nemez.chatapi.click.Message;

@Commands(CommandHolderType.String)
@AutoRegisterListener
@Version(major = 4, minor = 0, revision = 3, compatible = 4)
public class Misc implements Module, Listener
{
	private final String[] sudoBlacklist = new String[] {"(.*:)?e?sudo", "(.*:)?script.*", "(.*:)?stop",
			"(.*:)?modules", "(.*:)?sayn", "(.*:)?pex", "(.*:)?console_.*", "(.*:)?op", "(.*:)?login", "(.*:)?register",
			"(.*:)?.*pass"};
	JSONObject config;
	JSONArray unprotectedRegions;
	
	@EventHandler
	public void onFirstJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if (!player.hasPlayedBefore())
		{
			Utils.broadcast("", "\n§a§lPlease welcome §f" + player.getDisplayName() + " §a§lto Redstoner!\n", null);
			String[] message = new String[] {" \n \n \n \n \n \n \n \n \n \n \n \n ",
					"  &4Welcome to the Redstoner Server!", "  &6Before you ask us things, take a quick",
					"  &6look at &a&nredstoner.com/info", "  \n&6thank you and happy playing ;)", " \n \n"};
			getLogger().message(player, message);
		}
		Material spawnBlock = player.getLocation().getBlock().getType();
		if (spawnBlock == Material.PORTAL || spawnBlock == Material.ENDER_PORTAL)
		{
			getLogger().message(player, "&4Looks like you spawned in a portal... Let me help you out");
			getLogger().message(player, "&6You can use /back if you &nreally&6 want to go back");
			player.teleport(player.getWorld().getSpawnLocation());
		}
	}
	
	// Disables spectator teleportation
	@EventHandler(priority = EventPriority.LOWEST)
	public void onTeleport(PlayerTeleportEvent event)
	{
		Player player = event.getPlayer();
		if (!event.isCancelled() && event.getCause() == TeleportCause.SPECTATE && !player.hasPermission("utils.tp"))
		{
			event.setCancelled(true);
			getLogger().message(event.getPlayer(), true, "Spectator teleportation is disabled!");
		}
	}
	
	// Disables water and lava breaking stuff
	@EventHandler
	public void onLiquidFlow(BlockFromToEvent event)
	{
		Material m = event.getToBlock().getType();
		switch (m)
		{
			case AIR:
			case WATER:
			case STATIONARY_WATER:
			case LAVA:
			case STATIONARY_LAVA:
				return;
			default:
			{
				event.setCancelled(true);
			}
		}
	}
	
	@Command(hook = "tempadddef")
	public boolean tempAddDef(CommandSender sender, String user, String group)
	{
		return tempAdd(sender, user, group, "604800");
	}
	
	@Command(hook = "tempadd")
	public boolean tempAdd(CommandSender sender, String user, String group, String duration)
	{
		// Use it to make a proper duration output later. Too lazy rn.
		@SuppressWarnings("unused")
		int i = 0;
		try
		{
			i = Integer.valueOf(duration);
		}
		catch (NumberFormatException e)
		{
			getLogger().message(sender, true, "That is not a valid number!");
			return true;
		}
		Bukkit.dispatchCommand(sender, "pex user " + user + " group add " + group + " * " + duration);
		getLogger().message(sender, "Added to group " + group + "for " + duration + " seconds.");
		return true;
	}
	
	@Command(hook = "echo")
	public boolean echo(CommandSender sender, String text)
	{
		sender.sendMessage(ChatAPI.colorify(null, text));
		return true;
	}
	
	@Command(hook = "ping")
	public boolean ping(CommandSender sender)
	{
		if (sender instanceof Player)
		{
			int ping = getPing((Player) sender);
			getLogger().message(sender, "Your ping is " + ping + "ms.");
		}
		else
		{
			sender.sendMessage("Pong!");
		}
		return true;
	}
	
	@Command(hook = "ping2")
	public boolean ping(CommandSender sender, String password)
	{
		if (password.equals("pong"))
			if (sender instanceof Player)
			{
				int ping = getPing((Player) sender);
				getLogger().message(sender, new String[] {"Your ping is " + ping + "ms.", ping < 20
						? "&aThat's gr8 m8 r8 8/8"
						: (ping < 50 ? "F&eair enough you cunt!"
								: (ping < 100 ? "&eShite, but not shite enough."
										: "&cLooks like the server is about two months ahead of you. GET A NEW FRIGGIN' ISP ALREADY"))});
			}
			else
				getLogger().message(sender, true,
						"M8 you shitty cunt are not supposed to run this shit it's for players only!!!");
		else
			getLogger().message(sender, true, "&4WRONG PASSWORD, 4/3 ATTEMPTS FAILED! BAN COMMENCING!");
		return true;
	}
	
	public int getPing(Player player)
	{
		return ((CraftPlayer) player).getHandle().ping;
	}
	
	@Command(hook = "me")
	public boolean me(CommandSender sender, String text)
	{
		String name;
		if (sender instanceof Player)
			name = ((Player) sender).getDisplayName();
		else
			name = "§9" + sender.getName();
		text = ChatAPI.colorify(sender, text);
		Utils.broadcast(" §7- " + name + " §7⇦ ", text, null);
		return true;
	}
	
	@Command(hook = "say")
	public boolean say(CommandSender sender, String message)
	{
		String name = Utils.getName(sender);
		Utils.broadcast(" §7[§9" + name.replaceAll("[^0-9a-zA-Z§&\\[\\]]", "") + "§7]: ",
				"§r" + ChatAPI.colorify(null, message), null);
		return true;
	}
	
	@Command(hook = "sayn")
	public boolean say(CommandSender sender, String name, String message)
	{
		Utils.broadcast(" §7[§9" + ChatAPI.colorify(sender, name) + "§7]: ", "§r" + ChatAPI.colorify(null, message),
				null);
		return true;
	}
	
	@Command(hook = "sudo")
	public boolean sudo(CommandSender sender, String name, String command)
	{
		CommandSender target;
		if (name.equalsIgnoreCase("console"))
		{
			target = Bukkit.getConsoleSender();
		}
		else
			target = Bukkit.getPlayer(name);
		if (target == null)
		{
			getLogger().message(sender, false, "That player couldn't be found!");
			return true;
		}
		if (command.startsWith("/") || target.equals(Bukkit.getConsoleSender()))
		{
			String[] args = command.split(" ");
			for (String regex : sudoBlacklist)
			{
				if (args[0].matches((target.equals(Bukkit.getConsoleSender()) ? "" : "\\/") + regex))
				{
					getLogger().message(sender, true, "You can't sudo anyone into using that command!");
					return true;
				}
			}
			Bukkit.dispatchCommand(target, command.replaceFirst("/", ""));
			getLogger().message(sender, "Sudoed " + Utils.getName(target) + "&7 into running " + command);
		}
		else
		{
			((Player) target).chat(command);
			getLogger().message(sender, "Sudoed " + Utils.getName(target) + "&7 into saying " + command);
		}
		return true;
	}
	
	@Command(hook = "hasperm")
	public boolean hasPerm(CommandSender sender, boolean noformat, String name, String node)
	{
		Player p;
		if (name.contains("-"))
			try
			{
				p = Bukkit.getPlayer(UUID.fromString(name));
			}
			catch (Exception e)
			{
				if (noformat)
					sender.sendMessage("ERR: Invalid UUID");
				else
					getLogger().message(sender, "That UUID is not valid!");
				return true;
			}
		else
			p = Bukkit.getPlayer(name);
		if (p == null)
		{
			if (noformat)
			{
				Message m = new Message(sender, null);
				m.appendText("ERR: Invalid player");
				m.send();
			}
			else
			{
				getLogger().message(sender, "That player couldn't be found!");
			}
			return true;
		}
		
		if (noformat)
		{
			Message m = new Message(sender, null);
			m.appendText("" + p.hasPermission(node));
			m.send();
		}
		else
		{
			getLogger().message(sender, "" + p.hasPermission(node));
		}
		
		return true;
	}
	
	public boolean canBuild(Player player, Location location)
	{
		BlockBreakEvent event = new BlockBreakEvent(location.getBlock(), player);
		Bukkit.getPluginManager().callEvent(event);
		return event.isCancelled();
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command tempadd {\n" + 
				"    perm pex;\n" + 
				"    [string:user] [string:group] {\n" + 
				"        help Adds a user to a group for 1w.;\n" + 
				"        run tempadddef user group;\n" + 
				"    }\n" + 
				"    [string:user] [string:group] [string:duration] {\n" + 
				"        help Adds a user to a group for a specified duration.;\n" + 
				"        run tempadd user group duration;\n" + 
				"    }\n" + 
				"}\n" + 
				"command echo {\n" + 
				"    [string:text...] {\n" + 
				"        help Echoes back to you.;\n" + 
				"        run echo text;\n" + 
				"    }\n" + 
				"}\n" + 
				"command ping {\n" + 
				"    [empty] {\n" + 
				"        help Pongs :D;\n" + 
				"        run ping;\n" + 
				"    }\n" + 
				"    [string:password] {\n" + 
				"        help Pongs :D;\n" + 
				"        run ping2 password;\n" + 
				"    }\n" + 
				"}\n" + 
				"command me {\n" + 
				"    perm utils.me;\n" + 
				"    [string:text...] {\n" + 
				"        help /me's in chat.;\n" + 
				"        run me text;\n" + 
				"    }\n" + 
				"}\n" + 
				"command sudo {\n" + 
				"    perm utils.sudo;\n" + 
				"    [string:name] [string:command...] {\n" + 
				"        help Sudo'es another user (or console);\n" + 
				"        run sudo name command;\n" + 
				"    }\n" + 
				"}\n" + 
				"command hasperm {\n" +
				"    [flag:-f] [string:name] [string:node] {\n" + 
				"        perm utils.hasperm;\n" + 
				"        run hasperm -f name node;\n" +
				"        help Checks if a player has a given permission node or not. Returns \"true/false\" in chat. When -f is set, it returns it unformatted.;\n" + 
				"    }\n" + 
				"}" + 
				"command say {\n" +
				"    [string:message...] {\n" + 
				"        perm utils.say;\n" + 
				"        run say message;\n" +
				"        help A replacement for the default say command to make the format be more consistant.;\n" + 
				"    }\n" + 
				"}" + 
				"command sayn {\n" +
				"    [string:name] [string:message...] {\n" + 
				"        perm utils.sayn;\n" +
				"        type console;\n" +
				"        run sayn name message;\n" +
				"        help A replacement for the default say command to make the format be more consistant.;\n" + 
				"    }\n" + 
				"}";
	}
	// @format
}

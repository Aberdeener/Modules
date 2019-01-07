package com.redstoner.modules.misc;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;
import com.redstoner.modules.datamanager.DataManager;
import net.nemez.chatapi.ChatAPI;
import net.nemez.chatapi.click.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

@Commands (CommandHolderType.File)
@AutoRegisterListener
@Version (major = 5, minor = 1, revision = 0, compatible = 4)
public class Misc implements Module, Listener {
	private static final String[] SUDO_BLACKLIST = new String[] {
			"(.*:)?e?sudo",
			"(.*:)?script.*",
			"(.*:)?stop",
			"(.*:)?modules",
			"(.*:)?sayn",
			"(.*:)?pex",
			"(.*:)?console_.*",
			"(.*:)?op",
			"(.*:)?login",
			"(.*:)?register",
			"(.*:)?.*pass"
	};

	private static final String[] WELCOME_MSG = new String[] {
			"&4  Welcome to the Redstoner Server!\n",
			"&6  Before you ask us things, take a quick look at &a&nredstoner.com/info\n",
			"&6  Thank you and happy playing!)\n\n"
	};

	private static final PotionEffect NIGHT_VISION = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false);

	@EventHandler
	public void onFirstJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (!player.hasPlayedBefore()) {
			Utils.broadcast("", "\n§a§lPlease welcome §f" + player.getDisplayName() + " §a§lto Redstoner!\n", recipient -> !recipient.equals(player));

			getLogger().message(player, WELCOME_MSG);
		}

		Material spawnBlock = player.getLocation().getBlock().getType();

		if (spawnBlock == Material.END_PORTAL || spawnBlock == Material.NETHER_PORTAL) {
			getLogger().message(player, "&4Looks like you spawned in a portal... Let me help you out");
			getLogger().message(player, "&6You can use /back if you &nreally&6 want to go back");

			player.teleport(player.getWorld().getSpawnLocation());
		}
		
		event.setJoinMessage(null);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
	}

	// Disables spectator teleportation
	@EventHandler (priority = EventPriority.LOWEST)
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (!event.isCancelled() && event.getCause() == TeleportCause.SPECTATE && !player.hasPermission("utils.tp")) {
			getLogger().message(event.getPlayer(), true, "Spectator teleportation is disabled!");

			event.setCancelled(true);
		}
	}

	private static final Material[] LIQUID_FLOW_EXCEPTIONS = {
			Material.AIR,
			Material.CAVE_AIR,
			Material.VOID_AIR
	};

	// Disables water and lava breaking stuff
	@EventHandler
	public void onLiquidFlow(BlockFromToEvent event) {
		Block    toBlock = event.getToBlock();
		Material m       = toBlock.getType();

		for (Material exception : LIQUID_FLOW_EXCEPTIONS) {
			if (m == exception) return;
		}

		BlockData data = toBlock.getBlockData();

		if (!(data instanceof Waterlogged)) {
			event.setCancelled(true);
		}
	}

	@Command (hook = "tempadddef")
	public boolean tempAddDef(CommandSender sender, String user, String group) {
		return tempAdd(sender, user, group, "7d");
	}

	@Command (hook = "tempadd")
	public boolean tempAdd(CommandSender sender, String user, String group, String duration) {
		int parsed = MiscUtil.parseDuration(duration);

		if (parsed == -1) {
			getLogger().message(sender, true, "That is not a valid duration! (format: 1y2m3d4h5m6s)");
			return true;
		}

		Bukkit.dispatchCommand(sender, "pex user " + user + " group add " + group + " * " + parsed);
		getLogger().message(sender, "User " + user + " added to group " + group + " for " + duration);

		return true;
	}

	@Command (hook = "echo")
	public boolean echo(CommandSender sender, String text) {
		sender.sendMessage(ChatAPI.colorify(null, text));

		return true;
	}

	@Command (hook = "ping")
	public boolean ping(CommandSender sender) {
		if (sender instanceof Player) {
			int ping = MiscUtil.getPing((Player) sender);

			if (ping == -1) {
				getLogger().message(sender, "An error occured while getting your ping! Please message a staff member.");
			} else {
				getLogger().message(sender, "Your ping is " + ping + "ms.");
			}
		} else {
			sender.sendMessage("That's not how this works... <insert facepalm emoji here>");
		}

		return true;
	}

	@Command (hook = "ping_player")
	public boolean ping(CommandSender sender, String player) {
		Player playerObj = Bukkit.getPlayer(player);

		if (playerObj == null) {
			getLogger().message(sender, "That player is not online!");
			return true;
		}

		int ping = MiscUtil.getPing(playerObj);

		if (ping == -1) {
			getLogger().message(sender, "An error occured while getting that player's ping! Please message a staff member.");
		} else {
			getLogger().message(sender, ChatColor.GRAY + playerObj.getDisplayName() + ChatColor.GRAY + "'s ping is " + ping + "ms.");
		}

		return true;
	}

	@Command (hook = "sudo")
	public boolean sudo(CommandSender sender, String name, String command) {
		CommandSender target;

		if (name.equalsIgnoreCase("console")) {
			target = Bukkit.getConsoleSender();
		} else {
			target = Bukkit.getPlayer(name);
		}

		if (target == null) {
			getLogger().message(sender, false, "That player couldn't be found!");
			return true;
		}

		if (command.startsWith("/") || target.equals(Bukkit.getConsoleSender())) {
			String[] args = command.split(" ");

			for (String regex : SUDO_BLACKLIST) {
				if (args[0].matches((target.equals(Bukkit.getConsoleSender()) ? "" : "\\/") + regex)) {
					getLogger().message(sender, true, "You can't sudo anyone into using that command!");
					return true;
				}
			}

			Bukkit.dispatchCommand(target, command.replaceFirst("/", ""));
			getLogger().message(sender, "Sudoed " + Utils.getName(target) + "&7 into running " + command);
		} else {
			((Player) target).chat(command);
			getLogger().message(sender, "Sudoed " + Utils.getName(target) + "&7 into saying " + command);
		}

		return true;
	}

	@Command (hook = "hasperm")
	public boolean hasPerm(CommandSender sender, boolean noformat, String name, String node) {
		Player p;

		if (name.contains("-")) {
			try {
				p = Bukkit.getPlayer(UUID.fromString(name));
			} catch (Exception e) {
				if (noformat) {
					sender.sendMessage("ERR: Invalid UUID");
				} else {
					getLogger().message(sender, "That UUID is not valid!");
				}

				return true;
			}
		} else {
			p = Bukkit.getPlayer(name);
		}

		if (p == null) {
			if (noformat) {
				Message m = new Message(sender, null);
				m.appendText("ERR: Invalid player");
				m.send();
			} else {
				getLogger().message(sender, "That player couldn't be found!");
			}

			return true;
		}

		if (noformat) {
			Message m = new Message(sender, null);
			m.appendText("" + p.hasPermission(node));
			m.send();
		} else {
			getLogger().message(sender, ChatColor.GRAY + p.getDisplayName() + ChatColor.GRAY + (
					p.hasPermission(node)
					? " has that permission."
					: " does not have that permission."
			));
		}

		return true;
	}

	@Command (hook = "illuminate")
	public void illuminate(CommandSender sender) {
		Player player = (Player) sender;
		if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
			player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			getLogger().message(sender, "Night Vision Disabled.");
		} else {
			player.addPotionEffect(NIGHT_VISION, true);
			getLogger().message(sender, "Night Vision Enabled.");
		}
	}

	@Command (hook = "minecart")
	public void minecart(CommandSender sender) {
		String type = (String) DataManager.getOrDefault(sender, "minecart_default", "normal");
		minecartType(sender, type);
	}

	@Command (hook = "minecart_variation")
	public boolean minecartType(CommandSender sender, String type) {
		if (type.equals("help") || type.equals("h") || type.equals("?")) return false;

		Player p = (Player) sender;

		if (!MiscUtil.canBuild(p, p.getLocation())) {
			getLogger().message(sender, true, "You do not have permission to build here!");
			return true;
		}

		EntityType typeE = convertMinecartTypeString(type.toLowerCase());

		if (typeE != null) {
			p.getWorld().spawnEntity(p.getLocation(), typeE);
			getLogger().message(sender, "Minecart Spawned!");
		} else {
			getLogger().message(sender, true, "The type of Minecart you've requested does not exist.");
		}

		return false;
	}

	@Command (hook = "minecart_default")
	public boolean minecartDefault(CommandSender sender, String type) {
		EntityType typeE = convertMinecartTypeString(type.toLowerCase());

		if (type.equals("help") || type.equals("h") || type.equals("?")) return false;

		if (typeE != null) {
			DataManager.setData(sender, "minecart_default", type);
			getLogger().message(sender, "Set your default minecart to: " + type.toLowerCase());
		} else {
			getLogger().message(sender, true, "The type of Minecart you've requested does not exist.");
		}

		return true;
	}

	public EntityType convertMinecartTypeString(String type) {
		EntityType typeE = null;

		switch (type) {
			case "normal":
				typeE = EntityType.MINECART;
				break;
			case "chest":
				typeE = EntityType.MINECART_CHEST;
				break;
			case "furnace":
				typeE = EntityType.MINECART_FURNACE;
				break;
			case "hopper":
				typeE = EntityType.MINECART_HOPPER;
				break;
			case "tnt":
				typeE = EntityType.MINECART_TNT;
				break;
			case "command":
				typeE = EntityType.MINECART_COMMAND;
				break;
			case "spawner":
				typeE = EntityType.MINECART_MOB_SPAWNER;
				break;
		}

		return typeE;
	}
}
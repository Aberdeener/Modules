package com.redstoner.modules.adminchat;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.json.simple.JSONObject;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.BroadcastFilter;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

import net.nemez.chatapi.ChatAPI;

/**
 * AdminChat module. Allows staff to chat to other staff using /ac \<message\>
 * as well as a one char prefix or a toggle.
 * 
 * @author Pepich
 */
@Commands(CommandHolderType.File)
@AutoRegisterListener
@Version(major = 4, minor = 1, revision = 0, compatible = 4)
public class Adminchat implements Module, Listener {
	private static final char defaultKey = ',';
	private static final File keysLocation = new File(Main.plugin.getDataFolder(), "adminchat_keys.json");
	private ArrayList<UUID> actoggled;
	private static JSONObject keys;

	private static final BroadcastFilter AC_PERM_BROADCAST_FILTER = new BroadcastFilter() {
		@Override
		public boolean sendTo(CommandSender recipient) {
			return recipient.hasPermission("utils.ac");
		}
	};

	@Override
	public boolean onEnable() {
		keys = JsonManager.getObject(keysLocation);

		if (keys == null) {
			keys = new JSONObject();
			saveKeys();
		}

		actoggled = new ArrayList<>();
		return true;
	}

	@Command(hook = "ac_msg")
	public boolean acSay(CommandSender sender, String message) {
		String name;

		if (sender instanceof Player) name = ((Player) sender).getDisplayName();
		else name = sender.getName();

		Utils.broadcast("§8[§cAC§8] §9" + name + "§8: §b", ChatAPI.colorify(sender, message), AC_PERM_BROADCAST_FILTER);
		return true;
	}

	@Command(hook = "acn_msg")
	public boolean acnSay(CommandSender sender, String name, String message) {
		Utils.broadcast("§8[§cAC§8] §9" + name + "§8: §b", ChatAPI.colorify(sender, message), AC_PERM_BROADCAST_FILTER);
		return true;
	}

	/**
	 * Lets a Player toggle their AC toggle status to allow automatically sending
	 * chat messages to admin chat.
	 * 
	 * @param sender the issuer of the command.
	 * @param _void  ignored.
	 * @return true.
	 */
	@Command(hook = "act")
	public boolean acToggleCommand(CommandSender sender) {
		if (actoggled.contains(((Player) sender).getUniqueId())) {
			actoggled.remove(((Player) sender).getUniqueId());
			getLogger().message(sender, "ACT now §cdisabled");
		} else {
			actoggled.add(((Player) sender).getUniqueId());
			getLogger().message(sender, "ACT now §aenabled");
		}

		return true;
	}

	/**
	 * Lets a Player toggle their AC toggle status to allow automatically sending
	 * chat messages to admin chat.
	 * 
	 * @param sender the issuer of the command.
	 * @return true.
	 */
	@Command(hook = "act_on")
	public boolean acToggleOnCommand(CommandSender sender) {
		if (!actoggled.contains(((Player) sender).getUniqueId())) {
			actoggled.add(((Player) sender).getUniqueId());
			getLogger().message(sender, "ACT now §aenabled");
		} else {
			getLogger().message(sender, "ACT was already enabled");
		}

		return true;
	}

	/**
	 * Lets a Player toggle their AC toggle status to allow automatically sending chat messages to admin chat.
	 * 
	 * @param sender the issuer of the command.
	 * @return true.
	 */
	@Command(hook = "act_off")
	public boolean acToggleOffCommand(CommandSender sender) {
		if (actoggled.contains(((Player) sender).getUniqueId())) {
			actoggled.remove(((Player) sender).getUniqueId());
			getLogger().message(sender, "ACT now §cdisabled");
		} else {
			getLogger().message(sender, "ACT was already disabled");
		}
		
		return true;
	}

	/**
	 * Deals with chat events to allow for ackeys and actoggle.
	 * 
	 * @param event the chat event containing the player and the message.
	 */
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		
		if (!player.hasPermission("utils.ac")) return;
		
		if (event.getMessage().startsWith(getKey(player))) {
			event.setCancelled(true);
			acSay(event.getPlayer(), event.getMessage().replaceFirst(Pattern.quote(getKey(player)), ""));
		} else if (actoggled.contains(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
			acSay(event.getPlayer(), event.getMessage());
		}
	}

	/**
	 * Sets the ackey of a Player.
	 * 
	 * @param sender the issuer of the command.
	 * @param key    the key to be set. Set to NULL or "" to get your current key.
	 * @return true.
	 */
	@SuppressWarnings("unchecked")
	@Command(hook = "setackey")
	public boolean setAcKey(CommandSender sender, String key) {
		if (key.length() > 1) {
			getLogger().message(sender, true, "Could not set your key to §6" + key + " §7, it can be at most one char.");
			return true;
		}
		
		if (key == null || key.length() == 0) {
			getAcKey(sender);
			return true;
		}
		
		getLogger().message(sender, "Set your key to §6" + key);
		keys.put(((Player) sender).getUniqueId().toString(), key + "");
		
		saveKeys();
		return true;
	}

	/**
	 * This method will find the AdminChat key of any player.
	 * 
	 * @param player the player to get the key from.
	 * @return the key.
	 */
	public static String getKey(Player player) {
		String key = (String) keys.get(player.getUniqueId().toString());
		return (key == null ? "" + defaultKey : key);
	}

	/**
	 * Prints a Player's ackey to their chat.
	 * 
	 * @param sender the issuer of the command.
	 */
	public void getAcKey(CommandSender sender) {
		getLogger().message(sender, "Your current ackey is §6" + getKey((Player) sender));
	}

	/** Saves the keys. */
	private void saveKeys() {
		JsonManager.save(keys, keysLocation);
	}
}

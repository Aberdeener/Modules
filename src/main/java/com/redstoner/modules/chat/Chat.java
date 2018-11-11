package com.redstoner.modules.chat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.coremods.moduleLoader.ModuleLoader;
import com.redstoner.misc.BroadcastFilter;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;
import com.redstoner.modules.datamanager.DataManager;
import com.redstoner.modules.ignore.Ignore;

import net.nemez.chatapi.ChatAPI;

@Commands(CommandHolderType.File)
@AutoRegisterListener
@Version(major = 4, minor = 1, revision = 1, compatible = 4)
public class Chat implements Module, Listener {
	private final Map<String, String> defaults = new HashMap<>();
	private Set<UUID> chatonly = new HashSet<>();

	public Chat() {
		defaults.put("chat", " %n§7%c →§r %m");
		defaults.put("me", " §7- %n§7%c ⇦ %m");
		defaults.put("action", " §7- %n§7%c ⇦ %m");
		defaults.put("say", " §7[§9%n§7]%c:§r %m");
		defaults.put("shrug", " %n§7%c →§r %m ¯\\_(ツ)_/¯");
		defaults.put("print", "%m");
		defaults.put("%c", "(c)");
	}

	@Override
	public void firstLoad() {
		DataManager.setConfig("chat", defaults.get("chat"));
		DataManager.setConfig("me", defaults.get("me"));
		DataManager.setConfig("action", defaults.get("action"));
		DataManager.setConfig("say", defaults.get("say"));
		DataManager.setConfig("shrug", defaults.get("shrug"));
		DataManager.setConfig("print", defaults.get("print"));
		DataManager.setConfig("%c", defaults.get("%c"));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		event.setCancelled(true);
		broadcastFormatted("chat", player, message, event);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		chatonly.remove(event.getPlayer().getUniqueId());
	}

	@Command(hook = "me")
	public boolean me(CommandSender sender, String message) {
		broadcastFormatted("me", sender, message);
		return true;
	}

	@Command(hook = "chat")
	public boolean chat(CommandSender sender, String message) {
		broadcastFormatted("chat", sender, message);
		return true;
	}

	@Command(hook = "chatn")
	public boolean chatn(CommandSender sender, String name, String message) {
		broadcastFormatted("chat", sender, message, name);
		return true;
	}

	@Command(hook = "action")
	public boolean action(CommandSender sender, String message) {
		broadcastFormatted("action", sender, message);
		return true;
	}

	@Command(hook = "say")
	public boolean say(CommandSender sender, String message) {
		String name;
		
		if (sender instanceof Player) name = ((Player) sender).getName();
		else name = "§9CONSOLE";
		
		broadcastFormatted("say", sender, message, name);
		return true;
	}

	@Command(hook = "sayn")
	public boolean say(CommandSender sender, String name, String message) {
		broadcastFormatted("say", sender, message, name);
		return true;
	}

	@Command(hook = "shrug")
	public boolean shrug(CommandSender sender, String message) {
		broadcastFormatted("shrug", sender, message);
		return true;
	}

	@Command(hook = "shrugnoarg")
	public boolean shrug(CommandSender sender) {
		broadcastFormatted("shrug", sender, "");
		return true;
	}

	@Command(hook = "print")
	public boolean print(CommandSender sender, String message) {
		broadcastFormatted("print", sender, message);
		return true;
	}

	@Command(hook = "mute")
	public boolean mute(CommandSender sender, String player) {
		Player p = Bukkit.getPlayer(player);
		
		if (p == null) {
			getLogger().message(sender, true, "That player couldn't be found!");
			return true;
		}
		
		DataManager.setData(p, "muted", true);
		
		getLogger().message(sender, "Muted player &e" + Utils.getName(p) + "&7!");
		getLogger().message(p, "You have been &cmuted&7!");
		
		return true;
	}

	@Command(hook = "unmute")
	public boolean unmute(CommandSender sender, String player) {
		Player p = Bukkit.getPlayer(player);
		
		if (p == null) {
			getLogger().message(sender, true, "That player couldn't be found!");
			return true;
		}
		
		DataManager.setData(p, "muted", false);
		
		getLogger().message(sender, "Unmuted player &e" + Utils.getName(p) + "&7!");
		getLogger().message(p, "You have been &aunmuted&7!");
		
		return true;
	}

	@Command(hook = "chatonly")
	public void chatonly(CommandSender sender) {
		UUID uuid = ((Player) sender).getUniqueId();

		if (chatonly.contains(uuid)) {
			chatonly.remove(uuid);
			getLogger().message(sender, "You are no longer tagged with being only able to chat.");
		} else {
			chatonly.add(uuid);
			getLogger().message(sender, "You are now tagged with being only able to chat.");
		}
	}

	public boolean broadcastFormatted(String format, CommandSender sender, String message) {
		return broadcastFormatted(format, sender, message, Utils.getName(sender), null);
	}

	public boolean broadcastFormatted(String format, CommandSender sender, String message, String name) {
		return broadcastFormatted(format, sender, message, name, null);
	}

	public boolean broadcastFormatted(String format, CommandSender sender, String message, AsyncPlayerChatEvent event) {
		return broadcastFormatted(format, sender, message, Utils.getName(sender), event);
	}

	public boolean broadcastFormatted(String format, CommandSender sender, String message, String name, AsyncPlayerChatEvent event) {
		boolean isChatOnly = sender instanceof Player && chatonly.contains(((Player) sender).getUniqueId());
		
		if ((boolean) DataManager.getOrDefault(sender, "muted", false)) {
			getLogger().message(sender, true, "You have been muted!");
			getLogger().info(" &7User &e" + Utils.getName(sender) + " &7tried to &e" + format + " &7(&e" + message + "&7) while being &cmuted&7.");
			return false;
		}
		
		String raw = (String) DataManager.getConfigOrDefault(format, defaults.get(format));
		String formatted = raw.replace("%n", name).replace("%m", message).replace("%c", isChatOnly ? (String) DataManager.getConfigOrDefault("%c", defaults.get("%c")) : "");
		Utils.broadcast("", ChatAPI.colorify(sender, formatted), wrap(ModuleLoader.exists("Ignore") ? Ignore.getIgnoredBy(sender) : null, event));
		
		return true;
	}

	public BroadcastFilter wrap(BroadcastFilter filter, AsyncPlayerChatEvent event) {
		if (event == null) return filter;
		else return new BroadcastFilter() {
			@Override
			public boolean sendTo(CommandSender recipient) {
				if (recipient instanceof ConsoleCommandSender || filter == null) return true;
				return filter.sendTo(recipient) && event.getRecipients().contains(recipient);
			}
		};
	}
}

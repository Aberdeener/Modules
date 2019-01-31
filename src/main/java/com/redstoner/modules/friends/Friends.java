package com.redstoner.modules.friends;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.coremods.moduleLoader.ModuleLoader;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.Utils;
import com.redstoner.modules.CoreModule;
import com.redstoner.modules.Module;
import com.redstoner.modules.datamanager.DataManager;

import net.nemez.chatapi.click.Message;

@AutoRegisterListener
@Commands(CommandHolderType.File)
@Version(major = 5, minor = 1, revision = 0, compatible = 4)
public class Friends implements CoreModule, Listener {
	
	private static int GROUP_PREFIX_LENGETH = 6;
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		JSONArray friended_by = (JSONArray) DataManager.getOrDefault(player, "friended_by", new JSONArray());

		for (Object obj : friended_by) {
			UUID uuid = UUID.fromString((String) obj);
			Player p = Bukkit.getPlayer(uuid);

			if (p != null && p.canSee(player)) {
				getLogger().message(p, "Your friend &e" + player.getDisplayName() + "&7 just joined!");
				p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
			}
		}

		JSONArray notifications = (JSONArray) DataManager.getOrDefault(player, "scheduled_notifications", new JSONArray());
		for (Object obj : notifications)
			getLogger().message(player, (String) obj);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		JSONArray friended_by = (JSONArray) DataManager.getOrDefault(player, "friended_by", new JSONArray());

		for (Object obj : friended_by) {
			UUID uuid = UUID.fromString((String) obj);
			Player p = Bukkit.getPlayer(uuid);

			if (p != null && p.canSee(player)) {
				getLogger().message(p, "Your friend &e" + player.getDisplayName() + "&7 just left!");
				p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Command(hook = "add")
	public boolean add(CommandSender sender, String target) {
		if (target.equalsIgnoreCase("CONSOLE")) {
			getLogger().message(sender, true, "You can't add console to your friends!");
			return true;
		}

		OfflinePlayer p = Bukkit.getPlayer(target);
		
		if (p == null) p = Bukkit.getOfflinePlayer(target);		
		if (p == null || !p.hasPlayedBefore()) {
			getLogger().message(sender, true, "That player has never joined the server!");
			return true;
		}

		JSONArray friends = ((JSONArray) DataManager.getOrDefault(sender, "friends", new JSONArray()));

		if (friends.contains(p.getUniqueId().toString())) {
			getLogger().message(sender, true, "You are already friends with this person!");
			return true;
		}

		friends.add(p.getUniqueId().toString());
		DataManager.setData(sender, "friends", friends);
		DataManager.save(sender);
		
		JSONArray friended_by = ((JSONArray) DataManager.getOrDefault(p.getUniqueId().toString(), "friended_by", new JSONArray()));
		friended_by.add(getID(sender));
		
		DataManager.setData(p.getUniqueId().toString(), "friended_by", friended_by);
		DataManager.save(p.getUniqueId().toString());
		
		getLogger().message(sender, "You are now friends with &e" + p.getName() + "&7!");

		if (p instanceof Player)
			getLogger().message((Player) p, "&e" + Utils.getName(sender) + "&7 added you as a friend!");
		else {
			JSONArray notifications = (JSONArray) DataManager.getOrDefault(p.getUniqueId().toString(), "scheduled_notifications", new JSONArray());

			notifications.add("&e" + Utils.getName(sender) + "&7 added you as a friend!");
			notifications.remove("&e" + Utils.getName(sender) + "&7 removed you as a friend!");

			DataManager.setData(p.getUniqueId().toString(), "scheduled_notifications", notifications);
			DataManager.save(p.getUniqueId().toString());
		}

		return true;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Command(hook = "add_grouped")
	public boolean add_grouped(CommandSender sender, String target, String group) {
		if (target.equalsIgnoreCase("CONSOLE")) {
			getLogger().message(sender, true, "You can't add console to your friends!");
			return true;
		}

		OfflinePlayer p = Bukkit.getPlayer(target);

		if (p == null) p = Bukkit.getOfflinePlayer(target);
		if (p == null || !p.hasPlayedBefore()) {
			getLogger().message(sender, true, "That player has neevr joined the server!");
			return true;
		}

		JSONArray friends = ((JSONArray) DataManager.getOrDefault(sender, "group." + group, new JSONArray()));

		if (friends.contains(p.getUniqueId().toString())) {
			getLogger().message(sender, true, "This person already is part of that friendgroup!");
			return true;
		}

		friends.add(p.getUniqueId().toString());
		DataManager.setData(sender, "group." + group, friends);
		DataManager.save(sender);

		getLogger().message(sender, "&e" + p.getName() + "&7 is now part of the group &e" + group + "&7!");

		if (p instanceof Player)
			getLogger().message((Player) p, "&e" + Utils.getName(sender) + " &7added you to their friendgroup &e" + group + "&7!");
		else {
			JSONArray notifications = (JSONArray) DataManager.getOrDefault(p.getUniqueId().toString(), "scheduled_notifications", new JSONArray());

			notifications.add("&e" + Utils.getName(sender) + " &7added you to their friendgroup &e" + group + "&7!");
			notifications.remove("&e" + Utils.getName(sender) + " &7removed you from their friendgroup &e" + group + "&7!");

			DataManager.setData(p.getUniqueId().toString(), "scheduled_notifications", notifications);
			DataManager.save(p.getUniqueId().toString());
		}

		return true;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Command(hook = "del")
	public boolean del(CommandSender sender, String target) {
		if (target.equalsIgnoreCase("CONSOLE")) {
			getLogger().message(sender, true, "You can't have console as your friends!");
			return true;
		}

		OfflinePlayer p = Bukkit.getPlayer(target);

		if (p == null) p = Bukkit.getOfflinePlayer(target);
		if (p == null || !p.hasPlayedBefore()) {
			getLogger().message(sender, true, "That player couldn't be found!");
			return true;
		}

		JSONArray friends = ((JSONArray) DataManager.getOrDefault(sender, "friends", new JSONArray()));

		if (!friends.contains(p.getUniqueId().toString())) {
			getLogger().message(sender, true, "You are not friends with that player!");
			return true;
		}

		friends.remove(p.getUniqueId().toString());
		DataManager.setData(sender, "friends", friends);
		DataManager.save(sender);
		
		JSONArray friended_by = ((JSONArray) DataManager.getOrDefault(p.getUniqueId().toString(), "friended_by", new JSONArray()));
		friended_by.remove(getID(sender));
		
		DataManager.setData(p.getUniqueId().toString(), "friended_by", friended_by);
		DataManager.save(p.getUniqueId().toString());

		getLogger().message(sender, "You are no longer friends with &e" + p.getName() + "&7!");

		if (p instanceof Player)
			getLogger().message((Player) p, "&e" + Utils.getName(sender) + "&7 removed you as a friend!");
		else {
			JSONArray notifications = (JSONArray) DataManager.getOrDefault(p.getUniqueId().toString(), "scheduled_notifications", new JSONArray());

			notifications.add("&e" + Utils.getName(sender) + "&7 removed you as a friend!");
			notifications.remove("&e" + Utils.getName(sender) + "&7 added you as a friend!");

			DataManager.setData(p.getUniqueId().toString(), "scheduled_notifications", notifications);
			DataManager.save(p.getUniqueId().toString());
		}

		return true;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Command(hook = "del_grouped")
	public boolean del_grouped(CommandSender sender, String target, String group) {
		if (target.equalsIgnoreCase("CONSOLE")) {
			getLogger().message(sender, true, "You can't add console to your friends!");
			return true;
		}

		OfflinePlayer p = Bukkit.getPlayer(target);

		if (p == null) p = Bukkit.getOfflinePlayer(target);
		if (p == null || !p.hasPlayedBefore()) {
			getLogger().message(sender, true, "That player couldn't be found!");
			return true;
		}

		JSONArray friends = ((JSONArray) DataManager.getOrDefault(sender, "group." + group, new JSONArray()));

		if (!friends.contains(p.getUniqueId().toString())) {
			getLogger().message(sender, true, "This person isn't a part of that friendgroup!");
			return true;
		}

		friends.add(p.getUniqueId().toString());
		DataManager.setData(sender, "group." + group, friends);
		DataManager.save(sender);
		
		getLogger().message(sender, "&e" + p.getName() + "&7 is no longer a part of the group &e" + group + "&7!");

		if (p instanceof Player)
			getLogger().message((Player) p, "&e" + Utils.getName(sender) + " &7removed you from their friendgroup &e" + group + "&7!");
		else {
			JSONArray notifications = (JSONArray) DataManager.getOrDefault(p.getUniqueId().toString(), "scheduled_notifications", new JSONArray());

			notifications.add("&e" + Utils.getName(sender) + " &7removed you from their friendgroup &e" + group + "&7!");
			notifications.remove("&e" + Utils.getName(sender) + " &7added you to their friendgroup &e" + group + "&7!");

			DataManager.setData(p.getUniqueId().toString(), "scheduled_notifications", notifications);
			DataManager.save(p.getUniqueId().toString());
		}

		return true;
	}

	@Command(hook = "list")
	public boolean list(CommandSender sender) {
		JSONArray friends = (JSONArray) DataManager.getOrDefault(sender, "friends", new JSONArray());

		if (friends.size() == 0) {
			getLogger().message(sender, true, "You haven't added anyone to your friends list yet.");
			return true;
		}
		
		Message msg = new Message(sender, null)
					  .appendText(getLogger().getHeader() + "&7You have a total of &e" + friends.size() + "&7 friends:\n");
		
		for (int i = 0; i < friends.size(); i++) {
			UUID id = UUID.fromString((String) friends.get(i));
			Player p = Bukkit.getPlayer(id);
			if (p != null)
				msg.appendSuggestHover("&a" + p.getName(), "/msg " + p.getName() + " ", "&aONLINE\n&9"
			                         + p.getDisplayName() + "\n&7" + id.toString() + "\n\n&oClick to send a message.");
			else {
				String op = Bukkit.getOfflinePlayer(id).getName();
				msg.appendSuggestHover("&c" + op, "/mail send " + op + " ", "&cOFFLINE\n&7" + id.toString() + "\n\n&oClick to send a message.");
			}
			if (i != friends.size() - 1)
				msg.appendText("&7, ");
		}
		msg.send();

		return true;
	}

	@Command(hook = "list_group")
	public boolean list_group(CommandSender sender, String group) {
		JSONArray friends = (JSONArray) DataManager.getOrDefault(sender, "group." + group, new JSONArray());

		if (friends.size() == 0) {
			getLogger().message(sender, true, "You didn't add anyone to this group yet.");
			return true;
		}
		
		Message msg = new Message(sender, null)
					  .appendText(getLogger().getHeader() + "&7You have a total of &e" + friends.size() + "&7 friends added to this group[&e" + group + "&7]:");
		
		for (int i = 0; i < friends.size(); i++) {
			UUID id = UUID.fromString((String) friends.get(i));
			Player p = Bukkit.getPlayer(id);
			if (p != null)
				msg.appendSuggestHover("&a" + p.getName(), "/msg " + p.getName() + " ", "&aONLINE\n&9"
			                         + p.getDisplayName() + "\n&7" + id.toString() + "\n\n&oClick to send a message.");
			else {
				String op = Bukkit.getOfflinePlayer(id).getName();
				msg.appendSuggestHover("&c" + op, "/mail send " + op + " ", "&cOFFLINE\n&7" + id.toString() + "\n\n&oClick to send a message.");
			}
			if (i != friends.size() - 1)
				msg.appendText("&7, ");
		}
		msg.send();

		return true;		
	}

	private String getFriendsInGroup(CommandSender sender, String group) {
		JSONArray friends = (JSONArray) DataManager.getOrDefault(sender, group == null? "friends" : "group." + group, new JSONArray());
		
		if (friends.size() == 0)
			return "This group is Empty";
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < friends.size(); i++) {
			UUID id = UUID.fromString((String) friends.get(i));
			Player p = Bukkit.getPlayer(id);
			if (p != null)
				sb.append("&a" + p.getName());
			else
				sb.append("&c" + Bukkit.getOfflinePlayer(id).getName());
			
			if (i != friends.size() - 1)
				sb.append("&7, ");
		}
		
		return sb.toString();
	}
	
	private int getSizeOfGroup(CommandSender sender, String group) {
		return ((JSONArray) DataManager.getOrDefault(sender, group == null? "friends" : "group." + group, new JSONArray())).size();
	}
	
	@Command(hook = "list_groups")
	public boolean list_groups(CommandSender sender) {
		JSONObject raw = (JSONObject) DataManager.getOrDefault(sender, null, new JSONObject());
		Set<?> keys = raw.keySet();

		if (keys.size() == 0 || (keys.contains("friends") && keys.size() == 1)) {
			getLogger().message(sender, true, "You don't haven't created any friendgroups yet.");
			return true;
		}
		
		Message msg = new Message(sender, null)
				    .appendText(getLogger().getHeader() + "&7You have a total of &e" + keys.size() + "&7 friendgroups:\n");
		
		if (keys.contains("friends")) {
			msg.appendSendChatHover("&6friends", "/friends list",
					"&7Size: " + getSizeOfGroup(sender, null) + "\n\n" + getFriendsInGroup(sender, null))
			   .appendText("&7, ");
			keys.remove("friends");
		}
		
		Object[] keysArray= keys.toArray();
		for (int i = 0; i < keysArray.length; i++) {
			String group = ((String) keysArray[i]).substring(GROUP_PREFIX_LENGETH);
			msg.appendSendChatHover("&e" + group, "/friends list " + group,
					"&7Size: " + getSizeOfGroup(sender, group) + "\n\n" + getFriendsInGroup(sender, group));
			
			if (i != keysArray.length - 1)
				msg.appendText("&7, ");
		}
		
		msg.send();
		return true;
	}

	public static boolean isFriend(CommandSender player, CommandSender friend) {
		return isFriend(player, friend, null);
	}

	public static boolean isFriend(CommandSender player, CommandSender friend, String condition) {
		try {
			Module mod = ModuleLoader.getModule("Friends");
			Method m = mod.getClass().getDeclaredMethod("isFriend_", String.class);

			return (boolean) m.invoke(mod, player, friend, condition);
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isFriend_(CommandSender player, CommandSender friend, String group) {
		if (group == null) group = "friends";
		else if (!group.startsWith("group.")) group = "group." + group;

		JSONArray array = (JSONArray) DataManager.getOrDefault(player, group, new JSONArray());
		return array.contains(getID(friend));
	}

	private final String getID(CommandSender sender) {
		if (sender instanceof Player) return ((Player) sender).getUniqueId().toString();
		else return sender.getName();
	}
}

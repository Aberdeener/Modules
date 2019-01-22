package com.redstoner.modules.mail;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.coremods.moduleLoader.ModuleLoader;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;
import com.redstoner.modules.datamanager.DataManager;
import com.redstoner.modules.ignore.Ignore;

import net.nemez.chatapi.ChatAPI;

@AutoRegisterListener
@Commands(CommandHolderType.File)
@Version(major = 5, minor = 0, revision = 4, compatible = 4)
public class Mail implements Module, Listener
{
	
	private Map<String, List<Msg>> playerToMsg = new HashMap<>();
	private Map<Integer, Msg> idToMsg = new HashMap<>();
	private Map<String, Integer> lastSent = new HashMap<>();
	private Map<String, Map<Integer, Msg>> archives = new HashMap<>();
	private int lastID = 1;
	
	@Command(hook = "read")
	public void read(CommandSender sender) {
		
		String uuid = ((Player)sender).getUniqueId().toString();
		
		List<Msg> list = playerToMsg.get(uuid);
		if (list == null) {
			getLogger().message(sender, true, "You have no new messages.");
			return;
		}
		
		boolean shownTut = (boolean) DataManager.getOrDefault(sender, "showntut", false);
		
		if (!shownTut) {
			ChatAPI.createMessage(sender)
			.appendText(getLogger().getHeader() + "\n&7Looks like you've never read your"
					 + " messages before, here is a quick overview of it.\n\n&9There are 3 themes: ")
			.appendSendChatHover("&eLight", "/mail settings theme light",
					"&7[&cX&7][&9I&7][&8Reply&7] &afriend22&7: &fTest message!")
			.appendText("&7, ")
			.appendSendChatHover("&eDark", "/mail settings theme dark",
					"&8[&cX&8][&9I&8][&7Reply&8] &afriend22&7: &fTest message!")
			.appendText("&7, and ")
			.appendSendChatHover("&eGold", "/mail settings theme gold",
					"&6[&fX&6][&fI&6][&fReply&6] &afriend22&6: &fTest message!")
			.appendText("\n&7&o Hover to preview, click to select. Default is Dark.")
			
			.appendText("\n&9There are also 4 action sets: ")
			.appendSendChatHover("&eMinimal", "/mail settings actions minimal",
					"&8[&9I&8] &afriend22&7: &fTest message!")
			.appendText("&7, ")
			.appendSendChatHover("&eSimple", "/mail settings actions simple",
					"&8[&cX&8][&9I&8] &afriend22&7: &fTest message!")
			.appendText("&7, ")
			.appendSendChatHover("&eNormal", "/mail settings actions normal",
					"&8[&cX&8][&9I&8][&7Reply&8] &afriend22&7: &fTest message!")
			.appendText("&7, and ")
			.appendSendChatHover("&eFull", "/mail settings actions full",
					"&8[&cX&8][&9I&8][&7Reply&8][&6Archive&8] &afriend22&7: &fTest message!")
			.appendText("\n&7&o Hover to preview, click to select. Default is Normal")
			
			.appendText("\n&9You can see names in 2 ways: ")
			.appendSendChatHover("&eUsernames", "/mail settings names username",
					"&8[&cX&8][&9I&8][&7Reply&8] &afriend22&7: &fTest message!")
			.appendText("&7 or ")
			.appendSendChatHover("&eDisplayName", "/mail settings names displayname",
					"&8[&cX&8][&9I&8][&7Reply&8] &a&ofriendinator&r&7: &fTest message!")
			.appendText("\n&7&o Hover to preview, click to select. Default is Username")
			
			.appendText("\n\n&9What the things on the left mean:\n"
					  + "   &8[&cX&8]&7 - Deletes the message\n"
					  + "   &8[&9I&8]&7 - Shows some basic info when hovered over.\n"
					  + "   &8[&7Reply&8]&7 - Replys to the message; forms a message chain.\n"
					  + "   &8[&6Archive&8]&7 - Archives the message.\n\n"
					  + "For command help run ")
			.appendSendChatHover("&e/mail help", "/mail help", "Click to run")
			.appendText("&7 to get a list of commands.\n&2Now do ")
			.appendSendChatHover("&e/mail read","/mail","Click to run")
			.appendText("&2 again to start reading.")
			.send();
			DataManager.setData(sender, "showntut", true);
			DataManager.setData(sender, "theme", "DARK");
			DataManager.setData(sender, "actions", "NORMAL");
			DataManager.setData(sender, "names", "username");
			return;
		}
		
		String themeStr = (String) DataManager.getData(sender, "theme");
		String actions = (String) DataManager.getData(sender, "actions");
		String names = (String) DataManager.getData(sender, "names");
		Theme theme;
		
		if (!isValidTheme(themeStr)) {
			theme = Theme.DARK;	
			DataManager.setData(sender, "theme", theme);
			getLogger().message(sender, true, "Something went wrong with your theme setting. The setting has been reset.");
		}
		else
			theme = Theme.valueOf(themeStr);
		if (!isValidActions(actions)) {
			actions = "NORMAL";
			DataManager.setData(sender, "actions", actions);
			getLogger().message(sender, true, "Something went wrong with your action set setting. The setting has been reset.");
		}
		if (!isValidNames(names)) {
			names = "username";
			DataManager.setData(sender, "names", names);
			getLogger().message(sender, true, "Something went wrong with your names setting. The setting has been reset.");
		}
		
		boolean useDisplayName = names.equals("displayname");

		ChatAPI.send(sender, "\n" + getLogger().getHeader());
		ChatAPI.send(sender, "");
		switch (actions) {
			case "MINIMAL": 
				for (Msg m : list) {
					m.showMinimal(sender, theme, useDisplayName);
					m.read();
				}
				break;
			case "SIMPLE":
				for (Msg m : list) {
					m.showSimple(sender, theme, useDisplayName);
					m.read();
				}
				break;
			case "NORMAL":
				for (Msg m : list) { 
					m.showNormal(sender, theme, useDisplayName);
					m.read();
				}
				break;
			case "FULL":
				for (Msg m : list) { 
					m.showFull(sender, theme, useDisplayName);
					m.read();
				}
				break;
		}
		
		ChatAPI.createMessage(sender).appendText("\n" + theme.getClearColor() + "Do ")
		.appendSendChatHover(theme.getClearAccentColor() + "/mail clear","/mail clear","&cClick to Clear")
		.appendText(theme.getClearColor() + " to clear all of your messages.")
		.send();
		
		savePlayerMsgs(uuid);
		
	}
	
	@Command(hook = "delete")
	public void delete(CommandSender sender, int id) {
		Msg msg = idToMsg.get(id);
		String uuid = ((Player)sender).getUniqueId().toString();
		
		if (msg == null || !msg.getReciever().equals(uuid)) {
			getLogger().message(sender, true, "You have no messages with that ID[" + id + "].");
			return;
		}
		
		idToMsg.remove(id);
		
		List<Msg> list = playerToMsg.get(uuid);
		list.remove(msg);
		if (list.isEmpty())
			playerToMsg.remove(uuid);
		else
			playerToMsg.put(uuid, list);
		
		getLogger().message(sender, "Message Deleted. [ID=" + id + "]");
		savePlayerMsgs(uuid);
	}
	
	@Command(hook = "clear")
	public void clear(CommandSender sender) {
		String uuid = ((Player)sender).getUniqueId().toString();
		
		List<Msg> list = playerToMsg.get(uuid);
		
		if (list == null) {
			getLogger().message(sender, "Messages Cleared.");
			return;
		}
		
		playerToMsg.remove(uuid);
		for (Msg m : list)
			idToMsg.remove(m.getID());
		
		getLogger().message(sender, "Messages Cleared.");
		savePlayerMsgs(uuid);
	}
	
	@Command(hook = "send")
	@SuppressWarnings("deprecation")
	public void send(CommandSender sender, String player, String message) {
		OfflinePlayer op = Bukkit.getOfflinePlayer(player);
		if (!op.hasPlayedBefore()) 
			getLogger().message(sender, true, "&e" + player + "&7 has never joined the server.");
		else
			sendMessage(sender, op.getUniqueId().toString(), message, null);
	}
	
	@Command(hook = "reply")
	public void reply(CommandSender sender, int id, String message) {
		String uuid = ((Player)sender).getUniqueId().toString();
		
		Msg msg = idToMsg.get(id);
		
		if (msg == null || !msg.getReciever().equals(uuid)) {
			getLogger().message(sender, true, "You have no messages with that ID[" + id + "].");
			return;
		}
		
		sendMessage(sender, msg.getSender(), message, msg.getChain());
			
	}
	
	
	public void sendMessage(CommandSender sender, String r_uuid, String message, String chain) {
		
		OfflinePlayer r = Bukkit.getOfflinePlayer(UUID.fromString(r_uuid));
		
		//if (ModuleLoader.exists("Ignore") ? !Ignore.getIgnoredBy(sender).sendTo(r) : false)
		//{
		//	getLogger().message(sender, true, Utils.getName(r) + " has ignored you. Your message was not sent.");
		//	return;
		//}
		
		String s_uuid = ((Player)sender).getUniqueId().toString();
		
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));		
		
		int id = getNextID();
		
		Msg msg = new Msg(id, s_uuid, r_uuid, dateFormat.format(date), message, chain, false);
		
		idToMsg.put(id, msg);
		List<Msg> list = playerToMsg.getOrDefault(r_uuid, new ArrayList<>());
		list.add(msg);
		playerToMsg.put(r_uuid, list);
		lastSent.put(s_uuid, id);
		
		getLogger().message(sender, "Message Sent! [ID=" + id + "]");
		ChatAPI.createMessage(sender)
		.appendText(getLogger().getPrefix() + "Click ")
		.appendSendChatHover("&ehere", "/mail retract " + id, "&cClick to Retract")
		.appendText("&7 or do ")
		.appendSuggestHover("&e/mail retract [id]", "/mail retract", "Click to Copy")
		.appendText("&7 to retract it.")
		.send();
		
		
		if (r.isOnline()) {
			int num = playerToMsg.get(r_uuid).size();
			ChatAPI.createMessage((Player)r)
			.appendText(getLogger().getPrefix() + "&7You got &e" + num + "&7 message" + (num == 1? "" : "s") + "! Do ")
			.appendSendChatHover("&e/mail", "/mail", "Click to read your messages")
			.appendText("&7 to read them.")
			.send();
		}
		
		savePlayerMsgs(r_uuid);
	}
	
	@Command(hook = "retract")
	public void retract(CommandSender sender) {
		String uuid = ((Player)sender).getUniqueId().toString();
		int id = lastSent.get(uuid);
		if (id == 0)
			getLogger().message(sender, true, "You haven't sent a message this session.");
		else
			retract_id(sender, id);			
	}
	
	@Command(hook = "retract_id")
	public void retract_id(CommandSender sender, int id) {
		String uuid = ((Player)sender).getUniqueId().toString();
		Msg msg = idToMsg.get(id);
		
		if (msg == null || !msg.getSender().equals(uuid))
			getLogger().message(sender, true, "Unable to retract, that message doesn't exist. You either put in the wrong id, or it was deleted by the reciever.");
		else if (msg.isRead())
			getLogger().message(sender, true, "Unable to retract, message has already been read.");
		else {
			idToMsg.remove(id);
			
			String r_uuid = msg.getReciever();
			List<Msg> list = playerToMsg.get(r_uuid);
			list.remove(msg);
			if (list.isEmpty())
				playerToMsg.remove(r_uuid);
			else
				playerToMsg.put(r_uuid, list);
			getLogger().message(sender, "Message Retracted.");
			savePlayerMsgs(r_uuid);
		}
	}
	
	@Command(hook = "archive_read")
	public void archive_read(CommandSender sender) {
		String uuid = ((Player)sender).getUniqueId().toString();
		Map<Integer, Msg> list = archives.get(uuid);
		
		if (list == null) {
			getLogger().message(sender, true, "You have no archived messages.");
			return;
		}
		
		ChatAPI.send(sender, "&2--=[ Archived ]=--");
		ChatAPI.send(sender, "");
		
		String themeStr = (String) DataManager.getData(sender, "theme");
		String names = (String) DataManager.getData(sender, "names");
		Theme theme;
		
		if (!isValidTheme(themeStr)) {
			theme = Theme.DARK;	
			DataManager.setData(sender, "theme", theme);
			getLogger().message(sender, true, "Something went wrong with your theme setting. The setting has been reset.");
		}
		else
			theme = Theme.valueOf(themeStr);
		if (!isValidNames(names)) {
			names = "username";
			DataManager.setData(sender, "names", names);
			getLogger().message(sender, true, "Something went wrong with your names setting. The setting has been reset.");
		}
		
		for (Msg m : list.values()) {
			m.showArchived(sender, theme, names.equals("displayname"));
		}
		
		ChatAPI.send(sender, "\n&2These messages don't count towards your mail count.");
	}
	
	@Command(hook = "archive")
	public void archive(CommandSender sender, int id) {
		String uuid = ((Player)sender).getUniqueId().toString();
		Msg msg = idToMsg.get(id);
		
		if (msg == null || !msg.getReciever().equals(uuid)) {
			getLogger().message(sender, true, "You have no messages with that ID[" + id + "].");
			return;
		}
		
		msg.read();
		
		List<Msg> list = playerToMsg.get(uuid);
		list.remove(msg);
		if (list.isEmpty())
			playerToMsg.remove(uuid);
		else
			playerToMsg.put(uuid, list);
		idToMsg.remove(id);
		
		Map<Integer,Msg> list2 = archives.getOrDefault(uuid, new HashMap<>());
		int lastAID = list2.size();
		list2.put(lastAID, msg);
		archives.put(uuid, list2);
		
		msg.setID(list2.size() -1);
		getLogger().message(sender, "Message Archived. [ID=" + id + "] [ArchiveID=" + lastAID + "]");
		savePlayerMsgs(uuid);
		savePlayerArchive(uuid);
	}
	
	@Command(hook = "unarchive")
	public void unarchive(CommandSender sender, int id) {
		String uuid = ((Player)sender).getUniqueId().toString();
		Map<Integer,Msg> list = archives.get(uuid);
		
		if (id < 0 || list == null || !list.containsKey(id)) {
			getLogger().message(sender, true, "You have no archived messages with that ID.");
			return;
		}
		
		Msg msg = list.get(id);
		msg.setID(getNextID());
		
		list.remove(id);
		if (list.isEmpty())
			archives.remove(uuid);
		else
			archives.put(uuid, list);
		List<Msg> list2 = playerToMsg.getOrDefault(uuid, new ArrayList<>());
		list2.add(msg);
		playerToMsg.put(uuid, list2);
		idToMsg.put(msg.getID(), msg);
		
		getLogger().message(sender, "Message Unarchived. [ArchiveID=" + id + "] [ID=" + msg.getID() + "]");
		savePlayerMsgs(uuid);
		savePlayerArchive(uuid);
	}
	
	@Command(hook = "settings_theme")
	public void settings_theme(CommandSender sender) {
		String theme = (String) DataManager.getData(sender, "theme");
		if (!isValidTheme(theme)) {
			theme = Theme.DARK.toString();
			DataManager.setData(sender, "theme", theme);
		}
		getLogger().message(sender, "Your current theme is &e" + theme + "&7.");
		
	}
	@Command(hook = "settings_theme_set")
	public void settings_theme_set(CommandSender sender, String theme) {
		if (!isValidTheme(theme)) {
			ChatAPI.createMessage(sender)
			.appendText(getLogger().getPrefix(true) + "Invalid theme. Available themes are: ")
			.appendSendChatHover("&eLight", "/mail settings theme light", "Click to switch to &eLight")
			.appendText("&7, ")
			.appendSendChatHover("&eDark", "/mail settings theme dark", "Click to switch to &eDark")
			.appendText("&7, ")
			.appendSendChatHover("&eGold", "/mail settings theme gold", "Click to switch to &eGold")
			.send();
			
			return;
		}
		DataManager.setData(sender, "theme", theme.toUpperCase());
		getLogger().message(sender, "Theme set to &e" + theme + "&7.");
	}
	@Command(hook = "settings_actions")
	public void settings_actions(CommandSender sender) {
		String actions = (String) DataManager.getData(sender, "actions");
		if (!isValidActions(actions)) {
			actions = "NORMAL";
			DataManager.setData(sender, "actions", actions);
		}
		getLogger().message(sender, "Your current action set is &e" + actions.toLowerCase() + ".");
	}
	@Command(hook = "settings_actions_set")
	public void settings_actions_set(CommandSender sender, String actions) {
		if (!isValidActions(actions)) {
			
			ChatAPI.createMessage(sender)
			.appendText(getLogger().getPrefix(true) + "Invalid action set. Available options are:")
			.appendSendChatHover("\n &eMinimal", "/mail settings actions minimal", "Click to switch to &eMinimal")
			.appendText("&7: No click actions, ")
			.appendSendChatHover("\n &eSimple", "/mail settings actions simple", "Click to switch to &eSimple")
			.appendText("&7: Delete, ")
			.appendSendChatHover("\n &eNormal", "/mail settings actions normal", "Click to switch to &eNormal")
			.appendText("&7: : Delete and Reply.")
			.appendSendChatHover("\n &eFull", "/mail settings actions full", "Click to switch to &eFull")
			.appendText("&7: : Delete, Reply, and Archive.\nAll actions are still available with commands.")
			.send();
			
			return;
		}
		DataManager.setData(sender, "actions", actions.toUpperCase());
		getLogger().message(sender, "Action Set set to &e" + actions.toLowerCase() + "&7.");
	}
	@Command(hook = "settings_names")
	public void settings_names(CommandSender sender) {
		String names = (String) DataManager.getData(sender, "names");
		if (!isValidNames(names)) {
			names = "username";
			DataManager.setData(sender, "names", names);
		}
		getLogger().message(sender, "Your current names setting is &e" + names.toLowerCase() + ".");
	}
	@Command(hook = "settings_names_set")
	public void settings_names_set(CommandSender sender, String names) {
		if (!isValidNames(names)) {
			ChatAPI.createMessage(sender)
			.appendText(getLogger().getPrefix(true) + "Invalid name setting. Available options are: ")
			.appendSendChatHover("&eusername", "/mail settings names username", "Click to switch to &eusernames")
			.appendText(" &7or ")
			.appendSendChatHover("&edisplaynames", "/mail settings names displaynames", "Click to switch to &edisplaynames")
		    .send();
			
			return;
		}
		DataManager.setData(sender, "names", names.toLowerCase());
		getLogger().message(sender, "Names setting set to &e" + names.toLowerCase() + "&7.");
	}
	
	private boolean isValidTheme(String theme) {
		return theme != null && (theme.equalsIgnoreCase("light")
	                         || theme.equalsIgnoreCase("dark")
	                         || theme.equalsIgnoreCase("gold"));
	}
	
	private boolean isValidActions(String actions) {
		return actions != null && (actions.equalsIgnoreCase("minimal")
				               || actions.equalsIgnoreCase("simple")
				               || actions.equalsIgnoreCase("normal")
				               || actions.equalsIgnoreCase("full"));
	}
	private boolean isValidNames(String names) {
		return names != null && (names.equalsIgnoreCase("username")
				             || names.equalsIgnoreCase("displayname"));
	}
	
	private int getNextID() {
		if (lastID > 99999999)
			getLogger().warn("IDs have exceeded 8 digits, reload suggested.");
		return lastID++;
	}
	
	public void postEnable() {		
		File dir = new File(Main.plugin.getDataFolder(), "/mail");
		File[] filesInDir = dir.listFiles();
		if (filesInDir == null)
			return;
		for (File file : filesInDir) {
			if (file.isFile()) {
				JSONArray msgs = JsonManager.getArray(file);
				String uuid = file.getName().substring(0, file.getName().indexOf(".json"));
				
				List<Msg> list = new ArrayList<>();
				for (Object o : msgs) {
					Msg m = Msg.fromJSONObject((JSONObject)o, getNextID());
					list.add(m);
					idToMsg.put(m.getID(), m);
				}
				if (!list.isEmpty())
					playerToMsg.put(uuid, list);
			}
		}
		
		Bukkit.getOnlinePlayers().forEach((Player p) -> loadPlayer(p));
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		loadPlayer(event.getPlayer());
		
	}
	
	public void loadPlayer(Player p) {
		String uuid = p.getUniqueId().toString();
		
		File jsonfile = new File(Main.plugin.getDataFolder(), "/mail/archive/" + uuid + ".json");
		JSONArray fromArchive = JsonManager.getArray(jsonfile);
		Map<Integer,Msg> archives = new HashMap<>();
		
		if (fromArchive != null) {
			int i = 0;
			for (Object o : fromArchive) {
				Msg m = Msg.fromJSONObject(((JSONObject)o), i);
				archives.put(i, m);
				i++;
			}
			if (!archives.isEmpty())
				this.archives.put(uuid, archives);
		}
		
		
		List<Msg> list = playerToMsg.get(uuid);
		if (list != null && !list.isEmpty())
			ChatAPI.createMessage(p)
			.appendText(getLogger().getPrefix() + "&7You got &e" + list.size() +
					    "&7 message" + (list.size() == 1? "" : "s") + "! Do ")
			.appendSendChatHover("&e/mail", "/mail", "Click to read your messages")
			.appendText("&7 to read them.")
			.send();
	}
	
	@SuppressWarnings("unchecked")
	public void savePlayerMsgs(String uuid) {
		
		File jsonfile = new File(Main.plugin.getDataFolder(), "/mail/" + uuid + ".json");
				
		List<Msg> messages = playerToMsg.getOrDefault(uuid, new ArrayList<>());
		
		JSONArray m_array = new JSONArray();
		
		for (Msg m : messages)
			m_array.add(m.toJSONObject());
		
		JsonManager.save(m_array, jsonfile);
	}
	
	@SuppressWarnings("unchecked")
	public void savePlayerArchive(String uuid) {
		
		File jsonfile = new File(Main.plugin.getDataFolder(), "/mail/archive/" + uuid + ".json");
				
		Map<Integer,Msg> arch = archives.getOrDefault(uuid, new HashMap<>());
		
		JSONArray a_array = new JSONArray();
		
		for (Msg m : arch.values())
			a_array.add(m.toJSONObject());
		
		JsonManager.save(a_array, jsonfile);
	}
}

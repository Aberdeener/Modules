package com.redstoner.modules.chatalias;

import java.io.File;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.json.simple.JSONObject;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.modules.Module;

import net.nemez.chatapi.ChatAPI;

@Commands(CommandHolderType.File)
@AutoRegisterListener
@Version(major = 5, minor = 0, revision = 0, compatible = 4)
public class Chatalias implements Module, Listener {
	private final String[] commands = new String[] { "e?r", "e?m .+?", "e?t", "e?w", "e?msg .+?", "e?message .+?", "e?whisper .+?", "e?me", "cgsay", "ac", "bc",
			"say", "sayn .+?", "chat", "shrug", "action", "speak", "chatn", "speakn" };
	private JSONObject aliases = new JSONObject();

	@Override
	public boolean onEnable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			loadAliases(p.getUniqueId());
		}
		
		return true;
	}

	@Override
	public void onDisable() {
		for (Object key : aliases.keySet()) {
			UUID uuid = UUID.fromString((String) key);
			saveAliases(uuid);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		loadAliases(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		aliases.remove(event.getPlayer().getUniqueId().toString());
	}

	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		if (!aliases.containsKey(uuid.toString())) {
			loadAliases(player.getUniqueId());

			if (!aliases.containsKey(uuid.toString())) return;
		}

		JSONObject playerAliases = (JSONObject) aliases.get(uuid.toString());
		boolean changed = false;

		for (Object key : playerAliases.keySet()) {
			String keyword = (String) key;
			String replacement = (String) playerAliases.get(key);

			if (keyword.startsWith("RND;")) {
				keyword = keyword.replace("RND;", "");
				String[] results = replacement.split(" \\|\\| ");

				int rand = ThreadLocalRandom.current().nextInt(0, results.length);

				replacement = results[rand];
			}

			if (keyword.startsWith("R: ")) {
				keyword = keyword.replace("R: ", "");
				event.setMessage(event.getMessage().replaceAll(keyword, replacement));
			} else {
				if (keyword.startsWith("N: ")) keyword = keyword.replace("N: ", "");
				else {
					changed = true;
					playerAliases.put("N: " + key, replacement);
				}

				event.setMessage(event.getMessage().replace(keyword, replacement));
			}

			int maxLength;

			try {
				maxLength = Integer.valueOf(getPermissionContent(player, "utils.alias.length."));
			} catch (NumberFormatException e) {
				maxLength = 255;
			}

			if (event.getMessage().length() > maxLength) {
				getLogger().message(player, true, "The generated message is too long!");
				event.setCancelled(true);
				return;
			}
		}

		event.setMessage(ChatAPI.colorify(event.getPlayer(), event.getMessage()));

		if (changed) saveAliases(uuid);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled()) return;

		boolean listening = false;
		String command = "";

		for (String s : commands) {
			command = "^\\/(.*:)?" + s + " ";

			if (event.getMessage().matches(command + ".*")) {
				listening = true;
				break;
			}
		}

		if (!listening) return;

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		JSONObject playerAliases = (JSONObject) aliases.get(uuid.toString());
		String temp = event.getMessage().replaceAll(command, "");

		command = event.getMessage().replaceAll(Pattern.quote(temp) + "$", "");
		event.setMessage(event.getMessage().replaceFirst(Pattern.quote(command), ""));

		for (Object key : playerAliases.keySet()) {
			String keyword = (String) key;
			String replacement = (String) playerAliases.get(key);

			if (keyword.startsWith("RND;")) {
				keyword = keyword.replace("RND;", "");
				String[] results = replacement.split(" \\|\\| ");

				int rand = ThreadLocalRandom.current().nextInt(0, results.length);

				replacement = results[rand];
			}

			if (keyword.startsWith("R: ")) {
				keyword = keyword.replace("R: ", "");
				event.setMessage(event.getMessage().replaceAll(keyword, replacement));
			} else {
				if (keyword.startsWith("N: ")) keyword = keyword.replace("N: ", "");
				event.setMessage(event.getMessage().replace(keyword, replacement));
			}

			int maxLength;

			try {
				maxLength = Integer.valueOf(getPermissionContent(player, "utils.alias.length."));
			} catch (NumberFormatException e) {
				maxLength = 255;
			}

			if (event.getMessage().length() > maxLength) {
				getLogger().message(player, true, "The generated message is too long!");
				event.setCancelled(true);

				return;
			}
		}

		event.setMessage(command + event.getMessage());
	}

	@SuppressWarnings("unchecked")
	@Command(hook = "addalias")
	public boolean addAlias(CommandSender sender, boolean regex, boolean random, String keyword, String replacement) {
		if (regex && keyword.equals(".*")) {
			getLogger().message(sender, true, "You may not define the wildcard regex as an alias.");
			return true;
		}

		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		JSONObject data = (JSONObject) aliases.get(uuid.toString());
		keyword = (random ? "RND;" : "") + (regex ? "R: " : "N: ") + keyword;

		if (!data.containsKey(keyword)) {
			int maxAmount;

			try {
				maxAmount = Integer.valueOf(getPermissionContent(player, "utils.alias.amount."));
			} catch (NumberFormatException e) {
				maxAmount = 25;
			}

			if (data.size() == maxAmount) {
				getLogger().message(sender, true, "You already reached your maximum of aliases!");
				return true;
			}
		}

		data.put(keyword, replacement);

		if (sender.hasPermission("essentials.chat.color")) {
			getLogger().message(sender, "Successfully created alias " + keyword.substring(random ? 7 : 3) + " §7-> " + replacement + " §7for you.");
		} else {
			getLogger().message(sender, "Successfully created alias " + keyword.substring(random ? 7 : 3) + " §7-> " + replacement + " §7for you.");
		}

		saveAliases(uuid);
		return true;
	}

	@Command(hook = "delalias")
	public boolean delAlias(CommandSender sender, boolean regex, boolean random, String keyword) {
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		JSONObject data = (JSONObject) aliases.get(uuid.toString());

		keyword = (random ? "RND;" : "") + (regex ? "R: " : "N: ") + keyword;

		if (data.remove(keyword) != null) {
			getLogger().message(sender, "Successfully removed the alias!");
			saveAliases(uuid);

			return true;
		} else {
			getLogger().message(sender, true, "That alias doesn't exist! Hint: regex/no regex and random/not random does matter for this.");
			return true;
		}
	}

	@Command(hook = "listaliases")
	public boolean listAliases(CommandSender sender) {
		ChatAPI.createMessage(sender).appendText(getLogger().getHeader()).send();
		
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		JSONObject data = (JSONObject) aliases.get(uuid.toString());

		for (Object key : data.keySet()) {
			String d_key = (String) key;
			d_key = d_key.replace("RND;N:", "RND:").replace("RND;R:", "R-RND:");
			
			ChatAPI.createMessage(sender).appendText(d_key + " §7-> " + data.get(key)).send();
		}

		return true;
	}

	private String getPermissionContent(Player player, String permnode) {
		Set<PermissionAttachmentInfo> perms = player.getEffectivePermissions();

		for (PermissionAttachmentInfo perm : perms) if (perm.getPermission().toString().startsWith(permnode)) return perm.getPermission().replace(permnode, "");
		return null;
	}

	@SuppressWarnings("unchecked")
	private void loadAliases(UUID uuid) {
		JSONObject defaults = new JSONObject();
		defaults.put("dataFormat", "v2");

		JSONObject data = new JSONObject();
		data.put("N: ./", "/");

		defaults.put("data", data);

		JSONObject playerAliases = JsonManager.getObject(new File(Main.plugin.getDataFolder(), "aliases/" + uuid.toString() + ".json"));

		if (playerAliases == null) {
			playerAliases = defaults;
		}

		String dataFormat = (String) playerAliases.get("dataFormat");
		
		if (dataFormat == null) {
			JSONObject temp = new JSONObject();
			temp.put("dataFormat", "v2");

			JSONObject tempAliases = new JSONObject();

			for (Object key : playerAliases.keySet()) {
				tempAliases.put("N: " + key, playerAliases.get(key));
			}

			temp.put("data", tempAliases);
			aliases.put(uuid.toString(), temp.get("data"));

		} else if (dataFormat.equals("v2")) {
			aliases.put(uuid.toString(), playerAliases.get("data"));
		} else if (dataFormat.equals("v1")) {
			aliases.put(uuid.toString(), playerAliases.get("data"));
			saveAliases(uuid);
		} else {
			getLogger().error("Unknown data format for alias set of player " + uuid.toString());
			aliases.put(uuid.toString(), ((JSONObject) defaults.get("data")).clone());
			saveAliases(uuid);
		}
	}

	@SuppressWarnings("unchecked")
	private void saveAliases(UUID uuid) {
		JSONObject temp = new JSONObject();
		
		temp.put("dataFormat", "v2");
		temp.put("data", aliases.get(uuid.toString()));
		
		JsonManager.save(temp, new File(Main.plugin.getDataFolder(), "aliases/" + uuid.toString() + ".json"));
	}
}

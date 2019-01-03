package com.redstoner.modules.blockplacemods;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.Main;
import com.redstoner.modules.Module;
import com.redstoner.modules.blockplacemods.mods.ModBetterDirectional;
import com.redstoner.modules.blockplacemods.mods.ModCauldron;
import com.redstoner.modules.blockplacemods.mods.ModSlab;
import com.redstoner.modules.datamanager.DataManager;
import net.nemez.chatapi.click.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Commands (CommandHolderType.File)
@AutoRegisterListener
@Version (major = 4, minor = 1, revision = 1, compatible = 4)
public class BlockPlaceMods implements Module, Listener {
	private static final Map<String, BlockPlaceMod> mods        = new HashMap<>();
	private static final List<BlockPlaceMod>        enabledMods = new ArrayList<>();

	private final BlockPlaceMod[] modsToRegister = {
			new ModCauldron(),
			new ModSlab(),
			new ModBetterDirectional("Observer", Material.OBSERVER, "observers", false),
			new ModBetterDirectional("Piston", Material.PISTON, "pistons", false),
			};

	@Override
	public boolean onEnable() {
		for (BlockPlaceMod mod : modsToRegister) {
			mods.put(mod.name.toLowerCase(), mod);

			for (String alias : mod.aliases) {
				mods.put(alias.toLowerCase(), mod);
			}

			if (mod.onEnable()) {
				enabledMods.add(mod);
				Bukkit.getPluginManager().registerEvents(mod, Main.plugin);
			} else {
				getLogger().warn("Block place mod failed to enable, see any errors above!");
			}
		}

		return true;
	}

	@Override
	public void onDisable() {
		for (BlockPlaceMod mod : enabledMods) {
			mod.onDisable();
			HandlerList.unregisterAll(mod);
		}

		enabledMods.clear();
		mods.clear();
	}

	@SuppressWarnings("incomplete-switch")
	@Command (async = Command.AsyncType.ALWAYS, hook = "list_mods")
	public void listMods(CommandSender sender) {
		Player player = (Player) sender;
		String uuid   = player.getUniqueId().toString();

		Message msg = new Message(sender, sender);

		msg.appendText(ChatColor.DARK_GREEN + "--==[BlockPlaceMods]==--\n");
		msg.appendText(ChatColor.GRAY + "TIP: Hover over the following messages to see a description :)\n\n");

		for (BlockPlaceMod mod : modsToRegister) {

			msg.appendTextHover(
					ChatColor.DARK_PURPLE + "["
					+ ChatColor.DARK_BLUE + mod.name
					+ ChatColor.DARK_PURPLE + "]",

					ChatColor.GREEN + mod.description
			);

			msg.appendText(" ");

			boolean enabled = (boolean) DataManager.getOrDefault(uuid, "BlockPlaceMods", mod.name, mod.enabledByDefault);

			msg.appendTextHover(
					enabledMods.contains(mod) ? ChatColor.GREEN + "Loaded" : ChatColor.DARK_RED + "Not loaded",
					ChatColor.GRAY + (enabledMods.contains(mod) ? "The mod is working fine." : "Something is wrong!")
			);

			msg.appendText(ChatColor.RESET + ", ");
			msg.appendTextHover(
					enabled ? ChatColor.GREEN + "Enabled" : ChatColor.DARK_RED + "Disabled",
					ChatColor.GRAY + (enabled ? "You have this mod enabled." : "You have disabled this mod!")
			);

			if (mod.type != ModType.STATELESS) {
				Object state = DataManager.getOrDefault(uuid, "BlockPlaceMods", mod.name + "_state", null);

				if (state != null) {
					msg.appendText(ChatColor.AQUA + " -> ");

					switch (mod.type) {
						case STRING:
							msg.appendTextHover(ChatColor.GOLD + state.toString(), "String value - " + mod.typeDescription);
							break;
						case INTEGER:
						case UNSIGNED_INTEGER:
							msg.appendTextHover(ChatColor.DARK_GREEN + state.toString(), "Integer value - " + mod.typeDescription);
							break;
						case REDSTONE_LEVEL:
							msg.appendTextHover(ChatColor.RED + state.toString(), "Redstone level - " + mod.typeDescription);
					}

					msg.appendTextHover(
							enabled ? ChatColor.GREEN + "Enabled" : ChatColor.DARK_RED + "Disabled",
							ChatColor.GRAY + (enabled ? "You have this mod enabled." : "You have disabled this mod!")
					);
				}
			}
		}

		msg.send();
	}

	@Command (async = Command.AsyncType.ALWAYS, hook = "reset_mod")
	public void resetMod(CommandSender sender, String mod) {
		BlockPlaceMod bpm = mods.get(mod.toLowerCase());
		Message       msg = new Message(sender, sender);

		msg.appendText(ChatColor.DARK_GREEN + "[BlockPlaceMods] ");

		if (bpm == null) {
			msg.appendText(ChatColor.DARK_RED + "That mod does not exist!");
		} else {
			Player player = (Player) sender;
			DataManager.removeData(player.getUniqueId().toString(), "BlockPlaceMods", bpm.name);
			DataManager.removeData(player.getUniqueId().toString(), "BlockPlaceMods", bpm.name + "_state");

			msg.appendText(ChatColor.GREEN + "Successfully reset the settings for: " + ChatColor.DARK_PURPLE + bpm.name);
		}

		msg.send();
	}

	@Command (async = Command.AsyncType.ALWAYS, hook = "toggle_mod")
	public void toggleMod(CommandSender sender, String mod) {
		BlockPlaceMod bpm = mods.get(mod.toLowerCase());
		Message       msg = new Message(sender, sender);

		msg.appendText(ChatColor.DARK_GREEN + "[BlockPlaceMods] ");

		if (bpm == null) {
			msg.appendText(ChatColor.DARK_RED + "That mod does not exist!");
		} else {
			Player player = (Player) sender;
			String uuid   = player.getUniqueId().toString();

			boolean current = (boolean) DataManager.getOrDefault(uuid, "BlockPlaceMods", bpm.name, bpm.enabledByDefault);
			DataManager.setData(uuid, "BlockPlaceMods", bpm.name, !current);

			msg.appendText(
					ChatColor.GREEN + "The " + ChatColor.DARK_PURPLE + bpm.name
					+ ChatColor.GREEN + " mod has been "
					+ (current ? ChatColor.RED + "Disabled!" : ChatColor.GREEN + "Enabled!")
			);
		}

		msg.send();
	}

	@Command (async = Command.AsyncType.ALWAYS, hook = "set_mod_value")
	public void setModValue(CommandSender sender, String mod, String value) {
		BlockPlaceMod bpm = mods.get(mod.toLowerCase());
		Message       msg = new Message(sender, sender);

		msg.appendText(ChatColor.DARK_GREEN + "[BlockPlaceMods] ");

		if (bpm == null) {
			msg.appendText(ChatColor.DARK_RED + "That mod does not exist!");
		} else {
			Player player = (Player) sender;
			String uuid   = player.getUniqueId().toString();

			switch (bpm.type) {
				case STATELESS:
					msg.appendText(ChatColor.DARK_RED + "You cannot change the value of a stateless mod!");
					break;
				case STRING:
					DataManager.setData(uuid, "BlockPlaceMods", bpm.name + "_state", value);

					msg.appendText(
							ChatColor.GREEN + "Changed the value of "
							+ ChatColor.DARK_PURPLE + bpm.name + ChatColor.GREEN
							+ " to: " + ChatColor.GRAY + value
					);

					break;
				case INTEGER:
					try {
						DataManager.setData(uuid, "BlockPlaceMods", bpm.name + "_state", Integer.parseInt(value));
					} catch (NumberFormatException e) {
						msg.appendText(ChatColor.RED + "The specified value must be an integer!");
						break;
					}

					msg.appendText(
							ChatColor.GREEN + "Changed the value of "
							+ ChatColor.DARK_PURPLE + bpm.name + ChatColor.GREEN
							+ " to: " + ChatColor.GRAY + value
					);

					break;
				case UNSIGNED_INTEGER:
					try {
						int val = Integer.parseInt(value);

						if (val < 0) {
							msg.appendText(ChatColor.RED + "The specified value must be a positive integer!");
							break;
						}

						DataManager.setData(uuid, "BlockPlaceMods", bpm.name + "_state", val);
					} catch (NumberFormatException e) {
						msg.appendText(ChatColor.RED + "The specified value must be a positive integer!");
						break;
					}

					msg.appendText(
							ChatColor.GREEN + "Changed the value of "
							+ ChatColor.DARK_PURPLE + bpm.name + ChatColor.GREEN
							+ " to: " + ChatColor.GRAY + value
					);

					break;
				case REDSTONE_LEVEL:
					try {
						int val = Integer.parseInt(value);

						if (val < 1 || val > 15) {
							msg.appendText(ChatColor.RED + "The specified value must be an integer between 0 (exclusive) and 15 (inclusive)!");
							break;
						}

						DataManager.setData(uuid, "BlockPlaceMods", bpm.name + "_state", val);
					} catch (NumberFormatException e) {
						msg.appendText(ChatColor.RED + "The specified value must be an integer between 0 (exclusive) and 15 (inclusive)!");
						break;
					}

					msg.appendText(
							ChatColor.GREEN + "Changed the value of "
							+ ChatColor.DARK_PURPLE + bpm.name + ChatColor.GREEN
							+ " to: " + ChatColor.GRAY + value
					);

					break;
			}
		}

		msg.send();
	}
}

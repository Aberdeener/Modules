package com.redstoner.modules.blockplacemods;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.coremods.moduleLoader.ModuleLoader;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.Main;
import com.redstoner.modules.Module;
import com.redstoner.modules.blockplacemods.mods.ModBetterDirectional;
import com.redstoner.modules.blockplacemods.mods.ModCauldron;
import com.redstoner.modules.blockplacemods.mods.ModSlab;
import com.redstoner.modules.datamanager.DataManager;
import net.nemez.chatapi.click.Message;
import org.bukkit.Bukkit;
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
				getLogger().warn("Failed to enable the mod, &e" + mod.name + "&7!");
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
	
	@Command (async = Command.AsyncType.ALWAYS, hook = "list_mods")
	public void listMods(CommandSender sender) {
		Player player = (Player) sender;
		String uuid   = player.getUniqueId().toString();

		Message msg = new Message(sender, sender);

		msg.appendText(getLogger().getHeader() + "\n");

		int curMod = 1;
		for (BlockPlaceMod mod : modsToRegister) {
			
			boolean enabled = (boolean) DataManager.getOrDefault(uuid, "BlockPlaceMods", mod.name, mod.enabledByDefault);
			boolean loaded = enabledMods.contains(mod);
			
			String text = (enabled? "&a" : "&c") + (loaded? "" : "&m") + mod.name;
			String hover = (enabled? "&aEnabled" : "&cDisabled") + (loaded? "" : "\n&c&oThis mod is not loaded, report to staff!");
					
			if (mod.type != ModType.STATELESS) {
				Object state = DataManager.getOrDefault(uuid, "BlockPlaceMods", mod.name + "_state", null);
				if (state != null)
					hover += ("\n\n&7Value: &e" + state.toString() + "\n&7Type: &e" + mod.type.asString() + " \n&7Meaning: " + mod.typeDescription);
			}
			
			hover += "\n\n&7" + mod.description + "\n&e&oClick to " + (enabled? "&c&oDisable" : "&a&oEnable");
			msg.appendSendChatHover(text, "/bpm " + mod.name, hover);
			
			if (curMod != modsToRegister.length)
				msg.appendText("&7, ");
			curMod++;
		}
		msg.appendText("\n\n&2Hover over a mod for details.")
		   .send();
	}

	@Command (async = Command.AsyncType.ALWAYS, hook = "reset_mod")
	public void resetMod(CommandSender sender, String mod) {
		BlockPlaceMod bpm = mods.get(mod.toLowerCase());

		if (bpm == null) {
			getLogger().message(sender, true, "The mod, &e" + mod + "&7, does not exist!");
			return;
		}
		
		Player player = (Player) sender;
		DataManager.removeData(player.getUniqueId().toString(), "BlockPlaceMods", bpm.name);
		DataManager.removeData(player.getUniqueId().toString(), "BlockPlaceMods", bpm.name + "_state");

		getLogger().message(sender, "The &3" + bpm.name + "&7 mod has been reset back to it's original settings.");
	}
	
	@Command (async = Command.AsyncType.ALWAYS, hook = "toggle_mod_no_prefix")
	public void toggleModNoPrefix(CommandSender sender, String mod) {
		if (mod.equals("help"))
			try {
				Bukkit.getScheduler().callSyncMethod(ModuleLoader.getPlugin(), () -> Bukkit.dispatchCommand(sender, "bpm help 1")).get();
			} catch (Exception e) {
				getLogger().message(sender, true, "&4An error accorded trying to show you the help! Please inform a staff member!");
				e.printStackTrace();
			}
		else
			toggleMod(sender, mod);
	}
	
	@Command (async = Command.AsyncType.ALWAYS, hook = "toggle_mod")
	public void toggleMod(CommandSender sender, String mod) {
		BlockPlaceMod bpm = mods.get(mod.toLowerCase());

		if (bpm == null) {
			getLogger().message(sender, true, "The mod, &e" + mod + "&7, does not exist!");
			return;
		}
		
		Player player = (Player) sender;
		String uuid   = player.getUniqueId().toString();

		boolean current = (boolean) DataManager.getOrDefault(uuid, "BlockPlaceMods", bpm.name, bpm.enabledByDefault);
		DataManager.setData(uuid, "BlockPlaceMods", bpm.name, !current);

		getLogger().message(sender, "The &3" + bpm.name + "&7 mod has been " + (current ? "&cDisabled&7!" : "&aEnabled&7!"));
	}

	@Command (async = Command.AsyncType.ALWAYS, hook = "set_mod_value")
	public void setModValue(CommandSender sender, String mod, String value) {
		BlockPlaceMod bpm = mods.get(mod.toLowerCase());

		if (bpm == null) {
			getLogger().message(sender, true, "The mod, &e" + mod + "&7, does not exist!");
			return;
		}
		
		Player player = (Player) sender;
		String uuid   = player.getUniqueId().toString();

		switch (bpm.type) {
			case STATELESS:
				getLogger().message(sender, true, "You cannot change the value of a stateless mod!");
				break;
			case STRING:
				DataManager.setData(uuid, "BlockPlaceMods", bpm.name + "_state", value);
				getLogger().message(sender, "Changed the value of &3" + bpm.name + "&7 to &e" + value);
				break;
			case INTEGER:
				try {
					DataManager.setData(uuid, "BlockPlaceMods", bpm.name + "_state", Integer.parseInt(value));
					getLogger().message(sender, "Changed the value of &3" + bpm.name + "&7 to &e" + value);
				} catch (NumberFormatException e) {
					getLogger().message(sender, true, "The specified value must be an integer!");
				}
				break;
			case UNSIGNED_INTEGER:
				try {
					int val = Integer.parseInt(value);

					if (val < 0) {
						getLogger().message(sender, true, "The specified value must be zero or a positive integer!");
						break;
					}
					DataManager.setData(uuid, "BlockPlaceMods", bpm.name + "_state", val);
					getLogger().message(sender, "Changed the value of &3" + bpm.name + "&7 to &e" + value);
				} catch (NumberFormatException e) {
					getLogger().message(sender, true, "The specified value must be zero or a positive integer!");
				}
				break;
			case REDSTONE_LEVEL:
				try {
					int val = Integer.parseInt(value);

					if (val < 1 || val > 15) {
						getLogger().message(sender, true, "The specified value must be an integer between 1 and 15!");
						break;
					}
					DataManager.setData(uuid, "BlockPlaceMods", bpm.name + "_state", val);
					getLogger().message(sender, "Changed the value of &3" + bpm.name + "&7 to &e" + value);
				} catch (NumberFormatException e) {
					getLogger().message(sender, true, "The specified value must be an integer between 1 and 15!");
				}
				break;
		}
	}
}

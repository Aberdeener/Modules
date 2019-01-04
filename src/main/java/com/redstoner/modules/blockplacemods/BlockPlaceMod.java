package com.redstoner.modules.blockplacemods;

import com.redstoner.modules.datamanager.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class BlockPlaceMod implements Listener {
	public final String   name;
	public final String[] aliases;
	public final String   description;
	public final ModType  type;
	public final String   typeDescription;

	public final boolean enabledByDefault;

	protected BlockPlaceMod(String name, String description, ModType type, String typeDescription, boolean enabledByDefault, String... aliases) {
		this.name = name;
		this.aliases = aliases;
		this.description = description;

		this.type = type;
		this.typeDescription = typeDescription;

		this.enabledByDefault = enabledByDefault;
	}

	protected final boolean hasEnabled(Player player) {
		String uuid = player.getUniqueId().toString();
		return (boolean) DataManager.getOrDefault(uuid, "BlockPlaceMods", name, enabledByDefault);
	}

	protected final Object getState(Player player) {
		String uuid = player.getUniqueId().toString();
		return DataManager.getOrDefault(uuid, "BlockPlaceMods", name + "_state", null);
	}

	public boolean onEnable() {
		return true;
	}

	public void onDisable() {}
}

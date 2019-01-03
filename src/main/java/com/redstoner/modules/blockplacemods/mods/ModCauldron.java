package com.redstoner.modules.blockplacemods.mods;

import com.redstoner.modules.blockplacemods.BlockPlaceMod;
import com.redstoner.modules.blockplacemods.ModType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ModCauldron extends BlockPlaceMod {
	public ModCauldron() {
		super(
				"Cauldron",
				"With this mod enabled cauldrons are placed filled and cycle on a shiftless right click with an empty hand.",
				ModType.STATELESS,
				null,
				true,
				"CauldronFill", "AutoCauldron"
		);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (
				event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& !event.isCancelled()
				&& !event.getPlayer().isSneaking()
				&& event.getClickedBlock().getType() == Material.CAULDRON
				&& hasEnabled(event.getPlayer())
				&& event.getPlayer().getGameMode() == GameMode.CREATIVE
				&& event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR
		) {
			Block    block = event.getClickedBlock();
			Levelled data  = (Levelled) block.getBlockData();

			int newLevel = (data.getLevel() + 1) % (data.getMaximumLevel() + 1);

			data.setLevel(newLevel);
			block.setBlockData(data);
		}
	}

	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (
				event.getBlock().getType() == Material.CAULDRON
				&& !event.getPlayer().isSneaking()
				&& hasEnabled(event.getPlayer())
				&& event.getPlayer().getGameMode() == GameMode.CREATIVE
		) {
			Block    block = event.getBlock();
			Levelled data  = (Levelled) block.getBlockData();

			data.setLevel(data.getMaximumLevel());
			block.setBlockData(data);
		}
	}
}

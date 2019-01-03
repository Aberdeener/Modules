package com.redstoner.modules.blockplacemods.mods;

import com.redstoner.misc.Main;
import com.redstoner.modules.blockplacemods.BlockPlaceMod;
import com.redstoner.modules.blockplacemods.ModType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.RedstoneTorch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModTorch extends BlockPlaceMod {
	private static final Map<Block, Block> monitorBlocks = new HashMap<>();

	private int monitorId = -1;

	public ModTorch() {
		super(
				"Torch",
				"With this mod enabled redstone torches placed on redstone blocks get auto removed after they turn off.",
				ModType.STATELESS,
				null,
				true,
				"AutoTorch", "TorchRemoval", "RedstoneTorch"
		);
	}

	@Override
	public boolean onEnable() {
		monitorId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, () -> {
			for (Block block : monitorBlocks.keySet()) {
				Block against = monitorBlocks.get(block);

				if (block.getType() != Material.REDSTONE_TORCH || against.getType() != Material.REDSTONE_BLOCK) {
					monitorBlocks.remove(block);
					return;
				}

				RedstoneTorch data = (RedstoneTorch) block.getBlockData();

				if (!data.isPowered()) {
					block.setType(Material.AIR);
				}
			}
		}, 2, 2);

		return monitorId != -1;
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTask(monitorId);
		monitorId = -1;
	}

	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (
				event.getBlock().getType() == Material.REDSTONE_TORCH
				&& !event.getPlayer().isSneaking()
				&& hasEnabled(event.getPlayer())
				&& event.getPlayer().getGameMode() == GameMode.CREATIVE
		) {
			Block block = event.getBlock();
			Block against = event.getBlockAgainst();

			if (against.getType() == Material.REDSTONE_BLOCK) {
				monitorBlocks.put(block, against);
			}
		}
	}
}

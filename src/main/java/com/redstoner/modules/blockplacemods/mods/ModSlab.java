package com.redstoner.modules.blockplacemods.mods;

import com.redstoner.modules.blockplacemods.BlockPlaceMod;
import com.redstoner.modules.blockplacemods.ModType;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

public class ModSlab extends BlockPlaceMod {
	public ModSlab() {
		super(
				"Slab",
				"With this mod enabled Slabs are placed on the top half of the block unless you are sneaking.",
				ModType.STATELESS,
				null,
				true,
				"Step", "AutoSlab", "AutoStep", "SlabRotation", "StepRotation"
		);
	}

	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (
				event.getBlock().getBlockData() instanceof Slab
				&& !event.getPlayer().isSneaking()
				&& hasEnabled(event.getPlayer())
				&& event.getPlayer().getGameMode() == GameMode.CREATIVE
		) {
			Block block = event.getBlock();
			Slab  data  = (Slab) block.getBlockData();

			data.setType(Slab.Type.TOP);
			block.setBlockData(data);
		}
	}
}

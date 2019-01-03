package com.redstoner.modules.blockplacemods.mods;

import com.redstoner.modules.blockplacemods.BlockPlaceMod;
import com.redstoner.modules.blockplacemods.ModType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

public class ModBetterDirectional extends BlockPlaceMod {
	private static final BlockFace[][][] dirMap = {
			{
					{ null, null, null },
					{ null, BlockFace.WEST, null }, // (-1, 0, 0)
					{ null, null, null }
			},
			{
					{ null, BlockFace.DOWN, null }, // (0, -1, 0)
					{ BlockFace.NORTH, BlockFace.SELF, BlockFace.SOUTH }, // (0, 0, x)
					{ null, BlockFace.UP, null } // (0, 1, 0)
			},
			{
					{ null, null, null },
					{ null, BlockFace.EAST, null }, // (1, 0, 0)
					{ null, null, null }
			}
	};

	private final Material material;

	public ModBetterDirectional(String name, Material material, String materialPlural, boolean enabledByDefault) {
		super(
				name,
				"With this mod enabled " + materialPlural + " are placed with the bottom on the block clicked.",
				ModType.STATELESS,
				null,
				enabledByDefault,
				"BetterDirectional" + material.name().toLowerCase()
		);

		this.material = material;
	}

	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (
				event.getBlock().getType() == material
				&& !event.getPlayer().isSneaking()
				&& hasEnabled(event.getPlayer())
				&& event.getPlayer().getGameMode() == GameMode.CREATIVE
		) {
			Block       block = event.getBlock();
			Directional data  = (Directional) block.getBlockData();

			data.setFacing(getDirection(event));
			block.setBlockData(data);
		}
	}

	private BlockFace getDirection(BlockPlaceEvent event) {
		Location original = event.getBlockPlaced().getLocation();
		Location against  = event.getBlockAgainst().getLocation();

		Location dir = original.subtract(against);

		return dirMap[dir.getBlockX() + 1][dir.getBlockY() + 1][dir.getBlockZ() + 1];
	}
}

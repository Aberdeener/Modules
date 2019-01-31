package com.redstoner.modules.blockplacemods.mods;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import com.redstoner.modules.blockplacemods.BlockPlaceMod;
import com.redstoner.modules.blockplacemods.ModType;

public class ModPlayerDirectional extends BlockPlaceMod{
	
	private final Material material;
	private final boolean towards;
	
	public ModPlayerDirectional(String name, Material material, String materialPlural, boolean towards, boolean invertLogic, boolean enabledByDefault) {
		super(
				name,
				"With this mod enabled " + materialPlural + " are placed facing " + (towards? "towards you." : "away from you."),
				ModType.STATELESS, null,
				enabledByDefault,
				"PlayerDirectional" + material.name().toLowerCase()
		);

		this.material = material;
		this.towards = invertLogic? !towards : towards;
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (block.getType() == material && !player.isSneaking()
			&& hasEnabled(player)		&& player.getGameMode() == GameMode.CREATIVE) {
			
			Directional data  = (Directional) block.getBlockData();

			data.setFacing(getNewDirection(player, towards));
			block.setBlockData(data);
		}
	}
	
	private BlockFace getNewDirection(Player player, boolean towards) {
		double rotation = normalAngle(player.getLocation().getYaw());
		
		if (rotation >= 315 || rotation < 45) // South
			return towards? BlockFace.NORTH : BlockFace.SOUTH;
		if (rotation >= 45 && rotation < 135) // West
			return towards? BlockFace.EAST : BlockFace.WEST;
		if (rotation >= 135 && rotation < 225) // North
			return towards? BlockFace.SOUTH : BlockFace.NORTH;
		else                                  // East
			return towards? BlockFace.WEST : BlockFace.EAST;
	}
	
	private double normalAngle(double angle) {
	    return (angle %= 360) >= 0 ? angle : (angle + 360);
	  }

}

package com.redstoner.modules.blockplacemods.mods;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ModToggledCauldron extends ModToggledAbstract
{
	public ModToggledCauldron()
	{
		super("cauldron", false);
	}
	
	@Override
	public String getDescription()
	{
		return "If active, placed cauldrons are filled, and they cycle on right click";
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isSneaking()
				&& event.getClickedBlock().getType() == Material.CAULDRON && hasEnabled(event.getPlayer()))
		{
			Block block = event.getClickedBlock();
			block.setData((byte) ((block.getData() - 1) & 0x3));
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.getBlock().getType() == Material.CAULDRON && !event.getPlayer().isSneaking()
				&& hasEnabled(event.getPlayer()))
		{
			event.getBlock().setData((byte) 3);
		}
	}
}

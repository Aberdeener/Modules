package com.redstoner.modules.worldborder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
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

@AutoRegisterListener
@Commands(CommandHolderType.File)
@Version(major = 5, minor = 0, revision = 0, compatible = 4)
public class WorldBorder implements Module, Listener {
	
	Map<String, WorldBorderInfo> borderInfos = new HashMap<>();
	
	@Command(hook = "getwb")
	public void getWorldBorder(CommandSender sender, String world) {
		if (Bukkit.getWorld(world) == null) {
			getLogger().message(sender, true, "The world, &e" + world + "&7, doesn't exist.");
			return;
		}
		
		WorldBorderInfo info = borderInfos.get(world);
		if (info == null)
			getLogger().message(sender, true, "The world, &e" + world +"&7, hasn't been configured yet.");
		else
			getLogger().message(sender, "&e" + world + "&7's World Border is " + info.getMessage());
	}
	
	@Command(hook = "setwb")
	public void setWorldBorder(CommandSender sender, String world, int cx, int cz, int r) {
		if (Bukkit.getWorld(world) == null) {
			getLogger().message(sender, true, "The world, &e" + world + "&7, doesn't exist.");
			return;
		}
		
		WorldBorderInfo info = new WorldBorderInfo(cx, cz, r);
		borderInfos.put(world, info);
		save();
		getLogger().message(sender, "World Border for &e" + world + "&7 is now " + info.getMessage());
	}
	
	@Command(hook = "remwb")
	public void removeWorldBorder(CommandSender sender, String world) {
		if (Bukkit.getWorld(world) == null) {
			getLogger().message(sender, true, "The world, &e" + world + "&7, doesn't exist.");
			return;
		}
		
		WorldBorderInfo info = borderInfos.remove(world);
		if (info == null) {
			getLogger().message(sender, true, "The world, &e" + world +"&7, hasn't been configured yet.");
			return;
		}
		save();
		getLogger().message(sender, "World Border for &e" + world + "&7 has been removed.");
	}
	
	@SuppressWarnings("unchecked")
	private void save() {
		File file = new File(Main.plugin.getDataFolder(), "WorldBorder.json");
		JSONObject j = new JSONObject();
		
		for(String world : borderInfos.keySet())
			j.put(world, borderInfos.get(world).toJSONObject());
		JsonManager.save(j, file);
	}
	
	@SuppressWarnings("unchecked")
	public void postEnable() {
		File file = new File(Main.plugin.getDataFolder(), "WorldBorder.json");
		
		if (!file.exists()) {
			JSONObject j = new JSONObject();
			j.put("placeholder", new WorldBorderInfo(0, 0, 0).toJSONObject());
			JsonManager.save(j, file);
			return;
		}
		
		JSONObject j = JsonManager.getObject(file);
		
		if (j.get("placeholder") != null) {
			j.remove("placeholder");
			getLogger().warn("Detected placeholder in config, ignoring!");
		}
		
		for (Object o : j.keySet()) {
			String world = (String) o;
			WorldBorderInfo info = WorldBorderInfo.fromJSONObject((JSONObject) j.get(world));
			borderInfos.put(world, info);
			getLogger().info("Loaded world, " + world + ", with the boarder " + info.getMessage());
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		Location loc = event.getBlockPlaced().getLocation();
		if (loc == null)
			return;
	
		World world = loc.getWorld();
		if (world == null)
			return;
		WorldBorderInfo info = borderInfos.get(world.getName());
		if (info == null)
			return;
		
		if (!info.isCordanateWithinBounds(loc.getBlockX(), loc.getBlockZ())) 
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onVehicleMove(VehicleMoveEvent event) {
		Location to = event.getTo();
		WorldBorderInfo info = borderInfos.get(to.getWorld().getName());
		if (!info.isCordanateWithinBounds(to.getBlockX(), to.getBlockZ()))
			event.getVehicle().remove();
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		
		Player p = event.getPlayer();
		
		Location loc = getFinalLocation(p, event.getFrom(),
                event.getTo(), "&cYou've Reached the WorldBorder!");
		if (loc == null)
			event.setCancelled(true);
		else
			event.setTo(loc);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Location loc = getFinalLocation(event.getPlayer(), event.getFrom(),
				                        event.getTo(), "&cYou've Teleported to an Invalid Location, returning!");
		if (loc == null)
			event.setCancelled(true);
		else
			event.setTo(loc);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerPortal(PlayerPortalEvent event) {
		Location loc = getFinalLocation(event.getPlayer(), event.getFrom(),
                event.getTo(), "&cYou've Reached an Invalid Location, returning!");
		if (loc == null)
			event.setCancelled(true);
		else
			event.setTo(loc);
	}
	
	
	private Location getFinalLocation(Player p, Location from, Location to, String message) {
		WorldBorderInfo info = borderInfos.get(to.getWorld().getName());
		
		if (info == null || info.isCordanateWithinBounds(to.getBlockX(), to.getBlockZ()))
			return to;
		else {
			System.out.println(p.isInsideVehicle());
			if (p.isInsideVehicle())
				p.getVehicle().remove();
			ChatAPI.sendActionBar(p, message);
			return from;
		}
	}
}

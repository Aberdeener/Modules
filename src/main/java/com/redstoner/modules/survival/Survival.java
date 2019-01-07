package com.redstoner.modules.survival;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.Main;
import com.redstoner.modules.Module;
import com.redstoner.modules.datamanager.DataManager;

import net.nemez.chatapi.ChatAPI;

@Commands(CommandHolderType.File)
@AutoRegisterListener
@Version(major = 5, minor = 0, revision = 0, compatible = 4)
public class Survival implements Module, Listener {
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		World w1 = e.getFrom().getWorld();
		World w2 = e.getTo().getWorld();
		
		checkSleep(w1);
		if (!w1.getName().equals(w2.getName()))
			checkSleep(w2);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		checkSleep(e.getPlayer().getWorld());
	}
	
	boolean suspendEvents = false;
	
	public void checkSleep(World world) {
		if (suspendEvents || isDay(world.getTime()))
			return;
		
		int sleepingPlayers = 0;
		for (Player p : world.getPlayers()) 
			if (p.isSleeping())
				sleepingPlayers++;
		
		int perSleeping = 100 * sleepingPlayers / world.getPlayers().size();
		int perNeeded = (Integer) DataManager.getConfigOrDefault("perNeededToSleep", 51);
		
		if (perSleeping >= perNeeded) {
			notifyPlayers(world.getPlayers(), "&e" + perSleeping + "%&0 were sleeping. The &6sun&0 is rising!");
			world.setTime(23450);
			world.setStorm(false);
			world.setThundering(false);
			suspendEvents = true;
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> resumeEvents(), 20);
		}
		else
			notifyPlayers(world.getPlayers(), "&e" + perSleeping + "%&0 are sleeping. &e" + perNeeded + "%&0 needed");
		
	}
	
	public boolean resumeEvents() {
		suspendEvents = false;
		return true;
	}
	
	public boolean isDay(long time) {
		return time < 12300 || time > 23850;
	}
	
	public void notifyPlayers(List<Player> players, String msg) {
		for (Player p : players)
			ChatAPI.sendActionBar(p, "&0[&2Sleep&0] " + msg);
	}
}

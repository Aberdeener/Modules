package com.redstoner.modules.survival;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
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
@Version(major = 5, minor = 0, revision = 4, compatible = 4)
public class Survival implements Module, Listener {
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.SURVIVAL)
			return;
		World w1 = e.getFrom().getWorld();
		World w2 = e.getTo().getWorld();
		
		checkSleep(w1);
		if (!w1.getName().equals(w2.getName()))
			checkSleep(w2);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if (e.getPlayer().getGameMode() == GameMode.SURVIVAL)
			checkSleep(e.getPlayer().getWorld());
	}
	
	@EventHandler
	public void onGamemodeChange(PlayerGameModeChangeEvent e) {
		if (e.getNewGameMode() == GameMode.SURVIVAL || e.getPlayer().getGameMode() == GameMode.SURVIVAL)
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> checkSleep(e.getPlayer().getWorld()), 20);
	}
	
	boolean suspendEvents = false;
	
	private int lastPer = 0;
	
	public void checkSleep(World world) {
		if (suspendEvents || !canSleep(world.getTime(), world.isThundering()) || world.getPlayers().size() == 0)
			return;
		
		if (!((String)DataManager.getConfigOrDefault(world.getName() + ".enabled", "false")).equals("true"))
			return;
		
		int sleepingPlayers = 0;
		int totalPlayers = 0;

		for (Player p : world.getPlayers()) 
			if (p.isSleeping() && p.getGameMode() == GameMode.SURVIVAL)
				sleepingPlayers++;

		for (Player p : world.getPlayers()) 
			if (p.getGameMode() == GameMode.SURVIVAL)
				totalPlayers++;		

		if (totalPlayers == 0)
			return;
	
		int perSleeping = 100 * sleepingPlayers / totalPlayers;
		int perNeeded = (Integer) DataManager.getConfigOrDefault(world.getName() + ".perNeededToSleep", 51);
		
		if (perSleeping == lastPer)
			return;
		if (perSleeping >= perNeeded) {
			notifyPlayers(world.getPlayers(), "&e" + perSleeping + "%&f were sleeping. The &6sun&f is rising!");
			world.setTime(23450);
			world.setStorm(false);
			world.setThundering(false);
			suspendEvents = true;
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> resumeEvents(), 20);
		}
		else 
			notifyPlayers(world.getPlayers(), "&e" + perSleeping + "%&f are sleeping. &e" + perNeeded + "%&f needed");		
		lastPer = perSleeping;
	}
	
	public boolean resumeEvents() {
		suspendEvents = false;
		lastPer = 0;
		return true;
	}
	
	public boolean canSleep(long time, boolean thundering) {
		return !(time < 12300 || time > 23850) || thundering;
	}
	
	public void notifyPlayers(List<Player> players, String msg) {
		for (Player p : players)
			ChatAPI.sendActionBar(p, "&0[&2Sleep&0] " + msg);
	}
}

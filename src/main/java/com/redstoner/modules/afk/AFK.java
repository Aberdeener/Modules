package com.redstoner.modules.afk;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.BroadcastFilter;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;
import com.redstoner.modules.datamanager.DataManager;

@Commands(CommandHolderType.File)
@AutoRegisterListener
@Version(major = 4, minor = 0, revision = 6, compatible = 5)
public class AFK implements Module, Listener {
	private CustomListener listener;

	@Override
	public void firstLoad() {
		String[] choices = new String[] { "listen", "ignore" };

		DataManager.setConfig("indicator", "&7[AFK]");

		DataManager.setConfig("move", "listen", choices);
		DataManager.setConfig("look", "ignore", choices);
		DataManager.setConfig("chat", "listen", choices);
		DataManager.setConfig("interact", "listen", choices);
		DataManager.setConfig("command", "ignore", choices);
	}

	@Override
	public void migrate(Version old) {
		if (old.major() == 4 && old.minor() == 0 && old.revision() == 3) {
			String[] choices = new String[] { "listen", "ignore" };
			DataManager.setConfig("look", "ignore", choices);
		}
	}

	@Override
	public void postEnable() {
		listener = new CustomListener();
		update_afk_listeners(Bukkit.getConsoleSender());
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(listener);
	}

	@Command(hook = "afk")
	public boolean afk(CommandSender sender) {
		return afk(sender, false, "");
	}

	@Command(hook = "afks")
	public boolean afk(CommandSender sender, boolean silent) {
		return afk(sender, silent, "");
	}

	@Command(hook = "afk2")
	public boolean afk(CommandSender sender, boolean silent, String reason) {
		if (AFKUtil.isafk(sender)) {
			AFKUtil.unafk(sender, silent);
		} else {
			DataManager.setData(sender, "afk_time", System.currentTimeMillis());
			DataManager.setData(sender, "afk_reason", reason);
			DataManager.setState(sender, "afk_silent", silent);
			DataManager.setState(sender, "afk", true);

			if (!silent) Utils.broadcast("§7 * ", Utils.getName(sender) + "§7 is now AFK", null);
		}

		return true;
	}

	private void registerCustomListenerEvent(Class<? extends Event> event) {
		Bukkit.getPluginManager().registerEvent(event, listener, EventPriority.MONITOR, listener, Main.plugin);
	}

	/*
	 * This is perfectly valid code. Copied from the source code of the Event class:
	 * "All events require a static method named getHandlerList()"
	 */
	private void unregisterCustomListenerEvent(Class<? extends Event> clazz) {
		try {
			HandlerList list = (HandlerList) clazz.getMethod("getHandlerList").invoke(null);
			list.unregister(listener);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	private boolean getListenSetting(String name, String def) {
		return DataManager.getConfigOrDefault(name, def).equals("listen");
	}

	private void updateCustomListener(boolean listen, Class<? extends Event> clazz) {
		if (listen) registerCustomListenerEvent(PlayerInteractEvent.class);
		else unregisterCustomListenerEvent(PlayerInteractEvent.class);
	}

	@Command(hook = "update_afk_listeners")
	public boolean update_afk_listeners(CommandSender sender) {
		Utils.broadcast(null, "Updating afk listeners...", new BroadcastFilter() {
			@Override
			public boolean sendTo(CommandSender recipient) {
				return recipient.hasPermission("utils.afk.admin");
			}
		});

		updateCustomListener(getListenSetting("move", "listen") || getListenSetting("look", "ignore"), PlayerMoveEvent.class);
		updateCustomListener(getListenSetting("chat", "listen"), AsyncPlayerChatEvent.class);
		updateCustomListener(getListenSetting("interact", "listen"), PlayerInteractEvent.class);
		updateCustomListener(getListenSetting("command", "ignore"), PlayerCommandPreprocessEvent.class);

		return true;
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		DataManager.setState(event.getPlayer(), "afk", false);
	}
}

class CustomListener implements Listener, EventExecutor {
	private boolean move = true, look = false;

	@Override
	public void execute(Listener listener, Event event) throws EventException {
		if (event instanceof PlayerEvent) {
			if (event instanceof PlayerMoveEvent) {
				PlayerMoveEvent pevent = (PlayerMoveEvent) event;

				double distance = pevent.getFrom().distance(pevent.getTo());
				boolean moved = distance > 0;
				boolean looked = (pevent.getFrom().getPitch() != pevent.getTo().getPitch()) || (pevent.getFrom().getYaw() != pevent.getTo().getYaw());

				if ((move && moved) || (look && looked)) {
					Player player = pevent.getPlayer();

					if (AFKUtil.isafk(player) && !AFKUtil.isVanished(player)) AFKUtil.unafk(player, AFKUtil.isSilent(player));
				}
			} else {
				PlayerEvent pevent = (PlayerEvent) event;
				Player player = pevent.getPlayer();

				if (AFKUtil.isafk(player) && !AFKUtil.isVanished(player)) AFKUtil.unafk(player, AFKUtil.isSilent(player));
			}
		}
	}

	public void listenMove(boolean move) {
		this.move = move;
	}

	public void listenLook(boolean look) {
		this.look = look;
	}
}

class AFKUtil {
	protected static void unafk(CommandSender sender, boolean silent) {
		DataManager.setState(sender, "afk", false);

		if (!silent) Utils.broadcast("§7 * ", Utils.getName(sender) + "§7 is no longer AFK", null);
	}

	protected static boolean isafk(CommandSender sender) {
		return DataManager.getState(sender, "afk");
	}

	protected static boolean isVanished(Player player) {
		return DataManager.getState(player, "vanished");
	}

	protected static boolean isSilent(CommandSender sender) {
		return DataManager.getState(sender, "afk_silent");
	}
}

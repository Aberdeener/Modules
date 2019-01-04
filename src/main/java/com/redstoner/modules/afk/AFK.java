package com.redstoner.modules.afk;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.coremods.moduleLoader.ModuleLoader;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;
import com.redstoner.modules.datamanager.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

@Commands(CommandHolderType.File)
@AutoRegisterListener
@Version(major = 5, minor = 0, revision = 0, compatible = 4)
public class AFK implements Module {
	private AFKListener listener;

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
		listener = new AFKListener();

		Bukkit.getServer().getPluginManager().registerEvents(listener, ModuleLoader.getPlugin());
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

	@Command(hook = "afksilent")
	public boolean afk(CommandSender sender, boolean silent) {
		return afk(sender, silent, "");
	}

	@Command(hook = "afkreason")
	public boolean afk(CommandSender sender, boolean silent, String reason) {
		if (AFKUtil.isAfk(sender)) {
			AFKUtil.unAfk(sender, silent);
		} else {
			DataManager.setData(sender, "afk_time", System.currentTimeMillis());
			DataManager.setData(sender, "afk_reason", reason);
			DataManager.setState(sender, "afk_silent", silent);
			DataManager.setState(sender, "afk", true);

			if (!silent) Utils.broadcast("§7 * ", Utils.getName(sender) + "§7 is now AFK", null);
		}

		return true;
	}

	private boolean getListenSetting(String name, String def) {
		return DataManager.getConfigOrDefault(name, def).equals("listen");
	}

	@Command(hook = "update_afk_listeners")
	public boolean update_afk_listeners(CommandSender sender) {
		Utils.broadcast(null, "Updating afk listeners...", recipient -> recipient.hasPermission("utils.afk.admin"));

		listener.listenTo(
				getListenSetting("move", "listen"),
				getListenSetting("look", "ignore"),
				getListenSetting("chat", "listen"),
				getListenSetting("interact", "listen"),
				getListenSetting("command", "ignore")
		);

		return true;
	}
}

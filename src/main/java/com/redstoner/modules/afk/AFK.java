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

  	@Command(hook = "afkignore")
  	public boolean afk(CommandSender sender, boolean silent, boolean ignoreMovement) {
  		return afk(sender, silent, ignoreMovement, "");
  	}
	
	@Command(hook = "afkreason")
	public boolean afk(CommandSender sender, boolean silent, String reason) {
		if (silent == false && reason.equals("help"))
			return false;
		
//		String reasonLower = reason.toLowerCase();
//		if (reasonLower.startsWith("true "))
//			return afk(sender, silent, true, reason.substring(5));
//		else if (reasonLower.startsWith("false "))
//			return afk(sender, silent, false, reason);
//		else
			return afkmain(sender, silent, reason, false);
	}

	@Command(hook = "afkfull")
	public boolean afk(CommandSender sender, boolean silent, boolean ignoreMovement, String reason) {
		boolean oldIgnoringMovement = AFKUtil.isIgnoringMovement(sender);
		System.out.println("Am I stupid");
		DataManager.setState(sender, "afk_ignoreMovement", ignoreMovement);
		
		if (AFKUtil.isAfk(sender) && oldIgnoringMovement != ignoreMovement) {
			if (ignoreMovement) 
				getLogger().message(sender, "Your movements will now be ignored.");
			else
				getLogger().message(sender, "Your movements will no longer be ignored.");
			return afkmain(sender, silent, reason, true);
		}
		else if (AFKUtil.isAfk(sender)) {
			if (ignoreMovement) 
				getLogger().message(sender, "Your movements will still be ignored.");
			else
				getLogger().message(sender, "Your movements will still not be ignored.");
			return afkmain(sender, silent, reason, true);
		}
		return afkmain(sender, silent, reason, false);
	}
	
	public boolean afkmain(CommandSender sender, boolean silent, String reason, boolean keepAFK) {
		boolean isAFK = AFKUtil.isAfk(sender);
		
		if ( isAFK && reason.equals("") && !keepAFK)
			AFKUtil.unAfk(sender, silent);
		
		else if (isAFK && !reason.equals("")) {
			DataManager.setData(sender, "afk_reason", reason);
			getLogger().message(sender, "Your reason has been updated.");
		}
		else if (!keepAFK) {
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

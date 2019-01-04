package com.redstoner.modules.afk;

import com.redstoner.misc.Utils;
import com.redstoner.modules.datamanager.DataManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AFKUtil {
    protected static void unAfk(CommandSender sender, boolean silent) {
        DataManager.setState(sender, "afk", false);

        if (!silent) Utils.broadcast("§7 * ", Utils.getName(sender) + "§7 is no longer AFK", null);
    }

    protected static boolean isAfk(CommandSender sender) {
        return DataManager.getState(sender, "afk");
    }

    protected static boolean isVanished(Player player) {
        return DataManager.getState(player, "vanished");
    }

    protected static boolean isSilent(CommandSender sender) {
        return DataManager.getState(sender, "afk_silent");
    }

    protected static void checkedUnAfk(Player player) {
        if (isAfk(player) && !isVanished(player)) unAfk(player, isSilent(player));
    }
}
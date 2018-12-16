package com.redstoner.modules.misc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscUtil {
	private static final Pattern durationPattern = Pattern.compile("^(?:(?:(\\d*)y)?(?:(\\d*)m)?(?:(\\d*)d)?(?:(\\d*)h)?(?:(\\d*)m)?(?:(\\d*)s)?){1}$");

	private static int getIntGroup(Matcher matcher, int group) {
		String strGroup = matcher.group(group);
		return Integer.parseInt(strGroup == null ? "0" : strGroup);
	}

	protected static int parseDuration(String duration) {
		Matcher m = durationPattern.matcher(duration);

		if (m.matches()) {
			int years   = getIntGroup(m, 1);
			int months  = getIntGroup(m, 2);
			int days    = getIntGroup(m, 3);
			int hours   = getIntGroup(m, 4);
			int minutes = getIntGroup(m, 5);
			int seconds = getIntGroup(m, 6);

			return (years * 31557600) + (months * 2629800) + (days * 86400) + (hours * 3600) + (minutes * 60) + seconds;
		} else {
			return -1;
		}
	}

	protected static int getPing(Player player) {
		try {
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);

			int ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
			return ping;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
		}

		return -1;
	}

	protected static boolean canBuild(Player player, Location location) {
		BlockBreakEvent event = new BlockBreakEvent(location.getBlock(), player);
		Bukkit.getPluginManager().callEvent(event);
		return !event.isCancelled();
	}
}

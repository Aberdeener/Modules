package com.redstoner.modules.check;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.redstoner.exceptions.NonSaveableConfigException;
import com.redstoner.misc.mysql.Config;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.nemez.cmdmgr.Command;
import com.nemez.cmdmgr.Command.AsyncType;
import com.nemez.cmdmgr.CommandManager;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.coremods.moduleLoader.ModuleLoader;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.Main;
import com.redstoner.misc.mysql.MysqlHandler;
import com.redstoner.misc.mysql.elements.ConstraintOperator;
import com.redstoner.misc.mysql.elements.MysqlConstraint;
import com.redstoner.misc.mysql.elements.MysqlDatabase;
import com.redstoner.misc.mysql.elements.MysqlTable;
import com.redstoner.modules.Module;

import net.nemez.chatapi.click.Message;

@Commands(CommandHolderType.File)
@Version(major = 5, minor = 0, revision = 0, compatible = 4)
public class Check implements Module, Listener {
	MysqlTable table;
	String noTableReason;

	@Override
	public boolean onEnable() {
		Config config;

		try {
			config = Config.getConfig("check.json");
		} catch (IOException | org.json.simple.parser.ParseException e1) {
			e1.printStackTrace();
			return false;
		}

		if (config == null || !config.containsKey("database") || !config.containsKey("table")) {
			getLogger().warn("Could not load the Check config file, ip info for offline users and website data will be unavaliable!");
			noTableReason = "Could not load the config file";
			
			config.put("database", "redstoner");
			config.put("table", "users");

			try {
				config.save();
				getLogger().info("A default config file has been created for you.");
			} catch (IOException | NonSaveableConfigException e) {
				getLogger().error("A default config file was unable to be created.");
			}

			return true;
		}

		try {
			getLogger().info("Attempting to load the database and the table, this might take a few seconds.");
			MysqlDatabase database = MysqlHandler.INSTANCE.getDatabase(config.get("database") + "?autoReconnect=true");
			table = database.getTable(config.get("table"));
		} catch (Exception e) {
			getLogger().warn("Could not use the Check config file, ip info for offline users and website data will be unavaliable!");
			noTableReason = "Could not use the config file";
		}

		return true;
	}

	@Override
	public void postEnable() {
		CommandManager.registerCommand(getCommandString(), this, Main.plugin);
	}

	@SuppressWarnings("deprecation")
	@Command(hook = "checkCommand", async = AsyncType.ALWAYS)
	public void checkCommand(final CommandSender sender, final String player) {
		OfflinePlayer oPlayer = Bukkit.getPlayer(player);
		if (oPlayer == null) oPlayer = Bukkit.getServer().getOfflinePlayer(player);

		sendData(sender, oPlayer);

		if (ModuleLoader.exists("Tag")) {
			try {
				Bukkit.getScheduler().callSyncMethod(ModuleLoader.getPlugin(), () -> Bukkit.dispatchCommand(sender, "tag check " + player)).get();
			} catch (ExecutionException | InterruptedException e) {
				Message msg = new Message(sender, null);
				msg.appendText("&4Running /tag check failed! Please inform a dev about this incident!");

				e.printStackTrace();
			}
		}
	}
	
	@Command(hook = "ipinfo", async = AsyncType.ALWAYS)
	public void ipinfo(final CommandSender sender, final String ip) {
		String[] info = getIpInfo(ip);
		
		Message msg = new Message(sender, null);
		
		if (info == null) {
			msg.appendText("\n&6> &cData Unavailable");
		} else {
			String region = info[1];
			String asn = info[2];
			String org = info[3];

			msg.appendText("\n&6> IP: ").appendSuggestHover("&e" + ip, ip, "Click to copy!");
			msg.appendText("\n&6> Region: ").appendSuggestHover("&e" + region, region, "Click to copy!");
			msg.appendText("\n&6> ASN: ").appendSuggestHover("&e" + asn, asn, "Click to copy!");
			msg.appendText("\n&6> Org: ").appendSuggestHover("&e" + org, org, "Click to copy!");
		} 
		
		msg.send();
	}

	public String read(URL url) {
		String data = "";

		try {
			Scanner in = new Scanner(new InputStreamReader(url.openStream()));

			while (in.hasNextLine()) {
				data += in.nextLine();
			}

			in.close();
			return data;
		} catch (IOException e) {}

		return null;
	}

	public String[] getIpInfo(OfflinePlayer player) {
		String ip = "";
		
		if (player.isOnline()) {
			ip = player.getPlayer().getAddress().getHostString();
		} else if (table != null) {
			try {
				ip = (String) table.get("last_ip", new MysqlConstraint("uuid", ConstraintOperator.EQUAL, player.getUniqueId().toString().replace("-", "")))[0];
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
		return getIpInfo(ip);
	}
	
	public String[] getIpInfo(String ip) {
		String[] info = new String[4];

		try {
			URL ipinfo = new URL("https://ipapi.co/" + ip + "/json");
			JSONObject json = (JSONObject) new JSONParser().parse(read(ipinfo));

			info[0] = ip;

			Object o_country = json.get("country_name");
			Object o_region = json.get("region");
			Object o_asn = json.get("asn");
			Object o_org = json.get("org");

			String country = o_country == null ? "Unknown" : (String) o_country;
			String region = o_region == null ? "" : ", " + (String) o_region;
			String asn = o_asn == null ? "Unknown" : (String) o_asn;
			String org = o_org == null ? "Unknown" : (String) o_org;

			info[1] = country.equals("") ? "Unknown" : country + (region.equals(", ") ? "" : region);
			info[2] = asn.equals("") ? "Unknown" : asn;
			info[3] = org.equals("") ? "Unknown" : org;

			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getFirstJoin(OfflinePlayer player) {
		Long firstJoin = player.getFirstPlayed();
		Date date = new Date(firstJoin);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		return format.format(date);
	}

	public String getLastSeen(OfflinePlayer player) {
		Long lastSeen = player.getLastPlayed();
		Date date = new Date(lastSeen);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		return format.format(date);
	}

	public Object[] getWebsiteData(OfflinePlayer player) {
		if (table == null)
			return null;

		MysqlConstraint constraint = new MysqlConstraint("uuid", ConstraintOperator.EQUAL, player.getUniqueId().toString().replace("-", ""));

		try {
			int id = (int) table.get("id", constraint)[0];
			String email = (String) table.get("email", constraint)[0];
			boolean confirmed = (boolean) table.get("confirmed", constraint)[0];

			return new Object[] { "https://redstoner.com/users/" + id, email, confirmed };
		} catch (Exception e) {
			try {
				int id = (int) table.get("id", constraint)[0];
				String email = (String) table.get("email", constraint)[0];
				boolean confirmed = (boolean) table.get("confirmed", constraint)[0];

				return new Object[] { "https://redstoner.com/users/" + id, email, confirmed };
			} catch (Exception e2) {}

			return new Object[] { null };
		}
	}

	public String getAllNames(OfflinePlayer player) {
		String uuid = player.getUniqueId().toString().replace("-", "");
		String nameString = "";

		try {
			String rawJson = read(new URL("https://api.mojang.com/user/profiles/" + uuid + "/names"));
			JSONArray names = (JSONArray) new JSONParser().parse(rawJson);

			for (Object obj : names) {
				nameString += "&e" + ((JSONObject) obj).get("name") + "&7, ";
			}

			nameString = nameString.substring(0, nameString.length() - 2);
			return nameString;
		} catch (Exception e) {}

		return "None";
	}

	public void sendData(CommandSender sender, OfflinePlayer player) {
		try {
			// data
			String uuid = player.getUniqueId().toString();

			String firstJoin = getFirstJoin(player);
			String lastSeen = getLastSeen(player);
			firstJoin = (firstJoin.equals("1970-01-01 01:00")) ? "&eNever" : "&7(yyyy-MM-dd hh:mm) &e" + firstJoin;
			lastSeen = (lastSeen.equals("1970-1-1 01:00")) ? "&eNever" : "&7(yyyy-MM-dd hh:mm) &e" + lastSeen;

			Object[] websiteData = getWebsiteData(player);


			String[] ipInfo = getIpInfo(player);

			String namesUsed = getAllNames(player);

			// messages
			Message msg = new Message(sender, null);

			msg.appendText("\n" + getLogger().getHeader());
			msg.appendText("\n&7Data provided by redstoner:");
			msg.appendText("\n&6> UUID: ").appendSuggestHover("&e" + uuid, uuid, "Click to copy!");
			msg.appendText("\n&6> First joined: &e" + firstJoin);
			msg.appendText("\n&6> Last Seen: &e" + lastSeen);

			if (websiteData != null) {
				String websiteUrl = (websiteData[0] == null) ? "None" : (String) websiteData[0];
				String email = (websiteData[0] == null) ? "Unknown" : (String) websiteData[1];
				boolean emailNotConfirmed = (websiteData[0] == null) ? false : !((boolean) websiteData[2]);

				msg.appendText("\n&6> Website account: &e").appendLink(websiteUrl, websiteUrl);
				msg.appendText("\n&6> Email: &e" + (emailNotConfirmed ? "\n&6> &cEmail NOT Confirmed!" : "")).appendSuggestHover("&e" + email, email, "Click to copy!");
			}
			else {
				msg.appendTextHover("\n&6> Website account: &cData Unavailable", "&c" + noTableReason);
				msg.appendTextHover("\n&6> Email: &cData Unavailable", "&c" + noTableReason);
			}
			msg.appendText("\n\n&7Data provided by ipapi.co:");

			if (ipInfo == null && table == null) {
				msg.appendTextHover("\n&6> &cData Unavailable", "&c" + noTableReason);
			} else if (ipInfo == null) {
				msg.appendText("\n&6> &cData Unavailable");
			} else {
				String ip = ipInfo[0];
				String region = ipInfo[1];
				String asn = ipInfo[2];
				String org = ipInfo[3];

				msg.appendText("\n&6> IP: ").appendSuggestHover("&e" + ip, ip, "Click to copy!");
				msg.appendText("\n&6> Region: ").appendSuggestHover("&e" + region, region, "Click to copy!");
				msg.appendText("\n&6> ASN: ").appendSuggestHover("&e" + asn, asn, "Click to copy!");
				msg.appendText("\n&6> Org: ").appendSuggestHover("&e" + org, org, "Click to copy!");
			}

			msg.appendText("\n\n&7Data provided by mojang:");
			msg.appendText("\n&6> All ingame names used so far: &e" + namesUsed);
			msg.send();
		} catch (Exception e) {
			getLogger().message(sender, true, "Sorry, something went wrong while fetching data");
			e.printStackTrace();
		}
	}
}

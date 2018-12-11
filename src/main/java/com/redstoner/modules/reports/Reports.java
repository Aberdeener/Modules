package com.redstoner.modules.reports;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.modules.Module;

import net.nemez.chatapi.click.Message;

/** Report module. Allows reports to be created and handled by staff
 * 
 * @author Redempt */
@Commands(CommandHolderType.File)
@Version(major = 4, minor = 2, revision = 0, compatible = 4)
public class Reports implements Module
{
	private int task = 0;
	private JSONArray reports;
	private JSONArray archived;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd kk:mm");
	
	@Override
	public boolean onEnable()
	{
		reports = JsonManager.getArray(new File(Main.plugin.getDataFolder(), "reports.json"));
		archived = JsonManager.getArray(new File(Main.plugin.getDataFolder(), "archived_reports.json"));
		if (reports == null)
			reports = new JSONArray();
		if (archived == null)
			archived = new JSONArray();
		// Notify online staff of open reports
		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, () ->
		{
			if (reports.size() <= 0)
			{
				return;
			}
			for (Player player : Bukkit.getOnlinePlayers())
			{
				if (player.hasPermission("utils.report"))
				{
					getLogger().message(player, "&cThere are &e" + reports.size() 
							+ " &copen reports!");
				}
			}
			getLogger().info("&cThere are &e" + reports.size() 
							+ " &copen reports!");
		}, 2400, 2400);
		return true;
	}
	
	@Override
	public void onDisable()
	{
		// Save reports, cancel notifier task
		Bukkit.getScheduler().cancelTask(task);
		JsonManager.save(reports, new File(Main.plugin.getDataFolder(), "reports.json"));
		JsonManager.save(archived, new File(Main.plugin.getDataFolder(), "archived_reports.json"));
	}
	
	@Command(hook = "report_tp")
	public void tpReport(CommandSender sender, int id)
	{
		// Check for invalid ID
		Player player = (Player) sender;
		if (id > reports.size() - 1 || id < 0)
		{
			getLogger().message(sender, true, "Invalid ID!");
			return;
		}
		JSONObject report = (JSONObject) reports.get(id);
		String loc = (String) report.get("location");
		String[] split = loc.split(";");
		// Location from string
		int x = Integer.parseInt(split[0]);
		int y = Integer.parseInt(split[1]);
		int z = Integer.parseInt(split[2]);
		World world = Bukkit.getWorld(split[3]);
		Location location = new Location(world, x, y, z);
		player.teleport(location);
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "report_close")
	public void closeReport(CommandSender sender, int id)
	{
		// Check for invalid ID
		if (id > reports.size() - 1 || id < 0)
		{
			getLogger().message(sender, true, "Invalid ID!");
			return;
		}
		// Move report to archived reports
		JSONObject report = (JSONObject) reports.get(id);
		reports.remove(id);
		archived.add(report);
		getLogger().message(sender, "Report #" + id + " closed!");
	}
	
	@Command(hook = "report_open")
	public void listOpen(CommandSender sender)
	{
		int i = 0;
		
		Message msg = new Message(sender, null).appendText("\n" + getLogger().getHeader());
		for (Object object : reports)
		{
			JSONObject report = (JSONObject) object;
			msg.appendText("\n&8[&e" + i + "&8][&e" + report.get("time") + "&8]&3"
				    	+ report.get("name") + "&f: &e" + report.get("message"));
			i++;
		}
		if (i == 0)
			msg.appendText("\n&cThere are no open reports.");
		msg.send();
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "report")
	public void report(CommandSender sender, String message)
	{
		Player player = (Player) sender;
		// Create report JSONObject
		JSONObject report = new JSONObject();
		report.put("name", player.getName());
		report.put("time", dateFormat.format(new Date()));
		report.put("message", message);
		String loc = player.getLocation().getBlockX() + ";" + player.getLocation().getBlockY() + ";"
				   + player.getLocation().getBlockZ() + ";" + player.getLocation().getWorld().getName();
		report.put("location", loc);
		reports.add(report);
		getLogger().message(sender, "Report created! Use &e/undoreport&7 to retract the report.");
	}

	
	@Command(hook = "report_retract")
	public void retractReport(CommandSender sender)
	{
		String p_name = ((Player) sender).getName();
		
		JSONObject lastReport = null;
		
		for (Object o_report : reports) {
			JSONObject report = (JSONObject) o_report;
			
			String r_name = (String) report.get("name");
			if (r_name.equals(p_name))
				lastReport = report;
		}
		
		if (lastReport == null) {
			getLogger().message(sender, true, "You haven't submitted a report.");
			return;
		}
		
		reports.remove(lastReport);
		getLogger().message(sender, "Successfully retracted your last report.");
	}
}

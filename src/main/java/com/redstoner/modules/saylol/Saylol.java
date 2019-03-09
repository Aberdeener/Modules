package com.redstoner.modules.saylol;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.JSONArray;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.coremods.moduleLoader.ModuleLoader;
import com.redstoner.misc.BroadcastFilter;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;
import com.redstoner.modules.ignore.Ignore;

import net.nemez.chatapi.ChatAPI;
import net.nemez.chatapi.click.ClickCallback;
import net.nemez.chatapi.click.Message;

@AutoRegisterListener
@Commands(CommandHolderType.File)
@Version(major = 5, minor = 1, revision = 0, compatible = 4)
public class Saylol implements Module
{
	private long lastLol = 0;
	private File lolLocation = new File(Main.plugin.getDataFolder(), "lol.json");
	private JSONArray lols, handlers;
	private final String LOL_PREFIX = "§8[§blol§8] ";
	private Map<CommandSender, List<Integer>> searchCache = new HashMap<>();
	private final int PAGE_SIZE = 10;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onEnable()
	{
		lols = JsonManager.getArray(lolLocation);
		if (lols == null)
			lols = new JSONArray();
		handlers = new JSONArray();
		for (int i = 0; i < lols.size(); i++)
			handlers.add(new ClickCallback(true, true, "")
			{
				@Override
				public void run(CommandSender sender)
				{
					if (handlers.contains(this))
						clickAction((Player) sender, handlers.indexOf(this));
					else
						getLogger().message(sender, true, "That lol no longer exists!");
				}
			});
		return true;
	}
	
	@Override
	public void onDisable()
	{
		saveLolsSync();
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "addlol")
	public boolean addLol(CommandSender sender, String text)
	{
		if (lols.contains(text))
			getLogger().message(sender, true, "This lol already exists!");
		else
		{
			getLogger().message(sender, "Successfully added a new lol!");
			lols.add("&e" + text);
			handlers.add(new ClickCallback(true, true, "")
			{
				@Override
				public void run(CommandSender sender)
				{
					if (handlers.contains(this))
						clickAction((Player) sender, handlers.indexOf(this));
					else
						getLogger().message(sender, true, "That lol no longer exists!");
				}
			});
			saveLols();
			searchCache.clear();
		}
		return true;
	}
	
	@Command(hook = "dellol")
	public boolean delLol(CommandSender sender, int id)
	{
		if (lols.size() == 0)
		{
			getLogger().message(sender, true, "There are no lols yet!");
			return true;
		}
		if (id < 0 || id >= lols.size())
		{
			getLogger().message(sender, true, "The ID must be at least 0 and at most " + (lols.size() - 1));
			return true;
		}
		getLogger().message(sender, "Successfully deleted the lol: " + lols.remove(id));
		handlers.remove(id);
		saveLols();
		searchCache.clear();
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "setlol")
	public boolean setLol(CommandSender sender, int id, String text)
	{
		if (lols.size() == 0)
		{
			getLogger().message(sender, true, "There are no lols yet!");
			return true;
		}
		if (id < 0 || id >= lols.size())
		{
			getLogger().message(sender, true, "The ID must be at least 0 and at most " + (lols.size() - 1));
			return true;
		}
		getLogger().message(sender, "Successfully changed the lol: &e" + lols.get(id) + " &7to: &e" + text);
		lols.set(id, text);
		saveLols();
		searchCache.clear();
		return true;
	}
	
	@Command(hook = "lolid")
	public boolean lolId(CommandSender sender, int id)
	{
		if (lols.size() == 0)
		{
			getLogger().message(sender, true, "There are no lols yet!");
			return true;
		}
		long time = System.currentTimeMillis();
		if (time - lastLol < 15000)
		{
			getLogger().message(sender, true,
					"You can't use saylol for another " + (14 - (int) Math.ceil((time - lastLol) / 1000)) + "s.");
			return true;
		}
		if (id < 0 || id >= lols.size())
		{
			getLogger().message(sender, true, "The ID must be at least 0 and at most " + (lols.size() - 1));
			return true;
		}
		String name;
		if (sender instanceof Player)
			name = ((Player) sender).getDisplayName();
		else
			name = "&9" + sender.getName();
		Utils.broadcast(LOL_PREFIX, ChatAPI.colorify(null, name + "&8: &e" + lols.get(id)), new BroadcastFilter()
		{
			@Override
			public boolean sendTo(CommandSender recipient)
			{
				return recipient.hasPermission("utils.lol.see");
			}
		});
		lastLol = time;
		return true;
	}
	
	@Command(hook = "saylol")
	public boolean saylol(CommandSender sender)
	{
		if (lols.size() == 0)
		{
			getLogger().message(sender, true, "There are no lols yet!");
			return true;
		}
		long time = System.currentTimeMillis();
		if (time - lastLol < 15000)
		{
			getLogger().message(sender, true,
					"You can't use saylol for another " + (14 - (int) Math.ceil((time - lastLol) / 1000)) + "s.");
			return true;
		}
		String name;
		if (sender instanceof Player)
			name = ((Player) sender).getDisplayName();
		else
			name = "&9" + sender.getName();
		Random random = new Random();
		int id = random.nextInt(lols.size());
		Utils.broadcast(LOL_PREFIX, ChatAPI.colorify(null, name + "&8: &e" + lols.get(id)),
				        ModuleLoader.exists("Ignore")? Ignore.getIgnoredBy(sender) : null);
		lastLol = time;
		return true;
	}
	
	@Command(hook = "listlols")
	public boolean listLols(CommandSender sender, int page)
	{
		searchCache.put(sender, Arrays.asList(-1));
		
		if (lols.size() == 0)
		{
			getLogger().message(sender, true, "There are no lols yet!");
			return true;
		}
		page = page - 1;
		int start = page * PAGE_SIZE;
		int end = start + PAGE_SIZE;
		int pages = getMaxPage(lols.size());
		if (start < 0)
		{
			getLogger().message(sender, true, "Page number too small, must be at least 1!");
			return true;
		}
		if (start > lols.size())
		{
			getLogger().message(sender, true, "Page number too big, must be at most " + pages + "!");
			return true;
		}
		Message m = new Message(sender, null);
		m.appendText(getLogger().getHeader().replace("\n", ""));
		m.appendText(" &ePage " + (page + 1) + "/" + pages + ":");
		for (int i = start; i < end && i < lols.size(); i++)
			m.appendCallback("\n&a" + i + "&8: &e" + lols.get(i), getCallback(i));
		m.send();
		return true;
	}
	
	@Command(hook = "listlolsdef")
	public boolean listLolsDefault(CommandSender sender)
	{
		return listLols(sender, 1);
	}
	
	@Command(hook = "searchlol")
	public boolean search(CommandSender sender, boolean sensitive, String text)
	{		
		searchCache.remove(sender);
		
		List<Integer> results = new ArrayList<>();
		
		if (!sensitive)
			text = text.toLowerCase();
		for (int i = 0; i < lols.size(); i++)
		{
			String lol = (String) lols.get(i);
			if ((sensitive ? lol : lol.toLowerCase()).contains(text))
				results.add(i);
		}
		if (results.isEmpty()) {
			getLogger().message(sender, "&cCouldn't find any matching lols.");
			return true;
		}
		
		searchCache.put(sender, results);
		
		Message m = new Message(sender, null);
		m.appendText(getLogger().getHeader().replace("\n", ""));
		
		int size = results.size();
		if (size > PAGE_SIZE)
			m.appendText(" &ePage 1/" + getMaxPage(size) + ":");
		
		for (int i = 0; i < size && i < PAGE_SIZE; i++)
			m.appendCallback("\n&a" + i + "&8: &e" + lols.get(results.get(i)), getCallback(i));
		
		m.appendText("\n&7Use /lol page <number> to look at other pages.");
		m.send();
		return true;
	}
	
	@Command(hook = "matchlol")
	public boolean match(CommandSender sender, boolean sensitive, String regex)
	{
		
		searchCache.remove(sender);
		
		List<Integer> results = new ArrayList<>();
		
		if (!sensitive)
			regex = regex.toLowerCase();
		for (int i = 0; i < lols.size(); i++)
		{
			String lol = (String) lols.get(i);
			if ((sensitive ? lol : lol.toLowerCase()).matches(regex))
				results.add(i);
		}
		if (results.isEmpty()) {
			getLogger().message(sender, "&cCouldn't find any matching lols.");
			return true;
		}
		
		searchCache.put(sender, results);
		
		Message m = new Message(sender, null);
		m.appendText(getLogger().getHeader().replace("\n", ""));
		
		int size = results.size();
		if (size > PAGE_SIZE)
			m.appendText(" &ePage 1/" + getMaxPage(size) + ":");
		
		for (int i = 0; i < size && i < PAGE_SIZE; i++)
			m.appendCallback("\n&a" + i + "&8: &e" + lols.get(i), getCallback(i));
		
		m.appendText("\n&7Use /lol page <number> to look at other pages.");
		m.send();
		
		return true;
	}
	
	@Command(hook = "page")
	public boolean page(CommandSender sender, int page) {
		List<Integer> results = searchCache.get(sender);
		
		if (results == null || results.size() == 0) {
			getLogger().message(sender, true, "There's nothing to page through. Either you haven't"
					                        + " done a relivent command, or the lols have changed since you have.");
			return true;
		}
		
		int pages = getMaxPage(results.size());
		
		if (results.get(0) == -1)
			listLols(sender, page);
		else if (page < 1 || page > pages)
			getLogger().message(sender, true, "Page number not on range. Must be between &e1&7 and &e" + pages + "&7.");
		else {
			Message m = new Message(sender, null);
			m.appendText(getLogger().getHeader().replace("\n", " &ePage " + page + "/" + pages + ":"));
			
			for (int i = page*PAGE_SIZE-PAGE_SIZE; i < page*PAGE_SIZE; i++)
				m.appendCallback("\n&a" + i + "&8: &e" + lols.get(i), getCallback(i));
			
			m.send();
		}
		return true;
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		searchCache.remove(e.getPlayer());
	}
	
	public int getMaxPage(int size) {
		return (int) Math.ceil(size / (double) PAGE_SIZE);
	}
	
	public void saveLols()
	{
		JsonManager.save(lols, lolLocation);
	}
	
	public void saveLolsSync()
	{
		JsonManager.saveSync(lols, lolLocation);
	}
	
	public ClickCallback getCallback(int index)
	{
		return (ClickCallback) handlers.get(index);
	}
	
	public void clickAction(Player player, int index)
	{
		if (player.hasPermission("utils.lol.id"))
			Bukkit.getScheduler().callSyncMethod(ModuleLoader.getPlugin(), () -> Bukkit.dispatchCommand(player, "lol id " + index));
	}
}

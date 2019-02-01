package com.redstoner.modules.naming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.modules.Module;

import net.md_5.bungee.api.ChatColor;

@Commands(CommandHolderType.File)
@Version(major = 5, minor = 3, revision = 0, compatible = 4)
public class Naming implements Module
{	
	private final Pattern COLOR_CHECK = Pattern.compile(".*&[\\da-fk-or].*");
	private final ItemStack[] COST = new ItemStack[] {new ItemStack(Material.IRON_INGOT, 1)};
	
	@Command(hook = "name")
	public void name(CommandSender sender, String name)
	{
		Player player = (Player) sender;
		
		if (player.getGameMode() == GameMode.SURVIVAL && !processSurvivalPlayer(player, name, "rename the item"))
			return;
			
		name = ChatColor.translateAlternateColorCodes('&', name);
		ItemStack item = player.getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		if (meta == null)
		{
			getLogger().message(sender, true, "You can not rename that item!");
			return;
		}
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		getLogger().message(sender, "Name set to &f&o" + name + "&7.");
		player.updateInventory();
	}
	
	@Command(hook = "lore")
	public void lore(CommandSender sender, boolean append, String lore)
	{
		Player player = (Player) sender;
		
		if (player.getGameMode() == GameMode.SURVIVAL && !processSurvivalPlayer(player, lore, append? "append to the lore" : "add lore to the item"))
			return;
		
		ItemStack item = player.getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		if (meta == null)
		{
			getLogger().message(sender, true, "You can not change the lore of that item!");
			return;
		}
		List<String> currentLore;
		if (append)
			currentLore = meta.getLore();
		else
			currentLore = new ArrayList<String>();
		if (currentLore == null)
			currentLore = new ArrayList<String>();
		lore = ChatColor.translateAlternateColorCodes('&', lore);
		currentLore.add(lore);
		meta.setLore(currentLore);
		item.setItemMeta(meta);
		if (append)
			getLogger().message(sender, "Appended the following line to the lore: &5&o" + lore + "&7.");
		else			
			getLogger().message(sender, "Lore set to &5&o" + lore + "&7.");
		player.updateInventory();
	}
	
	private boolean processSurvivalPlayer(Player player, String str, String operation) {
				
		int levelsNeeded = COLOR_CHECK.matcher(str).matches()? 2 : 1;
		int levels = player.getLevel();
		
		if (levels < levelsNeeded) {
			getLogger().message(player, true, "You don't have enough levels. " + (levelsNeeded == 1?
					                          "You need &e1&7 level to " + operation + "." :
					                          "You need &e2&7 levels to " + operation + " with color/formatting."));
			return false;
		}
		
		HashMap<Integer, ItemStack> result = player.getInventory().removeItem(COST);
		if (result.size() > 0) {
			getLogger().message(player, true, "You don't have enough resources. You need &e1&7 iron ingot to " + operation + ".");
			return false;
		}
		player.setLevel(levels - levelsNeeded);
		return true;		
	}
}

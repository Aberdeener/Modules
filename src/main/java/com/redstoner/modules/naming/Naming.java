package com.redstoner.modules.naming;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.modules.Module;

import net.md_5.bungee.api.ChatColor;

@Commands(CommandHolderType.File)
@Version(major = 5, minor = 1, revision = 0, compatible = 4)
public class Naming implements Module
{
	@Command(hook = "anvil")
	public void anvil(CommandSender sender)
	{
		Player player = (Player) sender;
		Inventory inv = Bukkit.getServer().createInventory(player, InventoryType.ANVIL);
		player.openInventory(inv);
	}
	
	@Command(hook = "name")
	public void name(CommandSender sender, String name)
	{
		Player player = (Player) sender;
		
		if (player.getGameMode() == GameMode.SURVIVAL) {
			int level = player.getLevel();
			if (level < 1) {
				getLogger().message(sender, true, "You don't have enough levels to rename the item.");
				return;
			}
			else
				player.setLevel(level-1);
		}
			
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
		
		if (player.getGameMode() == GameMode.SURVIVAL) {
			int level = player.getLevel();
			if (level < 1) {
				getLogger().message(sender, true, "You don't have enough levels to rename the item.");
				return;
			}
			else
				player.setLevel(level-1);
		}
		
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
}

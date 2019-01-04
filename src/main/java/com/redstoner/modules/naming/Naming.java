package com.redstoner.modules.naming;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
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
@Version(major = 5, minor = 0, revision = 0, compatible = 4)
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
		name = ChatColor.translateAlternateColorCodes('&', name);
		ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		if (meta == null)
		{
			getLogger().message(sender, true, "You can not rename that item!");
			return;
		}
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		getLogger().message(sender, "Name set to " + name);
		((Player) sender).updateInventory();
	}
	
	@Command(hook = "lore")
	public void lore(CommandSender sender, boolean append, String lore)
	{
		ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
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
		getLogger().message(sender, "Lore set to " + lore);
		((Player) sender).updateInventory();
	}
}

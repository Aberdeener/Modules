package com.redstoner.modules.teleport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.modules.Module;
import com.redstoner.modules.datamanager.DataManager;

import net.nemez.chatapi.ChatAPI;

@Commands(CommandHolderType.File)
@AutoRegisterListener
@Version(major = 5, minor = 0, revision = 2, compatible = 4)
public class Teleport implements Module, Listener
{
	public static final String PERMISSION_TELEPORT = "utils.teleport.tp";
	public static final String PERMISSION_TELEPORT_OTHER = "utils.teleport.tp.other";	
	
	private Map<Player, Map<Player, TPAType>> pending_requests = new HashMap<>();
	private Map<Player, Stack<Player>> last_request = new HashMap<>();
	private Map<Player, Stack<Player>> last_request_got = new HashMap<>();
	
	@Command(hook = "tploc")
	public void tploc(CommandSender sender, int x, int y, int z) {
		Player p = (Player) sender;
		
		p.teleport(new Location(p.getWorld(), x, y, z), TeleportCause.COMMAND);
		getLogger().message(sender, "Teleported to &e(" + x + "," + y + "," + z + ")&7.");
	}
	
	@Command(hook = "tploc2")
	public void tploc2(CommandSender sender, String player, int x, int y, int z) {
		Player p = Bukkit.getPlayer(player);
		
		if (p == null)
			playerDoesNotExistError(sender, player);
		else if (sender.getName().equals(p.getName()))
			cannotTpToYourself(sender);
		else {
			p.teleport(new Location(p.getWorld(), x, y, z), TeleportCause.COMMAND);
			getLogger().message(sender, "Teleported &e" + p.getDisplayName() +
					            "&7 to &e(" + x + "," + y + "," + z + ")&7.");
			getLogger().message(p, "You've been teleported to &e(" + x + "," + y + "," + z + ")&7, by &e" +
					            (sender instanceof Player? ((Player)sender).getDisplayName()
					            	                     : sender.getName()) + "&7.");
		}
	}
	
	@Command(hook = "tp")
	public void teleport(CommandSender sender, String player) {
		if (!sender.hasPermission(PERMISSION_TELEPORT)) {
			tpa(sender, player);
			return;
		}
		
		Player p = Bukkit.getPlayer(player);
		if (p == null)
			playerDoesNotExistError(sender, player);
		else if (sender.getName().equals(p.getName()))
			cannotTpToYourself(sender);
		else {
			((Player)sender).teleport(p, TeleportCause.COMMAND);
			getLogger().message(sender, "Teleported to &e" + p.getDisplayName() + "&7.");
		}
	}
	
	@Command(hook = "tp2")
	public void teleport(CommandSender sender, String player, String player2) {
		if (!sender.hasPermission(PERMISSION_TELEPORT)
		  && sender.getName().equalsIgnoreCase(player2)) {
			tpahere(sender, player);
			return;
		}
		
		Player p1 = Bukkit.getPlayer(player);
		Player p2 = Bukkit.getPlayer(player2);
		
		if (p1 == null)
			playerDoesNotExistError(sender, player);
		else if (p2 == null)
			playerDoesNotExistError(sender, player2);
		else if (p1.getName().equals(p2.getName()))
			getLogger().message(sender, true, "You can't teleport a player to themselves.");
		else {
			p1.teleport(p2, TeleportCause.COMMAND);
			getLogger().message(sender, "&e" +p1.getDisplayName() + "&7 has been teleported to &e" +
			                            p2.getDisplayName() + "&7.");
			getLogger().message(p1, "You've been teleported to &e" + p2.getDisplayName() +
					                "&7 by &e" + (sender instanceof Player?
					                ((Player)sender).getDisplayName(): sender.getName()));
		}
	}
	
	@Command(hook = "tphere")
	public void tphere(CommandSender sender, String player) {
		Player p = Bukkit.getPlayer(player);
		if (p == null)
			playerDoesNotExistError(sender, player);
		else if (sender.getName().equals(p.getName()))
			cannotTpToYourself(sender);
		else {
			p.teleport((Player)sender);
			getLogger().message(sender, "&e" + p.getDisplayName() + "&7 has been teleported to you.");
			getLogger().message(p, "&e" + ((Player)sender).getDisplayName() + "&7 has teleported you to them.");
		}
	}
	
	@Command(hook = "tpa")
	public void tpa(CommandSender sender, String player) {
		Player p = Bukkit.getPlayer(player);
		if (p == null)
			playerDoesNotExistError(sender, player);		
		
		else if (sender.getName().equals(p.getName()))
			cannotTpToYourself(sender);
		
		else if ( (Boolean) DataManager.getOrDefault(p, "allow-tpa", true) == false )
			getLogger().message(sender, true, "&e" + p.getDisplayName() + "&7 doesn't accept TPA requests.");
		
		else {
			Player s = (Player) sender;
			insertIntoMaps(s, p, TPAType.TPA);
			getLogger().message(sender, "TPA request sent to &e" + p.getDisplayName() + "&7.");
			notifyAskie(p, s, TPAType.TPA);
		}
		
	}
	
	@Command(hook = "tpahere")
	public void tpahere(CommandSender sender, String player) {
		Player p = Bukkit.getPlayer(player);
		if (p == null)
			playerDoesNotExistError(sender, player);		
		
		else if (sender.getName().equals(p.getName()))
			cannotTpToYourself(sender);
		
		else if ( (Boolean) DataManager.getOrDefault(p, "allow-tpahere", true) == false )
			getLogger().message(sender, true, "&e" + p.getDisplayName() + "&7 doesn't accept TPA Here requests.");
		
		else {
			Player s = (Player) sender;
			insertIntoMaps(s, p, TPAType.TPAHERE);
			getLogger().message(sender, "TPA Here request sent to &e" + p.getDisplayName() + "&7.");
			notifyAskie(p, s, TPAType.TPAHERE);
		}
	}
	
	@Command(hook = "tpall")
	public void tpall(CommandSender sender)	{
		Player to = (Player) sender;
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equals(sender.getName()))
				continue;
			p.teleport(to, TeleportCause.COMMAND);
			getLogger().message(p, "&e" + to.getDisplayName() + "&7 has teleported everyone to them.");
		}
		getLogger().message(sender, "Everyone has sucessfully teleported to you.");
	}
	
	@Command(hook = "tpall2")
	public void tpall2(CommandSender sender, String player)	{
		Player to = Bukkit.getPlayer(player);
		if (to == null) {
			playerDoesNotExistError(sender, player);
			return;
		}
		
		String s = (sender instanceof Player? ((Player)sender).getDisplayName() :sender.getName());
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.teleport(to, TeleportCause.COMMAND);
			getLogger().message(p, "&e" + s + "&7 has teleported everyone to &e" + to.getDisplayName() + "&7.");
		}
		getLogger().message(sender, "Everyone was sucessfully teleported to &e" + to.getDisplayName() + "&7.");
	}
	
	@Command(hook = "tpaccept")
	public void tpaccept(CommandSender sender) {
		Player to = (Player) sender;
		Player from = getLastRequestGot(to);
		
		if (from == null)
			getLogger().message(sender, true, "You have no incoming TPA requests.");
		else
			teleport(to, from, pending_requests.get(to).get(from));		
	}
	
	@Command(hook = "tpaccept2")
	public void tpaccept2(CommandSender sender, String player) {
		Player to = (Player) sender;
		Player from = Bukkit.getPlayer(player);
		
		if (from == null) {
			playerDoesNotExistError(sender, player);
			return;
		}
		
		if (from.getName().equals(sender.getName())) {
			cannotTpToYourself(sender);
			return;
		}
		
		Map<Player, TPAType> m = pending_requests.get(to);
		
		if (m == null)
			getLogger().message(sender, true, "You have no incoming TPA requests.");
		else if (!m.containsKey(from))
			getLogger().message(sender, true, "&e" + from.getDisplayName() + "didn't send a TPA request.");
		else
			teleport(to, from, m.get(from));
	}
	
	
	private void teleport(Player to, Player from, TPAType type) {
		switch (pending_requests.get(to).get(from)) {
			case TPA: from.teleport(to, TeleportCause.COMMAND); break;
			case TPAHERE: to.teleport(from, TeleportCause.COMMAND); break;
		}
		clearRequest(to, from);
	
		getLogger().message(from, "&e" + to.getDisplayName() + "&7 has &aaccepted&7 your TPA request.");
		getLogger().message(to, "You've &aaccepted&7 &e" + from.getDisplayName() + "&7's TPA request.");
	}
	
	@Command(hook = "tpdeny")
	public void tpdeny(CommandSender sender) {
		Player to = (Player) sender;
		Player from = getLastRequestGot(to);
		
		if (from == null) {
			getLogger().message(sender, true, "You have no incoming TPA requests.");
			return;
		}
		
		clearRequest(to, from);
		
		getLogger().message(from, "&e" + to.getDisplayName() + "&7 has &cdenied&7 your TPA request.");
		getLogger().message(to, "You've &cdenied&7 &e" + from.getDisplayName() + "&7's TPA request.");
	}
	
	@Command(hook = "tpdeny2")
	public void tpdeny2(CommandSender sender, String player) {
		Player to = (Player) sender;
		Player from = Bukkit.getPlayer(player);
		
		if (from == null) {
			playerDoesNotExistError(sender, player);
			return;
		}
		
		if (from.getName().equals(sender.getName())) {
			cannotTpToYourself(sender);
			return;
		}
		
		Map<Player, TPAType> m = pending_requests.get(to);
		
		if (m == null)
			getLogger().message(sender, true, "You have no incoming TPA requests.");
		
		else if (!m.containsKey(from))
			getLogger().message(sender, true, "&e" + from.getDisplayName() + "&7 doesn't have an active TPA request with you.");
		
		else {
			clearRequest(to, from);
			getLogger().message(from, "&e" + to.getDisplayName() + "&7 has &cdenied&7 your TPA request.");
			getLogger().message(to, "You've &cdenied&7 &e" + from.getDisplayName() + "&7's TPA request.");
		}
	}
	
	@Command(hook = "tpacancel")
	public void tpacancel(CommandSender sender)	{
		Player from = (Player) sender;
		Player to = getLastRequest(from);
		
		if (to == null)
			getLogger().message(sender, true, "You don't have outgoing TPA requests.");
		else
			cancel(to, from);
	}
	
	@Command(hook = "tpacancel2")
	public void tpacancel2(CommandSender sender, String player)	{
		Player from = (Player) sender;
		Player to = Bukkit.getPlayer(player);
		
		if (to == null)
			playerDoesNotExistError(sender, player);
		else
			cancel(to, from);
	}
	
	private void cancel(Player to, Player from) {
		Stack<Player> s = last_request.get(from);
		
		if (s == null)
			getLogger().message(from, true, "You have no outgoing TPA requests.");
		
		else if (!s.contains(to))
			getLogger().message(from, true, "You didn't send a TPA request to &e"
		                                      + to.getDisplayName() + "&7.");
		
		else {
			clearRequest(to, from);
			getLogger().message(to, "&e" + from.getDisplayName() + "&7 has &ccanceled&7 their request.");
			getLogger().message(from, "You &ccanceled&7 your request to &e" + to.getDisplayName() + "&7.");
		}
	}
	
	@Command(hook = "tplist")
	public void tplist(CommandSender sender) {
		Player to = (Player) sender;
		
		Map<Player,TPAType> m = pending_requests.get(to);
		
		if (m == null) {
			getLogger().message(sender, true, "You don't have any incoming TPA requests.");
			return;
		}
		ChatAPI.send(sender, getLogger().getHeader());
		ChatAPI.send(sender, "");
		
		for (Player from : m.keySet()) {
			ChatAPI.createMessage(sender)
			.appendText("&e" + from.getDisplayName() + "&7: ")
			.appendSendChatHover("&aAccept", "/tpaccept " + from.getName(), "&eClick to &aAccept")
			.appendText("&7 or ")
			.appendSendChatHover("&cDeny", "/tpdeny " + from.getName(), "&eClick to &cDeny")
			.send();
		}
	}
	
	@Command(hook = "tptoggle")
	public void tptoggle(CommandSender sender, String status) {
		
		switch (status.toLowerCase()) {
			case "all":
				DataManager.setData(sender, "allow-tpa", true);
				DataManager.setData(sender, "allow-tpahere", true);
				break;
			case "tome":
				DataManager.setData(sender, "allow-tpa", true);
				DataManager.setData(sender, "allow-tpahere", false);
				break;
			case "tothem":
				DataManager.setData(sender, "allow-tpa", false);
				DataManager.setData(sender, "allow-tpahere", true);
				break;
			case "none":
				DataManager.setData(sender, "allow-tpa", false);
				DataManager.setData(sender, "allow-tpahere", false);
				break;
			default:
				getLogger().message(sender, true, "Invalid status, &e" + status +
						            "&7. Available: &eAll&7, &eToMe&7, &eToThem&7, and &eNone&7. ");
				return;
		}
		getLogger().message(sender, "Status set to &e" + status + "&7.");
	}
	
	private void playerDoesNotExistError(CommandSender sender, String player) {
		getLogger().message(sender, true, "The player, &e" + player + "&7, is not online.");
	}
	private void cannotTpToYourself(CommandSender sender) {
		getLogger().message(sender, true, "You can't teleport to yourself.");
	}
	
	private void insertIntoMaps(Player from, Player to, TPAType type) {
		Map<Player, TPAType> m = pending_requests.getOrDefault(to, new HashMap<>());
		m.put(from, type);
		pending_requests.put(to, m);
		
		Stack<Player> s1 = last_request.getOrDefault(from, new Stack<>());
		s1.push(to);
		last_request.put(from, s1);
		
		Stack<Player> s2 = last_request_got.getOrDefault(to, new Stack<>());
		s2.push(from);
		last_request_got.put(to, s2);
	}
	
	private void notifyAskie(Player to, Player from, TPAType type) {
		ChatAPI.createMessage(to)
		.appendText(getLogger().getPrefix() + "&e" + from.getDisplayName()
		          + "&7 has requested " +
				  (type == TPAType.TPA? "to teleport to you" : "you teleport to them") +
				  ".\nResponce: ")
		.appendSendChatHover("&aAccept", "/tpaccept " + from.getName(), "&eClick to &aAccept")
		.appendText("&7 or ")
		.appendSendChatHover("&cDeny", "/tpdeny " + from.getName(), "&eClick to &cDeny")
		.send();
	}
	
	private Player getLastRequest(Player from) {
		Stack<Player> stack = last_request.get(from);
		if (stack == null)
			return null;
		Player toReturn = stack.peek();
		if (stack.isEmpty())
			last_request.remove(from);
		else
			last_request.put(from, stack);
		return toReturn;
	}
	
	private Player getLastRequestGot(Player to) {
		Stack<Player> stack = last_request_got.get(to);
		if (stack == null)
			return null;
		Player toReturn = stack.peek();
		if (stack.isEmpty())
			last_request_got.remove(to);
		else
			last_request_got.put(to, stack);
		return toReturn;
	}
	
	private void clearRequest(Player to, Player from) {
		Stack<Player> s1 = last_request.get(from);
		Stack<Player> s2 = last_request_got.get(to);
		Map<Player, TPAType> m = pending_requests.get(to);
		
		s1.remove(to);
		s2.remove(from);
		m.remove(from);
		
		if (s1.isEmpty())
			last_request.remove(from);
		else
			last_request.put(from, s1);
		if (s2.isEmpty())
			last_request_got.remove(to);
		else
			last_request_got.put(to, s2);
		if (m.isEmpty())
			pending_requests.remove(from);
		else
			pending_requests.put(from, m);				
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		
		pending_requests.remove(p);
		last_request.remove(p);
		last_request_got.remove(p);
		
		Iterator<Player> pr_iterator = pending_requests.keySet().iterator();
		while (pr_iterator.hasNext()) {
			Player fl = pr_iterator.next();
			Map<Player, TPAType> m = pending_requests.get(fl);
			m.remove(p);
			if (m.isEmpty())
				pr_iterator.remove();
			else
				pending_requests.put(fl, m);
		}
		
		Iterator<Player> lr_iterator = last_request.keySet().iterator();
		while (lr_iterator.hasNext()) {
			Player fl = lr_iterator.next();
			Stack<Player> s = last_request.get(fl);
			s.remove(p);
			if (s.isEmpty())
				lr_iterator.remove();
			else
				last_request.put(fl, s);
		}
		
		Iterator<Player> lrg_iterator = last_request_got.keySet().iterator();
		while (lrg_iterator.hasNext()) {
			Player fl = lrg_iterator.next();
			Stack<Player> s = last_request_got.get(fl);
			s.remove(p);
			if (s.isEmpty())
				lrg_iterator.remove();
			else
				last_request_got.put(fl, s);
		}
	}
}


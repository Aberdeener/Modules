package com.redstoner.modules.mail;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import net.nemez.chatapi.ChatAPI;
import net.nemez.chatapi.click.Message;

public class Msg {
	
	private int id;
	private String sender;
	private String reciever;
	private String timeSent;
	private String message;
	private String chain;
	private boolean read;
	
	public Msg(int id, String sender, String reciever, String timeSent, String message, String chain, boolean read) {
		this.id = id;
		this.sender = sender;
		this.reciever = reciever;
		this.timeSent = timeSent;
		this.message = message;
		this.chain = chain;
		this.read = read;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getReciever() {
		return reciever;
	}

	
	public void read() {
		read = true;
	}
	
	public boolean isRead() {
		return read;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
	public String getChain() {
		Player p = Bukkit.getOfflinePlayer(UUID.fromString(sender)).getPlayer();
		
		String line = getPrefix(p) + p.getName() + "&7:&f " + message;
		
		if (chain == null)
			return line;
		else
			return chain + "\n" + line;
	}
	
	public void showMinimal(CommandSender viewer, Theme theme, boolean showDisplayName) {
		Player p = Bukkit.getOfflinePlayer(UUID.fromString(sender)).getPlayer();
		String name = getPrefix(p) + p.getName();
		
		Message msg = ChatAPI.createMessage(viewer)
		.appendText(theme.getBracketColor() + "[")
		.appendTextHover(theme.getInfoColor() + "I", "&9Time Sent:\n&7" + timeSent + "\nID: " + id)
		.appendText(theme.getBracketColor() + "] ");
		if (showDisplayName) 
			msg.appendTextHover(p.getDisplayName(), name);
		else
			msg.appendText(name);
		msg.appendText(theme.getColonColor() + ": ");
		if (chain == null)
			msg.appendText(message).send();
		else
			msg.appendTextHover(message, chain).send();
	}
	
	public void showSimple(CommandSender viewer, Theme theme, boolean showDisplayName) {
		Player p = Bukkit.getOfflinePlayer(UUID.fromString(sender)).getPlayer();
		String name = getPrefix(p) + p.getName();
		
		Message msg = ChatAPI.createMessage(viewer)
		.appendText(theme.getBracketColor() + "[")
		.appendSendChatHover(theme.getDeleteColor() + "X", "/mail delete " + id, "&cDelete")
		.appendText(theme.getBracketColor() + "][")
		.appendTextHover(theme.getInfoColor() + "I", "&9Time Sent:\n&7" + timeSent + "\nID: " + id)
		.appendText(theme.getBracketColor() + "] ");
		if (showDisplayName) 
			msg.appendTextHover(p.getDisplayName(), name);
		else
			msg.appendText(name);
		msg.appendText(theme.getColonColor() + ": ");
		if (chain == null)
			msg.appendText(message).send();
		else
			msg.appendTextHover(message, chain).send();
	}
	
	public void showNormal(CommandSender viewer, Theme theme, boolean showDisplayName) {
		Player p = Bukkit.getOfflinePlayer(UUID.fromString(sender)).getPlayer();
		String name = getPrefix(p) + p.getName();
		
		Message msg = ChatAPI.createMessage(viewer)
		.appendText(theme.getBracketColor() + "[")
		.appendSendChatHover(theme.getDeleteColor() + "X", "/mail delete " + id, "&cDelete")
		.appendText(theme.getBracketColor() + "][")
		.appendTextHover(theme.getInfoColor() + "I", "&9Time Sent:\n&7" + timeSent + "\nID: " + id)
		.appendText(theme.getBracketColor() + "][")
		.appendSuggestHover(theme.getReplyColor() + "Reply", "/mail reply " + id + " ", "&7Reply")
		.appendText(theme.getBracketColor() + "] ");
		if (showDisplayName) 
			msg.appendTextHover(p.getDisplayName(), name);
		else
			msg.appendText(name);
		msg.appendText(theme.getColonColor() + ": ");
		if (chain == null)
			msg.appendText(message).send();
		else
			msg.appendTextHover(message, chain).send();
	}
	
	public void showFull(CommandSender viewer, Theme theme, boolean showDisplayName) {
		Player p = Bukkit.getOfflinePlayer(UUID.fromString(sender)).getPlayer();
		String name = getPrefix(p) + p.getName();
		
		Message msg = ChatAPI.createMessage(viewer)
		.appendText(theme.getBracketColor() + "[")
		.appendSendChatHover(theme.getDeleteColor() + "X", "/mail delete " + id, "&cDelete")
		.appendText(theme.getBracketColor() + "][")
		.appendTextHover(theme.getInfoColor() + "I", "&9Time Sent:\n&7" + timeSent + "\nID: " + id)
		.appendText(theme.getBracketColor() + "][")
		.appendSuggestHover(theme.getReplyColor() + "Reply", "/mail reply " + id + " ", "&7Reply")
		.appendText(theme.getBracketColor() + "][")
		.appendSendChatHover(theme.getArchiveColor() + "Archive", "/mail archive " + id + " ", "&6Archive")
		.appendText(theme.getBracketColor() + "] ");
		if (showDisplayName) 
			msg.appendTextHover(p.getDisplayName(), name);
		else
			msg.appendText(name);
		msg.appendText(theme.getColonColor() + ": ");
		if (chain == null)
			msg.appendText(message).send();
		else
			msg.appendTextHover(message, chain).send();
	}
	
	public void showArchived(CommandSender viewer, Theme theme, boolean showDisplayName) {
		Player p = Bukkit.getOfflinePlayer(UUID.fromString(sender)).getPlayer();
		String name = getPrefix(p) + p.getName();
		
		Message msg = ChatAPI.createMessage(viewer)
		.appendText(theme.getBracketColor() + "[")
		.appendSendChatHover(theme.getArchiveColor() + "Unarchive", "/mail unarchive " + id, "&6Unarchive")
		.appendText(theme.getBracketColor() + "][")
		.appendTextHover(theme.getInfoColor() + "I", "&9Time Sent:\n&7" + timeSent + "\nID: " + id)
		.appendText(theme.getBracketColor() + "] ");
		if (showDisplayName) 
			msg.appendTextHover(p.getDisplayName(), name);
		else
			msg.appendText(name);
		msg.appendText(theme.getColonColor() + ": ");
		if (chain == null)
			msg.appendText(message).send();
		else
			msg.appendTextHover(message, chain).send();
	}
	
	private String getPrefix(Player player)
	{
		String[] teams = new String[] {"admin", "mod", "trainingmod", "helper", "trusted", "builder", "member", "visitor"};
		String[] prefixes = new String[] {"&4", "&c", "&c", "&9", "&3", "&a", "&f", "&7"};
		
		for (int i = 0; i < teams.length; i++)
			if (player.hasPermission("group." + teams[i]))
				return prefixes[i];
		return "&7";
		
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject j = new JSONObject();
		j.put("sender", sender);
		j.put("reciever", reciever);
		j.put("timeSent", timeSent);
		j.put("message", message);
		j.put("chain", chain);
		j.put("read", read);
		return j;
	}
	
	public static Msg fromJSONObject(JSONObject j, int id) {
		return new Msg(id, (String) j.get("sender"), (String) j.get("reciever"), (String) j.get("timeSent"),
				      (String) j.get("message"), (String) j.get("chain"), (boolean)j.get("read"));
	}
}

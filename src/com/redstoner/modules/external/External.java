package com.redstoner.modules.external;

import java.io.File;
import java.security.SecureRandom;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import net.nemez.chatapi.click.Message;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.modules.Module;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;

@Commands(CommandHolderType.File)
@Version(major = 4, minor = 0, revision = 0, compatible = 4)
public class External implements Module { 
	
	private final String FILENAME = "discordTokens.json";
	private final String DNE_LINK = "dne://";
	
	private JSONObject discordTables;
	private JSONObject byToken;
	private JSONObject byUUID;
	
	private String joinLink;
	private File savefile;
	
	private final String tokenCharacters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private SecureRandom rnd = new SecureRandom();
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onEnable() {
		savefile = new File(Main.plugin.getDataFolder(), FILENAME);
		
		discordTables = JsonManager.getObject(savefile);
		
		if (discordTables == null) {
			discordTables = new JSONObject();
			discordTables.put("joinLink", DNE_LINK);
			save();
		}
		
		Object joinLinkObject = discordTables.get("joinLink");
		
		if (joinLinkObject == null || ((String) joinLinkObject).equals(DNE_LINK)) {
			getLogger().error("Missing Join Link. Set: \"joinLink\" to \"_joinLink_\" in the discordTokens.json file.");
			return false;
		}
		
		joinLink = (String) joinLinkObject;
		
		Object byTokenObject = discordTables.get("byToken");
		
		if (byTokenObject == null) {
			discordTables.put("byToken", new JSONObject());
			discordTables.put("byUUID", new JSONObject());
		}
		
		byToken = (JSONObject) discordTables.get("byToken");
		byUUID = (JSONObject) discordTables.get("byUUID");
		
		return true;
		
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "discord")
	public void discord(CommandSender sender) {
		Player p = (Player) sender;
		String pUUID = p.getUniqueId().toString();
		
		Object tokenObject = byUUID.get(pUUID);
		String token = tokenObject == null? null : (String) tokenObject;
		
		if (token == null) {
			
			token = randomToken(8);
			Object UUIDObject = byToken.get(token);
			
			while (UUIDObject != null) {
				token = randomToken(8);
				UUIDObject = byToken.get(token);
			}
			byUUID.put(pUUID, token);
			byToken.put(token, pUUID);
			save();
		}
		
		new Message(sender, null).appendText("\n&cRedstoner&7 has a &2Discord&7 Now! \nClick ")
				                 .appendLinkHover("&e" + joinLink, joinLink, "&aClick to Join")
				                 .appendText("&7 to join. \n\nTo sync you rank, copy ")
				                 .appendSuggestHover("&e" + token, token, "&aClick to Copy")
				                 .appendText("&7 into &3#rank-sync&7.\n")
				                 .send();
	}
	
	private String randomToken(int length){
	   StringBuilder sb = new StringBuilder( length );
	   for( int i = 0; i < length; i++ ) 
	      sb.append( tokenCharacters.charAt( rnd.nextInt(tokenCharacters.length()) ) );
	   return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private void save() {
		
		discordTables.put("byToken", byToken);
		discordTables.put("byUUID", byUUID);
		JsonManager.save(discordTables, savefile);
	}
}
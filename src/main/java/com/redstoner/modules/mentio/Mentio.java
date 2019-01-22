package com.redstoner.modules.mentio;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.modules.Module;

import net.nemez.chatapi.ChatAPI;

@Commands(CommandHolderType.File)
@AutoRegisterListener
@Version(major = 5, minor = 1, revision = 0, compatible = 4)
public class Mentio implements Module, Listener
{
	private File mentioLocation = new File(Main.plugin.getDataFolder(), "mentio.json");
	private JSONObject mentios;
	public static Mentio instance;
	
	@Override
	public boolean onEnable()
	{
		instance = this;
		loadMentios();
		return true;
	}
	
	@Override
	public void onDisable()
	{
		saveMentios();
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "addmentio")
	public boolean addMentio(CommandSender sender, String trigger)
	{
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		JSONArray playerMentios = (JSONArray) mentios.get(uuid.toString());
		playerMentios = defaultMentio(playerMentios, player);
		if (playerMentios.contains(trigger))
			getLogger().message(sender, true, "You already had that as a mentio!");
		else
		{
			playerMentios.add(trigger);
			getLogger().message(sender, "Successfully added the trigger §e" + trigger + " §7for you!");
			mentios.put(uuid.toString(), playerMentios);
			saveMentios();
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "delmentio")
	public boolean delMentio(CommandSender sender, String trigger)
	{
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		JSONArray playerMentios = (JSONArray) mentios.get(uuid.toString());
		playerMentios = defaultMentio(playerMentios, player);
		if (!playerMentios.remove(trigger))
			getLogger().message(sender, true, "You didn't have that as a mentio!");
		else
		{
			getLogger().message(sender, "Successfully removed the trigger §e" + trigger + " §7for you!");
			mentios.put(uuid.toString(), playerMentios);
			saveMentios();
		}
		return true;
	}
	
	@Command(hook = "listmentios")
	public boolean listMentios(CommandSender sender)
	{
		ArrayList<String> message = new ArrayList<>();
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		JSONArray playerMentios = (JSONArray) mentios.get(uuid.toString());
		playerMentios = defaultMentio(playerMentios, player);
		for (Object raw : playerMentios)
		{
			String mentio = (String) raw;
			message.add("&2 -> &e" + mentio);
		}
		getLogger().message(sender, message.toArray(new String[] {}));
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray defaultMentio(JSONArray mentios, Player player) {
		if (mentios == null)
		{
			mentios = new JSONArray();
			mentios.add(player.getName());
			
			String displayName = player.getDisplayName().split(" ")[0].replaceAll("§[0-9a-fk-or]", "");
			if (!player.getName().equals(displayName))
				mentios.add(displayName);
		}
		return mentios;
	}
	
	public String modifyMessageWithMentio(CommandSender permholder, Player player, String message)
	{
		UUID uuid = player.getUniqueId();
		JSONArray playerMentios = (JSONArray) mentios.get(uuid.toString());
		playerMentios = defaultMentio(playerMentios, player);

		for (Object raw : playerMentios)
		{
			String mentio = (String) raw;
			
			String messageLC = message.toLowerCase();
			String mentioLC = mentio.toLowerCase();
			if (messageLC.contains(mentioLC))
			{				
				char color = 'r';
				int index = messageLC.indexOf(mentioLC);
				for (int i = index; i > 0; i--) {
					char next = messageLC.charAt(i-1);
					char cur = messageLC.charAt(i);
					if((next == '§' || next == '&') && ("" + cur).matches("[a-f0-9r]")) {
						color = cur;
						break;
					}	
				}
				return ChatAPI.colorify(permholder, message.substring(0, index)
						+ "§a§o" + message.substring(index, index + mentio.length()) + "§r" + (permholder.hasPermission(ChatAPI.PERMISSION_CHAT_COLOR)? "&" + color : ""))
						+ message.substring(index + mentio.length());				
			}
		}
		return null;
		
	}
	
	private void loadMentios()
	{
		mentios = JsonManager.getObject(mentioLocation);
		if (mentios == null)
			mentios = new JSONObject();
	}
	
	private void saveMentios()
	{
		JsonManager.save(mentios, mentioLocation);
	}
}

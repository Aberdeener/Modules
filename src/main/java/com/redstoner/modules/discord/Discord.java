package com.redstoner.modules.discord;

import java.io.IOException;
import java.security.SecureRandom;

import com.redstoner.exceptions.NonSaveableConfigException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.mysql.Config;
import com.redstoner.misc.mysql.MysqlHandler;
import com.redstoner.misc.mysql.elements.ConstraintOperator;
import com.redstoner.misc.mysql.elements.MysqlConstraint;
import com.redstoner.misc.mysql.elements.MysqlDatabase;
import com.redstoner.misc.mysql.elements.MysqlField;
import com.redstoner.misc.mysql.elements.MysqlTable;
import com.redstoner.misc.mysql.types.number.TinyInt;
import com.redstoner.misc.mysql.types.text.VarChar;
import com.redstoner.modules.Module;

import net.nemez.chatapi.click.Message;

@Commands(CommandHolderType.File)
@Version(major = 5, minor = 0, revision = 2, compatible = 4)
public class Discord implements Module {
	private MysqlTable table;

	private String inviteLink;

	private final String tokenCharacters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private SecureRandom rnd = new SecureRandom();

	@Override
	public boolean onEnable() {
		Config config;

		try {
			config = Config.getConfig("Discord.json");
		} catch (IOException | org.json.simple.parser.ParseException e1) {
			e1.printStackTrace();
			return false;
		}

		if (config == null || !config.containsKey("database") || !config.containsKey("table") || !config.containsKey("inviteLink")) {
			getLogger().error("Could not load the Discord config file, disabling!");

			config.put("database", "redstoner");
			config.put("table", "discord");
			config.put("inviteLink", "https://discord.gg/example");

			try {
                config.save();
            } catch (IOException | NonSaveableConfigException e) {}

			return false;
		}

		inviteLink = config.get("inviteLink");

		try {
			MysqlDatabase database = MysqlHandler.INSTANCE.getDatabase(config.get("database") + "?autoReconnect=true");

			MysqlField token = new MysqlField("token", new VarChar(8), false);
			MysqlField uuid = new MysqlField("uuid", new VarChar(36), false);
			MysqlField used = new MysqlField("used", new TinyInt(1), false);

			database.createTableIfNotExists((String) config.get("table"), token, uuid, used);

			table = database.getTable(config.get("table"));
		} catch (NullPointerException e) {
			getLogger().error("Could not use the Discord config, aborting!");
			return false;
		}

		return true;
	}

	@Command(hook = "discord")
	public void discord(CommandSender sender) {
		Player p = (Player) sender;
		String pUUID = p.getUniqueId().toString().replaceAll("-", "");

		String token = null;
		int tries = 0;

		Object[] existingToken = table.get("token", new MysqlConstraint("uuid", ConstraintOperator.EQUAL, pUUID));
		
		if (existingToken.length > 0)
			token = (String) existingToken[0];
		else {
			while (token == null) {
				token = randomToken(8);
				Object[] results = table.get("token", new MysqlConstraint("token", ConstraintOperator.EQUAL, token));

				if (results.length > 0) {
					token = null;
					tries++;
				}

				if (tries > 10) break;
			}

			if (token == null) {
				new Message(sender, null).appendText(
						"\n&4Could not find an unused token in 10 tries (a 1 in over 20 trillion chance)! Please take a screenshot and run the command again!")
						.send();
				return;
			}

			table.insert(token, pUUID, "0");

		}
		new Message(sender, null).appendText("\n&cRedstoner&7 has a &2Discord&7 Now! \nClick ")
				.appendLinkHover("&e" + inviteLink, inviteLink, "&aClick to Join").appendText("&7 to join. \n\nTo sync you rank, copy ")
				.appendSuggestHover("&e" + token, token, "&aClick to Copy").appendText("&7 into &3#rank-sync&7.\n").send();
	}

	private String randomToken(int length) {
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			sb.append(tokenCharacters.charAt(rnd.nextInt(tokenCharacters.length())));
		}

		return sb.toString();
	}
}

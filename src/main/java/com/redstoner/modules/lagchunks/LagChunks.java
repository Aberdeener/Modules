package com.redstoner.modules.lagchunks;

import com.nemez.cmdmgr.Command;
import com.nemez.cmdmgr.Command.AsyncType;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.modules.Module;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Commands (CommandHolderType.File)
@Version (major = 5, minor = 0, revision = 0, compatible = 4)
public class LagChunks implements Module {
	private List<LaggyChunk> laggyChunks = new ArrayList<>();

	private void scan(int amount) {
		laggyChunks.clear();

		for (World world : Bukkit.getServer().getWorlds()) {
			for (Chunk chunk : world.getLoadedChunks()) {
				if (chunk.getEntities().length > amount) {
					Location entLoc = chunk.getEntities()[0].getLocation();
					laggyChunks.add(new LaggyChunk(entLoc.getBlockX(), entLoc.getBlockY(), entLoc.getBlockZ(), world,
					                               chunk.getEntities().length
					));
				}
			}
		}
	}

	@Command (hook = "list_cmd")
	public void list(CommandSender sender) {
		if (laggyChunks.size() > 0) {
			ArrayList<String> message = new ArrayList<>();
			for (LaggyChunk lc : laggyChunks) {
				message.add("§b[§a" + laggyChunks.indexOf(lc) + "§b]: §a" + lc.x + "§7, §a" + lc.y + "§7, §a" + lc.z
				            + " §7(" + lc.world.getName() + ") §a- §b" + lc.amount + " entities");
			}
			message.add("§2-------------------");
			getLogger().message(sender, message.toArray(new String[] {}));
		} else
			getLogger().message(sender, true, "Couldn't find any chunks with that many entities.");
	}

	@Command (hook = "scan_cmd", async = AsyncType.ALWAYS)
	public void scan_cmd(CommandSender sender, int amount) {
		scan(amount);
		list(sender);
	}

	@Command (hook = "tp")
	public void tp(CommandSender sender, int number) {
		Player player = (Player) sender;
		if (number < laggyChunks.size()) {
			player.teleport(laggyChunks.get(number).getLocation());
			getLogger().message(player, "§aTeleported to chunk " + number + "!");
		} else {
			getLogger().message(sender, true, "§4Invalid chunk number! Use §e/lc list §4to show laggy chunks!");
		}
	}
}

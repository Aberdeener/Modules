package com.redstoner.modules.warn;

import org.bukkit.command.CommandSender;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.misc.CommandHolderType;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@Commands(CommandHolderType.File)
@Version(major = 4, minor = 1, revision = 0, compatible = 4)
public class Warn implements Module
{
	@Command(hook = "warn")
	public void warn_normal(CommandSender sender)
	{
		String name = Utils.getName(sender);
		Utils.broadcast(null, "§2Lag incoming! - §9" + name, null);
	}
	
	@Command(hook = "warnp")
	public void warn_possible(CommandSender sender)
	{
		String name = Utils.getName(sender);
		Utils.broadcast(null, "§2Possible lag incoming! - §9" + name, null);
	}
}

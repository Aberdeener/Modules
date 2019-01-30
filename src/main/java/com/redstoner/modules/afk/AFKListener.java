package com.redstoner.modules.afk;

import com.redstoner.misc.Utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class AFKListener implements Listener {
	private boolean
			move     = false,
			look     = false,
			chat     = false,
			interact = false,
			command  = false;

	protected void listenTo(boolean move, boolean look, boolean chat, boolean interact, boolean command) {
		this.move = move;
		this.look = look;
		this.chat = chat;
		this.interact = interact;
		this.command = command;

		String lt = "Listening to:";

		if (move) lt += " move";
		if (look) lt += " look";
		if (chat) lt += " chat";
		if (interact) lt+= " interact";
		if (command) lt += " command";

		Utils.broadcast(null, lt, recipient -> recipient.hasPermission("utils.afk.admin"));
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if (!AFKUtil.isAfk(player) || AFKUtil.isVanished(player) || AFKUtil.isIgnoringMovement(player))
			return;
		
		double distance = event.getFrom().distance(event.getTo());

		boolean moved  = distance > 0;
		boolean looked = (event.getFrom().getPitch() != event.getTo().getPitch()) || (event.getFrom().getYaw() != event.getTo().getYaw());

		if ((move && moved) || (look && looked)) AFKUtil.unAfk(event.getPlayer());
	}

	@EventHandler
	@SuppressWarnings("deprecation")
	public void onChat(PlayerChatEvent event) {
		if (chat) AFKUtil.checkedUnAfk(event.getPlayer());
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (interact) AFKUtil.checkedUnAfk(event.getPlayer());
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (command) AFKUtil.checkedUnAfk(event.getPlayer());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		AFKUtil.unAfk(event.getPlayer(), true);
	}
}
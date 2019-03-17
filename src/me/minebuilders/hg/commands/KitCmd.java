package me.minebuilders.hg.commands;

import org.bukkit.ChatColor;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Status;
public class KitCmd extends BaseCmd {

	public KitCmd() {
		forcePlayer = true;
		cmdName = "kit";
		forceInGame = true;
		argLength = 2;
		usage = "<kit>";
	}

	@Override
	public boolean run() {
		Status st = HG.plugin.players.get(player.getUniqueId()).getGame().getStatus();
		if (st == Status.WAITING || st == Status.COUNTDOWN) {
		HG.plugin.kit.setkit(player, args[1]);
		} else {
			player.sendMessage(ChatColor.RED + "You can't change your kit while the game is running!");
		}
		return true;
	}
}
package me.minebuilders.hg.commands;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class LeaveCmd extends BaseCmd {

	public LeaveCmd() {
		forcePlayer = true;
		cmdName = "leave";
		forceInGame = true;
		argLength = 1;
	}

	@Override
	public boolean run() {
		Game g = HG.plugin.players.get(player.getUniqueId()).getGame();
		g.leave(player);
		Util.msg(player, "&7&l[&3&lHungerGames&7&l] &cYou left &b&l" + g.getName() + "&c!");
		return true;
	}
}
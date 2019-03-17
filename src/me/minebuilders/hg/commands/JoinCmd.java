package me.minebuilders.hg.commands;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class JoinCmd extends BaseCmd {

	public JoinCmd() {
		forcePlayer = true;
		cmdName = "join";
		forceInGame = false;
		argLength = 2;
		usage = "<arena-name>";
	}

	@Override
	public boolean run() {

		if (HG.plugin.players.containsKey(player.getUniqueId())) {
			Util.msg(player, "&cYou're already in a game!");
		} else {
			Game g = HG.manager.getGame(args[1]);
			if (g != null && !g.getPlayers().contains(player.getUniqueId())) {
				g.join(player);
			} else {
				Util.msg(player, "&cThis arena does not exist!");
			}
		}
		return true;
	}
}
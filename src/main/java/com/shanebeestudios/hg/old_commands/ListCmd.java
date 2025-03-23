package com.shanebeestudios.hg.old_commands;

import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.util.Util;

public class ListCmd extends BaseCmd {

	public ListCmd() {
		forcePlayer = true;
		cmdName = "list";
		forceInGame = true;
		argLength = 1;
	}

	@Override
	public boolean run() {
		StringBuilder p = new StringBuilder();
        Game g = playerManager.getGame(player);
		for (String s : Util.convertUUIDListToStringList(g.getGamePlayerData().getPlayers())) {
			p.append("&6, &c").append(s);
		}
		p = new StringBuilder(p.substring(3));
		Util.scm(player, "&6Players:" + p);
		return true;
	}

}

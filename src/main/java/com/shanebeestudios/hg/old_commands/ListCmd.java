package com.shanebeestudios.hg.old_commands;

import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.api.util.Util;
import org.bukkit.entity.Player;

import java.util.List;

public class ListCmd extends BaseCmd {

	public ListCmd() {
		forcePlayer = true;
		cmdName = "list";
		forceInGame = true;
		argLength = 1;
	}

	@Override
	public boolean run() {
		StringBuilder builder = new StringBuilder();
        Game game = this.playerManager.getGame(this.player);
        if (game == null) return false;

		for (Player player : game.getGamePlayerData().getPlayers()) {
			builder.append("&6, &c").append(player.getName());
		}
		builder = new StringBuilder(builder.substring(3));
		Util.scm(this.player, "&6Players:" + builder);
		return true;
	}

}

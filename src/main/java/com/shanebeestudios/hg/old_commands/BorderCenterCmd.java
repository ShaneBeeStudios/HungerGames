package com.shanebeestudios.hg.old_commands;

import org.bukkit.Location;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.api.util.Util;

public class BorderCenterCmd extends BaseCmd {

	public BorderCenterCmd() {
		forcePlayer = true;
		cmdName = "bordercenter";
		forceInGame = false;
		argLength = 2;
		usage = "<arena-name>";
	}

	@Override
	public boolean run() {
		Game game = gameManager.getGame(args[1]);
		if (game != null) {
			String name = game.getGameArenaData().getName();
			Location l = player.getLocation();
			assert l.getWorld() != null;
			String loc = l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
			arenaConfig.getCustomConfig().set("arenas." + name + ".border.center", loc);
			game.getGameBorderData().setBorderCenter(l);
			arenaConfig.saveArenaConfig();
			Util.scm(player, lang.cmd_border_center.replace("<arena>", name));
		} else {
			Util.scm(player, lang.cmd_delete_noexist);
		}
		return true;
	}

}

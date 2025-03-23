package com.shanebeestudios.hg.old_commands;

import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.api.util.Util;

public class ChestRefillCmd extends BaseCmd {

	public ChestRefillCmd() {
		forcePlayer = true;
		cmdName = "chestrefill";
		forceInGame = false;
		argLength = 3;
		usage = "<arena-name> <time=remaining(30 second increments)>";
	}

	@Override
	public boolean run() {
		Game game = gameManager.getGame(args[1]);
		if (game != null) {
			String name = game.getGameArenaData().getName();
			int time = Integer.parseInt(args[2]);
			if (time % 30 != 0) {
				Util.sendMessage(player, "&c<time> must be in increments of 30");
				return true;
			}
			arenaConfig.getCustomConfig().set("arenas." + name + ".chest-refill", time);
			arenaConfig.saveArenaConfig();
			game.getGameArenaData().setChestRefillTime(time);
			Util.sendMessage(player, lang.cmd_chest_refill.replace("<arena>", name).replace("<sec>", String.valueOf(time)));
		} else {
			Util.sendMessage(player, lang.cmd_delete_noexist);
		}
		return true;
	}

}

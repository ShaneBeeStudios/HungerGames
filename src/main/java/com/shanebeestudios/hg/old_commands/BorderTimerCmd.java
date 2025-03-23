package com.shanebeestudios.hg.old_commands;

import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.api.util.Util;

public class BorderTimerCmd extends BaseCmd {

	public BorderTimerCmd() {
		forcePlayer = true;
		cmdName = "bordertimer";
		forceInGame = false;
		argLength = 4;
		usage = "<arena-name> <start=seconds> <end=seconds>";
	}

	@Override
	public boolean run() {
		Game game = gameManager.getGame(args[1]);
		if (game != null) {
			String name = game.getGameArenaData().getName();
			int start;
			int end;
			try {
				start = Integer.parseInt(args[2]);
				end = Integer.parseInt(args[3]);
				if (start % 30 != 0) {
					Util.sendMessage(player, sendHelpLine());
					Util.sendMessage(player, "&7<&rstart&7> &cneeds to be an increment of 30");
					return false;
				}
				if (start <= end) {
					Util.sendMessage(player, sendHelpLine());
					Util.sendMessage(player, "&7<&rstart&7> &cneeds to be greater than &7<&rend&7>");
					return false;
				}
			} catch (NumberFormatException e) {
				Util.sendMessage(player, sendHelpLine());
				return false;
			}
			arenaConfig.getCustomConfig().set("arenas." + name + ".border.countdown-start", start);
			arenaConfig.getCustomConfig().set("arenas." + name + ".border.countdown-end", end);
			arenaConfig.saveArenaConfig();
			game.getGameBorderData().setBorderTimer(start, end);
			Util.sendMessage(player, lang.cmd_border_timer.replace("<arena>", name).replace("<start>", args[2]).replace("<end>", args[3]));
		} else {
			Util.sendMessage(player, lang.cmd_delete_noexist);
		}
		return true;
	}

}

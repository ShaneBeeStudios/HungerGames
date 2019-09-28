package tk.shanebee.hg.commands;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

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
		Game game = HG.getPlugin().getManager().getGame(args[1]);
		if (game != null) {
			String name = game.getName();
			int start;
			int end;
			try {
				start = Integer.parseInt(args[2]);
				end = Integer.parseInt(args[3]);
				if (start % 30 != 0) {
					Util.scm(player, sendHelpLine());
					Util.scm(player, "&7<&rstart&7> &cneeds to be an increment of 30");
					return false;
				}
				if (start <= end) {
					Util.scm(player, sendHelpLine());
					Util.scm(player, "&7<&rstart&7> &cneeds to be greater than &7<&rend&7>");
					return false;
				}
			} catch (NumberFormatException e) {
				Util.scm(player, sendHelpLine());
				return false;
			}
			HG.getPlugin().getArenaConfig().getCustomConfig().set("arenas." + name + ".border.countdown-start", start);
			HG.getPlugin().getArenaConfig().getCustomConfig().set("arenas." + name + ".border.countdown-end", end);
			HG.getPlugin().getArenaConfig().saveCustomConfig();
			game.setBorderTimer(start, end);
			Util.scm(player, HG.getPlugin().getLang().cmd_border_timer.replace("<arena>", name).replace("<start>", args[2]).replace("<end>", args[3]));
		} else {
			Util.scm(player, HG.getPlugin().getLang().cmd_delete_noexist);
		}
		return true;
	}

}

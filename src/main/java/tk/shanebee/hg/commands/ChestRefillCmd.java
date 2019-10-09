package tk.shanebee.hg.commands;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

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
		Game game = HG.getPlugin().getManager().getGame(args[1]);
		if (game != null) {
			String name = game.getName();
			int time = Integer.valueOf(args[2]);
			if (time % 30 != 0) {
				Util.scm(player, "&c<time> must be in increments of 30");
				return true;
			}
			HG.getPlugin().getArenaConfig().getCustomConfig().set("arenas." + name + ".chest-refill", time);
			HG.getPlugin().getArenaConfig().saveCustomConfig();
			game.setChestRefillTime(time);
			Util.scm(player, HG.getPlugin().getLang().cmd_chest_refill.replace("<arena>", name).replace("<sec>", String.valueOf(time)));
		} else {
			Util.scm(player, HG.getPlugin().getLang().cmd_delete_noexist);
		}
		return true;
	}

}

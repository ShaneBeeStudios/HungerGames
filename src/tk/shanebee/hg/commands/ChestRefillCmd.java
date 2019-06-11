package tk.shanebee.hg.commands;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

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
		Game game = HG.manager.getGame(args[1]);
		if (game != null) {
			String name = game.getName();
			int time = Integer.valueOf(args[2]);
			HG.arenaconfig.getCustomConfig().set("arenas." + name + ".chest-refill", time);
			HG.arenaconfig.saveCustomConfig();
			game.setChestRefillTime(time);
			Util.scm(player, HG.lang.cmd_chest_refill.replace("<arena>", name).replace("<sec>", String.valueOf(time)));
		} else {
			Util.scm(player, HG.lang.cmd_delete_noexist);
		}
		return true;
	}

}

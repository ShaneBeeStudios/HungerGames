package tk.shanebee.hg.commands;

import tk.shanebee.hg.Util;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
public class KitCmd extends BaseCmd {

	public KitCmd() {
		forcePlayer = true;
		cmdName = "kit";
		forceInGame = true;
		argLength = 2;
		usage = "<kit>";
	}

	@Override
	public boolean run() {
		Status st = HG.plugin.players.get(player.getUniqueId()).getGame().getStatus();
		if (st == Status.WAITING || st == Status.COUNTDOWN) {
		HG.plugin.kit.setkit(player, args[1]);
		} else {
			Util.scm(player, HG.lang.cmd_kit_no_change);
		}
		return true;
	}
}
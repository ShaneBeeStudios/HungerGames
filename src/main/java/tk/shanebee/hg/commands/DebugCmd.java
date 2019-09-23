package tk.shanebee.hg.commands;

import tk.shanebee.hg.HG;

public class DebugCmd extends BaseCmd {

	public DebugCmd() {
		forcePlayer = false;
		cmdName = "debug";
		forceInGame = false;
		argLength = 2;
		usage = "<game>";
	}

	@Override
	public boolean run() {
			HG.plugin.getManager().runDebugger(sender, args[1]);
		return true;
	}
}
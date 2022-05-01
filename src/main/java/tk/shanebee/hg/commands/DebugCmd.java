package tk.shanebee.hg.commands;

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
			gameManager.runDebugger(sender, args[1]);
		return true;
	}
}
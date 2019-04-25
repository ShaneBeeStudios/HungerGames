package tk.shanebee.hg.commands;

import tk.shanebee.hg.Config;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

public class ReloadCmd extends BaseCmd {

	public ReloadCmd() {
		forcePlayer = true;
		cmdName = "reload";
		argLength = 1;
		forceInRegion = false;
	}

	@Override
	public boolean run() {
		Util.scm(player, HG.lang.cmd_reload_attempt);
		HG.plugin.stopAll();
		HG.arenaconfig.saveCustomConfig();
		HG.arenaconfig.reloadCustomConfig();
		HG.arenaconfig.load();
		Util.scm(player, HG.lang.cmd_reload_reloaded_arena);
		HG.plugin.kit.kititems.clear();
		HG.plugin.ism.setkits();
		Util.scm(player, HG.lang.cmd_reload_reloaded_kit);
		HG.plugin.items.clear();
		HG.ri.load();
		Util.scm(player, HG.lang.cmd_reload_reloaded_items);
		new Config(HG.plugin);
		Util.scm(player, HG.lang.cmd_reload_reloaded_config);
		
		Util.scm(player, HG.lang.cmd_reload_reloaded_success);
		return true;
	}
}

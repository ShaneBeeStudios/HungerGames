package tk.shanebee.hg.commands;

import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

public class ReloadCmd extends BaseCmd {

	public ReloadCmd() {
		forcePlayer = true;
		cmdName = "reload";
		argLength = 1;
		forceInRegion = false;
	}

	@Override
	public boolean run() {
		Util.scm(player, lang.cmd_reload_attempt);
		plugin.stopAll();
		//plugin.getArenaConfig().saveCustomConfig();
		plugin.getArenaConfig().reloadCustomConfig();
		plugin.getArenaConfig().load();
		Util.scm(player, lang.cmd_reload_reloaded_arena);
		plugin.getKitManager().getKits().clear();
		plugin.getItemStackManager().setKits();
		Util.scm(player, lang.cmd_reload_reloaded_kit);
		plugin.getItems().clear();
		plugin.getRandomItems().load();
		Util.scm(player, lang.cmd_reload_reloaded_items);
		new Config(plugin);
		Util.scm(player, lang.cmd_reload_reloaded_config);

		Util.scm(player, lang.cmd_reload_reloaded_success);
		return true;
	}

}

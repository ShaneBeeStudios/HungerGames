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
		Util.scm(player, HG.plugin.getLang().cmd_reload_attempt);
		HG.plugin.stopAll();
		//HG.plugin.getArenaConfig().saveCustomConfig();
		HG.plugin.getArenaConfig().reloadCustomConfig();
		HG.plugin.getArenaConfig().load();
		Util.scm(player, HG.plugin.getLang().cmd_reload_reloaded_arena);
		HG.plugin.getKitManager().getKits().clear();
		HG.plugin.getItemStackManager().setKits();
		Util.scm(player, HG.plugin.getLang().cmd_reload_reloaded_kit);
		HG.plugin.getItems().clear();
		HG.plugin.getRandomItems().load();
		Util.scm(player, HG.plugin.getLang().cmd_reload_reloaded_items);
		new Config(HG.plugin);
		Util.scm(player, HG.plugin.getLang().cmd_reload_reloaded_config);

		Util.scm(player, HG.plugin.getLang().cmd_reload_reloaded_success);
		return true;
	}

}

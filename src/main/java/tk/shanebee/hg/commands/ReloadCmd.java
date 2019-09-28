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
		Util.scm(player, HG.getPlugin().getLang().cmd_reload_attempt);
		HG.getPlugin().stopAll();
		//HG.getPlugin().getArenaConfig().saveCustomConfig();
		HG.getPlugin().getArenaConfig().reloadCustomConfig();
		HG.getPlugin().getArenaConfig().load();
		Util.scm(player, HG.getPlugin().getLang().cmd_reload_reloaded_arena);
		HG.getPlugin().getKitManager().getKits().clear();
		HG.getPlugin().getItemStackManager().setKits();
		Util.scm(player, HG.getPlugin().getLang().cmd_reload_reloaded_kit);
		HG.getPlugin().getItems().clear();
		HG.getPlugin().getRandomItems().load();
		Util.scm(player, HG.getPlugin().getLang().cmd_reload_reloaded_items);
		new Config(HG.getPlugin());
		Util.scm(player, HG.getPlugin().getLang().cmd_reload_reloaded_config);

		Util.scm(player, HG.getPlugin().getLang().cmd_reload_reloaded_success);
		return true;
	}

}

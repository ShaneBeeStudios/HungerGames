package tk.shanebee.hg.commands;

import org.bukkit.Location;
import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

public class BorderCenterCmd extends BaseCmd {

	public BorderCenterCmd() {
		forcePlayer = true;
		cmdName = "bordercenter";
		forceInGame = false;
		argLength = 2;
		usage = "<arena-name>";
	}

	@Override
	public boolean run() {
		Game game = HG.plugin.getManager().getGame(args[1]);
		if (game != null) {
			String name = game.getName();
			Location l = player.getLocation();
			assert l.getWorld() != null;
			String loc = l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
			HG.plugin.getArenaConfig().getCustomConfig().set("arenas." + name + ".border.center", loc);
			game.setBorderCenter(l);
			HG.plugin.getArenaConfig().saveCustomConfig();
			Util.scm(player, HG.plugin.getLang().cmd_border_center.replace("<arena>", name));
		} else {
			Util.scm(player, HG.plugin.getLang().cmd_delete_noexist);
		}
		return true;
	}

}

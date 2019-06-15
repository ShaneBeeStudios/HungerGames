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
		Game game = HG.manager.getGame(args[1]);
		if (game != null) {
			String name = game.getName();
			Location l = player.getLocation();
			assert l.getWorld() != null;
			String loc = l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":" + l.getYaw() + ":" + l.getPitch();
			HG.arenaconfig.getCustomConfig().set("arenas." + name + ".border.center", loc);
		} else {
			Util.scm(player, HG.lang.cmd_delete_noexist);
		}
		return true;
	}

}

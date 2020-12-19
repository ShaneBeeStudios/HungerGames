package tk.shanebee.hg.commands;

import org.bukkit.Location;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

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
		Game game = gameManager.getGame(args[1]);
		if (game != null) {
			String name = game.getGameArenaData().getName();
			Location l = player.getLocation();
			assert l.getWorld() != null;
			String loc = l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
			arenaConfig.getCustomConfig().set("arenas." + name + ".border.center", loc);
			game.getGameBorderData().setBorderCenter(l);
			arenaConfig.saveCustomConfig();
			Util.sendPrefixedMessage(player, lang.cmd_border_center.replace("<arena>", name));
		} else {
			Util.sendPrefixedMessage(player, lang.cmd_delete_noexist);
		}
		return true;
	}

}

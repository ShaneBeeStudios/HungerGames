package tk.shanebee.hg.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

public class SetLobbyWallCmd extends BaseCmd {

	public SetLobbyWallCmd() {
		forcePlayer = true;
		cmdName = "setlobbywall";
		forceInGame = false;
		argLength = 2;
		usage = "<arena-name>";
	}

	@Override
	public boolean run() {
		Game g = HG.manager.getGame(args[1]);
		if (g != null) {
			Block b = player.getTargetBlock(null, 6);
			if (Util.isWallSign(b.getType()) && g.setLobbyBlock((Sign)b.getState())) {
				Location l = b.getLocation();
				assert l.getWorld() != null;
				HG.arenaconfig.getCustomConfig().set(("arenas." + args[1] + "." + "lobbysign"), (l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ()));
				HG.arenaconfig.saveCustomConfig();
				Util.msg(player, HG.lang.cmd_lobbywall_set);
				HG.manager.checkGame(g, player);
			} else {
				Util.msg(player, HG.lang.cmd_lobbywall_notcorrect);
				Util.msg(player, HG.lang.cmd_lobbywall_format);
			}
		} else {
			player.sendMessage(HG.lang.cmd_delete_noexist);
		}
		return true;
	}
}
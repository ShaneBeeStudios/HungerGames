package tk.shanebee.hg.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

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
		Game g = HG.getPlugin().getManager().getGame(args[1]);
		if (g != null) {
			Block b = player.getTargetBlockExact(6);
			if (b !=  null && Util.isWallSign(b.getType()) && g.setLobbyBlock((Sign)b.getState())) {
				Location l = b.getLocation();
				assert l.getWorld() != null;
				HG.getPlugin().getArenaConfig().getCustomConfig().set(("arenas." + args[1] + "." + "lobbysign"), (l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ()));
				HG.getPlugin().getArenaConfig().saveCustomConfig();
				Util.scm(player, HG.getPlugin().getLang().cmd_lobbywall_set);
				HG.getPlugin().getManager().checkGame(g, player);
			} else {
				Util.scm(player, HG.getPlugin().getLang().cmd_lobbywall_notcorrect);
				Util.scm(player, HG.getPlugin().getLang().cmd_lobbywall_format);
			}
		} else {
			player.sendMessage(HG.getPlugin().getLang().cmd_delete_noexist);
		}
		return true;
	}
}
package me.minebuilders.hg.commands;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

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
			Block b = player.getTargetBlock((Set<Material>) null, 6);
			if (b.getType() == Material.WALL_SIGN && g.setLobbyBlock((Sign)b.getState())) {
				Location l = b.getLocation();
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
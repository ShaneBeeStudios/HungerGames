package com.shanebeestudios.hg.old_commands;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.game.Game;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

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
//        Game g = gameManager.getGame(args[1]);
//        if (g != null) {
//            Block b = player.getTargetBlockExact(6);
//            if (b != null && Tag.WALL_SIGNS.isTagged(b.getType()) && g.getGameBlockData().setLobbyBlock((Sign) b.getState())) {
//                Location l = b.getLocation();
//                assert l.getWorld() != null;
//                plugin.getArenaConfig().getCustomConfig().set(("arenas." + args[1] + "." + "lobbysign"), (l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ()));
//                plugin.getArenaConfig().saveArenaConfig();
//                Util.sendMessage(player, lang.cmd_lobbywall_set);
//                gameManager.checkGame(g, player);
//            } else {
//                Util.sendMessage(player, lang.cmd_lobbywall_notcorrect);
//                Util.sendMessage(player, lang.cmd_lobbywall_format);
//            }
//        } else {
//            player.sendMessage(lang.cmd_delete_noexist);
//        }
//        return true;
        return false;
    }
}

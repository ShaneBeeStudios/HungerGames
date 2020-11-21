package tk.shanebee.hg.commands;

import tk.shanebee.hg.Status;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

public class RdyCmd extends BaseCmd {

    public RdyCmd() {
        forcePlayer = true;
        cmdName = "rdy";
        forceInGame = true;
        argLength = 1;
        usage = "<rdy>";
    }

    @Override
    public boolean run() {
        Game game = playerManager.getPlayerData(player).getGame();
        Status st = game.getGameArenaData().getStatus();

        if (st == Status.WAITING || st == Status.COUNTDOWN) {
            boolean rdy = !playerManager.getPlayerData(player).getRdy();
            playerManager.getPlayerData(player).setRdy(rdy);
            if (rdy) {
                Util.scm(player, lang.cmd_rdy_for_game_to_start);
            } else {
                Util.scm(player, lang.cmd_not_rdy_for_game_to_start);
            }
        }
        return true;
    }

}

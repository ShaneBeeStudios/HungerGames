package tk.shanebee.hg.commands;

import tk.shanebee.hg.Status;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.gui.KitsGUI;
import tk.shanebee.hg.util.Util;

import java.util.ArrayList;

public class KitCmd extends BaseCmd {

    public KitCmd() {
        forcePlayer = true;
        cmdName = "kit";
        forceInGame = true;
        argLength = 1;
    }

    @Override
    public boolean run() {
        Game game = playerManager.getPlayerData(player).getGame();
        Status status = game.getGameArenaData().getStatus();
        if (!game.getKitManager().hasKits()) {
            Util.sendPrefixedMessage(player, lang.kit_disabled);
            return false;
        }
        if (status == Status.WAITING || status == Status.COUNTDOWN) {
            KitsGUI kitsGUI = new KitsGUI(game, player, new ArrayList<>(game.getKitManager().getKits().values()));
            kitsGUI.open(player);
        } else {
            Util.sendPrefixedMessage(player, lang.cmd_kit_no_change);
        }
        return true;
    }

}

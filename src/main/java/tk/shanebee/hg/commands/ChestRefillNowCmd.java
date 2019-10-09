package tk.shanebee.hg.commands;

import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

public class ChestRefillNowCmd extends BaseCmd {

    public ChestRefillNowCmd() {
        forcePlayer = false;
        cmdName = "chestrefillnow";
        forceInGame = false;
        argLength = 2;
        usage = "<arena-name>";
    }

    @Override
    public boolean run() {
        Game game = HG.getPlugin().getManager().getGame(args[1]);
        if (game != null) {
            if (game.getStatus() != Status.RUNNING) {
                Util.scm(sender, HG.getPlugin().getLang().listener_not_running);
                return true;
            }
            game.refillChests();
            Util.scm(sender, HG.getPlugin().getLang().cmd_chest_refill_now.replace("<arena>", game.getName()));
        } else {
            Util.scm(sender, HG.getPlugin().getLang().cmd_delete_noexist);
        }
        return true;
    }

}

package tk.shanebee.hg.commands;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

public class FakePlayerCmd extends BaseCmd {

    public FakePlayerCmd() {
        forcePlayer = false;
        cmdName = "fake";
        forceInGame = false;
        argLength = 2;
    }

    @Override
    public boolean run() {
        Game game = gameManager.getGame(args[1]);
        if (game != null) {
            game.addFakePlayer();
            Util.scm(sender, "Added fake player to " + args[1]);
        } else {
            Util.scm(sender, lang.cmd_delete_noexist);
        }
        return true;
    }

}

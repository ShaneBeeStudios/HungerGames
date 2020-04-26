package tk.shanebee.hg.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

import java.util.UUID;

public class DeleteCmd extends BaseCmd {

	public DeleteCmd() {
		forcePlayer = false;
		cmdName = "delete";
		forceInGame = false;
		argLength = 2;
		usage = "<arena-name>";
	}

	@Override
	public boolean run() {
		Game g = gameManager.getGame(args[1]);
		if (g != null) {
			try {
				Util.scm(sender, lang.cmd_delete_attempt.replace("<arena>", g.getName()));

				if (g.getStatus() == Status.FREE_ROAM || g.getStatus() == Status.RUNNING) {
					Util.scm(sender, "  &7- &cGame running! &aStopping..");
					g.forceRollback();
					g.stop(false);
				}
				if (!g.getPlayers().isEmpty()) {
					Util.scm(sender, lang.cmd_delete_kicking);
					for (UUID u : g.getPlayers()) {
						Player p = Bukkit.getPlayer(u);
						if (p != null) {
							g.leave(p, false);
						}
					}
				}
				arenaConfig.getCustomConfig().set("arenas." + args[1], null);
				arenaConfig.saveCustomConfig();
				Util.scm(sender, lang.cmd_delete_deleted.replace("<arena>", g.getName()));
				plugin.getGames().remove(g);
			} catch (Exception e) {
				Util.scm(sender, lang.cmd_delete_failed);
			}
		} else {
			Util.scm(sender, lang.cmd_delete_noexist);
		}
		return true;
	}
}

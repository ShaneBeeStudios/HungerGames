package tk.shanebee.hg.commands;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.Util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
		Game g = HG.plugin.getManager().getGame(args[1]);
		if (g != null) {
			try {
				Util.scm(sender, HG.plugin.lang.cmd_delete_attempt.replace("<arena>", g.getName()));

				if (g.getStatus() == Status.BEGINNING || g.getStatus() == Status.RUNNING) {
					Util.scm(sender, "  &7- &cGame running! &aStopping..");
					g.forceRollback();
					g.stop(false);
				}
				if (!g.getPlayers().isEmpty()) {
					Util.msg(sender, HG.plugin.lang.cmd_delete_kicking);
					for (UUID u : g.getPlayers()) {
						Player p = Bukkit.getPlayer(u);
						if (p != null) {
							g.leave(p, false);
						}
					}
				}
				HG.plugin.getArenaConfig().getCustomConfig().set("arenas." + args[1], null);
				HG.plugin.getArenaConfig().saveCustomConfig();
				Util.scm(sender, HG.plugin.lang.cmd_delete_deleted.replace("<arena>", g.getName()));
				HG.plugin.games.remove(g);
			} catch (Exception e) {
				Util.scm(sender, HG.plugin.lang.cmd_delete_failed);
			}
		} else {
			Util.scm(sender, HG.plugin.lang.cmd_delete_noexist);
		}
		return true;
	}
}
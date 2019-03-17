package me.minebuilders.hg.commands;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Status;
import me.minebuilders.hg.Util;

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
		Game g = HG.manager.getGame(args[1]);
		if (g != null) {
			try {
				Util.msg(sender, "&aAttempting to delete " + g.getName() + "!");

				if (g.getStatus() == Status.BEGINNING || g.getStatus() == Status.RUNNING) {
					Util.msg(sender, "  &7- &cGame running! &aStopping..");
					g.forceRollback();
					g.stop();
				}
				if (!g.getPlayers().isEmpty()) {
					Util.msg(sender, "  &7- &c&cPlayers detected! &aKicking..");
					for (UUID u : g.getPlayers()) {
						Player p = Bukkit.getPlayer(u);
						if (p != null) {
							g.leave(p);
						}
					}
				}
				HG.arenaconfig.getCustomConfig().set("arenas." + args[1], null);
				HG.arenaconfig.saveCustomConfig();
				HG.plugin.games.remove(g);
				Util.msg(sender, "&aSuccessfully deleted Hungergames arena!");
			} catch (Exception e) {
				Util.msg(sender, "&cFailed to delete arena!");
			}
		} else {
			sender.sendMessage("This arena does not exist!");
		}
		return true;
	}
}
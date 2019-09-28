package tk.shanebee.hg.commands;

import tk.shanebee.hg.*;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;
import tk.shanebee.hg.util.Vault;

public class JoinCmd extends BaseCmd {

	public JoinCmd() {
		forcePlayer = true;
		cmdName = "join";
		forceInGame = false;
		argLength = 2;
		usage = "<arena-name>";
	}

	@Override
	public boolean run() {

		if (HG.getPlugin().getPlayers().containsKey(player.getUniqueId())) {
			Util.scm(player, HG.getPlugin().getLang().cmd_join_in_game);
		} else {
			Game g = HG.getPlugin().getManager().getGame(args[1]);
			if (g != null && !g.getPlayers().contains(player.getUniqueId())) {
				if (Config.economy) {
					if (Vault.economy.getBalance(player) >= g.getCost()) {
						Vault.economy.withdrawPlayer(player, g.getCost());
						g.join(player);
					} else {
						Util.scm(player, HG.getPlugin().getLang().prefix +
								HG.getPlugin().getLang().cmd_join_no_money.replace("<cost>", String.valueOf(g.getCost())));
					}
				} else {
					g.join(player);
				}
			} else {
				Util.scm(player, HG.getPlugin().getLang().cmd_delete_noexist);
			}
		}
		return true;
	}
}
package tk.shanebee.hg.commands;

import org.bukkit.permissions.PermissionDefault;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.game.GameArenaData;
import tk.shanebee.hg.util.Util;

public class ListGamesCmd extends BaseCmd {

	public ListGamesCmd() {
		forcePlayer = false;
		cmdName = "listgames";
		forceInGame = false;
		argLength = 1;
		permissionDefault = PermissionDefault.TRUE;
	}

	@Override
	public boolean run() {
		Util.scm(sender, "&6&l Games:");
		for (Game game : plugin.getGames()) {
			GameArenaData gameArenaData = game.getGameArenaData();
			Util.sendPrefixedMessage(sender, " &4 - &6" + gameArenaData.getName() + "&4:&6" + gameArenaData.getStatus().getName());
		}
		return true;
	}
}

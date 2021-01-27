package tk.shanebee.hg.commands;

import org.bukkit.permissions.PermissionDefault;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

public class ListCmd extends BaseCmd {

	public ListCmd() {
		forcePlayer = true;
		cmdName = "list";
		forceInGame = true;
		argLength = 1;
		permissionDefault = PermissionDefault.TRUE;
	}

	@Override
	public boolean run() {
		StringBuilder p = new StringBuilder();
        Game g = playerManager.getGame(player);
		for (String s : Util.convertUUIDListToStringList(g.getGamePlayerData().getPlayers())) {
			p.append("&6, &c").append(s);
		}
		p = new StringBuilder(p.substring(3));
		Util.sendPrefixedMessage(player, "&6Players:" + p);
		return true;
	}

}

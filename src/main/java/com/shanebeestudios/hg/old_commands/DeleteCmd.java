package com.shanebeestudios.hg.old_commands;

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
//		Game g = gameManager.getGame(args[1]);
//		if (g != null) {
//			GamePlayerData gamePlayerData = g.getGamePlayerData();
//			GameArenaData gameArenaData = g.getGameArenaData();
//			try {
//				Util.scm(sender, lang.cmd_delete_attempt.replace("<arena>", gameArenaData.getName()));
//
//				if (gameArenaData.getStatus() == Status.BEGINNING || gameArenaData.getStatus() == Status.RUNNING) {
//					Util.scm(sender, "  &7- &cGame running! &aStopping..");
//					g.getGameBlockData().forceRollback();
//					g.stop(false);
//				}
//				if (!gamePlayerData.getPlayers().isEmpty()) {
//					Util.scm(sender, lang.cmd_delete_kicking);
//					for (UUID u : gamePlayerData.getPlayers()) {
//						Player p = Bukkit.getPlayer(u);
//						if (p != null) {
//							gamePlayerData.leave(p, false);
//						}
//					}
//				}
//				arenaConfig.getCustomConfig().set("arenas." + args[1], null);
//				arenaConfig.saveArenaConfig();
//				Util.scm(sender, lang.cmd_delete_deleted.replace("<arena>", gameArenaData.getName()));
//				plugin.getGames().remove(g);
//			} catch (Exception e) {
//				Util.scm(sender, lang.cmd_delete_failed);
//			}
//		} else {
//			Util.scm(sender, lang.cmd_delete_noexist);
//		}
		return true;
	}
}

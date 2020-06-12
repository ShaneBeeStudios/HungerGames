package tk.shanebee.hg.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;

import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

import java.util.List;

public class SetCarePackageCornerCmd extends BaseCmd {

	public SetCarePackageCornerCmd() {
		forcePlayer = true;
		cmdName = "packagecorner";
		argLength = 1;
		forceInRegion = true;
		usage = "<corner>";
	}

	@Override
	public boolean run() {
		// Gets arena, which player is in
		Game localGame = gameManager.getGame(player.getLocation());
		
		// Gets config of arena
		Configuration arenaConfiguration = arenaConfig.getCustomConfig();

		// Gets location of player
		Location currentPlayerLocation = player.getLocation();
		
		int playerBlockX = currentPlayerLocation.getBlockX();
		int playerBlockY = currentPlayerLocation.getBlockY();
		int playerBlockZ = currentPlayerLocation.getBlockZ();
	
		// Checks command argument
		int arg1;
		
		try {
			arg1 = Integer.parseInt(args[1]);
		} catch ( IndexOutOfBoundsException e ) {
			Util.scm(player, lang.cmd_packagecorner_specify);
			return true;
		}
		
		if (!(arg1 == 1 || arg1 == 2)) {
			
			// TODO change that to lang conversion
			Util.scm(player, lang.cmd_packagecorner_specify);
			return true;
			
		} else {
			
			if (arg1 == 1) {
				arenaConfiguration.set(
						"arenas." + localGame.getName() + ".care-package-bound.x", 
						playerBlockX);
				arenaConfiguration.set(
						"arenas." + localGame.getName() + ".care-package-bound.y", 
						playerBlockY);
				arenaConfiguration.set(
						"arenas." + localGame.getName() + ".care-package-bound.z", 
						playerBlockZ);
			}
			
			if (arg1 == 2) {
				arenaConfiguration.set(
						"arenas." + localGame.getName() + ".care-package-bound.x2", 
						playerBlockX);
				arenaConfiguration.set(
						"arenas." + localGame.getName() + ".care-package-bound.y2", 
						playerBlockY);
				arenaConfiguration.set(
						"arenas." + localGame.getName() + ".care-package-bound.z2", 
						playerBlockZ);
			}
			
		}
		
		localGame.setCarePackageCorner(arg1, playerBlockX, playerBlockY, playerBlockZ);
		arenaConfig.saveCustomConfig();
		
		Util.scm(player, lang.cmd_packagecorner_success.replace("<corner>", args[1]).replace("<arena>", localGame.getName()));
		
		gameManager.checkGame(localGame, player);
		return true;
	}
}

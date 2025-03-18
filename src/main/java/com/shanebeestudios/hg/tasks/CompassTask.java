package com.shanebeestudios.hg.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.HG;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.managers.PlayerManager;

import java.util.UUID;

public class CompassTask implements Runnable {

	private final PlayerManager playerManager;

	public CompassTask(HG plugin) {
		this.playerManager = plugin.getPlayerManager();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.getPlugin(), this, 25L, 25L);
	}

	@Override
	public void run() {
		for (Player p : Bukkit.getOnlinePlayers()) {

			if (p.getInventory().contains(Material.COMPASS)) {
				PlayerData pd = playerManager.getPlayerData(p.getUniqueId());

				if (pd != null) {

					String[] st = getNearestPlayer(p, pd);
					String info = ChatColor.translateAlternateColorCodes('&',
							HG.getPlugin().getLang().compass_nearest_player.replace("<player>", st[0]).replace("<distance>", st[1]));

					for (ItemStack it : p.getInventory()) {
						if (it != null && it.getType() == Material.COMPASS) {
							ItemMeta im = it.getItemMeta();
							im.setDisplayName(info);
							it.setItemMeta(im);
						}
					}
				}

			}
		}
	}

	private int cal(int i) {
		if (i < 0) {
			return -i;
		}
		return i;
	}

	private String[] getNearestPlayer(Player p, PlayerData pd) {

		Game g = pd.getGame();

		int x = p.getLocation().getBlockX();
		int y = p.getLocation().getBlockY();
		int z = p.getLocation().getBlockZ();

		int i = 200000;

		Player player = null;

		for (UUID u: g.getGamePlayerData().getPlayers()) {

			Player p2 = Bukkit.getPlayer(u);

			if (p2 != null && !p2.equals(p) && !pd.isOnTeam(u)) {

				Location l = p2.getLocation();

				int c = cal((int) (x - l.getX())) + cal((int) (y - l.getY())) + cal((int) (z - l.getZ()));

				if (i > c) {
					player = p2;
					i = c;
				}
			}
		}
		if (player != null) p.setCompassTarget(player.getLocation());

		return new String[] {(player==null?"none":player.getName()), String.valueOf(i)};
	}
}

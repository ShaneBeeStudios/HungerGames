package tk.shanebee.hg.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.MobEntry;
import tk.shanebee.hg.managers.MobManager;

import java.util.Random;
import java.util.UUID;

public class SpawnerTask implements Runnable {

	private Game game;
	private int id;
	private Random rg = new Random();
	private MobManager mobManager;

	public SpawnerTask(Game game, int i) {
		this.game = game;
		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.getPlugin(), this, i, i);
		this.mobManager = game.getMobManager();
	}

	@Override
	public void run() {
		for (UUID u : game.getPlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
				Location location = p.getLocation();
				World world = location.getWorld();
				int x = location.getBlockX();
				int z = location.getBlockZ();
				int y = location.getBlockY();

				int ran1 = getRandomNumber();
				int ran2 = getRandomNumber();

				x = x + ran1;
				z = z + ran2;

				location = getSafeLoc(world, x, y, z);

				if (location != null && game.isInRegion(location)) {
					MobEntry mobEntry;
					assert world != null;
					if (isDay(world)) {
						mobEntry = mobManager.getDayMobs().get(rg.nextInt(mobManager.getDayMobs().size()));
					} else {
						mobEntry = mobManager.getNightMobs().get(rg.nextInt(mobManager.getNightMobs().size()));
					}
					mobEntry.spawn(location);
					//w.spawnEntity(l, pickRandomMob(!isDay(w), rg.nextInt(10)));
				}
			}
		}
	}

	private boolean isDay(World w) {
		long time = w.getTime();
		return time < 12300 || time > 23850;
	}
	/*  NEW MOB SPAWNING ENGINE IN PLAY - leaving this here for a while, just in case
	private EntityType pickRandomMob(boolean isNight, int x) {
		if (isNight) {
			if (x < 3)
				return EntityType.ZOMBIE;
			if (x < 5)
				return EntityType.DROWNED;
			if (x < 7)
				return EntityType.SKELETON;
			if (x < 8)
				return EntityType.STRAY;

			return EntityType.CREEPER;
		} else {
			if (x < 2)
				return EntityType.COW;
			if (x < 5)
				return EntityType.PIG;
			if (x < 6)
				return EntityType.CHICKEN;
			if (x < 7)
				return EntityType.SHEEP;
			if (x < 8)
				return EntityType.PARROT;
			if (x < 9)
				return EntityType.SPIDER;

			return EntityType.CREEPER;
		}
	}

	 */

	private int getRandomNumber() {
		int r = rg.nextInt(25) - rg.nextInt(25);
		if (r <= 6 && r >= -6) {
			return getRandomNumber();
		}
		return r;
	}

	private Location getSafeLoc(World w, int x, int y, int z) {
		int trys = 30;

		while (trys > 0) {

			trys--;

			Material m = w.getBlockAt(x, y, z).getType();

			if (m.isSolid()) {
				y++;
			} else if (w.getBlockAt(x, y - 1, z).getType() == Material.AIR) {
				y--;
			} else if (w.getBlockAt(x, y + 1, z).getType().isSolid()) {
				int ran1 = getRandomNumber();
				int ran2 = getRandomNumber();

				x = x + ran1;
				z = z + ran2;
			} else {
				return new Location(w, x, y, z);
			}
		}
		return null;
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}

}

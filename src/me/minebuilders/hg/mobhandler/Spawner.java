package me.minebuilders.hg.mobhandler;

import java.util.Random;
import java.util.UUID;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Spawner implements Runnable{

	private Game g;
	private int id;
	private Random rg = new Random();

	public Spawner(Game game, int i) {
		this.g = game;
		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.plugin, this, i, i);
	}

	@Override
	public void run() {
		for (UUID u : g.getPlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
				Location l = p.getLocation();
				World w = l.getWorld();
				int x = l.getBlockX();
				int z = l.getBlockZ();
				int y = l.getBlockY();

				int ran1 = getRandomNumber();
				int ran2 = getRandomNumber();

				x = x + ran1;
				z = z + ran2;

				l = getSafeLoc(w, x, y, z);

				if (l != null && g.isInRegion(l))
					w.spawnEntity(l, pickRandomMob(!isDay(w), rg.nextInt(10)));
			}
		}
	}

	public boolean isDay(World w) {
		long time = w.getTime();
		return time < 12300 || time > 23850;
	}

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

	private int getRandomNumber() {
		int r = rg.nextInt(25) - rg.nextInt(25);
		if (r <= 6 && r >= -6) {
			return getRandomNumber();
		}
		return r;
	}

	@SuppressWarnings("deprecation")
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

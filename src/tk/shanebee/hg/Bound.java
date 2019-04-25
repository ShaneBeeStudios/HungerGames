package tk.shanebee.hg;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Bound {

	private int x;
	private int y;
	private int z;
	private int x2;
	private int y2;
	private int z2;
	private String world;

	public Bound(String world, int x, int y, int z, int x2, int y2, int z2) {
		this.world = world;
		this.x = Math.min(x,x2);
		this.y = Math.min(y, y2);
		this.z = Math.min(z, z2);
		this.x2 = Math.max(x,x2);
		this.y2 = Math.max(y, y2);
		this.z2 = Math.max(z, z2);
	}

	public Integer[] getRandomLocs() {
		Random r = new Random();
		return new Integer[] {r.nextInt(x2 - x + 1) + x, y2, r.nextInt(z2 - z + 1) + z};
	}

	public boolean isInRegion(Location loc) {
		if (!loc.getWorld().getName().equals(world)) return false;
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();
		if ((cx >= x && cx <= x2) && (cy >= y && cy <= y2) && (cz >= z && cz <= z2)) {
			return true;
		}
		return false;
	}

	public void removeEntities() {
		for (Entity e : Bukkit.getWorld(world).getEntities()) {
			if (isInRegion(e.getLocation()) && !(e instanceof Player)) {
				e.remove();
			}
		}
	}

	public ArrayList<Location> getBlocks(Material type) {
		World w = Bukkit.getWorld(world);
		ArrayList <Location> array = new ArrayList<Location>();
		for (int x3 = x; x3 <= x2; x3++) {
			for (int y3 = y; y3 <= y2; y3++) {
				for (int z3 = z; z3 <= z2; z3++) {
					Block b = w.getBlockAt(x3, y3, z3);
					if (b.getType() == type) {
						array.add(b.getLocation());
					}
				}
			}
		}
		return array;
	}

	public World getWorld() {
		return Bukkit.getWorld(world);
	}
}

package tk.shanebee.hg;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

@SuppressWarnings("WeakerAccess")
public class Bound {

	private int x;
	private int y;
	private int z;
	private int x2;
	private int y2;
	private int z2;
	private String world;

	/** Create a new bounding box between 2 sets of coordinates
	 * @param world World this bound is in
	 * @param x x coord of 1st corner of bound
	 * @param y y coord of 1st corner of bound
	 * @param z z coord of 1st corner of bound
	 * @param x2 x coord of 2nd corner of bound
	 * @param y2 y coord of 2nd corner of bound
	 * @param z2 z coord of 2nd corner of bound
	 */
	public Bound(String world, int x, int y, int z, int x2, int y2, int z2) {
		this.world = world;
		this.x = Math.min(x,x2);
		this.y = Math.min(y, y2);
		this.z = Math.min(z, z2);
		this.x2 = Math.max(x,x2);
		this.y2 = Math.max(y, y2);
		this.z2 = Math.max(z, z2);
	}

    /** Create a new bounding box between 2 locations (must be in same world)
     * @param location Location 1
     * @param location2 Location 2
     */
    public Bound(Location location, Location location2) {
        this(Objects.requireNonNull(location.getWorld()).getName(), ((int) location.getX()), ((int) location.getY()),
                ((int) location.getZ()), ((int) location2.getX()), ((int) location2.getY()), ((int) location2.getZ()));
    }

	public Integer[] getRandomLocs() {
		Random r = new Random();
		return new Integer[] {r.nextInt(x2 - x + 1) + x, y2, r.nextInt(z2 - z + 1) + z};
	}

	/** Check if a location is within the region of this bound
	 * @param loc Location to check
	 * @return True if location is within this bound
	 */
	public boolean isInRegion(Location loc) {
		if (!Objects.requireNonNull(loc.getWorld()).getName().equals(world)) return false;
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();
		return (cx >= x && cx <= x2) && (cy >= y && cy <= y2) && (cz >= z && cz <= z2);
	}

	void removeEntities() {
		for (Entity e : Objects.requireNonNull(Bukkit.getWorld(world)).getEntities()) {
			if (isInRegion(e.getLocation()) && !(e instanceof Player)) {
				e.remove();
			}
		}
	}

	/** Get location of all blocks of a type within a bound
	 * @param type Material type to check
	 * @return ArrayList of locations of all blocks of this type in this bound
	 */
	@SuppressWarnings("unused")
	public ArrayList<Location> getBlocks(Material type) {
		World w = Bukkit.getWorld(world);
		ArrayList <Location> array = new ArrayList<>();
		for (int x3 = x; x3 <= x2; x3++) {
			for (int y3 = y; y3 <= y2; y3++) {
				for (int z3 = z; z3 <= z2; z3++) {
					assert w != null;
					Block b = w.getBlockAt(x3, y3, z3);
					if (b.getType() == type) {
						array.add(b.getLocation());
					}
				}
			}
		}
		return array;
	}

	/** Get the world of this bound
	 * @return World of this bound
	 */
	public World getWorld() {
		return Bukkit.getWorld(world);
	}

	/** Get the greater corner of this bound
	 * @return Location of greater corner
	 */
	public Location getGreaterCorner() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	/** Get the lesser corner of this bound
	 * @return Location of lesser corner
	 */
	public Location getLesserCorner() {
		return new Location(Bukkit.getWorld(world), x2, y2, z2);
	}

	/** Get the center location of this bound
	 * @return The center location
	 */
	public Location getCenter() {
		BoundingBox box = new BoundingBox(x, y, z, x2, y2, z2);
		return new Location(this.getWorld(), box.getCenterX(), box.getCenterY(), box.getCenterZ());
	}

}

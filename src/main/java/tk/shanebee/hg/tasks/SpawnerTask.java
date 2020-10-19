package tk.shanebee.hg.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import tk.shanebee.hg.data.MobEntry;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.managers.MobManager;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SpawnerTask implements Runnable {

    private final Game game;
    private final int id;
    private final Random random = new Random();
    private final World world;
    private final List<MobEntry> dayMobs;
    private final List<MobEntry> nightMobs;

    public SpawnerTask(Game game, int i) {
        this.game = game;
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.getGameArenaData().getPlugin(), this, i, i);
        this.world = game.getGameArenaData().getBound().getWorld();
        MobManager mobManager = game.getMobManager();
        this.dayMobs = mobManager.getDayMobs();
        this.nightMobs = mobManager.getNightMobs();
    }

    @Override
    public void run() {
        for (UUID u : game.getGamePlayerData().getPlayers()) {
            Player player = Bukkit.getPlayer(u);
            if (player != null) {
                Location loc = player.getLocation().clone();

                loc = getSafeLoc(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

                if (loc != null && game.getGameArenaData().isInRegion(loc)) {
                    MobEntry mobEntry;
                    if (isDay(world)) {
                        mobEntry = dayMobs.get(random.nextInt(dayMobs.size()));
                    } else {
                        mobEntry = nightMobs.get(random.nextInt(nightMobs.size()));
                    }
                    mobEntry.spawn(loc);
                }
            }
        }
    }

    private boolean isDay(World w) {
        long time = w.getTime();
        return time < 12542 || time > 23460;
    }

    private int getRandomNumber() {
        int r = random.nextInt(20) + 6;
        return random.nextBoolean() ? r : -r;

    }

    private Location getSafeLoc(World w, int x, int y, int z) {
        int trys = 30;

        x = x + getRandomNumber();
        z = z + getRandomNumber();

        while (trys > 0) {
            trys--;

            Material material = w.getBlockAt(x, y, z).getType();
            Material below = w.getBlockAt(x, y - 1, z).getType();
            Material above = w.getBlockAt(x, y + 1, z).getType();

            if (material.isSolid()) {
                y++;
            } else if (below == Material.AIR) {
                y--;
            } else if (below == Material.WATER || below == Material.LAVA || above.isSolid()) {
                x = x + getRandomNumber();
                z = z + getRandomNumber();
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

package com.shanebeestudios.hg.plugin.tasks;

import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
import com.shanebeestudios.hg.game.GameEntityData;
import com.shanebeestudios.hg.game.GamePlayerData;
import com.shanebeestudios.hg.game.GameRegion;
import com.shanebeestudios.hg.plugin.configs.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class MobSpawnerTask implements Runnable {

    private final GamePlayerData gamePlayerData;
    private final GameArenaData gameArenaData;
    private final GameEntityData gameEntityData;
    private final GameRegion gameRegion;
    private final int taskId;
    private final Random random = new Random();
    private final World world;
    private final int cap = Config.MOBS_SPAWN_CAP_PER_PLAYER;

    public MobSpawnerTask(Game game) {
        this.gamePlayerData = game.getGamePlayerData();
        this.gameArenaData = game.getGameArenaData();
        this.gameEntityData = game.getGameEntityData();
        this.gameRegion = game.getGameArenaData().getGameRegion();
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.getGameArenaData().getPlugin(), this, Config.MOBS_SPAWN_INTERVAL, Config.MOBS_SPAWN_INTERVAL);
        this.world = game.getGameArenaData().getGameRegion().getWorld();
    }

    @Override
    public void run() {
        int entityCount = this.gameRegion.getEntityCount();
        int playerCap = this.gamePlayerData.getPlayers().size() * this.cap;
        // Prevent spawning if cap already reached
        if (entityCount > playerCap) return;

        for (Player player : this.gamePlayerData.getPlayers()) {
            // Keep checking cap as we spawn more
            if (entityCount > playerCap) return;

            Location spawnLocation = getSafeSpawnLocation(this.world, player.getLocation().clone());
            if (spawnLocation != null && this.gameArenaData.isInRegion(spawnLocation)) {
                if (this.gameEntityData.spawnMob(spawnLocation, isDayTime())) {
                    entityCount++;
                }
            }
        }
    }

    private boolean isDayTime() {
        long time = this.world.getTime();
        return time < 12542 || time > 23460;
    }

    private int getRandomNumber() {
        int randomInt = this.random.nextInt(20) + 6;
        return this.random.nextBoolean() ? randomInt : -randomInt;

    }

    private @Nullable Location getSafeSpawnLocation(World world, Location location) {
        int trys = 30;

        int x = location.getBlockX() + getRandomNumber();
        int y = location.getBlockY();
        int z = location.getBlockZ() + getRandomNumber();

        while (trys > 0) {
            trys--;

            Material material = world.getBlockAt(x, y, z).getType();
            Material below = world.getBlockAt(x, y - 1, z).getType();
            Material above = world.getBlockAt(x, y + 1, z).getType();

            if (material.isSolid()) {
                y++;
            } else if (below == Material.AIR) {
                y--;
            } else if (below == Material.WATER || below == Material.LAVA || above.isSolid()) {
                x = x + getRandomNumber();
                z = z + getRandomNumber();
            } else {
                return new Location(world, x, y, z);
            }
        }
        return null;
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

}

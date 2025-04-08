package com.shanebeestudios.hg.plugin.tasks;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.configs.Language;
import com.shanebeestudios.hg.api.data.PlayerData;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.plugin.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class NearestPlayerCompassTask implements Runnable {

    private final Game game;
    private final Language lang;
    private final PlayerManager playerManager;
    private final int taskId;

    public NearestPlayerCompassTask(Game game) {
        this.game = game;
        this.lang = game.getPlugin().getLang();
        this.playerManager = game.getPlugin().getPlayerManager();
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(HungerGames.getPlugin(), this, 10L, 10L);
    }

    @Override
    public void run() {
        for (Player player : this.game.getGamePlayerData().getPlayers()) {
            if (player.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
                PlayerData playerData = this.playerManager.getPlayerData(player.getUniqueId());
                if (playerData == null) continue;

                Player nearest = getNearestPlayer(player);
                if (nearest == null) continue;

                String info = this.lang.compass_nearest_player
                    .replace("<player>", nearest.getName())
                    .replace("<distance>", String.format("%.2f", nearest.getLocation().distance(player.getLocation())));

                player.sendActionBar(Util.getMini(info));
                player.setCompassTarget(nearest.getLocation());
            }
        }
    }

    private @Nullable Player getNearestPlayer(Player player) {
        double distance = 20000;
        Player nearest = null;
        for (Player potential : this.game.getGamePlayerData().getPlayers()) {
            if (potential != player) {
                double d = player.getLocation().distanceSquared(potential.getLocation());
                if (d < distance) {
                    distance = d;
                    nearest = potential;
                }
            }
        }
        return nearest;
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

}

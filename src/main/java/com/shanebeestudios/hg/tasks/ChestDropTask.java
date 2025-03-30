package com.shanebeestudios.hg.tasks;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.game.GameRegion;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.plugin.listeners.ChestDrop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChestDropTask implements Runnable {

    private final Game game;
    private final Language lang;
    private final int taskId;
    private final List<ChestDrop> chests = new ArrayList<>();

    public ChestDropTask(Game game) {
        this.game = game;
        HungerGames plugin = HungerGames.getPlugin();
        this.lang = plugin.getLang();
        this.taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, Config.randomChestInterval, Config.randomChestInterval);
    }

    public void run() {
        GameRegion gameRegion = game.getGameArenaData().getGameRegion();
        Integer[] i = gameRegion.getRandomLocs();

        int x = i[0];
        int y = i[1];
        int z = i[2];
        World w = gameRegion.getWorld();

        while (w.getBlockAt(x, y, z).getType() == Material.AIR) {
            y--;

            if (y <= 0) {
                i = gameRegion.getRandomLocs();

                x = i[0];
                y = i[1];
                z = i[2];
            }
        }

        y = y + 10;

        Location l = new Location(w, x, y, z);

        FallingBlock fb = w.spawnFallingBlock(l, Bukkit.getServer().createBlockData(Material.STRIPPED_SPRUCE_WOOD));

        this.chests.add(new ChestDrop(fb));

        for (Player player : this.game.getGamePlayerData().getPlayers()) {
            Util.sendMessage(player, this.lang.chest_drop_1);
            Util.sendMessage(player, this.lang.chest_drop_2
                .replace("<x>", String.valueOf(x))
                .replace("<y>", String.valueOf(y))
                .replace("<z>", String.valueOf(z)));
            Util.sendMessage(player, this.lang.chest_drop_1);
        }
    }

    public void shutdown() {
        Bukkit.getScheduler().cancelTask(taskId);
        for (ChestDrop cd : this.chests) {
            if (cd != null) cd.remove();
        }
    }
}

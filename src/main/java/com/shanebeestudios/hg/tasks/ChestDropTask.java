package com.shanebeestudios.hg.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.game.Bound;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.listeners.ChestDrop;
import com.shanebeestudios.hg.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChestDropTask implements Runnable {

    private final Game game;
    private final int timerID;
    private final List<ChestDrop> chests = new ArrayList<>();

    public ChestDropTask(Game game) {
        this.game = game;
        timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(HungerGames.getPlugin(), this, Config.randomChestInterval, Config.randomChestInterval);
    }

    public void run() {
        Bound bound = game.getGameArenaData().getBound();
        Integer[] i = bound.getRandomLocs();

        int x = i[0];
        int y = i[1];
        int z = i[2];
        World w = bound.getWorld();

        while (w.getBlockAt(x, y, z).getType() == Material.AIR) {
            y--;

            if (y <= 0) {
                i = bound.getRandomLocs();

                x = i[0];
                y = i[1];
                z = i[2];
            }
        }

        y = y + 10;

        Location l = new Location(w, x, y, z);

        FallingBlock fb = w.spawnFallingBlock(l, Bukkit.getServer().createBlockData(Material.STRIPPED_SPRUCE_WOOD));

        chests.add(new ChestDrop(fb));

        for (UUID u : game.getGamePlayerData().getPlayers()) {
            Player p = Bukkit.getPlayer(u);
            if (p != null) {
                Util.scm(p, HungerGames.getPlugin().getLang().chest_drop_1);
                Util.scm(p, HungerGames.getPlugin().getLang().chest_drop_2
                        .replace("<x>", String.valueOf(x))
                        .replace("<y>", String.valueOf(y))
                        .replace("<z>", String.valueOf(z)));
                Util.scm(p, HungerGames.getPlugin().getLang().chest_drop_1);
            }
        }
    }

    public void shutdown() {
        Bukkit.getScheduler().cancelTask(timerID);
        for (ChestDrop cd : chests) {
            if (cd != null) cd.remove();
        }
    }
}

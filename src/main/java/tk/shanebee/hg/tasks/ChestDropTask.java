package tk.shanebee.hg.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import tk.shanebee.hg.listeners.ChestDrop;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

public class ChestDropTask implements Runnable {

    private Game g;
    private int timerID;
    private List<ChestDrop> chests = new ArrayList<>();

    public ChestDropTask(Game g) {
        this.g = g;
        timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(HG.getPlugin(), this, Config.randomChestInterval, Config.randomChestInterval);
    }

    public void run() {
        Integer[] i = g.getRegion().getRandomLocs();

        int x = i[0];
        int y = i[1];
        int z = i[2];
        World w = g.getRegion().getWorld();

        while (w.getBlockAt(x, y, z).getType() == Material.AIR) {
            y--;

            if (y <= 0) {
                i = g.getRegion().getRandomLocs();

                x = i[0];
                y = i[1];
                z = i[2];
            }
        }

        y = y + 10;

        Location l = new Location(w, x, y, z);

        FallingBlock fb = w.spawnFallingBlock(l, Bukkit.getServer().createBlockData(Material.STRIPPED_SPRUCE_WOOD));

        chests.add(new ChestDrop(fb));

        for (UUID u : g.getGamePlayerData().getPlayers()) {
            Player p = Bukkit.getPlayer(u);
            if (p != null) {
                Util.scm(p, HG.getPlugin().getLang().chest_drop_1);
                Util.scm(p, HG.getPlugin().getLang().chest_drop_2
                        .replace("<x>", String.valueOf(x))
                        .replace("<y>", String.valueOf(y))
                        .replace("<z>", String.valueOf(z)));
                Util.scm(p, HG.getPlugin().getLang().chest_drop_1);
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

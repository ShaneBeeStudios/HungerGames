package com.shanebeestudios.hg.game;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.util.Util;

import java.util.UUID;

/**
 * Data holder for {@link BossBar BossBars}
 */
public class GameBarData extends Data {

    private BossBar bar;
    private final String title;

    protected GameBarData(Game game) {
        super(game);
        this.title = HungerGames.getPlugin().getLang().bossbar;
    }

    /**
     * Create a new {@link BossBar} to associate with this bar
     *
     * @param time Time to be displayed on the bar
     */
    public void createBossbar(int time) {
        int min = (time / 60);
        int sec = (time % 60);
        String title = this.title.replace("<min>", String.valueOf(min)).replace("<sec>", String.valueOf(sec));
        bar = Bukkit.createBossBar(Util.getColString(title), BarColor.GREEN, BarStyle.SEGMENTED_20);
        for (UUID uuid : getGame().getGamePlayerData().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            assert player != null;
            bar.addPlayer(player);
        }
        for (UUID uuid : getGame().getGamePlayerData().getSpectators()) {
            Player player = Bukkit.getPlayer(uuid);
            assert player != null;
            bar.addPlayer(player);
        }
    }

    /**
     * Update this bar with the remaining time
     * <p>Will update the time of the current {@link BossBar} if one is available</p>
     *
     * @param remaining Remaining time to show on bar
     */
    public void bossbarUpdate(int remaining) {
        if (bar == null) return;
        double remain = ((double) remaining) / ((double) getGame().gameArenaData.timer);
        int min = (remaining / 60);
        int sec = (remaining % 60);
        String title = this.title.replace("<min>", String.valueOf(min)).replace("<sec>", String.valueOf(sec));
        bar.setTitle(Util.getColString(title));
        bar.setProgress(remain);
        if (remain <= 0.5 && remain >= 0.2)
            bar.setColor(BarColor.YELLOW);
        if (remain < 0.2)
            bar.setColor(BarColor.RED);
    }

    /**
     * Clear the bar
     * <p>Will remove all players and delete the current {@link BossBar}</p>
     */
    public void clearBar() {
        if (bar != null) {
            bar.removeAll();
        }
        bar = null;
    }

    /**
     * Remove a player from this bar
     * <p>Will remove the player from the current {@link BossBar} if one is available</p>
     *
     * @param player Player to remove
     */
    public void removePlayer(Player player) {
        if (bar != null) {
            bar.removePlayer(player);
        }
    }

    /**
     * Add a player to this bar
     * <p>Will add the player to the current {@link BossBar} if one is available</p>
     *
     * @param player Player to add
     */
    public void addPlayer(Player player) {
        if (bar != null) {
            bar.addPlayer(player);
        }
    }

    /**
     * Get the associated {@link BossBar} if one exists
     *
     * @return Associated bossbar
     */
    public BossBar getBossBar() {
        return this.bar;
    }

}

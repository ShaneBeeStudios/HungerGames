package com.shanebeestudios.hg.api.game;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import org.bukkit.entity.Player;

/**
 * Data holder for {@link BossBar BossBars}
 */
public class GameBarData extends Data {

    private BossBar bar;
    private final String title;

    GameBarData(Game game) {
        super(game);
        this.title = HungerGames.getPlugin().getLang().bossbar;
    }

    /**
     * Create a new {@link BossBar} to associate with this bar
     *
     * @param time Time to be displayed on the bar
     */
    public void createBossBar(int time) {
        int min = (time / 60);
        int sec = (time % 60);
        String title = formatTitle(min, sec);
        this.bar = BossBar.bossBar(Util.getMini(title), 1f, Color.GREEN, Overlay.NOTCHED_20);
        for (Player player : getGame().getGamePlayerData().getPlayers()) {
            this.bar.addViewer(player);
        }
        for (Player player : getGame().getGamePlayerData().getSpectators()) {
            this.bar.addViewer(player);
        }
    }

    /**
     * Update this bar with the remaining time
     * <p>Will update the time of the current {@link BossBar} if one is available</p>
     *
     * @param remaining Remaining time to show on bar
     */
    public void bossBarUpdate(int remaining) {
        if (this.bar == null) return;
        float remain = ((float) remaining) / ((float) getGame().gameArenaData.timer);
        int min = (remaining / 60);
        int sec = (remaining % 60);
        String title = formatTitle(min, sec);
        this.bar.name(Util.getMini(title));
        this.bar.progress(remain);
        if (remain <= 0.5 && remain >= 0.2)
            this.bar.color(Color.YELLOW);
        if (remain < 0.2)
            this.bar.color(Color.RED);
    }

    /**
     * Clear the bar
     * <p>Will remove all players and delete the current {@link BossBar}</p>
     */
    public void clearBar() {
        if (this.bar != null) {
            this.bar.viewers().forEach(player ->
                this.bar.removeViewer((Audience) player));
        }
        this.bar = null;
    }

    /**
     * Remove a player from this bar
     * <p>Will remove the player from the current {@link BossBar} if one is available</p>
     *
     * @param player Player to remove
     */
    public void removePlayer(Player player) {
        if (this.bar != null) {
            this.bar.removeViewer(player);
        }
    }

    /**
     * Add a player to this bar
     * <p>Will add the player to the current {@link BossBar} if one is available</p>
     *
     * @param player Player to add
     */
    public void addPlayer(Player player) {
        if (this.bar != null) {
            this.bar.addViewer(player);
        }
    }

    private String formatTitle(int min, int sec) {
        return this.title
            .replace("<min>", String.valueOf(min))
            .replace("<sec>", String.valueOf(sec));
    }

}

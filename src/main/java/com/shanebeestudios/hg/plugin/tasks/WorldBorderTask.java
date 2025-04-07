package com.shanebeestudios.hg.plugin.tasks;

import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.plugin.configs.Language;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.game.GameArenaData;
import com.shanebeestudios.hg.api.game.GameBorderData;
import com.shanebeestudios.hg.api.game.GamePlayerData;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;

@SuppressWarnings("UnstableApiUsage")
public class WorldBorderTask implements Runnable {

    private final Game game;
    private final Language lang;
    private final GameArenaData gameArenaData;
    private final GamePlayerData gamePlayerData;
    private final GameBorderData gameBorderData;
    private final DamageSource damageSource;
    private final int taskId;

    private boolean hasStartedClosingIn = false;

    public WorldBorderTask(Game game) {
        this.game = game;
        this.lang = game.getPlugin().getLang();
        this.gameArenaData = game.getGameArenaData();
        this.gamePlayerData = game.getGamePlayerData();
        this.gameBorderData = game.getGameBorderData();

        this.damageSource = DamageSource.builder(DamageType.OUTSIDE_BORDER).build();
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.gamePlayerData.getPlugin(), this, 20, 20);
    }

    @Override
    public void run() {
        // Since it's a virtual world border we need to damage manually
        WorldBorder worldBorder = this.gameBorderData.getWorldBorder();
        this.gamePlayerData.getPlayers().forEach(player -> {
            if (!worldBorder.isInside(player.getLocation())) {
                player.damage(1.0, this.damageSource);
            }
        });

        if (!this.hasStartedClosingIn) {
            if (Config.WORLD_BORDER_INITIATE_ON_START) {
                this.hasStartedClosingIn = true;
                this.gameBorderData.startShrinking(this.gameArenaData.getTimer() - this.gameBorderData.getBorderCountdownEnd());
            } else if (this.game.getRemainingTime() <= this.gameBorderData.getBorderCountdownStart()) {
                this.hasStartedClosingIn = true;
                int closingIn = this.gameBorderData.getBorderCountdownStart() - this.gameBorderData.getBorderCountdownEnd();
                this.gameBorderData.startShrinking(closingIn);
                this.gamePlayerData.messageAllActivePlayers(this.lang.game_border_closing.replace("<seconds>", String.valueOf(closingIn)));

            }
        }
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

}

package tk.shanebee.hg.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.Language;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

import java.util.UUID;

public class FreeRoamTask implements Runnable {

    private final Game game;
    private final int id;
    private final int roamTime;

    public FreeRoamTask(Game game) {
        this.game = game;
        this.roamTime = game.getGameArenaData().getRoamTime();

        Language lang = HG.getPlugin().getLang();
        String gameStarted = lang.roam_game_started;
        String roamTimeString = lang.roam_time.replace("<roam>", "" + roamTime);

        for (UUID u : game.getGamePlayerData().getPlayers()) {
            Player player = Bukkit.getPlayer(u);
            if (player != null) {
                Util.scm(player, gameStarted);
                if (roamTime > 0) {
                    Util.scm(player, roamTimeString);
                }
                player.setHealth(20);
                player.setFoodLevel(20);
                game.getGamePlayerData().unFreeze(player);
            }
        }
        this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(HG.getPlugin(), this, roamTime * 20L);
    }

    @Override
    public void run() {
        if (roamTime > 0) {
            game.getGamePlayerData().msgAll(HG.getPlugin().getLang().roam_finished);
        }
        game.startGame();
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }

}

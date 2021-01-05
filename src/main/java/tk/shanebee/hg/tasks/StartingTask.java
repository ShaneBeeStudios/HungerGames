package tk.shanebee.hg.tasks;

import org.bukkit.Bukkit;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.data.Language;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.game.GameArenaData;
import tk.shanebee.hg.util.Util;

public class StartingTask implements Runnable {

    private int timer;
    private final int id;
    private final Game game;
    private final Language lang;

    public StartingTask(Game game) {
        HG plugin = HG.getPlugin();
        GameArenaData arenaData = game.getGameArenaData();
        this.timer = arenaData.getCountDownTime();
        this.game = game;
        this.lang = plugin.getLang();
        String name = arenaData.getName();
        String broadcast = lang.game_started
                .replace("<arena>", name)
                .replace("<seconds>", "" + timer);
        if (Config.broadcastJoinMessages) {
            Util.broadcast(broadcast);
            Util.broadcast(lang.game_join.replace("<arena>", name));
        } else {
            this.game.getGamePlayerData().msgAll(broadcast);
        }
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 5 * 20L, 20L);
    }

    @Override
    public void run() {
        timer -= 1;

        if (timer <= 0) {
            stop();
            game.startFreeRoam();
        } else if (timer % 10 == 0) {
            game.getGamePlayerData().msgAll(lang.game_countdown.replace("<timer>", "" + timer));
        }
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }

}

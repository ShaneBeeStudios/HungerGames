package tk.shanebee.hg.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.game.Game;

public class CountdownTask extends BukkitRunnable {

    private int timer;
    private Game game;

    public CountdownTask(Game game) {
        this.timer = 15;
        this.game = game;
        this.runTaskTimer(HG.getPlugin(), 5 * 20L, 5 * 20L);
    }

    @Override
    public void run() {
        if (this.timer > 0) {
            game.msgAllInGame(HG.getPlugin().getLang().game_countdown.replace("<timer>", String.valueOf(timer)));
        } else {
            game.startFreeRoam();
            this.cancel();
        }
        this.timer -= 5;
    }

}

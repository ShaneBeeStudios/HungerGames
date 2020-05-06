package tk.shanebee.hg.tasks;

import org.bukkit.Chunk;
import org.bukkit.scheduler.BukkitRunnable;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.game.Game;

import java.util.Iterator;

public class PrepareGameTask extends BukkitRunnable {

    private final Game game;
    private final HG plugin;
    private final Iterator<Chunk> chunks;

    public PrepareGameTask(HG plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
        game.setStatus(Status.LOADING);
        this.chunks = game.getBound().getChunks().iterator();
        this.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void run() {
        int i = 0;
        while (i < 5 && chunks.hasNext()) {
            chunks.next().addPluginChunkTicket(this.plugin);
            i++;
        }
        if (!chunks.hasNext()) {
            this.cancel();
            this.game.setStatus(Status.WAITING);
        }
    }

}

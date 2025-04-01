package com.shanebeestudios.hg.plugin.tasks;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.Constants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
import org.bukkit.block.TileState;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@SuppressWarnings("UnstableApiUsage")
public class ChestDropChestTask implements Runnable {

    private final Location startLocation;
    private BlockDisplay display;
    private final int taskId;

    public ChestDropChestTask(Location location) {
        this.startLocation = location.clone().add(0, 20, 0);
        this.startLocation.setYaw(180);
        this.startLocation.setPitch(0);
        this.display = location.getWorld().spawn(this.startLocation, BlockDisplay.class, blockDisplay -> {
            blockDisplay.setBlock(BlockType.CHEST.createBlockData(chest -> chest.setFacing(BlockFace.NORTH)));
            blockDisplay.setTeleportDuration(2);
        });
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(HungerGames.getPlugin(), this,
            2, 2);
    }

    @Override
    public void run() {
        if (this.startLocation.getBlock().getRelative(BlockFace.DOWN).isSolid()) {
            this.startLocation.add(-1, 0, -1);
            this.startLocation.getBlock().setBlockData(this.display.getBlock());
            if (this.startLocation.getBlock().getState() instanceof TileState state) {
                PersistentDataContainer pdc = state.getPersistentDataContainer();
                pdc.set(Constants.CHEST_DROP_BLOCK, PersistentDataType.BOOLEAN, true);
                state.update();
            }

            stop();
        } else {
            this.startLocation.add(0, -1, 0);
            this.display.teleport(this.startLocation);
        }
    }

    public void stop() {
        if (this.display != null && this.display.isValid()) this.display.remove();
        this.display = null;
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

}

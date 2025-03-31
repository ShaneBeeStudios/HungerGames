package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.api.util.BlockUtils;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameBlockData;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class GameBlockListener extends GameListenerBase {

    public GameBlockListener(HungerGames plugin) {
        super(plugin);
        BlockUtils.setupBuilder();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAttack(EntityDamageByEntityEvent event) {
        // Stop players from removing items from item frames
        if (event.getEntity() instanceof Hanging hanging) {
            handleItemFrame(hanging, event, !Config.itemframe_take);
        }
    }

    @EventHandler // Prevent players breaking item frames
    private void onBreakItemFrame(HangingBreakByEntityEvent event) {
        handleItemFrame(event.getEntity(), event, true);
    }

    private void handleItemFrame(Hanging hanging, Event event, boolean cancel) {
        if (this.gameManager.isInRegion(hanging.getLocation())) {
            Game game = this.gameManager.getGame(hanging.getLocation());
            switch (game.getGameArenaData().getStatus()) {
                case RUNNING:
                case FREE_ROAM:
                case COUNTDOWN:
                    if (cancel && event instanceof Cancellable cancellable) {
                        cancellable.setCancelled(true);
                    } else if (hanging instanceof ItemFrame itemFrame) {
                        game.getGameBlockData().recordItemFrame(itemFrame);
                    }
            }
        }
    }

    @EventHandler
    private void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (this.playerManager.hasSpectatorData(player)) {
            event.setCancelled(true);
            return;
        }
        if (this.gameManager.isInRegion(block.getLocation())) {

            if (Config.breakblocks && this.playerManager.hasPlayerData(player)) {
                Game game = this.playerManager.getPlayerData(player).getGame();
                GameBlockData gameBlockData = game.getGameBlockData();
                Status status = game.getGameArenaData().getStatus();
                if (status == Status.RUNNING || status == Status.FREE_ROAM) {
                    if (!BlockUtils.isEditableBlock(block.getType())) {
                        Util.sendMessage(player, this.lang.listener_no_edit_block);
                        event.setCancelled(true);
                    } else {
                        gameBlockData.recordBlockPlace(event.getBlockReplacedState());
                        if (isChest(block)) {
                            gameBlockData.addPlayerChest(block.getLocation());
                        }
                    }
                } else {
                    Util.sendMessage(player, this.lang.listener_not_running);
                    event.setCancelled(true);
                }
            } else {
                if (Permissions.COMMAND_CREATE.has(player)) {
                    Game game = this.plugin.getGameManager().getGame(block.getLocation());
                    Status status = game.getGameArenaData().getStatus();
                    switch (status) {
                        case FREE_ROAM:
                        case RUNNING:
                            game.getGameBlockData().recordBlockPlace(event.getBlockReplacedState());
                        default:
                            return;
                    }
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (this.playerManager.hasSpectatorData(player)) {
            event.setCancelled(true);
            return;
        }
        if (this.gameManager.isInRegion(block.getLocation())) {

            if (Config.breakblocks && this.playerManager.hasPlayerData(player)) {
                Game game = this.playerManager.getPlayerData(player).getGame();
                if (game.getGameArenaData().getStatus() == Status.RUNNING || !Config.protectCooldown) {
                    if (!BlockUtils.isEditableBlock(block.getType())) {
                        Util.sendMessage(player, this.lang.listener_no_edit_block);
                        event.setCancelled(true);
                    } else {
                        GameBlockData gameBlockData = game.getGameBlockData();
                        gameBlockData.recordBlockBreak(block);
                        if (isChest(block)) {
                            gameBlockData.removeGameChest(block.getLocation());
                            gameBlockData.removePlayerChest(block.getLocation());
                        }
                    }
                } else {
                    Util.sendMessage(player, this.lang.listener_not_running);
                    event.setCancelled(true);
                }
            } else {
                if (!this.playerManager.hasPlayerData(player) && Permissions.COMMAND_CREATE.has(player)) {
                    Game game = this.gameManager.getGame(block.getLocation());
                    Status status = game.getGameArenaData().getStatus();
                    switch (status) {
                        case FREE_ROAM:
                        case RUNNING:
                            game.getGameBlockData().removeGameChest(block.getLocation());
                        default:
                            return;
                    }
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onBucketEmpty(PlayerBucketEmptyEvent event) {
        handleBucketEvent(event, false);
    }

    @EventHandler
    private void onBucketDrain(PlayerBucketFillEvent event) {
        handleBucketEvent(event, true);
    }

    private void handleBucketEvent(PlayerBucketEvent event, boolean fill) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        final boolean WATER = event.getBucket() == Material.WATER_BUCKET && BlockUtils.isEditableBlock(Material.WATER);
        final boolean LAVA = event.getBucket() == Material.LAVA_BUCKET && BlockUtils.isEditableBlock(Material.LAVA);

        if (this.gameManager.isInRegion(block.getLocation())) {
            if (Config.breakblocks && this.playerManager.hasPlayerData(player)) {
                Game game = this.playerManager.getPlayerData(player).getGame();
                GameBlockData gameBlockData = game.getGameBlockData();
                if (game.getGameArenaData().getStatus() == Status.RUNNING || !Config.protectCooldown) {
                    if (fill && BlockUtils.isEditableBlock(block.getType())) {
                        gameBlockData.recordBlockBreak(block);
                    } else if (!fill && (WATER || LAVA)) {
                        gameBlockData.recordBlockPlace(block.getState());
                    } else {
                        Util.sendMessage(player, this.lang.listener_no_edit_block);
                        event.setCancelled(true);
                    }
                } else {
                    Util.sendMessage(player, this.lang.listener_not_running);
                    event.setCancelled(true);
                }
            } else {
                if (this.playerManager.hasPlayerData(player) || !Permissions.COMMAND_CREATE.has(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    private void onFallingBlockLand(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        if (!Config.breakblocks || !this.gameManager.isInRegion(block.getLocation())) return;
        Game game = this.gameManager.getGame(block.getLocation());
        Status status = game.getGameArenaData().getStatus();

        Material blockType = block.getType();
        Entity entity = event.getEntity();

        if (entity instanceof FallingBlock fallingBlock && (status == Status.RUNNING || status == Status.FREE_ROAM)) {
            // Block starts falling
            if (blockType == fallingBlock.getBlockData().getMaterial()) {
                game.getGameBlockData().recordBlockBreak(block);
            }

            // Falling block lands
            if (!blockType.isSolid()) {
                game.getGameBlockData().recordBlockPlace(block.getState());
            }
        }
    }

    @EventHandler
    private void onEntityExplode(EntityExplodeEvent event) {
        if (this.gameManager.isInRegion(event.getLocation())) {
            Game game = this.gameManager.getGame(event.getLocation());
            for (Block block : event.blockList()) {
                game.getGameBlockData().recordBlockBreak(block);
            }
            event.setYield(0);
        }
    }

    @EventHandler
    private void onBlockExplode(BlockExplodeEvent event) {
        if (gameManager.isInRegion(event.getBlock().getLocation())) {
            GameBlockData gameBlockData = gameManager.getGame(event.getBlock().getLocation()).getGameBlockData();
            for (Block block : event.blockList()) {
                gameBlockData.recordBlockBreak(block);
            }
            event.setYield(0);
        }
    }

    @EventHandler
    private void onLeafDecay(LeavesDecayEvent event) {
        if (!Config.fixleaves) return;
        Block block = event.getBlock();
        if (this.gameManager.isInRegion(block.getLocation())) {
            if (Config.breakblocks) {
                Game game = this.gameManager.getGame(block.getLocation());
                if (game.getGameArenaData().getStatus() == Status.RUNNING) {
                    game.getGameBlockData().recordBlockBreak(block);
                }
            }
        }
    }

    @EventHandler
    private void onTrample(PlayerInteractEvent event) {
        if (!Config.preventtrample) return;
        Player player = event.getPlayer();
        if (this.playerManager.hasSpectatorData(player)) {
            event.setCancelled(true);
            return;
        }
        if (this.gameManager.isInRegion(player.getLocation())) {
            if (event.getAction() == Action.PHYSICAL) {
                assert event.getClickedBlock() != null;
                Material block = event.getClickedBlock().getType();
                if (block == Material.FARMLAND) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // UTIL
    // TODO something better here
    private boolean isChest(Block block) {
        if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST || block.getState() instanceof Shulker) {
            return true;
        }
        return Util.isRunningMinecraft(1, 14) && block.getType() == Material.BARREL;
    }

}

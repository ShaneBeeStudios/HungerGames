package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.BlockUtils;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
import com.shanebeestudios.hg.game.GameBlockData;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
            handleItemFrame(hanging, event, !Config.ROLLBACK_ALLOW_ITEMFRAME_TAKE);
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

    @SuppressWarnings("DataFlowIssue")
    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (this.playerManager.hasSpectatorData(player)) {
            event.setCancelled(true);
            return;
        }
        if (!this.playerManager.hasPlayerData(player)) {
            if (this.gameManager.isInRegion(block.getLocation()) && !Permissions.COMMAND_CREATE.has(player)) {
                // Prevent non-game players placing blocks in arena
                event.setCancelled(true);
            }
            return;
        }
        Game game = this.playerManager.getPlayerData(player).getGame();
        GameArenaData gameArenaData = game.getGameArenaData();

        if (gameArenaData.getGameRegion().isInRegion(block.getLocation())) {
            if (Config.ROLLBACK_ALLOW_BREAK_BLOCKS) {
                GameBlockData gameBlockData = game.getGameBlockData();
                Status status = gameArenaData.getStatus();
                if (status == Status.RUNNING || (status == Status.ROLLBACK && !Config.ROLLBACK_PROTECT_DURING_FREE_ROAM)) {
                    if (!BlockUtils.isEditableBlock(block.getType())) {
                        Util.sendMessage(player, this.lang.listener_no_edit_block);
                        event.setCancelled(true);
                    } else if (isChest(block)) {
                        gameBlockData.logPlayerPlacedChest(block.getLocation());
                    }
                } else {
                    Util.sendMessage(player, this.lang.listener_not_running);
                    event.setCancelled(true);
                }
            } else if (!Permissions.COMMAND_CREATE.has(player)) {
                event.setCancelled(true);

            }
        } else {
            // Prevent placing blocks outside arena
            event.setCancelled(true);
        }

    }

    @SuppressWarnings("DataFlowIssue")
    @EventHandler
    private void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (this.playerManager.hasSpectatorData(player)) {
            event.setCancelled(true);
            return;
        }
        if (!this.playerManager.hasPlayerData(player)) {
            if (this.gameManager.isInRegion(block.getLocation()) && !Permissions.COMMAND_CREATE.has(player)) {
                // Prevent non-game players breaking blocks in arena
                event.setCancelled(true);
            }
            return;
        }
        Game game = this.playerManager.getPlayerData(player).getGame();
        GameArenaData gameArenaData = game.getGameArenaData();

        if (gameArenaData.getGameRegion().isInRegion(block.getLocation())) {
            if (Config.ROLLBACK_ALLOW_BREAK_BLOCKS) {
                Status status = gameArenaData.getStatus();
                if (status == Status.RUNNING || (status == Status.ROLLBACK && !Config.ROLLBACK_PROTECT_DURING_FREE_ROAM)) {
                    if (!BlockUtils.isEditableBlock(block.getType())) {
                        Util.sendMessage(player, this.lang.listener_no_edit_block);
                        event.setCancelled(true);
                    } else if (isChest(block)) {
                        GameBlockData gameBlockData = game.getGameBlockData();
                        gameBlockData.removeOpenedChest(block.getLocation());
                        gameBlockData.removePlayerChest(block.getLocation());
                    }
                } else {
                    Util.sendMessage(player, this.lang.listener_not_running);
                    event.setCancelled(true);
                }
            } else {
                if (Permissions.COMMAND_CREATE.has(player)) {
                    Status status = gameArenaData.getStatus();
                    switch (status) {
                        case FREE_ROAM:
                        case RUNNING:
                            game.getGameBlockData().removeOpenedChest(block.getLocation());
                        default:
                            return;
                    }
                }
                event.setCancelled(true);
            }
        } else {
            // Prevent breaking blocks outside arena
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBucketEmpty(PlayerBucketEmptyEvent event) {
        handleBucketEvent(event, false);
    }

    @EventHandler
    private void onBucketFill(PlayerBucketFillEvent event) {
        handleBucketEvent(event, true);
    }

    @SuppressWarnings("DataFlowIssue")
    private void handleBucketEvent(PlayerBucketEvent event, boolean fill) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        final boolean WATER = event.getBucket() == Material.WATER_BUCKET && BlockUtils.isEditableBlock(Material.WATER);
        final boolean LAVA = event.getBucket() == Material.LAVA_BUCKET && BlockUtils.isEditableBlock(Material.LAVA);

        if (this.gameManager.isInRegion(block.getLocation())) {
            if (Config.ROLLBACK_ALLOW_BREAK_BLOCKS && this.playerManager.hasPlayerData(player)) {
                Game game = this.playerManager.getPlayerData(player).getGame();
                if (game.getGameArenaData().getStatus() == Status.RUNNING || !Config.ROLLBACK_PROTECT_DURING_FREE_ROAM) {
                    if ((fill && !BlockUtils.isEditableBlock(block.getType())) ||
                        (!fill && !(WATER || LAVA))) {
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
    private void onTrample(PlayerInteractEvent event) {
        if (!Config.ROLLBACK_PREVENT_TRAMPLING) return;
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
    private boolean isChest(Block block) {
        return block.getType() == Material.CHEST || BlockUtils.isBonusBlock(block);
    }

}

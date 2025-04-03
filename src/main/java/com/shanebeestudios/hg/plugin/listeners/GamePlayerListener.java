package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class GamePlayerListener extends GameListenerBase {

    public GamePlayerListener(HungerGames plugin) {
        super(plugin);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.PHYSICAL && this.playerManager.hasPlayerData(player)) {
            PlayerData playerData = this.playerManager.getPlayerData(player);
            assert playerData != null;
            Status status = playerData.getGame().getGameArenaData().getStatus();
            if (status != Status.RUNNING && status != Status.FREE_ROAM) {
                event.setCancelled(true);
                Util.sendMessage(player, lang.listener_no_interact);
            }
        }
    }

    @EventHandler
    private void onLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.playerManager.hasPlayerData(player)) {
            PlayerData playerData = this.playerManager.getPlayerData(player);
            assert playerData != null;
            playerData.setOnline(false);
            playerData.getGame().getGamePlayerData().leave(player, false);
        }
        if (this.playerManager.hasSpectatorData(player)) {
            PlayerData playerData = this.playerManager.getSpectatorData(player);
            assert playerData != null;
            playerData.setOnline(false);
            playerData.getGame().getGamePlayerData().leaveSpectate(player);
        }
    }

    @EventHandler
    private void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (this.playerManager.hasSpectatorData(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onTeleportIntoArena(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location location = event.getTo();
        for (Game game : this.plugin.getGameManager().getGames()) {
            GameArenaData gameArenaData = game.getGameArenaData();
            if (gameArenaData.isInRegion(location) && gameArenaData.getStatus() == Status.RUNNING) {
                if (event.getCause() == TeleportCause.ENDER_PEARL && !game.getGamePlayerData().getPlayers().contains(player) && !game.getGamePlayerData().getSpectators().contains(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    private void onChat(AsyncChatEvent event) {
        if (!Config.SPECTATE_CHAT) {
            Player spectator = event.getPlayer();
            if (this.playerManager.hasSpectatorData(spectator)) {
                PlayerData data = this.playerManager.getSpectatorData(spectator);
                assert data != null;
                Game game = data.getGame();
                for (Player player : game.getGamePlayerData().getPlayers()) {
                    event.viewers().remove(player);
                }
            }
        }
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.playerManager.getPlayerData(player);
        if (playerData != null) {
            Status status = playerData.getGame().getGameArenaData().getStatus();
            if (status != Status.FREE_ROAM && status != Status.RUNNING) {
                event.setCancelled(true);
            }
        }
        // Prevent spectators from dropping items
        if (this.playerManager.hasSpectatorData(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onSprint(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        // Prevent spectators from losing food level
        if (this.playerManager.hasSpectatorData(player)) {
            player.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

}

package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.game.Game;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.PlayerInventory;

public class GameDamageListenerBase extends GameListenerBase {

    public GameDamageListenerBase(HungerGames plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAttack(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity attacker = event.getDamager();

        if (attacker instanceof Player attackerPlayer) {
            if (this.playerManager.hasSpectatorData(attackerPlayer)) {
                event.setCancelled(true);
                return;
            }
        }
        if (victim instanceof Player victimPlayer) {
            PlayerData playerData = playerManager.getPlayerData(victimPlayer);

            if (playerData != null) {
                Game game = playerData.getGame();

                if (game.getGameArenaData().getStatus() != Status.RUNNING) {
                    event.setCancelled(true);
                } else if (event.getFinalDamage() >= victimPlayer.getHealth()) {
                    if (hasTotem(victimPlayer)) return;
                    event.setCancelled(true);
                    this.killManager.processDeath(victimPlayer, game, attacker, event.getDamageSource());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDeathByOther(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (playerManager.hasSpectatorData(player)) {
                event.setCancelled(true);
                player.setFireTicks(0);
                return;
            }
            if (event instanceof EntityDamageByEntityEvent) return;
            PlayerData playerData = playerManager.getPlayerData(player);
            if (playerData != null) {
                if (event.getFinalDamage() >= player.getHealth()) {
                    if (hasTotem(player)) return;
                    event.setCancelled(true);
                    this.killManager.processDeath(player, playerData.getGame(), null, event.getDamageSource());
                }
            }
        }
    }

    // UTIL
    @SuppressWarnings("ConstantConditions")
    private boolean hasTotem(Player player) {
        PlayerInventory inv = player.getInventory();
        if (inv.getItemInMainHand() != null && inv.getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING)
            return true;
        return inv.getItemInOffHand() != null && inv.getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING;
    }


}

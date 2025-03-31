package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameRegion;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class GameEntityListener extends GameListenerBase {

    public GameEntityListener(HungerGames plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onTarget(EntityTargetEvent event) {
        Entity target = event.getTarget();
        if (target instanceof Player player) {
            if (this.playerManager.hasSpectatorData(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ItemFrame || entity instanceof ArmorStand) return;
        if (!(entity instanceof Player)) {
            if (this.gameManager.isInRegion(event.getLocation())) {
                Game game = gameManager.getGame(event.getLocation());
                if (entity instanceof LivingEntity) {
                    if (game.getGameArenaData().getStatus() != Status.RUNNING) {
                        event.setCancelled(true);
                        return;
                    }
                    if (event instanceof CreatureSpawnEvent creatureSpawnEvent) {
                        switch (creatureSpawnEvent.getSpawnReason()) {
                            case DEFAULT:
                            case NATURAL:
                                event.setCancelled(true);
                                return;
                        }
                    }
                }
                GameRegion gameRegion = game.getGameArenaData().getGameRegion();
                if (!gameRegion.hasEntity(entity)) gameRegion.addEntity(entity);
            }
        }
    }

    @EventHandler
    private void onEntityShoot(EntityShootBowEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasMetadata("death-message")) {
            event.getProjectile().setMetadata("death-message",
                new FixedMetadataValue(plugin, entity.getMetadata("death-message").get(0).asString()));
        }
        if (entity instanceof Player && playerManager.hasPlayerData(entity.getUniqueId())) {
            event.getProjectile().setMetadata("shooter", new FixedMetadataValue(plugin, entity.getName()));
        }
    }

    @EventHandler
    private void onEntityRemoved(EntityRemoveEvent event) {
        if (event.getCause() == EntityRemoveEvent.Cause.PLUGIN) return;

        Entity entity = event.getEntity();
        Game game = this.gameManager.getGame(entity.getLocation());
        if (game == null) return;

        GameRegion gameRegion = game.getGameArenaData().getGameRegion();
        gameRegion.removeEntity(entity);
    }

}

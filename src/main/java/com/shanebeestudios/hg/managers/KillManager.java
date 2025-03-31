package com.shanebeestudios.hg.managers;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.ItemUtils;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.data.Leaderboard;
import com.shanebeestudios.hg.events.PlayerDeathGameEvent;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameCommandData;
import com.shanebeestudios.hg.game.GamePlayerData;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Manager for deaths in game
 */
@SuppressWarnings("UnstableApiUsage")
public class KillManager {

    private final HungerGames plugin;
    private final Language lang;
    private final Leaderboard leaderboard;
    private final ItemStack trackingStick;

    public KillManager(HungerGames plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.leaderboard = plugin.getLeaderboard();

        this.trackingStick = new ItemStack(Material.STICK, 1);
        this.trackingStick.setData(DataComponentTypes.ITEM_NAME, Util.getMini(this.lang.tracking_stick_name
            .replace("<uses>", String.valueOf(Config.TRACKING_STICK_USES))));
    }

    /**
     * Get the death message when a player dies of natural causes (non-entity involved deaths)
     *
     * @param damageSource Source of the damage
     * @param name         Name of the player
     * @return Message that will be sent when the player dies
     */
    @SuppressWarnings("UnstableApiUsage")
    public String getDeathString(DamageSource damageSource, String name) {
        DamageType damageType = damageSource.getDamageType();
        if (damageType == DamageType.EXPLOSION) {
            return this.lang.death_explosion.replace("<player>", name);
        } else if (damageType == DamageType.FALL) {
            return this.lang.death_fall.replace("<player>", name);
        } else if (damageType == DamageType.FALLING_BLOCK) {
            return this.lang.death_falling_block.replace("<player>", name);
        } else if (damageType == DamageType.IN_FIRE || damageType == DamageType.ON_FIRE) {
            return this.lang.death_fire.replace("<player>", name);
        } else if (damageType == DamageType.MOB_PROJECTILE) {
            return this.lang.death_projectile.replace("<player>", name);
        } else if (damageType == DamageType.LAVA) {
            return this.lang.death_lava.replace("<player>", name);
        } else if (damageType == DamageType.MAGIC) {
            return this.lang.death_magic.replace("<player>", name);
        } else {
            return (this.lang.death_other_cause.replace("<player>", name).replace("<cause>", damageSource.toString().toLowerCase()));
        }
    }

    /**
     * Get the death message when a player is killed by an entity
     *
     * @param victimName Name of player who died
     * @param entity     Entity that killed this player
     * @return Death string including the victim's name and the killer
     */
    public String getKillString(String victimName, Entity entity) {
        if (entity.hasMetadata("death-message")) {
            return entity.getMetadata("death-message").getFirst().asString().replace("<player>", victimName);
        }
        switch (entity.getType()) {
            case ARROW:
                if (!isShotByPlayer(entity)) {
                    return this.lang.death_skeleton.replace("<player>", victimName);
                } else {
                    return getPlayerKillString(victimName, getShooter(entity), true);
                }
            case PLAYER:
                return getPlayerKillString(victimName, ((Player) entity), false);
            case ZOMBIE:
                return this.lang.death_zombie.replace("<player>", victimName);
            case SKELETON:
            case SPIDER:
                return this.lang.death_spider.replace("<player>", victimName);
            case DROWNED:
                return this.lang.death_drowned.replace("<player>", victimName);
            case TRIDENT:
                return this.lang.death_trident.replace("<player>", victimName);
            case STRAY:
                return this.lang.death_stray.replace("<player>", victimName);
            default:
                return this.lang.death_other_entity.replace("<player>", victimName);
        }
    }

    private String getPlayerKillString(String victimName, Player killer, boolean projectile) {
        String weapon;
        if (projectile) {
            weapon = "bow and arrow";
        } else if (killer.getInventory().getItemInMainHand().getType() == Material.AIR) {
            weapon = "fist";
        } else {
            ItemStack itemStack = killer.getInventory().getItemInMainHand();
            Component data = itemStack.getData(DataComponentTypes.ITEM_NAME);
            weapon = Util.unMini(data);
        }
        return this.lang.death_player.replace("<player>", victimName)
            .replace("<killer>", killer.getName())
            .replace("<weapon>", weapon);
    }

    /**
     * Check if the shooter was a player
     *
     * @param projectile The arrow which hit the player
     * @return True if the arrow was shot by a player
     */
    public boolean isShotByPlayer(Entity projectile) {
        return projectile instanceof Projectile && projectile.hasMetadata("shooter");
    }

    /**
     * Get the shooter of this arrow
     *
     * @param projectile The arrow in question
     * @return The player which shot the arrow
     */
    public Player getShooter(Entity projectile) {
        return Bukkit.getPlayer(projectile.getMetadata("shooter").get(0).asString());
    }

    @SuppressWarnings("UnstableApiUsage")
    public void processDeath(Player player, Game game, Entity attacker, DamageSource damageSource) {
        dropInventoryOfPlayer(player);
        player.setHealth(20);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            GamePlayerData gamePlayerData = game.getGamePlayerData();
            String deathString;

            DamageType damageType = damageSource.getDamageType();

            if (attacker instanceof Player attackerPlayer) {
                gamePlayerData.addKill(attackerPlayer);
                this.leaderboard.addStat(attackerPlayer, Leaderboard.Stats.KILLS);
                deathString = getKillString(player.getName(), attacker);
            } else if (damageType == DamageType.MOB_ATTACK) {
                deathString = getKillString(player.getName(), attacker);
            } else if (damageType == DamageType.MOB_PROJECTILE) {
                deathString = getKillString(player.getName(), attacker);
                if (isShotByPlayer(attacker) && getShooter(attacker) != player) {
                    gamePlayerData.addKill(getShooter(attacker));
                    this.leaderboard.addStat(getShooter(attacker), Leaderboard.Stats.KILLS);
                }
            } else {
                deathString = getDeathString(damageSource, player.getName());
            }

            // Send death message to all players in game
            gamePlayerData.msgAll(this.lang.death_fallen + " <light_purple>" + deathString);

            leaderboard.addStat(player, Leaderboard.Stats.DEATHS);
            leaderboard.addStat(player, Leaderboard.Stats.GAMES);

            for (Player alive : game.getGamePlayerData().getPlayers()) {
                if (alive != null && player != alive) {
                    alive.playSound(alive.getLocation(), Config.SOUNDS_DEATH, 5, 1);
                }
            }

            gamePlayerData.leave(player, true);
            game.getGameCommandData().runCommands(GameCommandData.CommandType.DEATH, player);

            // Call our death event so other plugins can pick up the fake death
            PlayerDeathGameEvent event = new PlayerDeathGameEvent(player, deathString, game);
            Bukkit.getPluginManager().callEvent(event);
            // Call bukkit player death event so other plugins can pick up on that too
            // TODO manage damage source
//			PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(player, Collections.emptyList(), 0, deathString);
//			Bukkit.getPluginManager().callEvent(playerDeathEvent);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> checkStick(game), 40L);
        }, 1);
    }

    private void dropInventoryOfPlayer(Player player) {
        PlayerInventory inventory = player.getInventory();
        Location location = player.getLocation();
        World world = player.getWorld();

        for (ItemStack itemStack : inventory.getStorageContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR && !ItemUtils.isCursed(itemStack)) {
                world.dropItemNaturally(location, itemStack).setPersistent(false);
            }
        }
        for (ItemStack itemStack : inventory.getArmorContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR && !ItemUtils.isCursed(itemStack)) {
                world.dropItemNaturally(location, itemStack).setPersistent(false);
            }
        }
    }

    private void checkStick(Game g) {
        if (Config.PLAYERS_FOR_TRACKING_STICK == g.getGamePlayerData().getPlayers().size()) {
            for (Player player : g.getGamePlayerData().getPlayers()) {
                Util.sendMessage(player, this.lang.track_bar);
                Util.sendMessage(player, this.lang.track_new1);
                Util.sendMessage(player, this.lang.track_new2);
                Util.sendMessage(player, this.lang.track_bar);
                player.getInventory().addItem(this.trackingStick.clone());
            }
        }
    }

}

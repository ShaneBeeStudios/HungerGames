package com.shanebeestudios.hg.data;

import com.shanebeestudios.hg.plugin.HungerGames;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MobEntry holds data for mobs to spawn in games
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MobEntry {

    // Normal Mob
    private EntityType type;
    private Component name;
    private final Map<EquipmentSlot, ItemStack> gear = new HashMap<>();
    private final List<PotionEffect> potionEffects = new ArrayList<>();
    private FixedMetadataValue deathMessageMeta = null;
    private Component deathMessage = null;

    // MythicMob
    private final MythicMob mythicMob;
    private final double mythicLevel;

    /**
     * Create a new mob entry
     *
     * @param type Type of new mob
     */
    public MobEntry(EntityType type) {
        this.type = type;
        this.mythicMob = null;
        this.mythicLevel = 0;
    }

    /**
     * Create a new mob entry using a MythicMob
     *
     * @param mythicMob   MythicMob to create
     * @param mythicLevel Level of MythicMob
     */
    public MobEntry(MythicMob mythicMob, double mythicLevel) {
        this.mythicLevel = mythicLevel;
        this.mythicMob = mythicMob;
    }

    /**
     * Get the name of this mob entry
     *
     * @return Name (Supports color codes)
     */
    public @Nullable Component getName() {
        return this.name;
    }

    /**
     * Set the name of this mob entry
     *
     * @param name Name to set
     */
    public void setName(@Nullable Component name) {
        this.name = name;
    }

    /**
     * Get the type of this mob entry
     *
     * @return EntityType of this mob entry
     */
    public EntityType getType() {
        return this.type;
    }

    /**
     * Set the type of this mob entry
     *
     * @param type EntityType for this mob entry to set
     */
    public void setType(@NotNull EntityType type) {
        this.type = type;
    }

    /**
     * Get the gear for this mob entry
     *
     * @param slot Slot to get gear from
     * @return Item in the corresponding slot
     */
    public ItemStack getGear(@NotNull EquipmentSlot slot) {
        return this.gear.get(slot);
    }

    /**
     * Add gear to this mob entry
     *
     * @param slot Slot to add an item to
     * @param item Item to add to the slot
     */
    public void addGear(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
        this.gear.put(slot, item);
    }

    /**
     * Get the potion effects for this mob entry
     *
     * @return List of potion effects for this mob entry
     */
    public List<PotionEffect> getPotionEffects() {
        return this.potionEffects;
    }

    /**
     * Add a potion effect to this mob entry
     *
     * @param potionEffect Potion effect to add
     */
    public void addPotionEffect(PotionEffect potionEffect) {
        this.potionEffects.add(potionEffect);
    }

    /**
     * Add a list of potion effects to this mob entry
     *
     * @param potionEffects Potion effect list to add
     */
    public void addPotionEffects(List<PotionEffect> potionEffects) {
        this.potionEffects.addAll(potionEffects);
    }

    /**
     * Get the death message for this mob entry
     * <p>This is the message players will see when a player is killed by this mob type</p>
     *
     * @return Death message
     */
    public @Nullable Component getDeathMessage() {
        return this.deathMessage;
    }

    /**
     * Set the death message for this mob entry
     * <p>This is the message players will see when a player is killed by this mob type</p>
     *
     * @param message Message to set
     */
    public void setDeathMessage(@Nullable Component message) {
        this.deathMessage = message;
        this.deathMessageMeta = new FixedMetadataValue(HungerGames.getPlugin(), message);
    }

    /**
     * Spawn a new mob from this mob entry
     *
     * @param location Location to spawn the mob at
     * @return The entity that was spawned
     */
    public @Nullable Entity spawn(@NotNull Location location) {
        // Spawn MythicMob
        if (this.mythicMob != null) {
            ActiveMob activeMob = this.mythicMob.spawn(BukkitAdapter.adapt(location), this.mythicLevel);
            if (this.deathMessageMeta != null) {
                activeMob.getEntity().getBukkitEntity().setMetadata("death-message", this.deathMessageMeta);
            }
            return activeMob.getEntity().getBukkitEntity();
        }
        // Spawn Regular Mob
        else {
            Class<? extends Entity> entityClass = this.type.getEntityClass();
            if (entityClass == null) {
                return null;
            }
            return location.getWorld().spawn(location, entityClass, entity -> {
                if (this.name != null) {
                    entity.customName(this.name);
                    entity.setCustomNameVisible(true);
                }
                if (entity instanceof LivingEntity livingEntity) {
                    EntityEquipment equip = livingEntity.getEquipment();
                    if (equip != null) {
                        for (EquipmentSlot slot : EquipmentSlot.values()) {
                            ItemStack gear = getGear(slot);
                            if (gear != null) {
                                equip.setItem(slot, gear);
                            }
                        }
                    }
                    if (!this.potionEffects.isEmpty()) {
                        for (PotionEffect effect : this.potionEffects) {
                            livingEntity.addPotionEffect(effect);
                        }
                    }
                    if (this.deathMessageMeta != null) {
                        livingEntity.setMetadata("death-message", this.deathMessageMeta);
                    }
                }
            });
        }
    }

    /**
     * Check if this MobEntry is a MythicMob
     *
     * @return True if a MythicMob
     */
    public boolean isMythic() {
        return this.mythicMob != null;
    }

    /**
     * Get the MythicMob held by this entry
     *
     * @return MythicMob if exists
     */
    public @Nullable MythicMob getMythicMob() {
        return this.mythicMob;
    }

    /**
     * Get the level of this MythicMob
     *
     * @return Level of this MythicMob
     */
    public double getMythicLevel() {
        return this.mythicLevel;
    }

}

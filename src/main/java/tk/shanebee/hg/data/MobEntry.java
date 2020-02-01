package tk.shanebee.hg.data;

import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * MobEntry holds data for mobs to spawn in games
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MobEntry {

	private EntityType type;
	private String name;
	private ItemStack hand = null;
	private ItemStack offHand = null;
	private ItemStack helmet = null;
	private ItemStack chest = null;
	private ItemStack leggings = null;
	private ItemStack boots = null;
	private List<PotionEffect> potionEffect = new ArrayList<>();
	private String deathMessage = null;
	private boolean mythic;
    private String mythicMob;
	private int mythicLevel;

	/** Create a new mob entry
	 * @param type Type of new mob
	 * @param name Name of new mob
	 */
	public MobEntry(EntityType type, String name) {
		this(type);
		this.mythic = false;
		this.name = name;
	}

	/** Create a new mob entry
	 * @param type Type of new mob
	 */
	public MobEntry(EntityType type) {
		this.type = type;
	}

	/** Create a new mob entry using a MythicMob
	 * @param mythicMob MythicMob to create
	 * @param mythicLevel Level of MythicMob
	 */
	public MobEntry(String mythicMob, int mythicLevel) {
		this.mythic = true;
		this.mythicMob = mythicMob;
		this.mythicLevel = mythicLevel;
	}

	/** Get the name of this mob entry
	 * @return Name (Supports color codes)
	 */
	public String getName() {
		return this.name;
	}

	/** Set the name of this mob entry
	 * @param name Name to set
	 */
	public void setName(String name) {
		if (name != null)
			this.name = name;
	}

	/** Get the type of this mob entry
	 * @return EntityType of this mob entry
	 */
	public EntityType getType() {
		return this.type;
	}

	/** Set the type of this mob entry
	 * @param type EntityType for this mob entry to set
	 */
	public void setType(EntityType type) {
		this.type = type;
	}

	/** Get the gear for this mob entry
	 * @param slot Slot to get gear from
	 * @return Item in the corresponding slot
	 */
	public ItemStack getGear(EquipmentSlot slot) {
		switch (slot) {
			case HAND:
				return this.hand;
			case OFF_HAND:
				return this.offHand;
			case HEAD:
				return this.helmet;
			case CHEST:
				return this.chest;
			case LEGS:
				return this.leggings;
			case FEET:
				return this.boots;
			default:
				return null;
		}
	}

	/** Add gear to this mob entry
	 * @param hand Hand slot of mob
	 * @param offHand Off hand slot of mob
	 * @param head Helmet slot of mob
	 * @param chest Chestplate slot of mob
	 * @param legs Leggings slot of mob
	 * @param feet Boots slot of mob
	 */
	public void addGear(ItemStack hand, ItemStack offHand, ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
		this.hand = hand;
		this.offHand = offHand;
		this.helmet = head;
		this.chest = chest;
		this.leggings = legs;
		this.boots = feet;
	}

	/** Add gear to this mob entry
	 * @param slot Slot to add an item to
	 * @param item Item to add to the slot
	 */
	public void addGear(EquipmentSlot slot, ItemStack item) {
		switch (slot) {
			case HAND:
				this.hand = item;
				break;
			case OFF_HAND:
				this.offHand = item;
				break;
			case HEAD:
				this.helmet = item;
				break;
			case CHEST:
				this.chest = item;
				break;
			case LEGS:
				this.leggings = item;
				break;
			case FEET:
				this.boots = item;
		}
	}

	/** Get the potion effects for this mob entry
	 * @return List of potion effects for this mob entry
	 */
	public List<PotionEffect> getPotionEffect() {
		return this.potionEffect;
	}

	/** Add a potion effect to this mob entry
	 * @param potionEffect Potion effect to add
	 */
	public void addPotionEffect(PotionEffect potionEffect) {
		this.potionEffect.add(potionEffect);
	}

	/** Add a list of potion effects to this mob entry
	 * @param potionEffects Potion effect list to add
	 */
	public void addPotionEffects(List<PotionEffect> potionEffects) {
		this.potionEffect.addAll(potionEffects);
	}

	/** Get the death message for this mob entry
	 * <p>This is the message players will see when a player is killed by this mob type</p>
	 * @return Death message
	 */
	public String getDeathMessage() {
		return this.deathMessage;
	}

	/** Set the death message for this mob entry
	 * <p>This is the message players will see when a player is killed by this mob type</p>
	 * @param message Message to set
	 */
	public void setDeathMessage(String message) {
		if (message != null)
			this.deathMessage = message;
	}

	/** Spawn a new mob from this mob entry
	 * @param location Location to spawn the mob at
	 */
	public void spawn(Location location) {
		assert location.getWorld() != null;
		if (!isMythic()) {
			Entity entity = location.getWorld().spawnEntity(location, this.type);
			if (name != null) {
				entity.setCustomName(Util.getColString(name));
				entity.setCustomNameVisible(true);
			}
			if (entity instanceof LivingEntity) {
				LivingEntity mob = ((LivingEntity) entity);
				EntityEquipment equip = mob.getEquipment();
				if (equip == null) return;
				if (hand != null)
					equip.setItemInMainHand(hand);
				if (offHand != null)
					equip.setItemInOffHand(offHand);
				if (helmet != null)
					equip.setHelmet(helmet);
				if (chest != null)
					equip.setChestplate(chest);
				if (leggings != null)
					equip.setLeggings(leggings);
				if (boots != null)
					equip.setBoots(boots);
				if (potionEffect != null) {
					for (PotionEffect effect : this.potionEffect) {
						mob.addPotionEffect(effect);
					}
				}
				if (deathMessage != null)
					mob.setMetadata("death-message", new FixedMetadataValue(HG.getPlugin(), deathMessage));
			}
		} else {
			MythicMob mob = HG.getPlugin().getMmMobManager().getMythicMob(mythicMob);
			ActiveMob activeMob = mob.spawn(BukkitAdapter.adapt(location), mythicLevel);
			if (deathMessage != null) {
				activeMob.getEntity().getBukkitEntity().setMetadata("death-message", new FixedMetadataValue(HG.getPlugin(), deathMessage));
			}
		}
	}

	/** Check if this MobEntry is a MythicMob
	 * @return True if a MythicMob
	 */
	public boolean isMythic() {
		return this.mythic;
	}

	/** Get the level of this MythicMob
	 * @return Level of this MythicMob
	 */
	public int getMythicLevel() {
		return this.mythicLevel;
	}

    @Override
    public String toString() {
        return "MobEntry{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", hand=" + hand +
                ", offHand=" + offHand +
                ", helmet=" + helmet +
                ", chest=" + chest +
                ", leggings=" + leggings +
                ", boots=" + boots +
                ", potionEffect=" + potionEffect +
                ", deathMessage='" + deathMessage + '\'' +
                ", mythic=" + mythic +
                ", mythicMob='" + mythicMob + '\'' +
                ", mythicLevel=" + mythicLevel +
                '}';
    }

}

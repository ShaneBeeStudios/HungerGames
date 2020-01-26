package tk.shanebee.hg.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.MobEntry;
import tk.shanebee.hg.util.PotionEffectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manager for mob spawning in games
 * <p>Get an instance of this class with {@link Game#getMobManager()}</p>
 */
@SuppressWarnings("unused")
public class MobManager {

	private List<MobEntry> dayMobs = new ArrayList<>();
	private List<MobEntry> nightMobs = new ArrayList<>();
	private FileConfiguration config;
	private Game game;

	public MobManager(Game game) {
		this.game = game;
		this.config = HG.getPlugin().getMobConfig().getMobs();
		loadMobs();
	}

	private void loadMobs() {
		String gameName = "default";
		for (String time : Arrays.asList("day", "night")) {
			if (config.getConfigurationSection("mobs." + time + "." + game.getName()) != null) {
				gameName = game.getName();
			}
			for (String key : config.getConfigurationSection("mobs." + time + "." + gameName).getKeys(false)) {
				key = "mobs." + time + "." + gameName + "." + key;
				if (getSection(key) != null) {
					MobEntry entry;
					// MYTHIC MOB
					if (getString(key, "type").startsWith("MM:") && HG.getPlugin().getMmMobManager() != null) {
						String mythicMob = getString(key, "type").replace("MM:", "");
						entry = new MobEntry(mythicMob, getInt(key, "level"));
					}
					// REGULAR MOB
					else {
						EntityType type = EntityType.valueOf(getString(key, "type"));
						entry = new MobEntry(type);
						if (getString(key, "name") != null) {
							entry.setName(getString(key, "name"));
						}
						entry.addGear(EquipmentSlot.HAND, getItemStack(key, "hand"));
						entry.addGear(EquipmentSlot.OFF_HAND, getItemStack(key, "off-hand"));
						entry.addGear(EquipmentSlot.HEAD, getItemStack(key, "helmet"));
						entry.addGear(EquipmentSlot.CHEST, getItemStack(key, "chestplate"));
						entry.addGear(EquipmentSlot.LEGS, getItemStack(key, "leggings"));
						entry.addGear(EquipmentSlot.FEET, getItemStack(key, "boots"));
						List<PotionEffect> potions = new ArrayList<>();
						for (String pot : getSection(key).getStringList("potion-effects")) {
							String[] poti = pot.split(":");
							PotionEffectType effectType = PotionEffectUtils.get(poti[0]);
							if (poti[2].equalsIgnoreCase("forever")) {
								assert effectType != null;
								potions.add(effectType.createEffect(2147483647, Integer.parseInt(poti[1])));
							} else {
								int dur = Integer.parseInt(poti[2]) * 20;
								assert effectType != null;
								potions.add(effectType.createEffect(dur, Integer.parseInt(poti[1])));
							}
						}
						entry.addPotionEffects(potions);
					}
					entry.setDeathMessage(getString(key, "death"));
					int chance = getInt(key, "chance");
					for (int i = 1; i <= chance; i++) {
						if (time.equalsIgnoreCase("day"))
							dayMobs.add(entry);
						else
							nightMobs.add(entry);
					}
				}
			}
		}
	}
	
	private ConfigurationSection getSection(String key) {
		return config.getConfigurationSection(key);
	}
	
	private String getString(String key, String section) {
		if (getSection(key).isSet(section))
			return getSection(key).getString(section);
		else return null;
	}
	
	private int getInt(String key, String section) {
		if (getSection(key).isSet(section))
			return getSection(key).getInt(section);
		else return 1;
	}
	
	private ItemStack getItemStack(String key, String section) {
		return HG.getPlugin().getItemStackManager().getItem(getString(key, section), false);
	}

	/** Get list of MobEntries for day time
	 * @return List of MobEntries
	 */
	public List<MobEntry> getDayMobs() {
		return this.dayMobs;
	}

	/** Add a new mob entry to the day mobs
	 * @param mobEntry Mob entry to add
	 */
	public void addDayMob(MobEntry mobEntry) {
		this.dayMobs.add(mobEntry);
	}

	/** Get list of MobEntries for night time
	 * @return List of MobEntries
	 */
	public List<MobEntry> getNightMobs() {
		return this.nightMobs;
	}

	/** Add a new mob entry to the night mobs
	 * @param mobEntry Mob entry to add
	 */
	public void addNightMob(MobEntry mobEntry) {
		this.nightMobs.add(mobEntry);
	}

	/** Get list of all MobEntries
	 * @return List of MobEntries
	 */
	public List<MobEntry> getAllMobs() {
		List<MobEntry> mobs = new ArrayList<>();
		mobs.addAll(this.dayMobs);
		mobs.addAll(this.nightMobs);
		return mobs;
	}

}

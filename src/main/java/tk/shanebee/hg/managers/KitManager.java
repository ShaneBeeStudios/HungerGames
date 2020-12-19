package tk.shanebee.hg.managers;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.hg.data.KitEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * General manager for kits
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class KitManager {

    private final HashMap<String, KitEntry> kitEntries = new HashMap<>();
    private final Map<Player, KitEntry> kits = new HashMap<>();

    /**
     * Set the kit the player will have when a game starts
     *
     * @param player   Player to set kit for
     * @param kitEntry Kit to set
     */
    public void setKit(Player player, KitEntry kitEntry) {
        kits.put(player, kitEntry);
    }

    /**
     * Apply a stored kit to a player
     *
     * @param player Player to apply kit to
     */
    public void applyKit(@NotNull Player player) {
        if (kits.containsKey(player)) {
            KitEntry kitEntry = kits.get(player);
            kitEntry.setInventoryContent(player);
        }
    }

    /**
     * Clear the list of players/kits when a game is done
     */
    public void resetPlayerKits() {
        kits.clear();
    }

    /**
     * Get a list of kits in this KitManager
     *
     * @return A string of all kits
     */
    public String getKitListString() {
        StringBuilder kits = new StringBuilder();
        if (kitEntries.size() > 0) {
            for (String s : kitEntries.keySet()) {
                kits.append(", ").append(s);
            }
            return kits.substring(1);
        }
        return null;
    }

    /**
     * Get a list of kits in this KitManager
     *
     * @return A list of all kit's names
     */
    public List<String> getKitList() {
        return new ArrayList<>(kitEntries.keySet());
    }

    /**
     * Get the kits for this KitManager
     *
     * @return A map of the kits
     */
    public HashMap<String, KitEntry> getKits() {
        return this.kitEntries;
    }

    /**
     * Check if this KitManager actually has kits
     *
     * @return True if kits exist
     */
    public boolean hasKits() {
        return this.kitEntries.size() > 0;
    }

    /**
     * Add a kit to this KitManager
     *
     * @param name The name of the kit
     * @param kit  The KitEntry to add
     */
    public void addKit(String name, KitEntry kit) {
        kitEntries.put(name, kit);
    }

    /**
     * Remove a kit entry from this KitManager
     *
     * @param name The kit entry to remove
     */
    public void removeKit(String name) {
        kitEntries.remove(name);
    }

    /**
     * Clear the kit entries in this KitManager
     */
    public void clearKits() {
        kitEntries.clear();
    }

}

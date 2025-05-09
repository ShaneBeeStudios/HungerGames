package com.shanebeestudios.hg.api.data;

import com.google.common.collect.ImmutableMap;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Language;
import com.shanebeestudios.hg.plugin.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Data holder for {@link KitEntry Kits}
 */
@SuppressWarnings("unused")
public class KitData {

    private final PlayerManager playerManager = HungerGames.getPlugin().getPlayerManager();
    private final Language lang = HungerGames.getPlugin().getLang();
    private final Map<String, KitEntry> kitEntries = new HashMap<>();
    private final Map<Player, KitEntry> preselectedKits = new HashMap<>();

    public boolean hasKitPermission(Player player, String kitName) {
        KitEntry kitEntry = this.kitEntries.get(kitName);
        return kitEntry == null || kitEntry.hasKitPermission(player);
    }

    /**
     * Give a player a preselected or default kit
     * <p>If the player selected a kit in the KitGui before the game
     * started, they will get this.
     * Otherwise, if there is a default kit set up they will get that.</p>
     *
     * @param player Player to give kit to
     */
    public void givePreselectedOrDefaultKit(Player player) {
        if (this.preselectedKits.containsKey(player)) {
            KitEntry kitEntry = this.preselectedKits.get(player);
            this.preselectedKits.remove(player);
            kitEntry.setInventoryContent(player);
        } else if (this.kitEntries.containsKey("default")) {
            setKit(player, this.kitEntries.get("default"));
            Util.sendPrefixedMessage(player, this.lang.kits_give_default);
        }
    }

    /**
     * Set a kit for a player
     *
     * @param player   The player to set the kit for
     * @param kitEntry The kit to set
     */
    public void setKit(Player player, KitEntry kitEntry) {
        PlayerData playerData = this.playerManager.getPlayerData(player);
        // This shouldn't happen, but just in case
        if (playerData == null) return;

        if (playerData.hasGameStared()) {
            kitEntry.setInventoryContent(player);
            this.preselectedKits.remove(player);
        } else {
            this.preselectedKits.put(player, kitEntry);
            Util.sendPrefixedMessage(player, this.lang.kits_kit_gui_pre_select
                .replace("<kit>", kitEntry.getName()));
        }
    }

    /**
     * Get a list of kits in this KitData
     *
     * @param player Player to check permission
     * @return A string of all kits
     */
    public String getKitListString(Player player) {
        if (!this.kitEntries.isEmpty()) {
            StringJoiner joiner = new StringJoiner(", ");
            this.kitEntries.forEach((kitName, kitEntry) -> {
                if (kitEntry.hasKitPermission(player)) joiner.add(kitName);
            });
            return joiner.toString();
        }
        return null;
    }

    /**
     * Get a list of kits in this KitData
     *
     * @param player Player to check permission
     * @return A list of all kit's names
     */
    public List<String> getKitNameList(@Nullable Player player) {
        List<String> names = new ArrayList<>();
        this.kitEntries.forEach((kitName, kitEntry) -> {
            if (player == null || kitEntry.hasKitPermission(player)) names.add(kitName);
        });
        return names;
    }

    /**
     * Get the kits for this KitData
     *
     * @return A map of the kits
     */
    public ImmutableMap<String, KitEntry> getKitEntries() {
        return ImmutableMap.copyOf(this.kitEntries);
    }

    public KitEntry getKitEntry(String kitName) {
        return this.kitEntries.get(kitName);
    }

    /**
     * Check if this KitData actually has kits
     *
     * @return True if kits exist
     */
    public boolean hasKits() {
        return !this.kitEntries.isEmpty();
    }

    /**
     * Add a kit to this KitData
     *
     * @param name The name of the kit
     * @param kit  The KitEntry to add
     */
    public void addKitEntry(String name, KitEntry kit) {
        this.kitEntries.put(name, kit);
    }

    /**
     * Remove a kit entry from this KitData
     *
     * @param name The kit entry to remove
     */
    public void removeKitEntry(String name) {
        this.kitEntries.remove(name);
    }

    /**
     * Clear the kit entries in this KitData
     */
    public void clearKits() {
        this.kitEntries.clear();
    }

    public void clearPreselectedKits() {
        this.preselectedKits.clear();
    }

}

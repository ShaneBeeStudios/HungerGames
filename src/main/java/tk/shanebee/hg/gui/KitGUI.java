package tk.shanebee.hg.gui;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.KitEntry;
import tk.shanebee.hg.data.Language;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GUI showing the contents of a kit to a player
 */
public class KitGUI implements InventoryHolder, Listener {

    private final Game game;
    private final Inventory inv;
    private final KitsGUI kitsGUI;
    private final KitEntry kitEntry;
    private final Language lang = HG.getPlugin().getLang();

    public KitGUI(Game game, KitsGUI kitsGUI, KitEntry kitEntry) {
        this.game = game;
        this.kitsGUI = kitsGUI;
        this.kitEntry = kitEntry;
        String title = lang.kit_gui_title.replace("<name>", kitEntry.getName());
        this.inv = Bukkit.createInventory(this, 54, Util.getColString(title));
        Bukkit.getPluginManager().registerEvents(this, HG.getPlugin());

        // Set contents
        ItemStack[] contents = kitEntry.getInventoryContents();
        for (int i = 0; i < contents.length && i < 54; i++) {
            inv.setItem(i, contents[i]);
        }

        // Set fillers
        for (int i = 36; i < 45; i++) {
            inv.setItem(i, getFiller());
        }
        inv.setItem(37, getFiller());
        inv.setItem(46, getFiller());
        inv.setItem(51, getFiller());
        inv.setItem(53, getFiller());

        // Set armor header
        ItemStack armor = new ItemStack(Material.SHIELD);
        ItemMeta armorMeta = armor.getItemMeta();
        armorMeta.setDisplayName(Util.getColString(lang.kit_armor_title));
        armorMeta.setLore(getLore("armor"));
        armor.setItemMeta(armorMeta);
        inv.setItem(45, armor);

        // Set armor slots
        inv.setItem(47, kitEntry.getHelmet());
        inv.setItem(48, kitEntry.getChestplate());
        inv.setItem(49, kitEntry.getLeggings());
        inv.setItem(50, kitEntry.getBoots());

        // Set potion effects
        ArrayList<PotionEffect> potions = kitEntry.getPotions();
        ItemStack potionE = new ItemStack(Material.POTION);
        PotionMeta potionEMeta = ((PotionMeta) potionE.getItemMeta());
        if (potions.size() > 0) {
            potionEMeta.setDisplayName(Util.getColString("&b" + lang.kit_effects_title));
            potionEMeta.setLore(getLore("effects"));
            potions.forEach(potionEffect -> potionEMeta.addCustomEffect(potionEffect, false));
            potionEMeta.setColor(Color.fromRGB(0, 255, 0));
        } else {
            potionEMeta.setDisplayName(Util.getColString("&c" + lang.kit_effects_title));
            potionEMeta.setLore(Arrays.asList(" ", Util.getColString(lang.kit_effects_none), " "));
            potionEMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            potionEMeta.setColor(Color.fromRGB(255, 0, 0));
        }
        potionE.setItemMeta(potionEMeta);
        inv.setItem(52, potionE);

        // Set ACCEPT
        ItemStack accept = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta acceptMeta = accept.getItemMeta();
        acceptMeta.setDisplayName(Util.getColString(lang.kit_accept_name));
        acceptMeta.setLore(getLore("accept"));
        accept.setItemMeta(acceptMeta);
        inv.setItem(39, accept);

        // Set CANCEL
        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(Util.getColString(lang.kit_cancel_name));
        cancelMeta.setLore(getLore("cancel"));
        cancel.setItemMeta(cancelMeta);
        inv.setItem(41, cancel);
    }

    /**
     * Open this GUI to a player
     *
     * @param player Player to open for
     */
    public void open(Player player) {
        player.openInventory(inv);
    }

    /**
     * Get the inventory of this GUI
     *
     * @return Inventory of this GUI
     */
    @NotNull
    @Override
    public Inventory getInventory() {
        return inv;
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);

        Player player = ((Player) event.getWhoClicked());
        int slot = event.getSlot();
        if (slot == 39) {
            game.getKitManager().setKit(player, kitEntry);
            player.closeInventory();
            Util.sendPrefixedMessage(player, lang.kit_selected.replace("<name>", kitEntry.getName()));
        } else if (slot == 41) {
            kitsGUI.open(player);
        }
    }

    private ItemStack getFiller() {
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);
        return filler;
    }

    private List<String> getLore(String key) {
        List<String> lore = new ArrayList<>();
        List<String> list = null;
        switch (key) {
            case "accept":
                list = lang.kit_accept_lore;
                break;
            case "cancel":
                list = lang.kit_cancel_lore;
                break;
            case "armor":
                list = lang.kit_armor_lore;
                break;
            case "effects":
                list = lang.kit_effects_lore;
        }
        if (list != null) {
            list.forEach(s -> {
                lore.add(Util.getColString(s));
            });
        }
        return lore;
    }

}

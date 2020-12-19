package tk.shanebee.hg.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.KitEntry;
import tk.shanebee.hg.data.Language;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GUI showing all available kits to a player
 */
public class KitsGUI implements InventoryHolder, Listener {

    private final List<KitEntry> kits = new ArrayList<>();
    private final List<KitGUI> guis = new ArrayList<>();
    private final Inventory inv;
    private final Language lang = HG.getPlugin().getLang();

    public KitsGUI(Game game, Player player, List<KitEntry> kits) {
        kits.forEach(kitEntry -> {
            if (kitEntry.hasKitPermission(player)) {
                this.kits.add(kitEntry);
                this.guis.add(new KitGUI(game, this, kitEntry));
            }
        });
        int size = (int) Math.ceil((double) kits.size() / 9);
        String title = lang.kits_gui_title.replace("<arena>", game.getGameArenaData().getName());
        this.inv = Bukkit.createInventory(this, size * 9, Util.getColString(title));
        Bukkit.getPluginManager().registerEvents(this, HG.getPlugin());

        AtomicInteger i = new AtomicInteger(0);
        kits.forEach(kitEntry -> {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Util.getColString(lang.kit_name.replace("<name>", kitEntry.getName())));
            item.setItemMeta(meta);
            inv.setItem(i.getAndIncrement(), item);
        });
    }

    public void open(Player player) {
        player.openInventory(inv);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inv;
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) return;
        if (event.getClickedInventory() != this.inv) return;
        event.setCancelled(true);

        int clickedSlot = event.getSlot();
        if (clickedSlot + 1 <= kits.size()) {
            KitGUI gui = guis.get(clickedSlot);
            gui.open((Player) event.getWhoClicked());
        }
    }

}

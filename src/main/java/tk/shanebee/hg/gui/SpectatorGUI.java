package tk.shanebee.hg.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

import java.util.Arrays;
import java.util.UUID;

public class SpectatorGUI implements InventoryHolder, Listener {

    private final Inventory inv;
    private final Game game;

    public SpectatorGUI(Game game) {
        this.game = game;
        int size = (game.getMaxPlayers() / 9) + 1;
        inv = Bukkit.createInventory(this, 9 * Math.min(size, 6), game.getName());
        Bukkit.getPluginManager().registerEvents(this, HG.getPlugin());
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inv;
    }

    private void initializeItems() {
        int i = 0;
        for (UUID uuid : game.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            inv.setItem(i, getHead(player));
            i++;
        }
    }

    private ItemStack getHead(OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = ((SkullMeta) head.getItemMeta());
        assert meta != null;
        meta.setOwningPlayer(player);
        meta.setDisplayName(player.getName());
        String[] lore = Util.getColString(HG.getPlugin().getLang().spectator_compass_head_lore).split(";");
        meta.setLore(Arrays.asList(lore));
        head.setItemMeta(meta);
        return head;
    }

    public void openInventory(Player player) {
        player.openInventory(inv);
        initializeItems();
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        if (inv.getHolder() != this) return;
        if (!game.getSpectators().contains(event.getWhoClicked().getUniqueId())) return;

        event.setCancelled(true);
        Player player = ((Player) event.getWhoClicked());
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        if (!(clickedItem.getItemMeta() instanceof SkullMeta)) return;
        Player clicked = getClicked(((SkullMeta) clickedItem.getItemMeta()));
        if (clicked == null) return;
        player.teleport(clicked);
    }

    private Player getClicked(SkullMeta meta) {
        OfflinePlayer player = meta.getOwningPlayer();
        if (player == null || !player.isOnline() || !game.getPlayers().contains(player.getUniqueId())) return null;
        return ((Player) player);
    }

}

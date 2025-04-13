package com.shanebeestudios.hg.api.gui;

import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpectatorGUI implements InventoryHolder {

    private final Language lang = HungerGames.getPlugin().getLang();
    private final Player player;
    private final Inventory inventory;
    private final Map<Integer, Player> activePlayers = new HashMap<>();

    public SpectatorGUI(Game game, Player player) {
        this.player = player;
        List<Player> gamePlayers = game.getGamePlayerData().getPlayers();

        int size = ((gamePlayers.size() / 9) + 1) * 9;
        String name = game.getGameArenaData().getName();
        this.inventory = Bukkit.createInventory(this, size,
            Util.getMini(this.lang.spectate_gui_title.replace("<arena>", name)));

        int slot = 0;
        for (Player activePlayer : gamePlayers) {
            ItemStack head = getPlayerHead(activePlayer);
            this.inventory.setItem(slot, head);
            this.activePlayers.put(slot, activePlayer);
            slot++;
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    private ItemStack getPlayerHead(OfflinePlayer player) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = ((SkullMeta) itemStack.getItemMeta());
        assert meta != null;
        meta.setOwningPlayer(player);
        meta.displayName(Util.getMini(player.getName()));
        List<Component> lore = new ArrayList<>();
        for (String line : this.lang.spectate_compass_head_lore) {
            lore.add(Util.getMini(line));
        }
        meta.lore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public void open() {
        this.player.openInventory(this.inventory);
    }

    public void click(int slot) {
        Player player = this.activePlayers.get(slot);
        if (player != null) {
            this.player.closeInventory();
            this.player.teleport(player.getLocation());
        }
    }

}

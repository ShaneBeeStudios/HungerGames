package com.shanebeestudios.hg.old_commands;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.shanebeestudios.hg.data.PlayerSession;
import com.shanebeestudios.hg.util.Util;

import java.util.ArrayList;
import java.util.Arrays;

public class WandCmd {

//    private final ItemStack WAND;
//
//    public WandCmd() {
//        forcePlayer = true;
//        cmdName = "wand";
//        argLength = 1;
//        WAND = new ItemStack(Material.BLAZE_ROD, 1);
//        ItemMeta meta = WAND.getItemMeta();
//        assert meta != null;
//        meta.setDisplayName(Util.getColString("&3HungerGames Wand"));
//        meta.setLore(new ArrayList<>(Arrays.asList(
//                Util.getColString("&7Left or Right Click"),
//                Util.getColString("&7to set positions")
//        )));
//        WAND.setItemMeta(meta);
//    }
//
//    @Override
//    public boolean run() {
//        if (plugin.getPlayerSessions().containsKey(player.getUniqueId())) {
//            plugin.getPlayerSessions().remove(player.getUniqueId());
//            Util.sendPrefixedMessage(player, "&cWand disabled!");
//        } else {
//            if (!player.getInventory().getItemInMainHand().isSimilar(WAND)) {
//                player.getInventory().addItem(WAND);
//            }
//            plugin.getPlayerSessions().put(player.getUniqueId(), new PlayerSession());
//            Util.sendPrefixedMessage(player, "&aWand enabled!");
//        }
//        return true;
//    }

}

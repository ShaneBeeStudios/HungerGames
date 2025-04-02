package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.events.ChestOpenEvent;
import com.shanebeestudios.hg.api.util.BlockUtils;
import com.shanebeestudios.hg.api.util.Constants;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameBlockData;
import com.shanebeestudios.hg.game.GameBlockData.ChestType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

public class GameChestListener extends GameListenerBase {

    public GameChestListener(HungerGames plugin) {
        super(plugin);
    }

    @EventHandler
    private void onChestOpen(ChestOpenEvent event) {
        Block block = event.getChest();
        Game game = event.getGame();
        GameBlockData gameBlockData = game.getGameBlockData();
        if (gameBlockData.canBeFilled(block.getLocation())) {
            ChestType chestType = event.getChestType();
            HungerGames.getPlugin().getGameManager().fillChests(game, block, chestType);
            gameBlockData.logChest(chestType, block.getLocation());
        }
    }

    @EventHandler // Handle chest drop
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof Chest chest) {
            if (chest.getPersistentDataContainer().has(Constants.CHEST_DROP_BLOCK, PersistentDataType.BOOLEAN)) {
                Game game = this.gameManager.getGame(chest.getLocation());
                if (game != null) {
                    chest.getBlock().setType(Material.AIR);
                    chest.getWorld().createExplosion(chest.getLocation().add(0.5, 0.5, 0.5), 1, false, false);
                    game.getGameBlockData().removeChest(ChestType.CHEST_DROP, chest.getLocation());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onChestUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && this.playerManager.hasPlayerData(player)) {
            Block block = event.getClickedBlock();
            PlayerData playerData = this.playerManager.getPlayerData(player);
            assert block != null;
            assert playerData != null;

            Game game = playerData.getGame();
            if (block.getType() == Material.CHEST) {
                ChestType chestType = GameBlockData.ChestType.REGULAR;
                if (((Chest) block.getState()).getPersistentDataContainer().has(Constants.CHEST_DROP_BLOCK, PersistentDataType.BOOLEAN)) {
                    chestType = GameBlockData.ChestType.CHEST_DROP;
                }
                Bukkit.getServer().getPluginManager().callEvent(new ChestOpenEvent(game, block, chestType));
            } else if (BlockUtils.isBonusBlock(block)) {
                Bukkit.getServer().getPluginManager().callEvent(new ChestOpenEvent(game, block, GameBlockData.ChestType.BONUS));
            }
        }
    }

}

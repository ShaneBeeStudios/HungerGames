package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.BlockUtils;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.api.events.ChestOpenEvent;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class GameChestListener extends GameListenerBase {

    public GameChestListener(HungerGames plugin) {
        super(plugin);
    }

    @EventHandler
    private void onChestOpen(ChestOpenEvent event) {
        Block block = event.getChest();
        Game game = event.getGame();
        GameBlockData gameBlockData = game.getGameBlockData();
        if (!gameBlockData.isLoggedChest(block.getLocation())) {
            HungerGames.getPlugin().getGameManager().fillChests(block, game, event.isBonus());
            gameBlockData.addGameChest(block.getLocation());
        }
    }

    @EventHandler
    private void onChestUse(PlayerInteractEvent event) {
        //noinspection deprecation
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && playerManager.hasPlayerData(player)) {
            Block block = event.getClickedBlock();
            assert block != null;
            PlayerData pd = playerManager.getPlayerData(player);
            if (block.getType() == Material.CHEST) {
                Bukkit.getServer().getPluginManager().callEvent(new ChestOpenEvent(pd.getGame(), block, false));
            } else if (BlockUtils.isBonusBlock(block)) {
                Bukkit.getServer().getPluginManager().callEvent(new ChestOpenEvent(pd.getGame(), block, true));
            }
        }
    }

}

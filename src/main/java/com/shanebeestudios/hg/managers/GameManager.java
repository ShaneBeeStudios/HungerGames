package com.shanebeestudios.hg.managers;

import com.google.common.collect.ImmutableList;
import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
import com.shanebeestudios.hg.game.GameItemData;
import com.shanebeestudios.hg.game.GameRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * General manager for games
 */
public class GameManager {

    private final HungerGames plugin;
    private final Map<String, Game> games = new HashMap<>();
    private final Language lang;
    private final Random rg = new Random();

    public GameManager(HungerGames plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
    }

    /**
     * Get all games
     *
     * @return All games
     */
    public ImmutableList<Game> getGames() {
        return ImmutableList.copyOf(this.games.values());
    }

    /**
     * Get names of all games
     *
     * @return Names of all games
     */
    public List<String> getGameNames() {
        return this.games.keySet().stream().sorted().collect(Collectors.toList());
    }

    /**
     * Stop all currently running games
     */
    public void stopAllGames() {
        PlayerManager playerManager = this.plugin.getPlayerManager();
        List<Player> players = new ArrayList<>();
        for (Game game : this.games.values()) {
            game.cancelTasks();
            game.getGameBlockData().forceRollback();
            players.addAll(game.getGamePlayerData().getPlayers());
            players.addAll(game.getGamePlayerData().getSpectators());
        }
        for (Player player : players) {
            UUID uuid = player.getUniqueId();
            player.closeInventory();
            if (playerManager.hasPlayerData(uuid)) {
                playerManager.getPlayerData(uuid).getGame().getGamePlayerData().leave(player, false);
                playerManager.removePlayerData(uuid);
            }
            if (playerManager.hasSpectatorData(uuid)) {
                playerManager.getSpectatorData(uuid).getGame().getGamePlayerData().leaveSpectate(player);
                playerManager.removePlayerData(uuid);
            }
        }
        this.games.clear();
    }

    public Game createGame(String name, Block corner1, Block corner2, List<Location> spawns, Sign sign,
                           int timer, int minPlayers, int maxPlayers, int cost) {
        GameRegion gameRegion = GameRegion.createNew(corner1, corner2);
        int roam = 1; // tODO  what are you?
        boolean isReady = true; // TODO yeah?
        Game game = new Game(name, gameRegion, spawns, sign, timer, minPlayers, maxPlayers, roam, isReady, cost);
        this.games.put(name, game);
        this.plugin.getArenaConfig().saveGameToConfig(game);
        return game;
    }

    public void loadGameFromConfig(String name, Game game) {
        this.games.put(name, game);
    }

    /**
     * Check the status of a game while being set up
     *
     * @param game   Game to check
     * @param player Player issuing the check
     */
    public void checkGame(Game game, Player player) {
        GameArenaData gameArenaData = game.getGameArenaData();
        String name = gameArenaData.getName();
        if (gameArenaData.getSpawns().size() < gameArenaData.getMaxPlayers()) {
            Util.sendPrefixedMessage(player, lang.check_need_more_spawns.replace("<number>",
                "" + (gameArenaData.getMaxPlayers() - gameArenaData.getSpawns().size())));
        } else if (gameArenaData.getStatus() == Status.BROKEN) {
            Util.sendPrefixedMessage(player, lang.check_broken_debug.replace("<arena>", name));
            Util.sendPrefixedMessage(player, lang.check_broken_debug_2.replace("<arena>", name));
        } else if (!game.getGameBlockData().isLobbyValid()) {
            Util.sendPrefixedMessage(player, lang.check_invalid_lobby);
            Util.sendPrefixedMessage(player, lang.check_set_lobby.replace("<arena>", name));
        } else {
            Util.sendPrefixedMessage(player, lang.check_ready_run.replace("<arena>", name));
            gameArenaData.setStatus(Status.READY);
        }
    }

    /**
     * Fill chests in a game
     *
     * @param block Chest to fill
     * @param game  Game this chest is in
     * @param bonus Whether or not this is a bonus chest
     */
    public void fillChests(Block block, Game game, boolean bonus) {
        Inventory i = ((InventoryHolder) block.getState()).getInventory();
        List<Integer> slots = new ArrayList<>();
        for (int slot = 0; slot <= 26; slot++) {
            slots.add(slot);
        }
        Collections.shuffle(slots);
        i.clear();
        int max = bonus ? Config.maxbonuscontent : Config.maxchestcontent;
        int min = bonus ? Config.minbonuscontent : Config.minchestcontent;

        int c = rg.nextInt(max) + 1;
        c = Math.max(c, min);
        while (c != 0) {
            ItemStack it = randomItem(game, bonus);
            int slot = slots.getFirst();
            slots.removeFirst();
            i.setItem(slot, it);
            c--;
        }
    }

    /**
     * Get a random item from a game's item list
     *
     * @param game  Game to get the item from
     * @param bonus Whether or not its a bonus item
     * @return Random ItemStack
     */
    public ItemStack randomItem(Game game, boolean bonus) {
        GameItemData gameItemData = game.getGameItemData();
        if (bonus) {
            int r = gameItemData.getBonusItems().size();
            if (r == 0) {
                return new ItemStack(Material.AIR);
            }
            int i = rg.nextInt(r) + 1;
            return gameItemData.getBonusItems().get(i);
        } else {
            int r = gameItemData.getItems().size();
            if (r == 0) {
                return new ItemStack(Material.AIR);
            }
            int i = rg.nextInt(r) + 1;
            return gameItemData.getItems().get(i);
        }
    }

    /**
     * Check if a location is in a game's bounds
     *
     * @param location The location to check for a game
     * @return True if the location is within a game's bounds
     */
    public boolean isInRegion(Location location) {
        for (Game g : this.games.values()) {
            if (g.getGameArenaData().isInRegion(location))
                return true;
        }
        return false;
    }

    /**
     * Get a game at a location
     *
     * @param location The location to check for a game
     * @return The game
     */
    public Game getGame(Location location) {
        for (Game g : this.games.values()) {
            if (g.getGameArenaData().isInRegion(location))
                return g;
        }
        return null;
    }

    /**
     * Get a game by name
     *
     * @param name The name of the game to find
     * @return The game
     */
    public Game getGame(String name) {
        return this.games.get(name);
    }

    /**
     * Get the number of games running
     *
     * @return Number of games running
     */
    public int gamesRunning() {
        int i = 0;
        for (Game game : this.games.values()) {
            switch (game.getGameArenaData().getStatus()) {
                case RUNNING:
                case COUNTDOWN:
                case FREE_ROAM:
                case ROLLBACK:
                    i++;
            }
        }
        return i;
    }

}

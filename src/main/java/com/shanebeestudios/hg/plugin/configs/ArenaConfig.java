package com.shanebeestudios.hg.plugin.configs;

import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.game.GameArenaData;
import com.shanebeestudios.hg.api.game.GameBorderData;
import com.shanebeestudios.hg.api.game.GameRegion;
import com.shanebeestudios.hg.api.parsers.LocationParser;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.Pair;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.managers.GameManager;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * General data handler for the plugin
 */
public class ArenaConfig {

    private final HungerGames plugin;
    private final GameManager gameManager;
    private File arenaDirectory;
    private final Map<String, Pair<File, FileConfiguration>> fileConfigMap = new HashMap<>();

    /**
     * @hidden
     */
    @ApiStatus.Internal
    public ArenaConfig(HungerGames plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        loadAllArenas();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Pair<File, FileConfiguration> getOrCreateConfig(String name) {
        if (this.fileConfigMap.containsKey(name)) {
            return this.fileConfigMap.get(name);
        }
        File file = new File(this.arenaDirectory, name + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Pair<File, FileConfiguration> fileConfig = Pair.of(file, config);
        this.fileConfigMap.put(name, fileConfig);
        return fileConfig;
    }

    private void saveArenaConfig(String name) {
        Pair<File, FileConfiguration> fileConfig = this.fileConfigMap.get(name);
        try {
            fileConfig.second().save(fileConfig.first());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void loadAllArenas() {
        Util.log("Loading arenas:");

        this.arenaDirectory = new File(this.plugin.getDataFolder(), "arenas");
        if (!this.arenaDirectory.exists()) {
            if (!this.arenaDirectory.mkdirs()) {
                Util.warning("Could not create arenas directory!");
            }
        }
        int readyCount = 0;
        int brokenCount = 0;
        for (File arenaFile : this.arenaDirectory.listFiles()) {
            String name = arenaFile.getName();
            if (name.endsWith(".yml")) {
                YamlConfiguration arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
                name = name.replace(".yml", "");
                boolean isReady = loadArena(arenaConfig, name);
                this.fileConfigMap.put(name, Pair.of(arenaFile, arenaConfig));
                if (isReady) {
                    readyCount++;
                } else {
                    brokenCount++;
                }
            }
        }
        if (readyCount == 0 && brokenCount == 0) {
            Util.log("- <red>No Arenas found. <grey>Time to create some!");
        }
        if (readyCount > 0) {
            Util.log("- <aqua>%s <grey>arenas have been <green>successfully loaded!", readyCount);
        }
        if (brokenCount > 0) {
            Util.log("- <aqua>%s <grey>arenas have <red>failed to successfully loaded!", brokenCount);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public boolean loadArena(FileConfiguration arenaConfig, String arenaName) {
        boolean isReady = true;
        boolean isDirty = false;
        List<Location> spawns = new ArrayList<>();
        Location lobbysign = null;
        int timer = 0;
        int cost = 0;
        int minPlayers = 0;
        int maxPlayers = 0;
        int freeRoamTime = 0;
        GameRegion gameRegion = null;
        List<String> commands;

        // INFO
        ConfigurationSection infoSection = arenaConfig.getConfigurationSection("info");
        try {
            timer = infoSection.getInt("timer");
            minPlayers = infoSection.getInt("min_players");
            maxPlayers = infoSection.getInt("max_players");
            freeRoamTime = infoSection.getInt("free_roam_time");
        } catch (Exception e) {
            Util.warning("Unable to load information for arena '" + arenaName + "'!");
            isReady = false;
        }
        try {
            cost = infoSection.getInt("cost");
        } catch (Exception ignore) {
        }

        // LOCATIONS
        ConfigurationSection locationsSection = arenaConfig.getConfigurationSection("locations");
        try {
            if (locationsSection.isString("lobby_sign")) {
                lobbysign = LocationParser.getBlockLocFromString(locationsSection.getString("lobby_sign"));
                isDirty = true;
            } else {
                lobbysign = locationsSection.getLocation("lobby_sign");
            }
        } catch (Exception e) {
            Util.warning("Unable to load lobby sign for arena '" + arenaName + "'!");
            Util.debug(e);
            isReady = false;
        }

        try {
            for (Object object : locationsSection.getList("spawns")) {
                if (object instanceof String s) {
                    Location locFromString = LocationParser.getLocFromString(s);
                    if (locFromString != null) {
                        spawns.add(locFromString);
                        isDirty = true;
                    }
                } else if (object instanceof Location location) {
                    spawns.add(location);
                }
            }
        } catch (Exception e) {
            Util.warning("Unable to load random spawns for arena '" + arenaName + "'!");
            isReady = false;
        }

        // REGION
        try {
            ConfigurationSection regionSection = arenaConfig.getConfigurationSection("region");
            BoundingBox boundingBox = regionSection.getObject("bounding_box", BoundingBox.class);

            if (regionSection.isSet("world")) {
                String world = regionSection.getString("world");
                gameRegion = GameRegion.loadFromConfig(world, boundingBox);
                isDirty = true;
            } else if (regionSection.isSet("world_key")) {
                String worldKey = regionSection.getString("world_key");
                gameRegion = GameRegion.loadFromConfig(NamespacedKey.fromString(worldKey), boundingBox);
            }
        } catch (Exception e) {
            Util.warning("Unable to load region bounds for arena " + arenaName + "!");
            isReady = false;
        }

        Game game = new Game(arenaName, gameRegion, spawns, lobbysign, timer, minPlayers, maxPlayers, freeRoamTime, isReady, cost);
        this.gameManager.loadGameFromConfig(arenaName, game);
        GameArenaData gameArenaData = game.getGameArenaData();

        World world = gameRegion.getWorld();
        if (world.getDifficulty() == Difficulty.PEACEFUL) {
            Util.warning("Difficulty in world '%s' for arena '%s' is set to PEACEFUL...", world.getName(), arenaName);
            Util.warning("This can have negative effects on the game, please consider raising the difficulty.");
        }

        // KITS
        this.plugin.getKitManager().loadGameKits(game, arenaConfig);
        // MOBS
        this.plugin.getMobManager().loadGameMobs(game, arenaConfig);
        // ITEMS
        this.plugin.getItemManager().loadGameItems(game, arenaConfig);

        // BORDER
        if (arenaConfig.isSet("game_border")) {
            ConfigurationSection borderSection = arenaConfig.getConfigurationSection("game_border");
            GameBorderData gameBorderData = game.getGameBorderData();

            if (borderSection.isSet("center_locations")) {
                List<?> centerLocations = borderSection.getList("center_locations");
                List<Location> centerLocationList = new ArrayList<>();
                for (Object object : centerLocations) {
                    if (object instanceof String locString) {
                        Location centerLocation = LocationParser.getBlockLocFromString(locString);
                        centerLocationList.add(centerLocation);
                        isDirty = true;
                    } else if (object instanceof Location location) {
                        centerLocationList.add(location);
                    }
                }
                gameBorderData.setCenterLocations(centerLocationList);
            } else if (borderSection.isSet("center_location")) { // Deprecated (May 5/2026)
                String centerLocString = borderSection.getString("center_location");
                Location borderCenter = LocationParser.getBlockLocFromString(centerLocString);
                gameBorderData.setCenterLocations(List.of(borderCenter));
                isDirty = true;
            }
            if (borderSection.isSet("final_size")) {
                int borderSize = borderSection.getInt("final_size");
                gameBorderData.setFinalBorderSize(borderSize);
            }
            if (borderSection.isSet("countdown_start") && borderSection.isSet("countdown_end")) {
                int countdownStart = borderSection.getInt("countdown_start");
                int countdownEnd = borderSection.getInt("countdown_end");
                gameBorderData.setBorderCountdownStart(countdownStart);
                gameBorderData.setBorderCountdownEnd(countdownEnd);
            }
        }

        // COMMANDS
        if (arenaConfig.isSet("commands")) {
            commands = arenaConfig.getStringList("commands");
        } else {
            commands = Collections.singletonList("none");
        }
        game.getGameCommandData().setCommands(commands);

        // CHEST REFILL
        if (arenaConfig.isConfigurationSection("chest_refill")) {
            ConfigurationSection chestRefillSection = arenaConfig.getConfigurationSection("chest_refill");
            if (chestRefillSection.isSet("time")) {
                int chestRefill = chestRefillSection.getInt("time");
                gameArenaData.setChestRefillTime(chestRefill);
            }
            if (chestRefillSection.isSet("repeat")) {
                int chestRefillRepeat = chestRefillSection.getInt("repeat");
                gameArenaData.setChestRefillRepeat(chestRefillRepeat);
            }
        }
        try {
            if (locationsSection.isSet("exit")) {
                if (locationsSection.isString("exit")) {
                    Location exitLocation = LocationParser.getLocFromString(locationsSection.getString("exit"));
                    if (exitLocation != null) {
                        gameArenaData.setExitLocation(exitLocation);
                        isDirty = true;
                    }
                } else if (locationsSection.isLocation("exit")) {
                    Location exitLocation = locationsSection.getLocation("exit");
                    gameArenaData.setExitLocation(exitLocation);
                }
            }

        } catch (Exception exception) {
            Util.log("- <yellow>Failed to setup exit location for arena '%s', defaulting to spawn location of world '%s'",
                arenaName, world.getName());
            Util.debug(exception);
        }
        if (gameArenaData.getStatus() == Status.BROKEN) {
            Util.warning("<red>Failed to properly initiate arena <white>'<aqua>%s<white>'<grey>", arenaName);
            Util.warning("Run <white>'<aqua>/hg debug %s<white>' <yellow>to check the status of the arena.", arenaName);
            isReady = false;
        } else {
            Util.log("- Loaded arena <white>'<aqua>%s<white>'<grey>", arenaName);
        }
        if (isDirty) {
            saveGameToConfig(game);
        }
        return isReady;
    }

    /**
     * Save a game to config
     *
     * @param game Game to save
     */
    public void saveGameToConfig(Game game) {
        String arenaName = game.getGameArenaData().getName();
        Pair<File, FileConfiguration> fileConfig = getOrCreateConfig(arenaName);
        FileConfiguration gameSection = fileConfig.second();

        GameArenaData gameArenaData = game.getGameArenaData();

        // CHEST REFILL
        ConfigurationSection chestRefillSection = gameSection.createSection("chest_refill");
        int chestRefillTime = gameArenaData.getChestRefillTime();
        if (chestRefillTime > 0) chestRefillSection.set("time", chestRefillTime);
        int chestRefillRepeat = gameArenaData.getChestRefillRepeat();
        if (chestRefillRepeat > 0) chestRefillSection.set("repeat", chestRefillRepeat);

        // INFO
        ConfigurationSection infoSection = gameSection.createSection("info");
        infoSection.set("cost", gameArenaData.getCost());
        infoSection.set("timer", gameArenaData.getTimer());
        infoSection.set("min_players", gameArenaData.getMinPlayers());
        infoSection.set("max_players", gameArenaData.getMaxPlayers());
        infoSection.set("free_roam_time", gameArenaData.getFreeRoamTime());

        // REGION
        ConfigurationSection regionSection = gameSection.createSection("region");
        regionSection.set("world_key", gameArenaData.getGameRegion().getWorld().getKey().toString());
        regionSection.set("bounding_box", gameArenaData.getGameRegion().getBoundingBox());

        // COMMANDS
        gameSection.set("commands", game.getGameCommandData().getCommands());

        // LOCATIONS
        ConfigurationSection locationsSection = gameSection.createSection("locations");
        locationsSection.set("spawns", gameArenaData.getSpawns());

        Location exit = gameArenaData.getExitLocation();
        if (exit != null) {
            locationsSection.set("exit", exit);
        }
        locationsSection.set("lobby_sign", game.getLobbyLocation());

        // BORDER
        GameBorderData borderData = game.getGameBorderData();
        if (!borderData.isDefault()) {
            ConfigurationSection borderSection = gameSection.createSection("game_border");
            borderSection.set("center_locations", borderData.getCenterLocations());
            borderSection.set("final_size", borderData.getFinalBorderSize());
            borderSection.set("countdown_start", borderData.getBorderCountdownStart());
            borderSection.set("countdown_end", borderData.getBorderCountdownEnd());
        }

        saveArenaConfig(arenaName);
    }

    /**
     * Remove an arena
     *
     * @param game Game to remove
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void removeArena(Game game) {
        String name = game.getGameArenaData().getName();
        Pair<File, FileConfiguration> fileFileConfigurationPair = this.fileConfigMap.get(name);
        fileFileConfigurationPair.first().delete();
        this.fileConfigMap.remove(name);
    }

}

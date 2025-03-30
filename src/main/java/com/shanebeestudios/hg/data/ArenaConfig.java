package com.shanebeestudios.hg.data;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.game.GameRegion;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
import com.shanebeestudios.hg.managers.ItemStackManager;
import com.shanebeestudios.hg.managers.KitManager;
import com.shanebeestudios.hg.tasks.CompassTask;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * General data handler for the plugin
 */
public class ArenaConfig {

    private FileConfiguration arenaConfig = null;
    private File arenaConfigFile = null;
    private final HungerGames plugin;
    private final ItemStackManager itemStackManager;

    public ArenaConfig(HungerGames plugin) {
        this.plugin = plugin;
        this.itemStackManager = plugin.getItemStackManager();
        reloadCustomConfig();
        loadArenas();
    }

    /**
     * Get arena data file
     *
     * @return Arena data file
     */
    public FileConfiguration getConfig() {
        return arenaConfig;
    }

    public void reloadCustomConfig() {
        if (this.arenaConfigFile == null) {
            this.arenaConfigFile = new File(plugin.getDataFolder(), "arenas.yml");
        }
        if (!this.arenaConfigFile.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                this.arenaConfigFile.createNewFile();
            } catch (IOException e) {
                Util.warning("&cCould not create arena.yml!");
                Util.debug(e);
            }
            this.arenaConfig = YamlConfiguration.loadConfiguration(this.arenaConfigFile);
            saveArenaConfig();
            Util.log("New arenas.yml file has been successfully generated!");
        } else {
            this.arenaConfig = YamlConfiguration.loadConfiguration(this.arenaConfigFile);
        }
    }

    public FileConfiguration getCustomConfig() {
        if (arenaConfig == null) {
            this.reloadCustomConfig();
        }
        return arenaConfig;
    }

    public void saveArenaConfig() {
        if (arenaConfig == null || arenaConfigFile == null) {
            return;
        }
        try {
            getCustomConfig().save(arenaConfigFile);
        } catch (IOException ex) {
            Util.log("Could not save config to " + arenaConfigFile);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void loadArenas() {
        Util.log("Loading arenas:");
        Configuration pluginConfig = plugin.getHGConfig().getConfig();
        int freeRoamTime = pluginConfig.getInt("settings.free-roam");

        if (this.arenaConfigFile.exists()) {
            new CompassTask(this.plugin);

            ConfigurationSection allArenasSection = this.arenaConfig.getConfigurationSection("arenas");

            if (allArenasSection != null) {
                for (String arenaName : allArenasSection.getKeys(false)) {
                    boolean isReady = true;
                    List<Location> spawns = new ArrayList<>();
                    Sign lobbysign = null;
                    int timer = 0;
                    int cost = 0;
                    int minPlayers = 0;
                    int maxPlayers = 0;
                    GameRegion gameRegion = null;
                    List<String> commands;

                    ConfigurationSection arenaSection = allArenasSection.getConfigurationSection(arenaName);

                    // INFO
                    ConfigurationSection infoSection = arenaSection.getConfigurationSection("info");
                    try {
                        timer = infoSection.getInt("timer");
                        minPlayers = infoSection.getInt("min_players");
                        maxPlayers = infoSection.getInt("max_players");
                    } catch (Exception e) {
                        Util.warning("Unable to load information for arena '" + arenaName + "'!");
                        isReady = false;
                    }
                    try {
                        cost = infoSection.getInt("cost");
                    } catch (Exception ignore) {
                    }

                    // LOCATIONS
                    ConfigurationSection locationsSection = arenaSection.getConfigurationSection("locations");
                    try {
                        // TODO proper class for this
                        lobbysign = (Sign) getBlockLocFromString(locationsSection.getString("lobby_sign")).getBlock().getState();
                    } catch (Exception e) {
                        Util.warning("Unable to load lobby sign for arena '" + arenaName + "'!");
                        Util.debug(e);
                        isReady = false;
                    }

                    try {
                        for (String location : locationsSection.getStringList("spawns")) {
                            spawns.add(getLocFromString(location));
                        }
                    } catch (Exception e) {
                        Util.warning("Unable to load random spawns for arena '" + arenaName + "'!");
                        isReady = false;
                    }

                    // REGION
                    try {
                        ConfigurationSection regionSection = arenaSection.getConfigurationSection("region");
                        String world = regionSection.getString("world");
                        BoundingBox boundingBox = regionSection.getObject("bounding_box", BoundingBox.class);
                        gameRegion = GameRegion.loadFromConfig(world, boundingBox);
                    } catch (Exception e) {
                        Util.warning("Unable to load region bounds for arena " + arenaName + "!");
                        isReady = false;
                    }

                    Game game = new Game(arenaName, gameRegion, spawns, lobbysign, timer, minPlayers, maxPlayers, freeRoamTime, isReady, cost);
                    this.plugin.getGameManager().loadGameFromConfig(arenaName, game);
                    GameArenaData gameArenaData = game.getGameArenaData();

                    World world = gameRegion.getWorld();
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        Util.warning("Difficulty in world '%s' for arena '%s' is set to PEACEFUL...", world.getName(), arenaName);
                        Util.warning("This can have negative effects on the game, please consider raising the difficulty.");
                    }

                    // KITS
                    KitManager kitManager = plugin.getItemStackManager().loadGameKits(arenaName, arenaSection);
                    if (kitManager != null)
                        game.setKitManager(kitManager);

                    // ITEMS
                    if (arenaSection.isSet("items")) {
                        ConfigurationSection itemsSection = arenaSection.getConfigurationSection("items");
                        if (itemsSection.isSet("items")) {
                            HashMap<Integer, ItemStack> items = new HashMap<>();
                            this.itemStackManager.loadItems(itemsSection.getMapList("items"), items);
                            game.getGameItemData().setItems(items);
                            Util.log(items.size() + " Random items have been loaded for arena: &b" + arenaName);
                        }
                        if (itemsSection.isSet("bonus")) {
                            HashMap<Integer, ItemStack> bonusItems = new HashMap<>();
                            this.itemStackManager.loadItems(itemsSection.getMapList("bonus"), bonusItems);
                            game.getGameItemData().setBonusItems(bonusItems);
                            Util.log(bonusItems.size() + " Random bonus items have been loaded for arena: &b" + arenaName);
                        }
                    }

                    // BORDER
                    if (arenaSection.isSet("border")) {
                        ConfigurationSection borderSection = arenaSection.getConfigurationSection("border");
                        if (borderSection.isSet("center")) {
                            Location borderCenter = getBlockLocFromString(borderSection.getString("center"));
                            game.getGameBorderData().setBorderCenter(borderCenter);
                        }
                        if (borderSection.isSet("size")) {
                            int borderSize = borderSection.getInt("size");
                            game.getGameBorderData().setBorderSize(borderSize);
                        }
                        if (borderSection.isSet("countdown_start") && borderSection.isSet("countdown_end")) {
                            int countdownStart = borderSection.getInt("countdown_start");
                            int countdownEnd = borderSection.getInt("countdown_end");
                            game.getGameBorderData().setBorderTimer(countdownStart, countdownEnd);
                        }
                    }

                    // COMMANDS
                    if (arenaSection.isSet("commands")) {
                        commands = arenaSection.getStringList("commands");
                    } else {
                        //this.arenaConfig.set("arenas." + arenaName + ".commands", Collections.singletonList("none"));
                        // TODO test that this works (not sure if it saves when just setting a section)
                        arenaSection.set("commands", Collections.singletonList("none"));
                        saveArenaConfig();
                        commands = Collections.singletonList("none");
                    }
                    game.getGameCommandData().setCommands(commands);

                    // CHEST REFILL
                    if (arenaSection.isConfigurationSection("chest_refill")) {
                        ConfigurationSection chestRefillSection = arenaSection.getConfigurationSection("chest_refill");
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
                        Location exitLocation;
                        boolean persistent = false;
                        if (locationsSection.isSet("exit")) {
                            exitLocation = getLocFromString(locationsSection.getString("exit"));
                            persistent = true;
                        } else if (this.arenaConfig.isSet("global_exit_location")) {
                            exitLocation = getLocFromString(this.arenaConfig.getString("global_exit_location"));
                        } else {
                            exitLocation = game.getLobbyLocation().getWorld().getSpawnLocation();
                        }
                        gameArenaData.setExit(exitLocation, persistent);
                    } catch (Exception exception) {
                        World mainWorld = Bukkit.getWorlds().getFirst();
                        gameArenaData.setExit(mainWorld.getSpawnLocation(), false);
                        Util.log("- <yellow>Failed to setup exit location for arena '%s', defaulting to spawn location of world '%s'",
                            arenaName, world.getName());
                        Util.debug(exception);
                    }
                    Util.log("- Loaded arena <white>'<aqua>%s<white>'<grey>", arenaName);

                }
                Util.log("- Arenas have been <green>successfully loaded!");
            } else {
                Util.log("- <red>No Arenas found. <grey>Time to create some!");
            }
        }
    }

    public void setGlobalExit(Location location) {
        String locString = locToString(location);
        this.arenaConfig.set("global_exit_location", locString);
        saveArenaConfig();
    }

    public void saveGameToConfig(String arenaName, Game game) {
        ConfigurationSection gameSection = this.arenaConfig.createSection("arenas." + arenaName);
        GameArenaData gameArenaData = game.getGameArenaData();

        ConfigurationSection infoSection = gameSection.createSection("info");
        infoSection.set("cost", gameArenaData.getCost());
        infoSection.set("timer", gameArenaData.getTimer());
        infoSection.set("min_players", gameArenaData.getMinPlayers());
        infoSection.set("max_players", gameArenaData.getMaxPlayers());

        ConfigurationSection regionSection = gameSection.createSection("region");
        regionSection.set("world", gameArenaData.getBound().getWorld().getName());
        regionSection.set("bounding_box", gameArenaData.getBound().getBoundingBox());

        gameSection.set("commands", game.getGameCommandData().getCommands());

        ConfigurationSection locationsSection = gameSection.createSection("locations");
        List<String> spawns = new ArrayList<>();
        gameArenaData.getSpawns().forEach(spawn -> spawns.add(locToString(spawn)));
        locationsSection.set("spawns", spawns);

        Location exit = gameArenaData.getPersistentExit();
        if (exit != null) {
            locationsSection.set("exit", blockLocToString(exit));
        }
        locationsSection.set("lobby_sign", blockLocToString(game.getLobbyLocation()));

        // TODO border

        saveArenaConfig();
    }

    public String blockLocToString(Location location) {
        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }

    public String locToString(Location location) {
        float yaw = (float)Math.floor(location.getYaw());
        float pitch = (float)Math.floor(location.getPitch());
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + yaw + ":" + pitch;
    }

    public Location getLocFromString(String stringLocation) {
        String[] split = stringLocation.split(":");
        World world = Bukkit.getWorld(split[0]);
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = 0;
        float pitch = 0;
        if (split.length >= 5) {
            yaw = Float.parseFloat(split[4]);
        }
        if (split.length == 6) {
            pitch = Float.parseFloat(split[5]);
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    public Location getBlockLocFromString(String stringLocation) {
        String[] split = stringLocation.split(":");
        return new Location(Bukkit.getServer().getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }

}

package com.shanebeestudios.hg;

import com.shanebeestudios.hg.api.util.NBTApi;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.data.Leaderboard;
import com.shanebeestudios.hg.data.RandomItems;
import com.shanebeestudios.hg.managers.GameManager;
import com.shanebeestudios.hg.managers.ItemStackManager;
import com.shanebeestudios.hg.managers.KillManager;
import com.shanebeestudios.hg.managers.KitManager;
import com.shanebeestudios.hg.managers.Placeholders;
import com.shanebeestudios.hg.managers.PlayerManager;
import com.shanebeestudios.hg.managers.SessionManager;
import com.shanebeestudios.hg.plugin.commands.MainCommand;
import com.shanebeestudios.hg.plugin.configs.ArenaConfig;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.plugin.configs.MobConfig;
import com.shanebeestudios.hg.plugin.listeners.GameBlockListener;
import com.shanebeestudios.hg.plugin.listeners.GameChestListener;
import com.shanebeestudios.hg.plugin.listeners.GameCommandListener;
import com.shanebeestudios.hg.plugin.listeners.GameCompassListener;
import com.shanebeestudios.hg.plugin.listeners.GameDamageListenerBase;
import com.shanebeestudios.hg.plugin.listeners.GameEntityListener;
import com.shanebeestudios.hg.plugin.listeners.GameLobbyListener;
import com.shanebeestudios.hg.plugin.listeners.GamePlayerListener;
import com.shanebeestudios.hg.plugin.listeners.GameTrackingStickListener;
import com.shanebeestudios.hg.plugin.listeners.McmmoListeners;
import com.shanebeestudios.hg.plugin.listeners.SessionWandListener;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.exceptions.UnsupportedVersionException;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.mobs.MobManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * <b>Main class for HungerGames</b>
 */
public class HungerGames extends JavaPlugin {

    //Instances
    private static HungerGames plugin;
    private Config config;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private ArenaConfig arenaconfig;
    private KillManager killManager;
    private RandomItems randomItems;
    private Language lang;
    private KitManager kitManager;
    private ItemStackManager itemStackManager;
    private Leaderboard leaderboard;
    private Metrics metrics;
    private MobManager mmMobManager;

    // Managers
    private final SessionManager sessionManager = new SessionManager();

    //Mobs
    private MobConfig mobConfig;

    //NMS Nbt
    private NBTApi nbtApi;

    /**
     * @hidden
     */
    @Override
    public void onLoad() {
        try {
            CommandAPI.onLoad(new CommandAPIBukkitConfig(this)
                .setNamespace("hungergames")
                .verboseOutput(false)
                .silentLogs(true)
                .skipReloadDatapacks(true));
        } catch (UnsupportedVersionException ignore) {
            Util.log("CommandAPI does not support this version of Minecraft, will update soon.");
        }
    }

    @Override
    public void onEnable() {
        if (!Util.isRunningMinecraft(1, 21, 4)) {
            Util.warning("HungerGames does not support your server version!");
            Util.warning("Only versions 1.21.4+ are supported");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        loadPlugin(true);
    }

    public void loadPlugin(boolean load) {
        long start = System.currentTimeMillis();
        plugin = this;

        config = new Config(this);
        metrics = new Metrics(this, 25144);
        nbtApi = new NBTApi();

        //MythicMob check
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            mmMobManager = MythicProvider.get().getMobManager();
            Util.log("<grey>MythicMobs found, MythicMobs hook <green>enabled");
        } else {
            Util.log("<grey>MythicMobs not found, MythicMobs hooks have been <red>disabled");
        }
        lang = new Language(this);
        kitManager = new KitManager();
        itemStackManager = new ItemStackManager(this);
        mobConfig = new MobConfig(this);
        randomItems = new RandomItems(this);
        playerManager = new PlayerManager();
        this.gameManager = new GameManager(this);
        this.arenaconfig = new ArenaConfig(this);
        this.leaderboard = new Leaderboard(this);
        this.killManager = new KillManager(this);

        //PAPI check
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
            Util.log("<grey>PAPI found, Placeholders have been <green>enabled");
        } else {
            Util.log("<grey>PAPI not found, Placeholders have been <red>disabled");
        }
        //mcMMO check
        if (Bukkit.getPluginManager().getPlugin("mcMMO") != null) {
            if (Util.classExists("com.gmail.nossr50.events.skills.secondaryabilities.SubSkillEvent")) {
                getServer().getPluginManager().registerEvents(new McmmoListeners(this), this);
                Util.log("<grey>mcMMO found, mcMMO event hooks <green>enabled");
            } else {
                Util.log("<grey>mcMMO classic found. HungerGames does not support mcMMO classic, mcMMO hooks <red>disabled");
            }
        } else {
            Util.log("<grey>mcMMO not found, mcMMO event hooks have been <red>disabled");
        }

        loadCommands();
        loadListeners();

        if (this.getDescription().getVersion().contains("Beta")) {
            Util.log("<yellow>YOU ARE RUNNING A BETA VERSION, please use with caution");
            Util.log("<yellow>Report any issues to: <aqua>https://github.com/ShaneBeeStudios/HungerGames/issues");
        }

        Util.log("HungerGames has been <green>enabled<grey> in <aqua>%.2f seconds<grey>!", (float) (System.currentTimeMillis() - start) / 1000);
    }

    public void reloadPlugin() {
        unloadPlugin(true);
    }

    private void unloadPlugin(boolean reload) {
        this.gameManager.stopAllGames();
        plugin = null;
        config = null;
        metrics = null;
        nbtApi = null;
        mmMobManager = null;
        lang = null;
        kitManager = null;
        itemStackManager = null;
        mobConfig = null;
        randomItems = null;
        playerManager = null;
        arenaconfig = null;
        killManager = null;
        gameManager = null;
        leaderboard = null;
        HandlerList.unregisterAll(this);
        if (reload) {
            loadPlugin(false);
        }
    }

    @Override
    public void onDisable() {
        // I know this seems odd, but this method just
        // nulls everything to prevent memory leaks
        unloadPlugin(false);
        Util.log("HungerGames has been disabled!");
    }

    private void loadListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new GameBlockListener(this), this);
        pluginManager.registerEvents(new GameChestListener(this), this);
        pluginManager.registerEvents(new GameCommandListener(this), this);
        pluginManager.registerEvents(new GameCompassListener(this), this);
        pluginManager.registerEvents(new GameDamageListenerBase(this), this);
        pluginManager.registerEvents(new GameEntityListener(this), this);
        pluginManager.registerEvents(new GameLobbyListener(this), this);
        pluginManager.registerEvents(new GamePlayerListener(this), this);
        pluginManager.registerEvents(new GameTrackingStickListener(this), this);
        pluginManager.registerEvents(new SessionWandListener(this), this);
    }

    private void loadCommands() {
        if (CommandAPI.isLoaded()) {
            CommandAPI.onEnable();
            new MainCommand(this);
        }
    }

    /**
     * Get the instance of this plugin
     *
     * @return This plugin
     */
    public static HungerGames getPlugin() {
        return plugin;
    }

    /**
     * Get an instance of the RandomItems manager
     *
     * @return RandomItems manager
     */
    public RandomItems getRandomItems() {
        return this.randomItems;
    }

    /**
     * Get an instance of the KillManager
     *
     * @return KillManager
     */
    public KillManager getKillManager() {
        return this.killManager;
    }

    /**
     * Get an instance of the plugins main kit manager
     *
     * @return The kit manager
     */
    public KitManager getKitManager() {
        return this.kitManager;
    }

    /**
     * Get an instance of the ItemStackManager
     *
     * @return ItemStackManager
     */
    public ItemStackManager getItemStackManager() {
        return this.itemStackManager;
    }

    /**
     * Get the instance of the game manager
     *
     * @return The game manager
     */
    public GameManager getGameManager() {
        return this.gameManager;
    }

    /**
     * Get an instance of the PlayerManager
     *
     * @return PlayerManager
     */
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    /**
     * Get an instance of the ArenaConfig
     *
     * @return ArenaConfig
     */
    public ArenaConfig getArenaConfig() {
        return this.arenaconfig;
    }

    /**
     * Get an instance of HG's leaderboards
     *
     * @return Leaderboard
     */
    public Leaderboard getLeaderboard() {
        return this.leaderboard;
    }

    /**
     * Get an instance of the language file
     *
     * @return Language file
     */
    public Language getLang() {
        return this.lang;
    }

    /**
     * Get an instance of {@link Config}
     *
     * @return Config file
     */
    public Config getHGConfig() {
        return config;
    }

    /**
     * Get an instance of the mob confile
     *
     * @return Mob config
     */
    public MobConfig getMobConfig() {
        return this.mobConfig;
    }

    /**
     * Get the NBT API
     *
     * @return NBT API
     */
    public NBTApi getNbtApi() {
        return this.nbtApi;
    }

    public Metrics getMetrics() {
        return this.metrics;
    }

    /**
     * Get an instance of the MythicMobs MobManager
     *
     * @return MythicMobs MobManager
     */
    public MobManager getMmMobManager() {
        return this.mmMobManager;
    }

    // Managers
    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

}

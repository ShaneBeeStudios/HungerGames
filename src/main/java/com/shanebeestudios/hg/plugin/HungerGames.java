package com.shanebeestudios.hg.plugin;

import com.shanebeestudios.hg.api.data.Leaderboard;
import com.shanebeestudios.hg.api.region.TaskUtils;
import com.shanebeestudios.hg.api.util.NBTApi;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.commands.MainCommand;
import com.shanebeestudios.hg.plugin.configs.ArenaConfig;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.plugin.configs.Language;
import com.shanebeestudios.hg.plugin.listeners.GameBlockListener;
import com.shanebeestudios.hg.plugin.listeners.GameChestListener;
import com.shanebeestudios.hg.plugin.listeners.GameCommandListener;
import com.shanebeestudios.hg.plugin.listeners.GameCompassListener;
import com.shanebeestudios.hg.plugin.listeners.GameDamageListenerBase;
import com.shanebeestudios.hg.plugin.listeners.GameEntityListener;
import com.shanebeestudios.hg.plugin.listeners.GameKitGuiListener;
import com.shanebeestudios.hg.plugin.listeners.GameLobbyListener;
import com.shanebeestudios.hg.plugin.listeners.GamePlayerListener;
import com.shanebeestudios.hg.plugin.listeners.GameTrackingStickListener;
import com.shanebeestudios.hg.plugin.listeners.SessionWandListener;
import com.shanebeestudios.hg.plugin.managers.GameManager;
import com.shanebeestudios.hg.plugin.managers.ItemManager;
import com.shanebeestudios.hg.plugin.managers.KillManager;
import com.shanebeestudios.hg.plugin.managers.KitManager;
import com.shanebeestudios.hg.plugin.managers.MobManager;
import com.shanebeestudios.hg.plugin.managers.Placeholders;
import com.shanebeestudios.hg.plugin.managers.PlayerManager;
import com.shanebeestudios.hg.plugin.managers.SessionManager;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.exceptions.UnsupportedVersionException;
import io.lumine.mythic.api.MythicProvider;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>Main class for HungerGames</b>
 */
public class HungerGames extends JavaPlugin {

    //Instances
    private static HungerGames PLUGIN_INSTANCE;

    private Leaderboard leaderboard;
    private Metrics metrics;

    // Configs
    private Config config;
    private Language lang;
    private ArenaConfig arenaConfig;

    // Managers
    private ItemManager itemManager;
    private PlayerManager playerManager;
    private KitManager kitManager;
    private GameManager gameManager;
    private KillManager killManager;
    private SessionManager sessionManager;
    private MobManager mobManager;
    private io.lumine.mythic.api.mobs.MobManager mythicMobManager;

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
        NBTApi.initializeNBTApi();
        TaskUtils.initialize(this);
        loadPlugin(true);
    }

    @SuppressWarnings("deprecation")
    public void loadPlugin(boolean load) {
        long start = System.currentTimeMillis();
        PLUGIN_INSTANCE = this;

        this.config = new Config(this);

        //MythicMob check
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            this.mythicMobManager = MythicProvider.get().getMobManager();
            Util.log("<grey>MythicMobs found, MythicMobs hook <green>enabled");
        } else {
            Util.log("<grey>MythicMobs not found, MythicMobs hook <red>disabled");
        }
        this.lang = new Language(this);
        this.itemManager = new ItemManager(this);
        this.playerManager = new PlayerManager();
        this.kitManager = new KitManager(this);
        this.sessionManager = new SessionManager();
        this.mobManager = new MobManager(this);
        this.gameManager = new GameManager(this);
        this.arenaConfig = new ArenaConfig(this);
        this.leaderboard = new Leaderboard(this);
        this.killManager = new KillManager(this);

        //PAPI check
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
            Util.log("<grey>PAPI found, Placeholders have been <green>enabled");
        } else {
            Util.log("<grey>PAPI not found, Placeholders have been <red>disabled");
        }

        loadCommands();
        loadListeners();

        if (this.getDescription().getVersion().contains("beta")) {
            Util.warning("YOU ARE RUNNING A BETA VERSION, please use with caution");
            Util.warning("Report any issues to: <aqua>https://github.com/ShaneBeeStudios/HungerGames/issues");
        }

        setupMetrics();

        Util.log("HungerGames has been <green>enabled<grey> in <aqua>%.2f seconds<grey>!", (float) (System.currentTimeMillis() - start) / 1000);
    }

    public void reloadPlugin() {
        unloadPlugin(true);
    }

    private void unloadPlugin(boolean reload) {
        this.gameManager.stopAllGames();
        PLUGIN_INSTANCE = null;
        this.config = null;
        this.metrics = null;
        this.mobManager = null;
        this.mythicMobManager = null;
        this.lang = null;
        this.itemManager = null;
        this.kitManager = null;
        this.playerManager = null;
        this.arenaConfig = null;
        this.killManager = null;
        this.gameManager = null;
        this.leaderboard.saveLeaderboard();
        this.leaderboard = null;
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

    private void setupMetrics() {
        this.metrics = new Metrics(this, 25144);
        // Config
        this.metrics.addCustomChart(new DrilldownPie("config", () -> {
            Map<String, Map<String, Integer>> map = new HashMap<>();
            map.put("worldborder-enabled", Map.of("" + Config.WORLD_BORDER_ENABLED, 1));
            map.put("chestdrop-enabled", Map.of("" + Config.CHESTS_CHEST_DROP_ENABLED, 1));
            map.put("reward-enabled", Map.of("" + Config.REWARD_ENABLED, 1));
            map.put("spectate-enabled", Map.of("" + Config.SPECTATE_ENABLED, 1));
            return map;
        }));

        // Arenas
        this.metrics.addCustomChart(new SimplePie("arenas-count", () ->
            "" + this.gameManager.getGames().size()));
    }

    private void loadListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new GameBlockListener(this), this);
        pluginManager.registerEvents(new GameChestListener(this), this);
        pluginManager.registerEvents(new GameCommandListener(this), this);
        pluginManager.registerEvents(new GameCompassListener(this), this);
        pluginManager.registerEvents(new GameDamageListenerBase(this), this);
        pluginManager.registerEvents(new GameEntityListener(this), this);
        pluginManager.registerEvents(new GameKitGuiListener(this), this);
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
        return PLUGIN_INSTANCE;
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
     * Get an instance of the plugins main item manager
     *
     * @return The item manager
     */
    public ItemManager getItemManager() {
        return this.itemManager;
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
        return this.arenaConfig;
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
     * Get an instance of the MobManager
     *
     * @return MobManager
     */
    public MobManager getMobManager() {
        return this.mobManager;
    }

    public Metrics getMetrics() {
        return this.metrics;
    }

    /**
     * Get an instance of the MythicMobs MobManager
     *
     * @return MythicMobs MobManager
     */
    public io.lumine.mythic.api.mobs.MobManager getMythicMobManager() {
        return this.mythicMobManager;
    }

    // Managers
    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

}

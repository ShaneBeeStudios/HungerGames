package com.shanebeestudios.hg;

import com.shanebeestudios.hg.commands.AddSpawnCmd;
import com.shanebeestudios.hg.commands.BaseCmd;
import com.shanebeestudios.hg.commands.BorderCenterCmd;
import com.shanebeestudios.hg.commands.BorderSizeCmd;
import com.shanebeestudios.hg.commands.BorderTimerCmd;
import com.shanebeestudios.hg.commands.ChestRefillCmd;
import com.shanebeestudios.hg.commands.ChestRefillNowCmd;
import com.shanebeestudios.hg.commands.CreateCmd;
import com.shanebeestudios.hg.commands.DebugCmd;
import com.shanebeestudios.hg.commands.DeleteCmd;
import com.shanebeestudios.hg.commands.JoinCmd;
import com.shanebeestudios.hg.commands.KitCmd;
import com.shanebeestudios.hg.commands.LeaveCmd;
import com.shanebeestudios.hg.commands.ListCmd;
import com.shanebeestudios.hg.commands.ListGamesCmd;
import com.shanebeestudios.hg.commands.NBTCmd;
import com.shanebeestudios.hg.commands.ReloadCmd;
import com.shanebeestudios.hg.commands.SetExitCmd;
import com.shanebeestudios.hg.commands.SetLobbyWallCmd;
import com.shanebeestudios.hg.commands.SpectateCmd;
import com.shanebeestudios.hg.commands.StartCmd;
import com.shanebeestudios.hg.commands.StopCmd;
import com.shanebeestudios.hg.commands.TeamCmd;
import com.shanebeestudios.hg.commands.ToggleCmd;
import com.shanebeestudios.hg.commands.WandCmd;
import com.shanebeestudios.hg.data.ArenaConfig;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.data.Leaderboard;
import com.shanebeestudios.hg.data.MobConfig;
import com.shanebeestudios.hg.data.PlayerSession;
import com.shanebeestudios.hg.data.RandomItems;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.listeners.CancelListener;
import com.shanebeestudios.hg.listeners.CommandListener;
import com.shanebeestudios.hg.listeners.GameListener;
import com.shanebeestudios.hg.listeners.McmmoListeners;
import com.shanebeestudios.hg.listeners.WandListener;
import com.shanebeestudios.hg.managers.ItemStackManager;
import com.shanebeestudios.hg.managers.KillManager;
import com.shanebeestudios.hg.managers.KitManager;
import com.shanebeestudios.hg.managers.Manager;
import com.shanebeestudios.hg.managers.Placeholders;
import com.shanebeestudios.hg.managers.PlayerManager;
import com.shanebeestudios.hg.util.NBTApi;
import com.shanebeestudios.hg.util.Util;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.mobs.MobManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <b>Main class for HungerGames</b>
 */
public class HungerGames extends JavaPlugin {

    //Maps
    private Map<String, BaseCmd> cmds;
    private Map<UUID, PlayerSession> playerSession;

    //Lists
    private List<Game> games;

    //Instances
    private static HungerGames plugin;
    private Config config;
    private Manager manager;
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

    //Mobs
    private MobConfig mobConfig;

    //NMS Nbt
    private NBTApi nbtApi;

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

        if (load) {
            cmds = new HashMap<>();
        }
        games = new ArrayList<>();
        playerSession = new HashMap<>();

        config = new Config(this);
        metrics = new Metrics(this, 25144);
        nbtApi = new NBTApi();

        //MythicMob check
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            mmMobManager = MythicProvider.get().getMobManager();
            Util.log("&7MythicMobs found, MythicMobs hook &aenabled");
        } else {
            Util.log("&7MythicMobs not found, MythicMobs hooks have been &cdisabled");
        }
        lang = new Language(this);
        kitManager = new KitManager();
        itemStackManager = new ItemStackManager(this);
        mobConfig = new MobConfig(this);
        randomItems = new RandomItems(this);
        playerManager = new PlayerManager();
        arenaconfig = new ArenaConfig(this);
        killManager = new KillManager();
        manager = new Manager(this);
        leaderboard = new Leaderboard(this);

        //PAPI check
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
            Util.log("&7PAPI found, Placeholders have been &aenabled");
        } else {
            Util.log("&7PAPI not found, Placeholders have been &cdisabled");
        }
        //mcMMO check
        if (Bukkit.getPluginManager().getPlugin("mcMMO") != null) {
            if (Util.classExists("com.gmail.nossr50.events.skills.secondaryabilities.SubSkillEvent")) {
                getServer().getPluginManager().registerEvents(new McmmoListeners(this), this);
                Util.log("&7mcMMO found, mcMMO event hooks &aenabled");
            } else {
                Util.log("&7mcMMO classic found. HungerGames does not support mcMMO classic, mcMMO hooks &cdisabled");
            }
        } else {
            Util.log("&7mcMMO not found, mcMMO event hooks have been &cdisabled");
        }

        //noinspection ConstantConditions
        getCommand("hg").setExecutor(new CommandListener(this));
        if (load) {
            loadCmds();
        }
        getServer().getPluginManager().registerEvents(new WandListener(this), this);
        getServer().getPluginManager().registerEvents(new CancelListener(this), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);

        if (this.getDescription().getVersion().contains("Beta")) {
            Util.log("&eYOU ARE RUNNING A BETA VERSION, please use with caution");
            Util.log("&eReport any issues to: &bhttps://github.com/ShaneBeeStudios/HungerGames/issues");
        }

        Util.log("HungerGames has been &aenabled&7 in &b%.2f seconds&7!", (float) (System.currentTimeMillis() - start) / 1000);
    }

    public void reloadPlugin() {
        unloadPlugin(true);
    }

    private void unloadPlugin(boolean reload) {
        stopAll();
        games = null;
        playerSession = null;
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
        manager = null;
        leaderboard = null;
        HandlerList.unregisterAll(this);
        if (reload) {
            loadPlugin(false);
        } else {
            cmds = null;
        }
    }

    @Override
    public void onDisable() {
        // I know this seems odd, but this method just
        // nulls everything to prevent memory leaks
        unloadPlugin(false);
        Util.log("HungerGames has been disabled!");
    }

    private void loadCmds() {
        cmds.put("team", new TeamCmd());
        cmds.put("addspawn", new AddSpawnCmd());
        cmds.put("create", new CreateCmd());
        cmds.put("join", new JoinCmd());
        cmds.put("leave", new LeaveCmd());
        cmds.put("reload", new ReloadCmd());
        cmds.put("setlobbywall", new SetLobbyWallCmd());
        cmds.put("wand", new WandCmd());
        cmds.put("kit", new KitCmd());
        cmds.put("debug", new DebugCmd());
        cmds.put("list", new ListCmd());
        cmds.put("listgames", new ListGamesCmd());
        cmds.put("forcestart", new StartCmd());
        cmds.put("stop", new StopCmd());
        cmds.put("toggle", new ToggleCmd());
        cmds.put("setexit", new SetExitCmd());
        cmds.put("delete", new DeleteCmd());
        cmds.put("chestrefill", new ChestRefillCmd());
        cmds.put("chestrefillnow", new ChestRefillNowCmd());
        cmds.put("bordersize", new BorderSizeCmd());
        cmds.put("bordercenter", new BorderCenterCmd());
        cmds.put("bordertimer", new BorderTimerCmd());
        if (Config.spectateEnabled) {
            cmds.put("spectate", new SpectateCmd());
        }
        if (nbtApi != null) {
            cmds.put("nbt", new NBTCmd());
        }

        ArrayList<String> cArray = new ArrayList<>();
        cArray.add("join");
        cArray.add("leave");
        cArray.add("kit");
        cArray.add("listgames");
        cArray.add("list");

        for (String bc : cmds.keySet()) {
            getServer().getPluginManager().addPermission(new Permission("hg." + bc));
            if (cArray.contains(bc))
                //noinspection ConstantConditions
                getServer().getPluginManager().getPermission("hg." + bc).setDefault(PermissionDefault.TRUE);

        }
    }

    /**
     * Stop all games
     */
    public void stopAll() {
        ArrayList<UUID> ps = new ArrayList<>();
        for (Game g : games) {
            g.cancelTasks();
            g.getGameBlockData().forceRollback();
            ps.addAll(g.getGamePlayerData().getPlayers());
            ps.addAll(g.getGamePlayerData().getSpectators());
        }
        for (UUID u : ps) {
            Player p = Bukkit.getPlayer(u);
            if (p != null) {
                p.closeInventory();
                if (playerManager.hasPlayerData(u)) {
                    playerManager.getPlayerData(u).getGame().getGamePlayerData().leave(p, false);
                    playerManager.removePlayerData(u);
                }
                if (playerManager.hasSpectatorData(u)) {
                    playerManager.getSpectatorData(u).getGame().getGamePlayerData().leaveSpectate(p);
                    playerManager.removePlayerData(u);
                }
            }
        }
        games.clear();
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
     * Get the instance of the manager
     *
     * @return The manager
     */
    public Manager getManager() {
        return this.manager;
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
     * Get a list of all loaded games
     *
     * @return A list of games
     */
    public List<Game> getGames() {
        return this.games;
    }

    /**
     * Get player sessions map
     *
     * @return Player Sessions map
     */
    public Map<UUID, PlayerSession> getPlayerSessions() {
        return this.playerSession;
    }

    /**
     * Get a map of commands
     *
     * @return Map of commands
     */
    public Map<String, BaseCmd> getCommands() {
        return this.cmds;
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

}

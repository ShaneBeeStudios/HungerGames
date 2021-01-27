package tk.shanebee.hg.commands;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.listeners.CommandListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for handling commands
 */
public class CommandHandler {

    private final HG plugin;
    private final Map<String, BaseCmd> COMMANDS;

    public CommandHandler(HG plugin) {
        this.plugin = plugin;
        this.COMMANDS = new HashMap<>();
        loadCommands();
        registerPermissions();
        PluginCommand command = plugin.getCommand("hg");
        if (command != null) {
            command.setExecutor(new CommandListener(plugin, this));
        }
    }

    private void loadCommands() {
        registerCommand(AddSpawnCmd.class);
        registerCommand(BorderCenterCmd.class);
        registerCommand(BorderSizeCmd.class);
        registerCommand(BorderTimerCmd.class);
        registerCommand(ChestRefillCmd.class);
        registerCommand(ChestRefillNowCmd.class);
        registerCommand(CreateCmd.class);
        registerCommand(DebugCmd.class);
        registerCommand(DeleteCmd.class);
        registerCommand(JoinCmd.class);
        registerCommand(KitCmd.class);
        registerCommand(LeaveCmd.class);
        registerCommand(ListCmd.class);
        registerCommand(ListGamesCmd.class);
        registerCommand(ReloadCmd.class);
        registerCommand(SetExitCmd.class);
        registerCommand(SetLobbyWallCmd.class);
        registerCommand(StartCmd.class);
        registerCommand(StopCmd.class);
        registerCommand(ToggleCmd.class);
        registerCommand(WandCmd.class);
        if (Config.spectateEnabled) {
            registerCommand(SpectateCmd.class);
        }
        if (plugin.getNbtApi() != null) {
            registerCommand(NBTCmd.class);
        }
    }

    private void registerCommand(Class<? extends BaseCmd> cmdClass) {
        try {
            BaseCmd command = cmdClass.newInstance();
            COMMANDS.put(command.cmdName, command);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerPermissions() {
        PluginManager pm = Bukkit.getPluginManager();
        COMMANDS.forEach((command, baseCmd) -> {
            String perm = "hg." + command;
            if (pm.getPermission(perm) == null) {
                Permission permission = new Permission(perm);
                permission.setDescription(String.format("Grants user access to HungerGames command '/hg %s'", command));
                permission.setDefault(baseCmd.permissionDefault);
                pm.addPermission(permission);
            }
        });
    }

    /**
     * Check if a command exists
     *
     * @param command Command to check
     * @return True if command exists
     */
    public boolean commandExists(String command) {
        return COMMANDS.containsKey(command);
    }

    /**
     * Get a list of all available commands
     *
     * @return List of all available commands
     */
    @NotNull
    public List<BaseCmd> getCommands() {
        return new ArrayList<>(COMMANDS.values());
    }

    /**
     * Get a command
     * <p>If command is unavailable, an IllegalArgumentException will be thrown.
     * First check if the command is available using {@link #commandExists(String)}</p>
     *
     * @param command Command to get
     * @return Command if available
     */
    @NotNull
    public BaseCmd getCommand(String command) {
        Preconditions.checkArgument(COMMANDS.containsKey(command), "HungerGames command does not exist: '/hg %s'", command);
        return COMMANDS.get(command);
    }

}

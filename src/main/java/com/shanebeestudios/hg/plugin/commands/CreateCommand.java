package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.data.PlayerSession;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.managers.SessionManager;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class CreateCommand extends SubCommand {

    private final SessionManager sessionManager;

    public CreateCommand(HungerGames plugin) {
        super(plugin);
        this.sessionManager = plugin.getSessionManager();
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("create")
            .withPermission(Permissions.COMMAND_CREATE.permission())
            .then(new StringArgument("name")
                .then(new IntegerArgument("min-players")
                    .then(new IntegerArgument("max-players")
                        .then(new IntegerArgument("time")
                            .then(new IntegerArgument("cost")
                                .setOptional(true)
                                .executesPlayer(info -> {
                                    CommandArguments args = info.args();
                                    Player player = info.sender();
                                    UUID uuid = player.getUniqueId();
                                    String name = args.getByClass("name", String.class);
                                    Integer minPlayers = args.getByClass("min-players", Integer.class);
                                    Integer maxPlayers = args.getByClass("max-players", Integer.class);
                                    Integer time = args.getByClass("time", Integer.class);
                                    Integer cost = args.getByClassOrDefault("cost", Integer.class, 0);

                                    if (name == null || minPlayers == null || maxPlayers == null || time == null) {
                                        // TODO error
                                        return;
                                    }

                                    Game game = this.plugin.getGameManager().getGame(name);
                                    if (game != null) {
                                        // TODO message, game already exists
                                        return;
                                    }

                                    if (this.sessionManager.hasPlayerSession(uuid)) {
                                        // TODO message
                                    } else {
                                        PlayerSession playerSession = this.sessionManager.createPlayerSession(uuid, name, time, minPlayers, maxPlayers, cost);
                                        playerSession.start(player);
                                    }
                                }))))));
    }

}

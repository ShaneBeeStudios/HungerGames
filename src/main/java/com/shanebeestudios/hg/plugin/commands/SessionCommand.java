package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.managers.SessionManager;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.entity.Player;

public class SessionCommand extends SubCommand {

    private final SessionManager sessionManager;

    public SessionCommand(HungerGames plugin) {
        super(plugin);
        this.sessionManager = plugin.getSessionManager();
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("session")
            .withPermission(Permissions.COMMAND_SESSION.permission())
            .then(LiteralArgument.literal("end_session")
                .executesPlayer(info -> {
                    Player player = info.sender();
                    if (this.sessionManager.hasPlayerSession(player)) {
                        this.sessionManager.endPlayerSession(player);
                        Util.sendPrefixedMessage(player, this.lang.command_session_ended);
                    } else {
                        Util.sendPrefixedMessage(player, this.lang.command_session_no_session);
                    }
                }));
    }

}

package com.shanebeestudios.hg.api.command;

import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.plugin.managers.GameManager;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.ExecutionInfo;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public abstract class CustomArg {

    private static GameManager GAME_MANAGER;

    /**
     * Initialize GameManager constant
     * <p>Internally used for reloads</p>
     *
     * @param gameManager GameManager instance
     */
    @ApiStatus.Internal
    public static void init(GameManager gameManager) {
        GAME_MANAGER = gameManager;
    }

    /**
     * @hidden
     */
    public static Game getGame(ExecutionInfo<?, ?> info) throws WrapperCommandSyntaxException {
        Game game = info.args().getByClass("game", Game.class);
        if (game == null) {
            String raw = info.args().getRaw("game");
            throw CommandAPI.failWithString("invalid game '" + raw + "'");
        }
        return game;
    }

    /**
     * Custom command argument for {@link Game Games}
     */
    public static final CustomArg GAME = new CustomArg() {
        @Override
        public Argument<?> get(String name) {
            return new CustomArgument<>(new StringArgument(name), info ->
                GAME_MANAGER.getGame(info.input().toLowerCase(Locale.ROOT)))
                .includeSuggestions(ArgumentSuggestions.stringCollectionAsync(info ->
                    CompletableFuture.supplyAsync(GAME_MANAGER::getGameNames)));
        }
    };

    public abstract Argument<?> get(String name);
}

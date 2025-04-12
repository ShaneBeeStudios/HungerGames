package com.shanebeestudios.hg.api.command;

import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Language;
import com.shanebeestudios.hg.plugin.managers.GameManager;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public abstract class CustomArg {

    private static Language LANG;
    private static GameManager GAME_MANAGER;

    /**
     * Initialize GameManager constant
     * <p>Internally used for reloads</p>
     *
     * @param gameManager GameManager instance
     */
    @ApiStatus.Internal
    public static void init(HungerGames plugin, GameManager gameManager) {
        LANG = plugin.getLang();
        GAME_MANAGER = gameManager;
    }

    /**
     * Custom command argument for {@link Game Games}
     */
    public static final CustomArg GAME = new CustomArg() {
        @Override
        public Argument<?> get(String name) {
            return new CustomArgument<>(new StringArgument(name), info -> {
                String gameName = info.input().toLowerCase(Locale.ROOT);
                Game game = GAME_MANAGER.getGame(gameName);
                if (game == null) {
                    String msg = LANG.command_base_invalid_game.replace("<arena>", gameName);
                    Util.throwCustomArgException(msg);
                }
                return game;
            }).includeSuggestions(ArgumentSuggestions.stringCollectionAsync(info ->
                CompletableFuture.supplyAsync(GAME_MANAGER::getGameNames)));
        }
    };

    public abstract Argument<?> get(String name);
}

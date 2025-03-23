package com.shanebeestudios.hg.api.command;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.managers.GameManager;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public abstract class CustomArg {

    private static final GameManager GAME_MANAGER = HungerGames.getPlugin().getGameManager();

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

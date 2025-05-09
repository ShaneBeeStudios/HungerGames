package com.shanebeestudios.hg.api.command;

import com.shanebeestudios.hg.api.data.PlayerData;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.game.GameTeam;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Language;
import com.shanebeestudios.hg.plugin.managers.GameManager;
import com.shanebeestudios.hg.plugin.managers.PlayerManager;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public abstract class CustomArg {

    private static Language LANG;
    private static GameManager GAME_MANAGER;
    private static PlayerManager PLAYER_MANAGER;

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
        PLAYER_MANAGER = plugin.getPlayerManager();
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

    public static final CustomArg GAME_PLAYER_FOR_TEAM = new CustomArg() {
        @Override
        public Argument<?> get(String name) {
            return new CustomArgument<>(new EntitySelectorArgument.OnePlayer(name), info -> {
                Player player = info.currentInput();
                if (PLAYER_MANAGER.getGame(player) == null) {
                    String msg = LANG.command_team_player_not_available.replace("<player>", player.getName());
                    Util.throwCustomArgException(msg);
                }
                return player;
            }).replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> {
                if (!(info.sender() instanceof Player player)) return null;

                // No player data = no game
                PlayerData playerData = PLAYER_MANAGER.getPlayerData(player);
                if (playerData == null) return null;

                // Not in game = no team
                Game game = playerData.getGame();
                if (game == null) return null;

                // No team = no invite
                GameTeam gameTeam = game.getGameScoreboard().getGameTeam(player);
                if (gameTeam == null) return null;

                // Only leader can invite players
                if (gameTeam.getLeader() != player) return null;

                List<String> names = game.getGamePlayerData().getPlayers().stream()
                    .filter(p -> !gameTeam.isOnTeam(p) && !gameTeam.isPending(p))
                    .map(Player::getName)
                    .toList();
                return CompletableFuture.supplyAsync(() -> names);
            }));
        }
    };

    public static final CustomArg GAME_PLAYER_ON_TEAM = new CustomArg() {
        @Override
        public Argument<?> get(String name) {
            return new CustomArgument<>(new EntitySelectorArgument.OnePlayer(name), info -> {
                Player player = info.currentInput();
                if (PLAYER_MANAGER.getGame(player) == null) {
                    String msg = LANG.command_team_player_not_available.replace("<player>", player.getName());
                    Util.throwCustomArgException(msg);
                }
                return player;
            }).replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> {
                if (!(info.sender() instanceof Player player)) return null;

                // No player data = no game
                PlayerData playerData = PLAYER_MANAGER.getPlayerData(player);
                if (playerData == null) return null;

                // Not in game = no team
                Game game = playerData.getGame();
                if (game == null) return null;

                // No team = no team members
                GameTeam gameTeam = game.getGameScoreboard().getGameTeam(player);
                if (gameTeam == null) return null;

                List<String> names = gameTeam.getPlayers().stream()
                    .filter(teamMember -> teamMember != player)
                    .map(Player::getName)
                    .toList();
                return CompletableFuture.supplyAsync(() -> names);
            }));
        }
    };

    public abstract Argument<?> get(String name);
}

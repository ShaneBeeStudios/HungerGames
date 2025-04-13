package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.game.GameArenaData;
import com.shanebeestudios.hg.api.game.GameBorderData;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.Location2DArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.wrappers.Location2D;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditCommand extends SubCommand {

    public EditCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("edit")
            .withPermission(Permissions.COMMAND_EDIT.permission())
            .then(CustomArg.GAME.get("game")
                .then(chestRefillRepeat())
                .then(chestRefillTime())
                .then(border())
                .then(info())
                .then(locations())
            );
    }

    @SuppressWarnings("DataFlowIssue")
    private Argument<?> border() {
        return LiteralArgument.literal("border")
            .then(LiteralArgument.literal("final_size")
                .then(new IntegerArgument("final_size", 5)
                    .executes(info -> {
                        Game game = info.args().getByClass("game", Game.class);
                        int finalSize = info.args().getByClass("final_size", Integer.class);
                        GameBorderData gameBorderData = game.getGameBorderData();
                        gameBorderData.setFinalBorderSize(finalSize);
                        Util.sendPrefixedMessage(info.sender(), "Border final size set to %s", finalSize);
                        saveGame(game);
                    })))
            .then(LiteralArgument.literal("countdown_start")
                .then(new IntegerArgument("countdown_start", 5)
                    .executes(info -> {
                        Game game = info.args().getByClass("game", Game.class);
                        int countdownStart = info.args().getByClass("countdown_start", Integer.class);
                        GameBorderData gameBorderData = game.getGameBorderData();
                        gameBorderData.setBorderCountdownStart(countdownStart);
                        Util.sendPrefixedMessage(info.sender(), "Border countdown start set to %s", countdownStart);
                        saveGame(game);
                    })))
            .then(LiteralArgument.literal("countdown_end")
                .then(new IntegerArgument("countdown_end", 5)
                    .executes(info -> {
                        Game game = info.args().getByClass("game", Game.class);
                        int countdownEnd = info.args().getByClass("countdown_end", Integer.class);
                        GameBorderData gameBorderData = game.getGameBorderData();
                        gameBorderData.setBorderCountdownEnd(countdownEnd);
                        Util.sendPrefixedMessage(info.sender(), "Border countdown end set to %s", countdownEnd);

                        saveGame(game);
                    })))
            .then(LiteralArgument.literal("center_location")
                .then(new Location2DArgument("center_location", LocationType.BLOCK_POSITION)
                    .executes(info -> {
                        Game game = info.args().getByClass("game", Game.class);
                        Location2D centerLocation = info.args().getByClass("center_location", Location2D.class);
                        GameBorderData gameBorderData = game.getGameBorderData();
                        gameBorderData.setCenterLocation(convert(centerLocation));
                        Util.sendPrefixedMessage(info.sender(), "Border center location set to %s", centerLocation);
                        saveGame(game);
                    })));
    }

    @SuppressWarnings("DataFlowIssue")
    private Argument<?> chestRefillTime() {
        return LiteralArgument.literal("chest-refill-time")
            .then(new IntegerArgument("time", 30)
                .executes(info -> {
                    Game game = info.args().getByClass("game", Game.class);
                    CommandSender sender = info.sender();
                    String name = game.getGameArenaData().getName();
                    int time = info.args().getByClass("time", Integer.class);
                    if (time % 30 != 0) {
                        Util.sendPrefixedMessage(sender, "<yellow><time> <red>must be in increments of 30");
                        return;
                    }
                    game.getGameArenaData().setChestRefillTime(time);
                    saveGame(game);
                    Util.sendPrefixedMessage(sender, this.lang.command_edit_chest_refill_time_set
                        .replace("<arena>", name)
                        .replace("<sec>", String.valueOf(time)));

                }));
    }

    @SuppressWarnings("DataFlowIssue")
    private Argument<?> chestRefillRepeat() {
        return LiteralArgument.literal("chest-refill-repeat")
            .then(new IntegerArgument("time", 30)
                .executes(info -> {
                    Game game = info.args().getByClass("game", Game.class);
                    CommandSender sender = info.sender();
                    String name = game.getGameArenaData().getName();
                    int time = info.args().getByClass("time", Integer.class);
                    if (time % 30 != 0) {
                        Util.sendPrefixedMessage(sender, "<yellow><time> <red>must be in increments of 30");
                        return;
                    }
                    game.getGameArenaData().setChestRefillRepeat(time);
                    saveGame(game);
                    Util.sendPrefixedMessage(sender, this.lang.command_edit_chest_refill_repeat_set
                        .replace("<arena>", name)
                        .replace("<sec>", String.valueOf(time)));
                }));
    }

    @SuppressWarnings("DataFlowIssue")
    private Argument<?> info() {
        return LiteralArgument.literal("info")
            .then(LiteralArgument.literal("free_roam_time")
                .then(new IntegerArgument("seconds", -1)
                    .executes(info -> {
                        Game game = info.args().getByClass("game", Game.class);
                        int freeRoamTime = info.args().getByClass("seconds", Integer.class);
                        game.getGameArenaData().setFreeRoamTime(freeRoamTime);
                        Util.sendPrefixedMessage(info.sender(), "Free roam time set to %s", freeRoamTime);
                        saveGame(game);
                    })))
            .then(LiteralArgument.literal("cost")
                .then(new IntegerArgument("dollars", 0)
                    .executes(info -> {
                        Game game = info.args().getByClass("game", Game.class);
                        int cost = info.args().getByClass("dollars", Integer.class);
                        game.getGameArenaData().setCost(cost);
                        Util.sendPrefixedMessage(info.sender(), "Cost set to %s", cost);
                        saveGame(game);
                    })))
            .then(LiteralArgument.literal("timer")
                .then(new IntegerArgument("seconds", 30)
                    .executes(info -> {
                        Game game = info.args().getByClass("game", Game.class);
                        int timerSeconds = info.args().getByClass("seconds", Integer.class);
                        game.getGameArenaData().setTimer(timerSeconds);
                        Util.sendPrefixedMessage(info.sender(), "Timer set to %s", timerSeconds);
                        saveGame(game);
                    })))
            .then(LiteralArgument.literal("min_players")
                .then(new IntegerArgument("min", 2)
                    .executes(info -> {
                        Game game = info.args().getByClass("game", Game.class);
                        int minPlayers = info.args().getByClass("min", Integer.class);
                        if (minPlayers > game.getGameArenaData().getMaxPlayers()) {
                            throw CommandAPI.failWithString("Min players cannot be greater than max players");
                        }
                        game.getGameArenaData().setMinPlayers(minPlayers);
                        Util.sendPrefixedMessage(info.sender(), "Min players set to %s", minPlayers);
                        saveGame(game);
                    })))
            .then(LiteralArgument.literal("max_players")
                .then(new IntegerArgument("max", 2)
                    .executes(info -> {
                        Game game = info.args().getByClass("game", Game.class);
                        int maxPlayers = info.args().getByClass("max", Integer.class);
                        if (maxPlayers < game.getGameArenaData().getMinPlayers()) {
                            throw CommandAPI.failWithString("Max players cannot be less than min players");
                        }
                        game.getGameArenaData().setMaxPlayers(maxPlayers);
                        Util.sendPrefixedMessage(info.sender(), "Max players set to %s", maxPlayers);
                        saveGame(game);
                    })));
    }

    private Argument<?> locations() {
        return LiteralArgument.literal("locations")
            .then(LiteralArgument.literal("lobby_wall")
                .executesPlayer(info -> {
                    Game game = info.args().getByClass("game", Game.class);
                    Player player = info.sender();
                    Block targetBlock = player.getTargetBlockExact(10);
                    if (targetBlock != null && Tag.WALL_SIGNS.isTagged(targetBlock.getType()) && game.getGameBlockData().setLobbyBlock(targetBlock.getLocation())) {
                        Util.sendPrefixedMessage(player, this.lang.command_edit_lobbywall_set);
                        saveGame(game);
                    } else {
                        Util.sendPrefixedMessage(player, this.lang.command_edit_lobbywall_incorrect);
                        Util.sendMessage(player, this.lang.command_edit_lobbywall_format);
                    }
                }))
            .then(LiteralArgument.literal("clear_spawns")
                .executes(info -> {
                    Game game = info.args().getByClass("game", Game.class);
                    game.getGameArenaData().clearSpawns();
                    Util.sendPrefixedMessage(info.sender(), "Spawns have been cleared. <yellow>Arena <white>'<aqua>%s<white>'<yellow> has a max of <red>%s<yellow> players, so make sure to add spawns for them.",
                        game.getGameArenaData().getName(), game.getGameArenaData().getMaxPlayers());
                    saveGame(game);
                }))
            .then(LiteralArgument.literal("add_spawn")
                .then(new LocationArgument("location", LocationType.BLOCK_POSITION)
                    .executesPlayer(info -> {
                        Game game = info.args().getByClass("game", Game.class);
                        GameArenaData gameArenaData = game.getGameArenaData();
                        Location location = info.args().getByClass("location", Location.class);
                        assert location != null;
                        location.add(0.5, 0, 0.5);
                        location.setPitch(0);
                        location.setYaw(info.sender().getLocation().getYaw());
                        gameArenaData.addSpawn(location);

                        int spawnCount = gameArenaData.getSpawns().size();
                        int maxPlayers = gameArenaData.getMaxPlayers();
                        if (spawnCount < maxPlayers) {
                            Util.sendPrefixedMessage(info.sender(),
                                "<yellow>You currently have <aqua>%s <yellow>spawns but a max of <red>%s <yellow>players, you should add <green>%s<yellow> more spawns",
                                spawnCount, maxPlayers, maxPlayers - spawnCount);
                        } else {
                            Util.sendPrefixedMessage(info.sender(), "<green>Spawns all set!");
                        }
                        saveGame(game);
                    })));
    }

    private Location convert(Location2D location2D) {
        return new Location(location2D.getWorld(), location2D.getBlockX(), 0, location2D.getBlockZ());
    }

}

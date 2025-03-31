package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameBorderData;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.Location2DArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.wrappers.Location2D;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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
                .then(lobbyWall())
            );
    }

    @SuppressWarnings("DataFlowIssue")
    private Argument<?> border() {
        return LiteralArgument.literal("border")
            .then(LiteralArgument.literal("final_size")
                .then(new IntegerArgument("final_size", 5)
                    .executes(info -> {
                        Game game = CustomArg.getGame(info);
                        int finalSize = info.args().getByClass("final_size", Integer.class);
                        GameBorderData gameBorderData = game.getGameBorderData();
                        gameBorderData.setFinalBorderSize(finalSize);
                        saveGame(game);
                    })))
            .then(LiteralArgument.literal("countdown_start")
                .then(new IntegerArgument("countdown_start", 5)
                    .executes(info -> {
                        Game game = CustomArg.getGame(info);
                        int countdownStart = info.args().getByClass("countdown_start", Integer.class);
                        GameBorderData gameBorderData = game.getGameBorderData();
                        gameBorderData.setBorderCountdownStart(countdownStart);
                        saveGame(game);
                    })))
            .then(LiteralArgument.literal("countdown_end")
                .then(new IntegerArgument("countdown_end", 5)
                    .executes(info -> {
                        Game game = CustomArg.getGame(info);
                        int countdownEnd = info.args().getByClass("countdown_end", Integer.class);
                        GameBorderData gameBorderData = game.getGameBorderData();
                        gameBorderData.setBorderCountdownEnd(countdownEnd);
                        saveGame(game);
                    })))
            .then(LiteralArgument.literal("center_location")
                .then(new Location2DArgument("center_location", LocationType.BLOCK_POSITION)
                    .executes(info -> {
                        Game game = CustomArg.getGame(info);
                        Location2D centerLocation = info.args().getByClass("center_location", Location2D.class);
                        GameBorderData gameBorderData = game.getGameBorderData();
                        gameBorderData.setCenterLocation(convert(centerLocation));
                        saveGame(game);
                    })));
    }

    @SuppressWarnings("DataFlowIssue")
    private Argument<?> chestRefillTime() {
        return LiteralArgument.literal("chest-refill-time")
            .then(new IntegerArgument("time", 30)
                .executes(info -> {
                    Game game = CustomArg.getGame(info);
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
                    Game game = CustomArg.getGame(info);
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

    private Argument<?> lobbyWall() {
        return LiteralArgument.literal("lobby_wall")
            .executesPlayer(info -> {
                Game game = CustomArg.getGame(info);
                Player player = info.sender();
                Block targetBlock = player.getTargetBlockExact(10);
                if (targetBlock != null && Tag.WALL_SIGNS.isTagged(targetBlock.getType()) && game.getGameBlockData().setLobbyBlock((Sign) targetBlock.getState())) {
                    Util.sendPrefixedMessage(player, this.lang.command_edit_lobbywall_set);
                    saveGame(game);
                } else {
                    Util.sendPrefixedMessage(player, this.lang.command_edit_lobbywall_incorrect);
                    Util.sendMessage(player, this.lang.command_edit_lobbywall_format);
                }
            });
    }

    private Location convert(Location2D location2D) {
        return new Location(location2D.getWorld(), location2D.getBlockX(), 0, location2D.getBlockZ());
    }

}

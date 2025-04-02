package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.NBTApi;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NBTCommand extends SubCommand {

    public NBTCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("nbt")
            .withPermission(Permissions.COMMAND_NBT.permission())
            .executesPlayer(info -> {
                Player player = info.sender();
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if (itemStack.getType() == Material.AIR) {
                    Util.sendPrefixedMessage(player, "<red>You cannot get NBT from an empty hand.");
                    return;
                }

                NBT.getComponents(itemStack, readableNBT -> {
                    Util.sendPrefixedMessage(player, "NBT of held item sent to console!");
                    Util.log("NBT: %s", readableNBT.toString());
                    String pretty = NBTApi.getPrettyNBT((NBTCompound) readableNBT, "   ");
                    if (pretty != null) {
                        Util.log("Pretty NBT:");
                        Bukkit.getConsoleSender().sendMessage(System.lineSeparator() + pretty);
                    }
                });
            });
    }


}

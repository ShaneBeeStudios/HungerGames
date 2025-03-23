package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.HungerGames;
import dev.jorel.commandapi.CommandTree;

public class MainCommand {

    CommandTree command;

    public MainCommand(HungerGames plugin) {
        this.command = new CommandTree("hungergames");
        this.command.withAliases("hg");

        // Register sub-commands
        this.command.then(new CreateCommand(plugin).register());
        this.command.then(new JoinCommand().register());
        this.command.then(new KitCommand().register());
        this.command.then(new StopCommand().register());
        this.command.then(new StopAllCommand().register());

        this.command.register();
    }

}

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
        this.command.then(new ForceStartCommand(plugin).register());
        this.command.then(new JoinCommand(plugin).register());
        this.command.then(new KitCommand().register());
        this.command.then(new ListGamesCommand(plugin).register());
        this.command.then(new PermissionsCommand().register());
        this.command.then(new SetExitCommand(plugin).register());
        this.command.then(new StatusCommand(plugin).register());
        this.command.then(new StopCommand().register());
        this.command.then(new StopAllCommand().register());
        this.command.then(new TeamCommand(plugin).register());
        this.command.then(new ToggleCommand(plugin).register());

        this.command.register();
    }

}

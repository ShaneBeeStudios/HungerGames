package com.shanebeestudios.hg.plugin.commands;

import dev.jorel.commandapi.arguments.Argument;

public abstract class SubCommand {

    protected abstract Argument<?> register();

}

package com.shanebeestudios.hg.plugin.permission;

import com.shanebeestudios.hg.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;

import java.util.LinkedHashMap;
import java.util.Map;

public class Permissions {

    public record Permission(String permission, org.bukkit.permissions.Permission bukkitPermission) {
        public boolean has(CommandSender sender) {
            return sender.hasPermission(this.bukkitPermission.getName());
        }
    }

    static final Map<String, org.bukkit.permissions.Permission> PERMISSIONS = new LinkedHashMap<>();

    // Command permissions
    public static final Permission COMMAND_CREATE = getCommand("create", "Create a new game arena", PermissionDefault.OP);
    public static final Permission COMMAND_JOIN = getCommand("join", "Join a game", PermissionDefault.TRUE);
    public static final Permission COMMAND_JOIN_OTHERS = getCommand("join_others", "Join other players to a game", PermissionDefault.OP);
    public static final Permission COMMAND_STOP = getCommand("stop", "Stop the game", PermissionDefault.OP);
    public static final Permission COMMAND_STOP_ALL =  getCommand("stopallgames", "Stop all games", PermissionDefault.OP);

    private static Permission getCommand(String perm, String description, PermissionDefault defaultPermission) {
        return getBase("command", perm, description, defaultPermission);
    }

    private static Permission getBase(String base, String perm, String description, PermissionDefault defaultPermission) {
        String stringPerm = "hungergames." + base + "." + perm;
        org.bukkit.permissions.Permission bukkitPermission = DefaultPermissions.registerPermission(stringPerm, description, defaultPermission);
        PERMISSIONS.put(stringPerm, bukkitPermission);
        return new Permission(stringPerm, bukkitPermission);
    }

    public static void debug() {
        Util.logMini("Permissions:");
        for (Map.Entry<String, org.bukkit.permissions.Permission> entry : PERMISSIONS.entrySet()) {
            String color = switch (entry.getValue().getDefault()) {
                case OP -> "yellow";
                case TRUE, NOT_OP -> "green";
                case FALSE -> "red";
            };
            Util.logMini("  <white>'<#F09616>%s<white>':", entry.getKey());
            Util.logMini("    <grey> Description: <aqua>%s", entry.getValue().getDescription());
            Util.logMini("    <grey> Default: <%s>%s", color, entry.getValue().getDefault().toString());
        }
    }


}

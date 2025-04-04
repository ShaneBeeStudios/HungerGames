package com.shanebeestudios.hg.plugin.permission;

import com.shanebeestudios.hg.api.util.Util;
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
    public static final Permission COMMAND_DEBUG = getCommand("debug", "Debug an arena", PermissionDefault.OP);
    public static final Permission COMMAND_DELETE = getCommand("delete", "Delete a game arena", PermissionDefault.OP);
    public static final Permission COMMAND_EDIT = getCommand("edit", "Edit a game arena", PermissionDefault.OP);
    public static final Permission COMMAND_FORCE_START = getCommand("forcestart", "Force start a game arena", PermissionDefault.OP);
    public static final Permission COMMAND_JOIN = getCommand("join", "Join a game", PermissionDefault.TRUE);
    public static final Permission COMMAND_JOIN_OTHERS = getCommand("join_others", "Join other players to a game", PermissionDefault.OP);
    public static final Permission COMMAND_KIT = getCommand("kit", "Get a kit in a game", PermissionDefault.TRUE);
    public static final Permission COMMAND_LEAVE = getCommand("leave", "Leave a game", PermissionDefault.TRUE);
    public static final Permission COMMAND_LIST = getCommand("list", "List all players in your game", PermissionDefault.TRUE);
    public static final Permission COMMAND_NBT = getCommand("nbt", "Get the NBT of an item from the configs", PermissionDefault.OP);
    public static final Permission COMMAND_PERMISSIONS = getCommand("permissions", "List of permissions", PermissionDefault.OP);
    public static final Permission COMMAND_REFILL_CHESTS = getCommand("refill_chests", "Refill chests in a game", PermissionDefault.OP);
    public static final Permission COMMAND_RELOAD = getCommand("reload", "Reload configs/arenas", PermissionDefault.OP);
    public static final Permission COMMAND_SET_EXIT = getCommand("setexit", "Set the exit of a game, all games or the global exit", PermissionDefault.OP);
    public static final Permission COMMAND_SETTINGS = getCommand("settings", "Temporarily modify some config settings (not saved to file)", PermissionDefault.OP);
    public static final Permission COMMAND_SPECTATE = getCommand("spectate", "Spectate a game", PermissionDefault.TRUE);
    public static final Permission COMMAND_STATUS = getCommand("status", "Show status of a game", PermissionDefault.TRUE);
    public static final Permission COMMAND_STOP = getCommand("stop", "Stop the game", PermissionDefault.OP);
    public static final Permission COMMAND_STOP_ALL = getCommand("stopallgames", "Stop all games", PermissionDefault.OP);
    public static final Permission COMMAND_TEAM = getCommand("team", "Create/join teams in a game", PermissionDefault.TRUE);
    public static final Permission COMMAND_TEAM_TELEPORT = getCommand("team.teleport", "Teleport to a team member", PermissionDefault.TRUE);
    public static final Permission COMMAND_TOGGLE = getCommand("toggle", "Toggle a game", PermissionDefault.OP);

    // Other Permissions
    public static final Permission KITS = getBase("kits", "Whether a player can use kits", PermissionDefault.TRUE);

    private static Permission getCommand(String perm, String description, PermissionDefault defaultPermission) {
        return getBase("command." + perm, description, defaultPermission);
    }

    public static Permission registerKitPermission(String kitName, String kitPermission) {
        return getBase("kit." + kitPermission, "Permission for kit '" + kitName + "'", PermissionDefault.OP);
    }

    private static Permission getBase(String perm, String description, PermissionDefault defaultPermission) {
        String stringPerm = "hungergames." + perm;
        org.bukkit.permissions.Permission bukkitPermission = DefaultPermissions.registerPermission(stringPerm, description, defaultPermission);
        PERMISSIONS.put(stringPerm, bukkitPermission);
        return new Permission(stringPerm, bukkitPermission);
    }

    public static void debug() {
        Util.log("Permissions:");
        for (Map.Entry<String, org.bukkit.permissions.Permission> entry : PERMISSIONS.entrySet()) {
            String color = switch (entry.getValue().getDefault()) {
                case OP -> "yellow";
                case TRUE, NOT_OP -> "green";
                case FALSE -> "red";
            };
            Util.log("  <white>'<#F09616>%s<white>':", entry.getKey());
            Util.log("    <grey> Description: <aqua>%s", entry.getValue().getDescription());
            Util.log("    <grey> Default: <%s>%s", color, entry.getValue().getDefault().toString());
        }
    }


}

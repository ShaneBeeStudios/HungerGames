package com.shanebeestudios.hg.plugin.update;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.shanebeestudios.hg.api.region.TaskUtils;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class to check for plugin updates
 */
@SuppressWarnings("deprecation")
public class UpdateChecker implements Listener {

    private final HungerGames plugin;
    private final String pluginVersion;
    private final String serverVersion = Bukkit.getMinecraftVersion();
    private ModrinthVersion currentUpdateVersion;

    public UpdateChecker(HungerGames plugin) {
        this.plugin = plugin;
        this.pluginVersion = plugin.getPluginMeta().getVersion();

        if (Config.SETTINGS_UPDATE_CHECKER_ENABLED) {
            setupJoinListener();
            checkUpdate(Config.SETTINGS_UPDATE_CHECKER_ASYNC);
        } else {
            Util.log("<red>Update checker disabled!");
        }
    }

    private void setupJoinListener() {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            private void onJoin(PlayerJoinEvent event) {
                Player player = event.getPlayer();
                if (!Permissions.UPDATE_CHECKER.has(player)) return;

                TaskUtils.getEntityScheduler(player).runTaskLater(() -> getUpdateVersion(true).thenApply(version -> {
                    Util.sendPrefixedMessage(player, "Update available: <green>" + version.getUpdateVersion());
                    Util.sendMessage(player, "Download at: <aqua>" + version.getUpdateLink());
                    return true;
                }), 30);
            }
        }, this.plugin);
    }

    private void checkUpdate(boolean async) {
        Util.log("Checking for update...");
        getUpdateVersion(async).thenApply(modrinthVersion -> {
            Util.log("<red>Plugin is not up to date!");
            Util.log(" - Current version: <red>v%s", this.pluginVersion);
            Util.log(" - Available update: <green>v%s", modrinthVersion.getUpdateVersion());
            if (modrinthVersion.isServerSupported(this.serverVersion)) {
                Util.log(" - Download at: <aqua>" + modrinthVersion.getUpdateLink());
            } else {
                Util.log(" - <red>Your server version <grey>(<yellow>%s<grey>) <red>does not support this update.", this.serverVersion);
                Util.log(" - Supported Versions:");
                for (String supportedVersion : modrinthVersion.getSupportedVersions()) {
                    Util.log("   - %s", supportedVersion);
                }
            }
            return true;
        }).exceptionally(throwable -> {
            Util.log("<green>Plugin is up to date!");
            return true;
        });
    }

    private CompletableFuture<ModrinthVersion> getUpdateVersion(boolean async) {
        CompletableFuture<ModrinthVersion> updateVersionFuture = new CompletableFuture<>();
        if (this.currentUpdateVersion != null) {
            updateVersionFuture.complete(this.currentUpdateVersion);
        } else {
            CompletableFuture<ModrinthVersion> latestReleaseFuture = new CompletableFuture<>();
            if (async) {
                TaskUtils.getGlobalScheduler().runTaskAsync(() -> {
                    ModrinthVersion lastest = getLatestVersionFromModrinth();
                    if (lastest == null) latestReleaseFuture.cancel(true);
                    latestReleaseFuture.complete(lastest);
                });
            } else {
                ModrinthVersion latest = getLatestVersionFromModrinth();
                if (latest == null) latestReleaseFuture.cancel(true);
                latestReleaseFuture.complete(latest);
            }
            latestReleaseFuture.thenApply(version -> {
                if (version.getUpdateVersion().compareTo(this.pluginVersion) <= 0) {
                    updateVersionFuture.cancel(true);
                } else {
                    this.currentUpdateVersion = version;
                    updateVersionFuture.complete(this.currentUpdateVersion);
                }
                return true;
            });
        }
        return updateVersionFuture;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private ModrinthVersion getLatestVersionFromModrinth() {
        try {
            URL url = new URL("https://api.modrinth.com/v2/project/UPP8KYnj/version");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            JsonArray elements = new Gson().fromJson(reader, JsonArray.class);
            JsonElement latestVersion = elements.get(0);
            return new ModrinthVersion(latestVersion);
        } catch (IOException e) {
            if (Config.SETTINGS_DEBUG) {
                e.printStackTrace();
            } else {
                Util.log("<red>Checking for update failed!");
            }
        }
        return null;
    }

}

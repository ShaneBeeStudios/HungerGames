package com.shanebeestudios.hg.plugin.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ModrinthVersion {

    private final String updateVersion;
    private final List<String> supportedVersions = new ArrayList<>();

    public ModrinthVersion(JsonElement jsonElement) {
        JsonObject json = jsonElement.getAsJsonObject();
        this.updateVersion = json.get("version_number").getAsString();
        JsonArray gameVersions = json.getAsJsonArray("game_versions");
        gameVersions.forEach(version -> this.supportedVersions.add(version.getAsString()));
    }

    public String getUpdateVersion() {
        return this.updateVersion;
    }

    public String getUpdateLink() {
        return "https://modrinth.com/plugin/hungergames-sb/version/" + this.updateVersion;
    }

    public List<String> getSupportedVersions() {
        return this.supportedVersions;
    }

    public boolean isServerSupported(String serverVersion) {
        return this.supportedVersions.contains(serverVersion);
    }

}

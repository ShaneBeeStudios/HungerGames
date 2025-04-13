package com.shanebeestudios.hg.api.game;

import com.shanebeestudios.hg.api.util.Constants;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.configs.Config;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Data holder for a {@link Game Game's} lobby signs
 */
public class GameLobbyWall extends Data {

    private final GameArenaData gameArenaData;
    private Location signLoc1;
    private Location signLoc2;
    private Location signLoc3;

    protected GameLobbyWall(Game game) {
        super(game);
        this.gameArenaData = game.getGameArenaData();
    }

    /**
     * Get location of far left sign of lobby wall
     *
     * @return Location of sign
     */
    public Location getSignLocation() {
        return this.signLoc1;
    }

    /**
     * Set the lobby block for this game
     *
     * @param location The location of the sign to which the lobby will be set at
     * @return True if lobby is set
     */
    @SuppressWarnings("CallToPrintStackTrace")
    protected boolean setLobbyBlock(Location location) {
        GameArenaData gameArenaData = this.game.getGameArenaData();
        try {
            this.signLoc1 = location;
            Sign sign1 = (Sign) location.getBlock().getState();
            PersistentDataContainer pdc = sign1.getPersistentDataContainer();
            pdc.set(Constants.LOBBY_SIGN_KEY, PersistentDataType.STRING, gameArenaData.getName());
            BlockFace face;
            BlockData sign1Data = sign1.getBlockData();
            if (sign1Data instanceof Directional directional) {
                // Wall Sign
                face = Util.getSignFace(directional.getFacing());
            } else if (sign1Data instanceof Rotatable rotatable) {
                // Floor/Hanging Sign
                face = Util.getSignFace(rotatable.getRotation());
            } else {
                return false;
            }
            Sign sign2 = (Sign) sign1.getBlock().getRelative(face).getState();
            Sign sign3 = (Sign) sign2.getBlock().getRelative(face).getState();
            this.signLoc2 = sign2.getLocation();
            this.signLoc3 = sign3.getLocation();

            updateSignLines(List.of(sign1, sign2, sign3));

            sign1.setWaxed(true);
            sign2.setWaxed(true);
            sign3.setWaxed(true);
            sign1.update(true);
            sign2.update(true);
            sign3.update(true);
        } catch (Exception e) {
            Util.warning("Failed to setup lobby wall for arena '%s', msg: %s", gameArenaData.getName(), e.getMessage());
            if (Config.SETTINGS_DEBUG) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    void updateLobbyBlock() {
        if (this.signLoc1 == null || this.signLoc2 == null || this.signLoc3 == null) {
            Util.warning("The lobby wall seems to be missing for '%s'", this.game.getGameArenaData().getName());
            return;
        }
        if (this.signLoc1.getBlock().getState() instanceof Sign sign1 && this.signLoc2.getBlock().getState() instanceof Sign sign2 && this.signLoc3.getBlock().getState() instanceof Sign sign3) {
            updateSignLines(List.of(sign1, sign2, sign3));
        }
    }

    void updateSignLines(List<Sign> signs) {
        for (int signCount = 0; signCount < 3; signCount++) {
            List<String> lines = this.lang.lobby_signs_lines.get(signCount);
            Sign sign = signs.get(signCount);
            SignSide side = sign.getSide(Side.FRONT);
            for (int lineCount = 0; lineCount < 4; lineCount++) {
                String line = replacements(lines.get(lineCount));
                if (line == null || line.isEmpty()) continue;
                side.line(lineCount, Util.getMini(line));
            }
            sign.update(true);
        }
    }

    private @Nullable String replacements(String line) {
        if (line.contains("<cost>") && this.gameArenaData.getCost() <= 0) return null;
        return line
            .replace("<arena>", this.gameArenaData.getName())
            .replace("<status>", this.gameArenaData.getStatus().getStringName())
            .replace("<cost>", "" + this.gameArenaData.getCost())
            .replace("<alive>", "" + this.game.getGamePlayerData().getPlayers().size())
            .replace("<max_players>", "" + this.gameArenaData.getMaxPlayers());
    }

    boolean isLobbyValid() {
        return this.signLoc1.getBlock().getState() instanceof Sign
            && this.signLoc2 != null && this.signLoc2.getBlock().getState() instanceof Sign
            && this.signLoc3 != null && this.signLoc3.getBlock().getState() instanceof Sign;
    }

}

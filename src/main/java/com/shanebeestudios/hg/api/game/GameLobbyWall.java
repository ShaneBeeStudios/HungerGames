package com.shanebeestudios.hg.api.game;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.util.Constants;
import com.shanebeestudios.hg.api.util.Util;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.sign.Side;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Data holder for a {@link Game Game's} lobby signs
 */
public class GameLobbyWall extends Data {

    private Location signLoc1;
    private Location signLoc2;
    private Location signLoc3;

    protected GameLobbyWall(Game game) {
        super(game);
    }

    /** Get location of far left sign of lobby wall
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
    protected boolean setLobbyBlock(Location location) {
        GameArenaData gameArenaData = this.game.getGameArenaData();
        try {
            this.signLoc1 = location;
            Sign sign1 = (Sign) location.getBlock().getState();
            PersistentDataContainer pdc = sign1.getPersistentDataContainer();
            pdc.set(Constants.LOBBY_SIGN_KEY, PersistentDataType.STRING, gameArenaData.getName());
            Block c = sign1.getBlock();
            BlockFace face = Util.getSignFace(((Directional) sign1.getBlockData()).getFacing());
            Sign sign2 = (Sign) c.getRelative(face).getState();
            Sign sign3 = (Sign) sign2.getBlock().getRelative(face).getState();
            this.signLoc2 = sign2.getLocation();
            this.signLoc3 = sign3.getLocation();

            sign1.getSide(Side.FRONT).line(0, Util.getMini(this.lang.lobby_sign_1_1));
            sign1.getSide(Side.FRONT).line(1, Util.getMini("<bold>" + gameArenaData.getName()));
            sign1.getSide(Side.FRONT).line(2, Util.getMini(this.lang.lobby_sign_1_3));
            if (gameArenaData.cost > 0)
                sign1.getSide(Side.FRONT).line(3, Util.getMini(HungerGames.getPlugin().getLang().lobby_sign_cost.replace("<cost>", String.valueOf(gameArenaData.cost))));
            sign2.getSide(Side.FRONT).line(0, Util.getMini(this.lang.lobby_sign_2_1));
            sign2.getSide(Side.FRONT).line(1, gameArenaData.getStatus().getName());
            sign3.getSide(Side.FRONT).line(0, Util.getMini(this.lang.lobby_sign_3_1));
            sign3.getSide(Side.FRONT).line(1, Util.getMini("<bold>0/%s", gameArenaData.getMaxPlayers()));
            sign1.setWaxed(true);
            sign2.setWaxed(true);
            sign3.setWaxed(true);
            sign1.update(true);
            sign2.update(true);
            sign3.update(true);
        } catch (Exception e) {
            Util.warning("Failed to setup lobby wall for arena '%s', msg: %s", gameArenaData.getName(), e.getMessage());
            return false;
        }
        return true;
    }

    void updateLobbyBlock() {
        if (this.signLoc2 == null || this.signLoc3 == null) {
            Util.warning("The lobby wall seems to be missing for '%s'", this.game.getGameArenaData().getName());
            return;
        }
        if (this.signLoc2.getBlock().getState() instanceof Sign sign2 && this.signLoc3.getBlock().getState() instanceof Sign sign3) {
            GameArenaData gameArenaData = this.game.getGameArenaData();
            sign2.getSide(Side.FRONT).line(1, gameArenaData.getStatus().getName());
            sign3.getSide(Side.FRONT).line(1, Util.getMini("<bold>%s/%s",
                this.game.getGamePlayerData().getPlayers().size(),
                gameArenaData.getMaxPlayers()));
            sign2.update(true);
            sign3.update(true);
        }
    }

    boolean isLobbyValid() {
        return this.signLoc1.getBlock().getState() instanceof Sign && this.signLoc2.getBlock().getState() instanceof Sign
            && this.signLoc3.getBlock().getState() instanceof Sign;
    }

}

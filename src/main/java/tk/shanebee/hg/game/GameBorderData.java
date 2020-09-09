package tk.shanebee.hg.game;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import tk.shanebee.hg.data.Config;

import java.util.Arrays;
import java.util.List;

/**
 * Data class for holding a {@link Game Game's} world border
 */
public class GameBorderData extends Data {

    private Location borderCenter = null;
    private int borderSize;
    private int borderCountdownStart;
    private int borderCountdownEnd;

    protected GameBorderData(Game game) {
        super(game);
    }

    private double getBorderSize(Location center) {
        Bound bound = game.gameArenaData.getBound();
        double x1 = Math.abs(bound.getGreaterCorner().getX() - center.getX());
        double x2 = Math.abs(bound.getLesserCorner().getX() - center.getX());
        double z1 = Math.abs(bound.getGreaterCorner().getZ() - center.getZ());
        double z2 = Math.abs(bound.getLesserCorner().getZ() - center.getZ());

        double x = Math.max(x1, x2);
        double z = Math.max(z1, z2);
        double r = Math.max(x, z);

        return (r * 2) + 10;
    }

    /**
     * Set the center of the border of this game
     *
     * @param borderCenter Location of the center
     */
    public void setBorderCenter(Location borderCenter) {
        this.borderCenter = borderCenter;
    }

    /**
     * Set the final size for the border of this game
     *
     * @param borderSize The final size of the border
     */
    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public void setBorderTimer(int start, int end) {
        this.borderCountdownStart = start;
        this.borderCountdownEnd = end;
    }

    public List<Integer> getBorderTimer() {
        return Arrays.asList(borderCountdownStart, borderCountdownEnd);
    }

    public void setBorder(int time) {
        Location center;
        if (Config.centerSpawn && borderCenter == null) {
            center = game.gameArenaData.spawns.get(0);
        } else if (borderCenter != null) {
            center = borderCenter;
        } else {
            center = game.gameArenaData.bound.getCenter();
        }
        World world = center.getWorld();
        assert world != null;
        WorldBorder border = world.getWorldBorder();
        double size = Math.min(border.getSize(), getBorderSize(center));

        border.setCenter(center);
        border.setSize(((int) size));
        border.setWarningTime(5);
        border.setDamageBuffer(2);
        border.setSize(borderSize, time);
    }

    void resetBorder() {
        World world = game.gameArenaData.getBound().getWorld();
        assert world != null;
        world.getWorldBorder().reset();
    }

}

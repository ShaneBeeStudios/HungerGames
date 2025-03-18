package com.shanebeestudios.hg.data;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class PlayerSession {

    private Location loc1;

    private Location loc2;

    public PlayerSession(Location l1, Location l2) {
        setInfo(l1, l2);
    }

    public void setLoc1(Location loc1) {
        this.loc1 = loc1;
    }

    public void setLoc2(Location loc2) {
        this.loc2 = loc2;
    }

    public Location getLoc1() {
        return loc1;
    }

    public Location getLoc2() {
        return loc2;
    }

    public void setInfo(Location l1, Location l2) {
        setLoc1(l1);
        setLoc2(l2);
    }

    public boolean hasValidSelection() {
        if (loc1 == null || loc2 == null) return false;
        return true;
    }

    public boolean isBigEnough() {
        if (loc1 == null || loc2 == null) return false;
        BoundingBox boundingBox = new BoundingBox(loc1.getX(), loc1.getY(), loc1.getZ(), loc2.getX(), loc2.getY(), loc2.getZ());
        return boundingBox.getWidthX() > 5 && boundingBox.getWidthZ() > 5 && boundingBox.getHeight() > 5;
    }

    @Override
    public String toString() {
        return "PlayerSession{loc1=" + loc1 + ", loc2=" + loc2 + '}';
    }

}

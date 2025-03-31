package com.shanebeestudios.hg.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ItemFrameData {

    private final Location location;
    private final BlockFace attachedFace;
    private final ItemStack itemStack;
    private final Rotation rotation;
    private final UUID uuid;

    public ItemFrameData(ItemFrame itemFrame) {
        this.location = itemFrame.getLocation();
        this.attachedFace = itemFrame.getAttachedFace();
        this.itemStack = itemFrame.getItem().clone();
        this.rotation = itemFrame.getRotation();
        this.uuid = itemFrame.getUniqueId();
    }

    public void resetItem() {
        World world = this.location.getWorld();
        if (world == null) return;

        Entity entity = Bukkit.getEntity(this.uuid);
        if (entity == null) {
            // Respawn if the entity was knocked off
            entity = world.spawn(this.location, ItemFrame.class, itemFrame ->
                itemFrame.setFacingDirection(this.attachedFace.getOppositeFace(), true));
        }
        if (entity instanceof ItemFrame itemFrame && entity.isValid()) {
            itemFrame.setItem(this.itemStack);
            itemFrame.setRotation(this.rotation);
        }
    }

}

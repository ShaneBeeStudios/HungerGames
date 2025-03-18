package com.shanebeestudios.hg.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;


public class ItemFrameData {

    private final Location location;
    private final ItemStack itemStack;
    private final Rotation rotation;
    private final UUID uuid;

    public ItemFrameData(ItemFrame itemFrame) {
        this.location = itemFrame.getLocation();
        this.itemStack = itemFrame.getItem().clone();
        this.rotation = itemFrame.getRotation();
        this.uuid = itemFrame.getUniqueId();
    }

    public void resetItem() {
        World world = location.getWorld();
        if (world == null) return;

        Entity entity = Bukkit.getEntity(uuid);
        if (entity instanceof ItemFrame && !entity.isDead()) {
            ItemFrame itemFrame = ((ItemFrame) entity);
            itemFrame.setItem(itemStack);
            itemFrame.setRotation(rotation);
        }
    }

}

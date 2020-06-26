package tk.shanebee.hg.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@SuppressWarnings("unused") // will remove later
public class ItemFrameData {

    private final Location location;
    private final ItemStack itemStack;
    private final Rotation rotation;
    private final UUID uuid;

    public ItemFrameData(Location location, ItemStack itemStack, Rotation rotation, UUID uuid) {
        this.location = location;
        this.itemStack = itemStack;
        this.rotation = rotation;
        this.uuid = uuid;
    }

    public void respawn() {
        World world = location.getWorld();
        if (world == null) return;

        world.spawn(location, ItemFrame.class, itemFrame -> {
            itemFrame.setRotation(this.rotation);
            if (this.itemStack != null) {
                itemFrame.setItem(this.itemStack.clone());
            }
        });
    }

    public void resetItem() {
        World world = location.getWorld();
        if (world == null) return;

        Entity entity = Bukkit.getEntity(uuid);
        if (entity instanceof ItemFrame && !entity.isDead()) {
            ItemFrame itemFrame = ((ItemFrame) entity);
            itemFrame.setItem(itemStack);
        }
    }

}

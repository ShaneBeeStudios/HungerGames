package com.shanebeestudios.hg.api.util;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * NBT class for adding NBT to items
 * <p>(Mainly for internal use)</p>
 */
@SuppressWarnings("CallToPrintStackTrace")
public class NBTApi {

    private static boolean ENABLED;

    public static void initializeNBTApi() {
        MinecraftVersion.replaceLogger(HgLogger.getLogger());
        if (!NBT.preloadApi()) {
            Util.warning("NBT-API unavailable for your server version.");
            Util.warning(" - Some items may not be loaded correctly if you are using the 'data' option");
            ENABLED = false;
        } else {
            ENABLED = true;
        }
    }

    /**
     * Check if the NBT-API is enabled
     *
     * @return Whether enabled
     */
    public static boolean isEnabled() {
        return ENABLED;
    }

    /**
     * Validate an NBT string
     *
     * @param nbtString String to validate
     * @return Error if validation failed
     */
    public static String validateNBT(String nbtString) {
        try {
            NBT.parseNBT(nbtString);
            return null;
        } catch (NbtApiException ex) {
            return ex.getMessage();
        }
    }

    /**
     * Apply NBT to an item
     *
     * @param itemStack Item to add NBT to
     * @param nbtString NBT string to add
     */
    public static void applyNBTToItem(ItemStack itemStack, String nbtString) {
        if (!isEnabled()) {
            Util.warning("NBT API is not enabled and cannot apply NBT to item.");
            return;
        }
        try {
            ReadWriteNBT nbt = NBT.parseNBT(nbtString);
            NBT.modifyComponents(itemStack, itemNbt -> {
                itemNbt.mergeCompound(nbt);
            });
        } catch (NbtApiException ex) {
            Util.warning("Invalid NBT '%s'", nbtString);
            Util.warning("Error: %s", ex.getMessage());
        }
    }

    /**
     * Apply NBT to an entity
     *
     * @param entity    Entity to add NBT to
     * @param nbtString NBT string to add
     */
    public static void applyNBTToEntity(Entity entity, String nbtString) {
        if (!isEnabled()) {
            Util.warning("NBT API is not enabled and cannot apply NBT to entity.");
            return;
        }
        try {
            ReadWriteNBT nbt = NBT.parseNBT(nbtString);
            NBT.modify(entity, itemNbt -> {
                itemNbt.mergeCompound(nbt);
            });
        } catch (NbtApiException ex) {
            Util.warning("Invalid NBT '%s'", nbtString);
            Util.warning("Error: %s", ex.getMessage());
        }
    }

    // Cache these classes/methods to prevent retrieving them too often
    private static final Class<?> ICHAT_BASE_COMPONENT_CLASS = ReflectionUtils.getNMSClass("net.minecraft.network.chat.IChatBaseComponent");
    private static final Class<?> CRAFT_CHAT_MESSAGE_CLASS = ReflectionUtils.getOBCClass("util.CraftChatMessage");
    private static final Class<?> TEXT_TAG_VISITOR_CLASS;
    private static final Class<?> NBT_BASE_CLASS = ReflectionUtils.getNMSClass("net.minecraft.nbt.NBTBase");
    private static final Method FROM_COMPONENT;
    private static final Method VISIT_METHOD;
    private static final boolean IS_RUNNING_1_20_5 = Util.isRunningMinecraft(1, 20, 5);

    static {
        TEXT_TAG_VISITOR_CLASS = ReflectionUtils.getNMSClass("net.minecraft.nbt.TextComponentTagVisitor");
        Method from_comp = null;
        Method visit = null;
        try {
            assert TEXT_TAG_VISITOR_CLASS != null;
            assert CRAFT_CHAT_MESSAGE_CLASS != null;
            visit = TEXT_TAG_VISITOR_CLASS.getDeclaredMethod("visit", NBT_BASE_CLASS);
            from_comp = CRAFT_CHAT_MESSAGE_CLASS.getMethod("fromComponent", ICHAT_BASE_COMPONENT_CLASS);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        FROM_COMPONENT = from_comp;
        VISIT_METHOD = visit;
    }

    /**
     * Get a pretty NBT string
     * <p>This is the same as what vanilla Minecraft outputs when using the '/data' command</p>
     *
     * @param compound Compound to convert to pretty
     * @param split    When null NBT will print on one long line, if not null NBT compound will be
     *                 split into lines with JSON style, and this string will start each line off
     *                 (usually spaces)
     * @return Pretty string of NBTCompound
     */
    @SuppressWarnings("deprecation")
    public static @Nullable String getPrettyNBT(String nbtString, String split) {
        Object nmsNBT = new NBTContainer(nbtString).getCompound();
        String s = split != null ? split : "";
        try {
            Object tagVisitorInstance;
            if (IS_RUNNING_1_20_5) {
                tagVisitorInstance = TEXT_TAG_VISITOR_CLASS.getConstructor(String.class).newInstance(s);
            } else {
                tagVisitorInstance = TEXT_TAG_VISITOR_CLASS.getConstructor(String.class, int.class).newInstance(s, 0);
            }
            Object prettyComponent = VISIT_METHOD.invoke(tagVisitorInstance, nmsNBT);
            return ((String) FROM_COMPONENT.invoke(CRAFT_CHAT_MESSAGE_CLASS, prettyComponent));
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}

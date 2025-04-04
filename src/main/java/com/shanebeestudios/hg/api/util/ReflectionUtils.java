package com.shanebeestudios.hg.api.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility methods for reflection
 */
@SuppressWarnings({"unused", "CallToPrintStackTrace"})
public class ReflectionUtils {

    private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();

    /**
     * Get a CraftBukkit class
     *
     * @param obcClassString String of craftbukkit class (everything after "org.bukkit.craftbukkit")
     * @return CraftBukkit class
     */
    public static @Nullable Class<?> getOBCClass(String obcClassString) {
        String name = CRAFTBUKKIT_PACKAGE + "." + obcClassString;
        return getClass(name);
    }

    /**
     * Get a Minecraft class
     *
     * @param nmsClass Path of class to get (ex: "net.minecraft.world.entity.Entity")
     * @return Minecraft class
     */
    public static @Nullable Class<?> getNMSClass(String nmsClass) {
        return getClass(nmsClass);
    }

    /**
     * Get a Minecraft class with an optional Bukkit mapping alternative
     *
     * @param nmsClass      Path of class to get (ex: "net.minecraft.world.entity.Entity")
     * @param bukkitMapping Optional Bukkit mapping for NMS class (just the class, not the package)
     * @return Minecraft class
     */
    public static @Nullable Class<?> getNMSClass(String nmsClass, String bukkitMapping) {
        return getClass(nmsClass);
    }

    private static @Nullable Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get a Minecraft entity from a Bukkit entity
     *
     * @param entity Bukkit entity to convert
     * @return Minecraft entity
     */
    public static @Nullable Object getNMSEntity(Entity entity) {
        try {
            Method getHandle = entity.getClass().getMethod("getHandle");
            return getHandle.invoke(entity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the value of a field from an object
     *
     * @param field  Name of field
     * @param clazz  Class with field
     * @param object Object which contains field
     * @return Object from field
     */
    public static @Nullable Object getField(String field, Class<?> clazz, Object object) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(object);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Set the value of a field in an object
     *
     * @param field  Name of field to set
     * @param clazz  Class with field
     * @param object Object which holds field
     * @param toSet  Object to set
     */
    public static void setField(String field, Class<?> clazz, Object object, Object toSet) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, toSet);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Set the value of a field in an object
     *
     * @param field  Name of field to set
     * @param object Object which holds field
     * @param toSet  Object to set
     */
    public static void setField(String field, Object object, Object toSet) {
        try {
            Field f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, toSet);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean classExists(final String className) {
        try {
            Class.forName(className);
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

}

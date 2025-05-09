package com.shanebeestudios.hg.api.util;

import java.util.logging.Logger;

/**
 * A Logger wrapper
 */
public class HgLogger extends Logger {

    protected HgLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    /**
     * Get an instance of HgLogger
     *
     * @return new instance of HgLogger
     */
    public static HgLogger getLogger() {
        return new HgLogger("", null);
    }

    @Override
    public void info(String msg) {
        String message = msg.replace("[NBTAPI]", "<grey>[<aqua>NBT<dark_aqua>API<grey>]");
        Util.log(message);
    }

}

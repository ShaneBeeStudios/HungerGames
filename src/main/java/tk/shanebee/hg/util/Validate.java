package tk.shanebee.hg.util;

public abstract class Validate extends org.apache.commons.lang.Validate {

    /**
     * Validate if a value is between a min and max value
     *
     * @param value Value to validate
     * @param min   Min value amount
     * @param max   Max value amount
     */
    public static void isBetween(int value, int min, int max) {
        isTrue(value >= min && value <= max, "Value must be between " + min + " and " + max);
    }

}

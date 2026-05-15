package com.shanebeestudios.hg.api.util;

/**
 * A pair of 2 objects.
 *
 * @param first  First object
 * @param second Second object
 * @param <F>    Class type of the first object
 * @param <S>    Class type of the second object
 */
public record Pair<F, S>(F first, S second) {

    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }

}

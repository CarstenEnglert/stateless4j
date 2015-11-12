package com.github.oxo42.stateless4j.delegates;

/**
 * Represents a function that accepts an input and produces a primitive boolean result.
 *
 * @param <T1> Input argument type
 */
public interface FuncBoolean<T1> {

    /**
     * Applies this function to the given input.
     *
     * @param arg1 Input argument
     * @return Resulting boolean
     */
    boolean call(T1 arg1);
}

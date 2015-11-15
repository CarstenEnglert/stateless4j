package com.github.oxo42.stateless4j.delegates;

/**
 * Represents a condition that accepts an input and produces a primitive boolean result.
 *
 * @param <T> Input argument type
 */
public interface FuncCondition<T> {

    /**
     * Applies this condition to the given input.
     *
     * @param arg Input argument
     * @return if this condition is fulfilled
     */
    boolean check(T arg);
}

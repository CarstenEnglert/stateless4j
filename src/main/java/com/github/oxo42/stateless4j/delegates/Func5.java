package com.github.oxo42.stateless4j.delegates;

/**
 * Represents a function that accepts four inputs and produces a result
 *
 * @param <T1> Input argument type
 * @param <T2> Input argument type
 * @param <T3> Input argument type
 * @param <T4> Input argument type
 * @param <R>  Result type
 */
public interface Func5<T1, T2, T3, T4, R> {

    /**
     * Applies this function to the given input
     *
     * @param arg1 Input argument
     * @param arg2 Input argument
     * @param arg3 Input argument
     * @param arg4 Input argument
     * @return Result
     */
    R call(T1 arg1, T2 arg2, T3 arg3, T4 arg4);
}

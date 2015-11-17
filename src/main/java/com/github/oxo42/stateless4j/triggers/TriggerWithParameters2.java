package com.github.oxo42.stateless4j.triggers;

/**
 * Parameterised trigger for two input arguments.
 *
 * @param <TArg0> The type of the first input argument
 * @param <TArg1> The type of the second input argument
 * @param <T>     The type used to represent the triggers that cause state transitions
 */
public class TriggerWithParameters2<TArg0, TArg1, T> extends TriggerWithParameters<T> {

    /**
     * Create a configured trigger
     *
     * @param underlyingTrigger Trigger represented by this trigger configuration
     * @param classe0           Class argument for the first input argument
     * @param classe1           Class argument for the second input argument
     */
    public TriggerWithParameters2(T underlyingTrigger, Class<TArg0> classe0, Class<TArg1> classe1) {
        super(underlyingTrigger, classe0, classe1);
    }
}

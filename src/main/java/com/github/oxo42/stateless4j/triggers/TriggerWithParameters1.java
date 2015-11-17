package com.github.oxo42.stateless4j.triggers;

/**
 * Parameterised trigger for a single input argument.
 *
 * @param <TArg> The type of the input argument
 * @param <T>    The type used to represent the triggers that cause state transitions
 */
public class TriggerWithParameters1<TArg, T> extends TriggerWithParameters<T> {

    /**
     * Create a configured trigger
     *
     * @param underlyingTrigger Trigger represented by this trigger configuration
     * @param classe            Class argument for the input argument
     */
    public TriggerWithParameters1(T underlyingTrigger, Class<TArg> classe) {
        super(underlyingTrigger, classe);
    }
}

package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.delegates.Func2;

/**
 * Simple representation of the {@code stateAccessor} and {@code stateMutator}, for cases where a state machine is only used in a single context.
 *
 * @param <S> Type of the state to store
 * @param <C> Type of the associated context (which is ignored in this implementation)
 */
public class StateReference<S, C> implements Func2<C, S>, Action2<S, C> {

    private S state;

    public StateReference(S initialState) {
        state = initialState;
    }

    /**
     * The currently stored state value
     *
     * @return The currently stored state value
     */
    public S getState() {
        return state;
    }

    /**
     * The currently stored state value
     *
     * @param context The associated context to retrieve the currently stored state value for (ignored here)
     * @return The currently stored state value
     */
    @Override
    public S call(C context) {
        return getState();
    }

    /**
     * Store the given state value
     *
     * @param value The state value to set
     */
    public void setState(S value) {
        state = value;
    }

    /**
     * Store the given state value
     *
     * @param value The state value to set
     * @param context The associated context to set the value in (ignored here)
     */
    @Override
    public void doIt(S value, C context) {
        setState(value);
    }
}

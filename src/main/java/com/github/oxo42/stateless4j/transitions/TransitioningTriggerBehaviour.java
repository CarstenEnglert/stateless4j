package com.github.oxo42.stateless4j.transitions;

import com.github.oxo42.stateless4j.OutVar;
import com.github.oxo42.stateless4j.delegates.FuncCondition;
import com.github.oxo42.stateless4j.triggers.TriggerBehaviour;

public class TransitioningTriggerBehaviour<S, T, C> extends TriggerBehaviour<S, T, C> {

    private final S destination;

    public TransitioningTriggerBehaviour(T trigger, S destination, FuncCondition<C> guard) {
        super(trigger, guard);
        this.destination = destination;
    }

    @Override
    public boolean resultsInTransitionFrom(S source, C context, Object[] args, OutVar<S> dest) {
        dest.set(destination);
        return true;
    }
}

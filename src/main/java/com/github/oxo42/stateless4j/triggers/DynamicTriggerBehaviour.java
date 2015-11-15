package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.OutVar;
import com.github.oxo42.stateless4j.delegates.Func3;
import com.github.oxo42.stateless4j.delegates.FuncCondition;

public class DynamicTriggerBehaviour<S, T, C> extends TriggerBehaviour<S, T, C> {

    private final Func3<C, Object[], S> destination;

    public DynamicTriggerBehaviour(T trigger, Func3<C, Object[], S> destination, FuncCondition<C> guard) {
        super(trigger, guard);
        assert destination != null : "destination is null";
        this.destination = destination;
    }

    @Override
    public boolean resultsInTransitionFrom(S source, C context, Object[] args, OutVar<S> dest) {
        dest.set(destination.call(context, args));
        return true;
    }
}

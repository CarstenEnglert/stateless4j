package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.OutVar;
import com.github.oxo42.stateless4j.delegates.FuncCondition;

public class IgnoredTriggerBehaviour<S, T, C> extends TriggerBehaviour<S, T, C> {

    public IgnoredTriggerBehaviour(T trigger, FuncCondition<C> guard) {
        super(trigger, guard);
    }

    @Override
    public boolean resultsInTransitionFrom(S source, C context, Object[] args, OutVar<S> dest) {
        return false;
    }
}

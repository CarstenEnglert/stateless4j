package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.OutVar;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;

public class IgnoredTriggerBehaviour<S, T, C> extends TriggerBehaviour<S, T, C> {

    public IgnoredTriggerBehaviour(T trigger, FuncBoolean<C> guard) {
        super(trigger, guard);
    }

    @Override
    public boolean resultsInTransitionFrom(S source, Object[] args, OutVar<S> dest) {
        return false;
    }
}

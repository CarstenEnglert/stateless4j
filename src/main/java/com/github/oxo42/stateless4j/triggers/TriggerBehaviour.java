package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.delegates.FuncCondition;
import com.github.oxo42.stateless4j.OutVar;

public abstract class TriggerBehaviour<S, T, C> {

    private final T trigger;
    private final FuncCondition<C> guard;

    protected TriggerBehaviour(T trigger, FuncCondition<C> guard) {
        this.trigger = trigger;
        this.guard = guard;
    }

    public T getTrigger() {
        return trigger;
    }

    public boolean isGuardConditionMet(C context) {
        return guard.check(context);
    }

    public abstract boolean resultsInTransitionFrom(S source, C context, Object[] args, OutVar<S> dest);
}

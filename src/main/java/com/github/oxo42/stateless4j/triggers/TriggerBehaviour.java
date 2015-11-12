package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.delegates.FuncBoolean;
import com.github.oxo42.stateless4j.OutVar;

public abstract class TriggerBehaviour<S, T, C> {

    private final T trigger;
    private final FuncBoolean<C> guard;

    protected TriggerBehaviour(T trigger, FuncBoolean<C> guard) {
        this.trigger = trigger;
        this.guard = guard;
    }

    public T getTrigger() {
        return trigger;
    }

    public boolean isGuardConditionMet(C context) {
        return guard.call(context);
    }

    public abstract boolean resultsInTransitionFrom(S source, Object[] args, OutVar<S> dest);
}

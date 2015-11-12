package com.github.oxo42.stateless4j.transitions;

public class Transition<S, T, C> {

    private final S source;
    private final S destination;
    private final T trigger;
    private final C context;

    /**
     * Construct a transition
     *
     * @param source      The state transitioned from
     * @param destination The state transitioned to
     * @param trigger     The trigger that caused the transition
     * @param context     The context the state/transition is associated with
     */
    public Transition(S source, S destination, T trigger, C context) {
        this.source = source;
        this.destination = destination;
        this.trigger = trigger;
        this.context = context;
    }

    /**
     * The state transitioned from
     *
     * @return The state transitioned from
     */
    public S getSource() {
        return source;
    }

    /**
     * The state transitioned to
     *
     * @return The state transitioned to
     */
    public S getDestination() {
        return destination;
    }

    /**
     * The trigger that caused the transition
     *
     * @return The trigger that caused the transition
     */
    public T getTrigger() {
        return trigger;
    }

    /**
     * The context the state/transition is associated with
     *
     * @return The context the state/transition is associated with
     */
    public C getContext() {
        return context;
    }

    /**
     * True if the transition is a re-entry, i.e. the identity transition
     *
     * @return True if the transition is a re-entry
     */
    public boolean isReentry() {
        return getSource().equals(getDestination());
    }
}

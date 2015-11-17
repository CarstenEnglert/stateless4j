package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.delegates.Action3;
import com.github.oxo42.stateless4j.delegates.Func2;
import com.github.oxo42.stateless4j.transitions.Transition;
import com.github.oxo42.stateless4j.triggers.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Models behaviour as transitions between a finite set of states
 *
 * @param <S> The type used to represent the states
 * @param <T> The type used to represent the triggers that cause state transitions
 * @param <C> The type used to represent the context in which the state machine is being applied
 */
public class StateMachine<S, T, C> {

    protected final StateMachineConfig<S, T, C> config;
    protected final Func2<C, S> stateAccessor;
    protected final Action2<S, C> stateMutator;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected Action3<S, T, C> unhandledTriggerAction = new Action3<S, T, C>() {

        @Override
        public void doIt(S state, T trigger, C context) {
            throw new IllegalStateException(
                    String.format(
                            "No valid leaving transitions are permitted from state '%s' for trigger '%s' in context '%s'. Consider ignoring the trigger.",
                            state, trigger, context)
            );
        }

    };

    /**
     * Construct a state machine with external state storage.
     *
     * @param stateAccessor  State accessor
     * @param stateMutator   State mutator
     * @param config         State machine configuration
     */
    public StateMachine(Func2<C, S> stateAccessor, Action2<S, C> stateMutator, StateMachineConfig<S, T, C> config) {
        this.config = config;
        this.stateAccessor = stateAccessor;
        this.stateMutator = stateMutator;
    }

    public StateConfiguration<S, T, C> configure(S state) {
        return config.configure(state);
    }
    
    public StateMachineConfig<S, T, C> configuration() {
        return config;
    }

    /**
     * The current state
     *
     * @param context The context to get the current state for
     * @return The current state
     */
    public S getState(C context) {
        return stateAccessor.call(context);
    }

    /**
     * Apply the given state in the provided context
     * 
     * @param value   The state to set
     * @param context The context to set the current state for
     */
    private void setState(S value, C context) {
        stateMutator.doIt(value, context);
    }

    /**
     * The currently-permissible trigger values
     *
     * @param context The context to get the permitted triggers for
     * @return The currently-permissible trigger values
     */
    public List<T> getPermittedTriggers(C context) {
        return getCurrentRepresentation(context).getPermittedTriggers(context);
    }

    /**
     * The configured representation of the current state
     *
     * @param context The context to get the current state representation for
     * @return The configured representation of the current state
     */
    StateRepresentation<S, T, C> getCurrentRepresentation(C context) {
        S state = getState(context);
        StateRepresentation<S, T, C> representation = config.getRepresentation(state);
        return representation == null ? new StateRepresentation<S, T, C>(state) : representation;
    }

    /**
     * Transition from the current state via the specified trigger.
     * The target state is determined by the configuration of the current state.
     * Actions associated with leaving the current state and entering the new one
     * will be invoked
     *
     * @param trigger The trigger to fire
     * @param context The context to fire the trigger for
     */
    public void fire(T trigger, C context) {
        publicFire(trigger, context);
    }

    /**
     * Transition from the current state via the specified trigger.
     * The target state is determined by the configuration of the current state.
     * Actions associated with leaving the current state and entering the new one
     * will be invoked.
     *
     * @param trigger The trigger to fire
     * @param context The context to fire the trigger for
     * @param arg     The argument
     * @param <TArg> Type of the trigger argument
     */
    public <TArg> void fire(TriggerWithParameters1<TArg, T> trigger, C context, TArg arg) {
        assert trigger != null : "trigger is null";
        publicFire(trigger.getTrigger(), context, arg);
    }

    /**
     * Transition from the current state via the specified trigger.
     * The target state is determined by the configuration of the current state.
     * Actions associated with leaving the current state and entering the new one
     * will be invoked.
     *
     * @param trigger The trigger to fire
     * @param context The context to fire the trigger for
     * @param arg0    The first argument
     * @param arg1    The second argument
     * @param <TArg0> Type of the first trigger argument
     * @param <TArg1> Type of the second trigger argument
     */
    public <TArg0, TArg1> void fire(TriggerWithParameters2<TArg0, TArg1, T> trigger, C context, TArg0 arg0, TArg1 arg1) {
        assert trigger != null : "trigger is null";
        publicFire(trigger.getTrigger(), context, arg0, arg1);
    }

    /**
     * Transition from the current state via the specified trigger.
     * The target state is determined by the configuration of the current state.
     * Actions associated with leaving the current state and entering the new one
     * will be invoked.
     *
     * @param trigger The trigger to fire
     * @param context The context to fire the trigger for
     * @param arg0    The first argument
     * @param arg1    The second argument
     * @param arg2    The third argument
     * @param <TArg0> Type of the first trigger argument
     * @param <TArg1> Type of the second trigger argument
     * @param <TArg2> Type of the third trigger argument
     */
    public <TArg0, TArg1, TArg2> void fire(TriggerWithParameters3<TArg0, TArg1, TArg2, T> trigger, C context, TArg0 arg0, TArg1 arg1, TArg2 arg2) {
        assert trigger != null : "trigger is null";
        publicFire(trigger.getTrigger(), context, arg0, arg1, arg2);
    }

    protected void publicFire(T trigger, C context, Object... args) {
        logger.debug("Firing " + trigger);
        TriggerWithParameters<T> configuration = config.getTriggerConfiguration(trigger);
        if (configuration != null) {
            configuration.validateParameters(args);
        }

        TriggerBehaviour<S, T, C> triggerBehaviour = getCurrentRepresentation(context).tryFindHandler(trigger, context);
        if (triggerBehaviour == null) {
            unhandledTriggerAction.doIt(getCurrentRepresentation(context).getUnderlyingState(), trigger, context);
            return;
        }

        S source = getState(context);
        OutVar<S> destination = new OutVar<>();
        if (triggerBehaviour.resultsInTransitionFrom(source, context, args, destination)) {
            Transition<S, T, C> transition = new Transition<>(source, destination.get(), trigger, context);

            getCurrentRepresentation(context).exit(transition);
            setState(destination.get(), context);
            getCurrentRepresentation(context).enter(transition, args);
        }
    }

    /**
     * Override the default behaviour of throwing an exception when an unhandled trigger is fired
     *
     * @param unhandledTriggerAction An action to call when an unhandled trigger is fired
     */
    public void onUnhandledTrigger(Action3<S, T, C> unhandledTriggerAction) {
        if (unhandledTriggerAction == null) {
            throw new IllegalStateException("unhandledTriggerAction");
        }
        this.unhandledTriggerAction = unhandledTriggerAction;
    }

    /**
     * Determine if the state machine is in the supplied state
     *
     * @param state   The state to test for
     * @param context The context to get the current state for
     * @return True if the current state is equal to, or a substate of, the supplied state
     */
    public boolean isInState(S state, C context) {
        return getCurrentRepresentation(context).isIncludedIn(state);
    }

    /**
     * Returns true if {@code trigger} can be fired  in the current state
     *
     * @param trigger Trigger to test
     * @param context The context to get the current state for
     * @return True if the trigger can be fired, false otherwise
     */
    public boolean canFire(T trigger, C context) {
        return getCurrentRepresentation(context).canHandle(trigger, context);
    }
}

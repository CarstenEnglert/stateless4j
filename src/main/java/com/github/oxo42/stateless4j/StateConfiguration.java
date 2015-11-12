package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.*;
import com.github.oxo42.stateless4j.transitions.Transition;
import com.github.oxo42.stateless4j.transitions.TransitioningTriggerBehaviour;
import com.github.oxo42.stateless4j.triggers.*;

public class StateConfiguration<S, T, C> {

    private final FuncBoolean<C> NO_GUARD = new FuncBoolean<C>() {
        @Override
        public boolean call(C context) {
            return true;
        }
    };
    private final StateRepresentation<S, T, C> representation;
    private final Func2<S, StateRepresentation<S, T, C>> lookup;

    public StateConfiguration(final StateRepresentation<S, T, C> representation, final Func2<S, StateRepresentation<S, T, C>> lookup) {
        assert representation != null : "representation is null";
        assert lookup != null : "lookup is null";
        this.representation = representation;
        this.lookup = lookup;
    }

    /**
     * Accept the specified trigger and transition to the destination state
     *
     * @param trigger          The accepted trigger
     * @param destinationState The state that the trigger will cause a transition to
     * @return The reciever
     */
    public StateConfiguration<S, T, C> permit(T trigger, S destinationState) {
        enforceNotIdentityTransition(destinationState);
        return publicPermit(trigger, destinationState);
    }

    /**
     * Accept the specified trigger and transition to the destination state
     *
     * @param trigger          The accepted trigger
     * @param destinationState The state that the trigger will cause a transition to
     * @param guard            Function that must return true in order for the trigger to be accepted
     * @return The reciever
     */
    public StateConfiguration<S, T, C> permitIf(T trigger, S destinationState, FuncBoolean<C> guard) {
        enforceNotIdentityTransition(destinationState);
        return publicPermitIf(trigger, destinationState, guard);
    }

    /**
     * Accept the specified trigger, execute exit actions and re-execute entry actions. Reentry behaves as though the
     * configured state transitions to an identical sibling state
     * <p>
     * Applies to the current state only. Will not re-execute superstate actions, or  cause actions to execute
     * transitioning between super- and sub-states
     *
     * @param trigger The accepted trigger
     * @return The reciever
     */
    public StateConfiguration<S, T, C> permitReentry(T trigger) {
        return publicPermit(trigger, representation.getUnderlyingState());
    }

    /**
     * Accept the specified trigger, execute exit actions and re-execute entry actions. Reentry behaves as though the
     * configured state transitions to an identical sibling state
     * <p>
     * Applies to the current state only. Will not re-execute superstate actions, or  cause actions to execute
     * transitioning between super- and sub-states
     *
     * @param trigger The accepted trigger
     * @param guard   Function that must return true in order for the trigger to be accepted
     * @return The reciever
     */
    public StateConfiguration<S, T, C> permitReentryIf(T trigger, FuncBoolean<C> guard) {
        return publicPermitIf(trigger, representation.getUnderlyingState(), guard);
    }

    /**
     * ignore the specified trigger when in the configured state
     *
     * @param trigger The trigger to ignore
     * @return The receiver
     */
    public StateConfiguration<S, T, C> ignore(T trigger) {
        return ignoreIf(trigger, NO_GUARD);
    }

    /**
     * ignore the specified trigger when in the configured state, if the guard returns true
     *
     * @param trigger The trigger to ignore
     * @param guard   Function that must return true in order for the trigger to be ignored
     * @return The receiver
     */
    public StateConfiguration<S, T, C> ignoreIf(T trigger, FuncBoolean<C> guard) {
        assert guard != null : "guard is null";
        representation.addTriggerBehaviour(new IgnoredTriggerBehaviour<S, T, C>(trigger, guard));
        return this;
    }

    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param entryAction Action to execute
     * @return The receiver
     */
    public StateConfiguration<S, T, C> onEntry(final Action entryAction) {
        assert entryAction != null : "entryAction is null";
        return onEntry(new Action1<Transition<S, T, C>>() {
            @Override
            public void doIt(Transition<S, T, C> t) {
                entryAction.doIt();
            }
        });
    }

    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param entryAction Action to execute, providing details of the transition
     * @return The receiver
     */
    public StateConfiguration<S, T, C> onEntry(final Action1<Transition<S, T, C>> entryAction) {
        assert entryAction != null : "entryAction is null";
        representation.addEntryAction(new Action2<Transition<S, T, C>, Object[]>() {
            @Override
            public void doIt(Transition<S, T, C> arg1, Object[] arg2) {
                entryAction.doIt(arg1);
            }
        });
        return this;
    }

    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute
     * @return The receiver
     */
    public StateConfiguration<S, T, C> onEntryFrom(T trigger, final Action entryAction) {
        assert entryAction != null : "entryAction is null";
        return onEntryFrom(trigger, new Action1<Transition<S, T, C>>() {
            @Override
            public void doIt(Transition<S, T, C> arg1) {
                entryAction.doIt();
            }
        });
    }

    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @return The receiver
     */
    public StateConfiguration<S, T, C> onEntryFrom(T trigger, final Action1<Transition<S, T, C>> entryAction) {
        assert entryAction != null : "entryAction is null";
        representation.addEntryAction(trigger, new Action2<Transition<S, T, C>, Object[]>() {
            @Override
            public void doIt(Transition<S, T, C> arg1, Object[] arg2) {
                entryAction.doIt(arg1);
            }
        });
        return this;
    }

    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @param classe0     Class argument
     * @param <TArg0>     Type of the first trigger argument
     * @return The receiver
     */
    public <TArg0> StateConfiguration<S, T, C> onEntryFrom(TriggerWithParameters1<TArg0, S, T> trigger, final Action1<TArg0> entryAction, final Class<TArg0> classe0) {
        assert entryAction != null : "entryAction is null";
        return onEntryFrom(trigger, new Action2<TArg0, Transition<S, T, C>>() {
            @Override
            public void doIt(TArg0 arg1, Transition<S, T, C> arg2) {
                entryAction.doIt(arg1);
            }
        }, classe0);
    }

    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @param classe0     Class argument
     * @param <TArg0>     Type of the first trigger argument
     * @return The receiver
     */
    public <TArg0> StateConfiguration<S, T, C> onEntryFrom(TriggerWithParameters1<TArg0, S, T> trigger, final Action2<TArg0, Transition<S, T, C>> entryAction, final Class<TArg0> classe0) {
        assert trigger != null : "trigger is null";
        assert entryAction != null : "entryAction is null";
        representation.addEntryAction(trigger.getTrigger(), new Action2<Transition<S, T, C>, Object[]>() {
            @SuppressWarnings("unchecked")
            @Override
            public void doIt(Transition<S, T, C> t, Object[] arg2) {
                entryAction.doIt((TArg0) arg2[0], t);
            }
        });
        return this;
    }

    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @param classe0     Class argument
     * @param classe1     Class argument
     * @param <TArg0>     Type of the first trigger argument
     * @param <TArg1>     Type of the second trigger argument
     * @return The receiver
     */
    public <TArg0, TArg1> StateConfiguration<S, T, C> onEntryFrom(TriggerWithParameters2<TArg0, TArg1, S, T> trigger, final Action2<TArg0, TArg1> entryAction, final Class<TArg0> classe0, final Class<TArg1> classe1) {
        assert entryAction != null : "entryAction is null";
        return onEntryFrom(trigger, new Action3<TArg0, TArg1, Transition<S, T, C>>() {
            @Override
            public void doIt(TArg0 a0, TArg1 a1, Transition<S, T, C> t) {
                entryAction.doIt(a0, a1);
            }
        }, classe0, classe1);
    }

    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @param classe0     Class argument
     * @param classe1     Class argument
     * @param <TArg0>     Type of the first trigger argument
     * @param <TArg1>     Type of the second trigger argument
     * @return The receiver
     */
    public <TArg0, TArg1> StateConfiguration<S, T, C> onEntryFrom(TriggerWithParameters2<TArg0, TArg1, S, T> trigger, final Action3<TArg0, TArg1, Transition<S, T, C>> entryAction, final Class<TArg0> classe0, final Class<TArg1> classe1) {
        assert trigger != null : "trigger is null";
        assert entryAction != null : "entryAction is null";
        representation.addEntryAction(trigger.getTrigger(), new Action2<Transition<S, T, C>, Object[]>() {
            @SuppressWarnings("unchecked")
            @Override
            public void doIt(Transition<S, T, C> t, Object[] args) {
                entryAction.doIt(
                        (TArg0) args[0],
                        (TArg1) args[1], t);
            }
        });
        return this;
    }

    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @param classe0     Class argument
     * @param classe1     Class argument
     * @param classe2     Class argument
     * @param <TArg0>     Type of the first trigger argument
     * @param <TArg1>     Type of the second trigger argument
     * @param <TArg2>     Type of the third trigger argument
     * @return The receiver
     */
    public <TArg0, TArg1, TArg2> StateConfiguration<S, T, C> onEntryFrom(TriggerWithParameters3<TArg0, TArg1, TArg2, S, T> trigger, final Action3<TArg0, TArg1, TArg2> entryAction, final Class<TArg0> classe0, final Class<TArg1> classe1, final Class<TArg2> classe2) {
        assert entryAction != null : "entryAction is null";
        return onEntryFrom(trigger, new Action4<TArg0, TArg1, TArg2, Transition<S, T, C>>() {
            @Override
            public void doIt(TArg0 a0, TArg1 a1, TArg2 a2, Transition<S, T, C> t) {
                entryAction.doIt(a0, a1, a2);
            }
        }, classe0, classe1, classe2);
    }

    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @param classe0     Class argument
     * @param classe1     Class argument
     * @param classe2     Class argument
     * @param <TArg0>     Type of the first trigger argument
     * @param <TArg1>     Type of the second trigger argument
     * @param <TArg2>     Type of the third trigger argument
     * @return The receiver
     */
    public <TArg0, TArg1, TArg2> StateConfiguration<S, T, C> onEntryFrom(TriggerWithParameters3<TArg0, TArg1, TArg2, S, T> trigger, final Action4<TArg0, TArg1, TArg2, Transition<S, T, C>> entryAction, final Class<TArg0> classe0, final Class<TArg1> classe1, final Class<TArg2> classe2) {
        assert trigger != null : "trigger is null";
        assert entryAction != null : "entryAction is null";
        representation.addEntryAction(trigger.getTrigger(), new Action2<Transition<S, T, C>, Object[]>() {
            @SuppressWarnings("unchecked")
            @Override
            public void doIt(Transition<S, T, C> t, Object[] args) {
                entryAction.doIt(
                        (TArg0) args[0],
                        (TArg1) args[1],
                        (TArg2) args[2], t);
            }
        });
        return this;
    }

    /**
     * Specify an action that will execute when transitioning from the configured state
     *
     * @param exitAction Action to execute
     * @return The receiver
     */
    public StateConfiguration<S, T, C> onExit(final Action exitAction) {
        assert exitAction != null : "exitAction is null";
        return onExit(new Action1<Transition<S, T, C>>() {
            @Override
            public void doIt(Transition<S, T, C> arg1) {
                exitAction.doIt();
            }
        });
    }

    /**
     * Specify an action that will execute when transitioning from the configured state
     *
     * @param exitAction Action to execute
     * @return The receiver
     */
    public StateConfiguration<S, T, C> onExit(Action1<Transition<S, T, C>> exitAction) {
        assert exitAction != null : "exitAction is null";
        representation.addExitAction(exitAction);
        return this;
    }

    /**
     * Sets the superstate that the configured state is a substate of
     * <p>
     * Substates inherit the allowed transitions of their superstate.
     * When entering directly into a substate from outside of the superstate,
     * entry actions for the superstate are executed.
     * Likewise when leaving from the substate to outside the supserstate,
     * exit actions for the superstate will execute.
     *
     * @param superstate The superstate
     * @return The receiver
     */
    public StateConfiguration<S, T, C> substateOf(S superstate) {
        StateRepresentation<S, T, C> superRepresentation = lookup.call(superstate);
        representation.setSuperstate(superRepresentation);
        superRepresentation.addSubstate(representation);
        return this;
    }

    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @return The reciever
     */
    public StateConfiguration<S, T, C> permitDynamic(T trigger, final Func<S> destinationStateSelector) {
        return permitDynamicIf(trigger, destinationStateSelector, NO_GUARD);
    }

    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param <TArg0>                  Type of the first trigger argument
     * @return The receiver
     */
    public <TArg0> StateConfiguration<S, T, C> permitDynamic(TriggerWithParameters1<TArg0, S, T> trigger, Func2<TArg0, S> destinationStateSelector) {
        return permitDynamicIf(trigger, destinationStateSelector, NO_GUARD);
    }

    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param <TArg0>                  Type of the first trigger argument
     * @param <TArg1>                  Type of the second trigger argument
     * @return The receiver
     */
    public <TArg0, TArg1> StateConfiguration<S, T, C> permitDynamic(
            TriggerWithParameters2<TArg0, TArg1, S, T> trigger,
            Func3<TArg0, TArg1, S> destinationStateSelector) {
        return permitDynamicIf(trigger, destinationStateSelector, NO_GUARD);
    }

    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param <TArg0>                  Type of the first trigger argument
     * @param <TArg1>                  Type of the second trigger argument
     * @param <TArg2>                  Type of the third trigger argument
     * @return The receiver
     */
    public <TArg0, TArg1, TArg2> StateConfiguration<S, T, C> permitDynamic(TriggerWithParameters3<TArg0, TArg1, TArg2, S, T> trigger, final Func4<TArg0, TArg1, TArg2, S> destinationStateSelector) {
        return permitDynamicIf(trigger, destinationStateSelector, NO_GUARD);
    }

    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param guard                    Function that must return true in order for the  trigger to be accepted
     * @return The reciever
     */
    public StateConfiguration<S, T, C> permitDynamicIf(T trigger, final Func<S> destinationStateSelector, FuncBoolean<C> guard) {
        assert destinationStateSelector != null : "destinationStateSelector is null";
        return publicPermitDynamicIf(trigger, new Func2<Object[], S>() {
            @Override
            public S call(Object[] arg0) {
                return destinationStateSelector.call();
            }
        }, guard);
    }

    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param guard                    Function that must return true in order for the  trigger to be accepted
     * @param <TArg0>                  Type of the first trigger argument
     * @return The reciever
     */
    public <TArg0> StateConfiguration<S, T, C> permitDynamicIf(TriggerWithParameters1<TArg0, S, T> trigger, final Func2<TArg0, S> destinationStateSelector, FuncBoolean<C> guard) {
        assert trigger != null : "trigger is null";
        assert destinationStateSelector != null : "destinationStateSelector is null";
        return publicPermitDynamicIf(
                trigger.getTrigger(), new Func2<Object[], S>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public S call(Object[] args) {
                        return destinationStateSelector.call((TArg0) args[0]);

                    }
                },
                guard
        );
    }

    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param guard                    Function that must return true in order for the  trigger to be accepted
     * @param <TArg0>                  Type of the first trigger argument
     * @param <TArg1>                  Type of the second trigger argument
     * @return The reciever
     */
    public <TArg0, TArg1> StateConfiguration<S, T, C> permitDynamicIf(TriggerWithParameters2<TArg0, TArg1, S, T> trigger, final Func3<TArg0, TArg1, S> destinationStateSelector, FuncBoolean<C> guard) {
        assert trigger != null : "trigger is null";
        assert destinationStateSelector != null : "destinationStateSelector is null";
        return publicPermitDynamicIf(
                trigger.getTrigger(), new Func2<Object[], S>() {
                    @SuppressWarnings("unchecked")

                    @Override
                    public S call(Object[] args) {
                        return destinationStateSelector.call(
                                (TArg0) args[0],
                                (TArg1) args[1]);
                    }
                },
                guard
        );
    }

    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param guard                    Function that must return true in order for the  trigger to be accepted
     * @param <TArg0>                  Type of the first trigger argument
     * @param <TArg1>                  Type of the second trigger argument
     * @param <TArg2>                  Type of the third trigger argument
     * @return The reciever
     */
    public <TArg0, TArg1, TArg2> StateConfiguration<S, T, C> permitDynamicIf(TriggerWithParameters3<TArg0, TArg1, TArg2, S, T> trigger,
            final Func4<TArg0, TArg1, TArg2, S> destinationStateSelector, FuncBoolean<C> guard) {
        assert trigger != null : "trigger is null";
        assert destinationStateSelector != null : "destinationStateSelector is null";
        return publicPermitDynamicIf(
                trigger.getTrigger(), new Func2<Object[], S>() {
                    @SuppressWarnings("unchecked")

                    @Override
                    public S call(Object[] args) {
                        return destinationStateSelector.call(
                                (TArg0) args[0],
                                (TArg1) args[1],
                                (TArg2) args[2]
                        );
                    }
                }, guard
        );
    }

    void enforceNotIdentityTransition(S destination) {
        if (destination.equals(representation.getUnderlyingState())) {
            throw new IllegalStateException("Permit() (and PermitIf()) require that the destination state is not equal to the source state. To accept a trigger without changing state, use either Ignore() or PermitReentry().");
        }
    }

    StateConfiguration<S, T, C> publicPermit(T trigger, S destinationState) {
        return publicPermitIf(trigger, destinationState, NO_GUARD);
    }

    StateConfiguration<S, T, C> publicPermitIf(T trigger, S destinationState, FuncBoolean<C> guard) {
        assert guard != null : "guard is null";
        representation.addTriggerBehaviour(new TransitioningTriggerBehaviour<>(trigger, destinationState, guard));
        return this;
    }

    StateConfiguration<S, T, C> publicPermitDynamic(T trigger, Func2<Object[], S> destinationStateSelector) {
        return publicPermitDynamicIf(trigger, destinationStateSelector, NO_GUARD);
    }

    StateConfiguration<S, T, C> publicPermitDynamicIf(T trigger, Func2<Object[], S> destinationStateSelector, FuncBoolean<C> guard) {
        assert destinationStateSelector != null : "destinationStateSelector is null";
        assert guard != null : "guard is null";
        representation.addTriggerBehaviour(new DynamicTriggerBehaviour<>(trigger, destinationStateSelector, guard));
        return this;
    }
}

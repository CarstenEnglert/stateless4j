package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Action;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class StateMachineTests {

    boolean fired = false;
    String entryArgS = null;
    int entryArgI = 0;

    @Test
    public void CanUseReferenceTypeMarkers() {
        RunSimpleTest(
                new Enum<?>[]{State.A, State.B, State.C},
                new Enum<?>[]{Trigger.X, Trigger.Y},
                new Enum<?>[]{Context.M, Context.N});
    }

    @Test
    public void CanUseValueTypeMarkers() {
        RunSimpleTest(State.values(), Trigger.values(), Context.values());
    }

    <S extends Enum<?>, T extends Enum<?>, C extends Enum<?>> void RunSimpleTest(S[] states, T[] transitions, C[] contexts) {
        S a = states[0];
        S b = states[1];
        T x = transitions[0];
        C context = contexts[0];

        StateMachineConfig<S, T, C> config = new StateMachineConfig<>();

        config.configure(a)
                .permit(x, b);

        StateReference<S, C> reference = new StateReference<>(a);
        StateMachine<S, T, C> sm = new StateMachine<>(reference, reference, config);
        sm.fire(x, context);

        assertEquals(b, sm.getState(context));
    }

    @Test
    public void SubstateIsIncludedInCurrentState() {
        StateMachineConfig<State, Trigger, Context> config = new StateMachineConfig<>();

        config.configure(State.B).substateOf(State.C);

        StateReference<State, Context> reference = new StateReference<>(State.B);
        StateMachine<State, Trigger, Context> sm = new StateMachine<>(reference, reference, config);

        assertEquals(State.B, sm.getState(Context.M));
        assertTrue(sm.isInState(State.C, Context.M));
    }

    @Test
    public void WhenInSubstate_TriggerIgnoredInSuperstate_RemainsInSubstate() {
        StateMachineConfig<State, Trigger, Context> config = new StateMachineConfig<>();

        config.configure(State.B)
                .substateOf(State.C);

        config.configure(State.C)
                .ignore(Trigger.X);

        StateReference<State, Context> reference = new StateReference<>(State.B);
        StateMachine<State, Trigger, Context> sm = new StateMachine<>(reference, reference, config);
        sm.fire(Trigger.X, Context.M);

        assertEquals(State.B, sm.getState(Context.M));
    }

    @Test
    public void PermittedTriggersIncludeSuperstatePermittedTriggers() {
        StateMachineConfig<State, Trigger, Context> config = new StateMachineConfig<>();

        config.configure(State.A)
                .permit(Trigger.Z, State.B);

        config.configure(State.B)
                .substateOf(State.C)
                .permit(Trigger.X, State.A);

        config.configure(State.C)
                .permit(Trigger.Y, State.A);

        StateReference<State, Context> reference = new StateReference<>(State.B);
        StateMachine<State, Trigger, Context> sm = new StateMachine<>(reference, reference, config);
        List<Trigger> permitted = sm.getPermittedTriggers(Context.M);

        assertTrue(permitted.contains(Trigger.X));
        assertTrue(permitted.contains(Trigger.Y));
        assertFalse(permitted.contains(Trigger.Z));
    }

    @Test
    public void PermittedTriggersAreDistinctValues() {
        StateMachineConfig<State, Trigger, Context> config = new StateMachineConfig<>();

        config.configure(State.B)
                .substateOf(State.C)
                .permit(Trigger.X, State.A);

        config.configure(State.C)
                .permit(Trigger.X, State.B);

        StateReference<State, Context> reference = new StateReference<>(State.B);
        StateMachine<State, Trigger, Context> sm = new StateMachine<>(reference, reference, config);
        List<Trigger> permitted = sm.getPermittedTriggers(Context.M);

        assertEquals(1, permitted.size());
        assertEquals(Trigger.X, permitted.get(0));
    }

    @Test
    public void AcceptedTriggersRespectGuards() {
        StateMachineConfig<State, Trigger, Context> config = new StateMachineConfig<>();

        config.configure(State.B)
                .permitIf(Trigger.X, State.A, new FuncBoolean<Context>() {

                    @Override
                    public boolean call(Context context) {
                        return false;
                    }
                });

        StateReference<State, Context> reference = new StateReference<>(State.B);
        StateMachine<State, Trigger, Context> sm = new StateMachine<>(reference, reference, config);

        assertEquals(0, sm.getPermittedTriggers(Context.M).size());
    }

    @Test
    public void WhenDiscriminatedByGuard_ChoosesPermitedTransition() {
        StateMachineConfig<State, Trigger, Context> config = new StateMachineConfig<>();

        config.configure(State.B)
                .permitIf(Trigger.X, State.A, IgnoredTriggerBehaviourTests.returnFalse)
                .permitIf(Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnTrue);

        StateReference<State, Context> reference = new StateReference<>(State.B);
        StateMachine<State, Trigger, Context> sm = new StateMachine<>(reference, reference, config);
        sm.fire(Trigger.X, Context.M);

        assertEquals(State.C, sm.getState(Context.M));
    }

    private void setFired() {
        fired = true;
    }

    @Test
    public void WhenTriggerIsIgnored_ActionsNotExecuted() {
        StateMachineConfig<State, Trigger, Context> config = new StateMachineConfig<>();

        config.configure(State.B)
                .onEntry(new Action() {

                    @Override
                    public void doIt() {
                        setFired();
                    }
                })
                .ignore(Trigger.X);

        fired = false;

        StateReference<State, Context> reference = new StateReference<>(State.B);
        StateMachine<State, Trigger, Context> sm = new StateMachine<>(reference, reference, config);
        sm.fire(Trigger.X, Context.M);

        assertFalse(fired);
    }

    @Test
    public void IfSelfTransitionPermited_ActionsFire() {
        StateMachineConfig<State, Trigger, Context> config = new StateMachineConfig<>();

        config.configure(State.B)
                .onEntry(new Action() {

                    @Override
                    public void doIt() {
                        setFired();
                    }
                })
                .permitReentry(Trigger.X);

        fired = false;

        StateReference<State, Context> reference = new StateReference<>(State.B);
        StateMachine<State, Trigger, Context> sm = new StateMachine<>(reference, reference, config);
        sm.fire(Trigger.X, Context.M);

        assertTrue(fired);
    }

    @Test(expected = IllegalStateException.class)
    public void ImplicitReentryIsDisallowed() {
        StateMachineConfig<State, Trigger, Context> config = new StateMachineConfig<>();

        StateReference<State, Context> reference = new StateReference<>(State.B);
        StateMachine<State, Trigger, Context> sm = new StateMachine<>(reference, reference, config);

        config.configure(State.B)
                .permit(Trigger.X, State.B);
    }

    @Test(expected = IllegalStateException.class)
    public void TriggerParametersAreImmutableOnceSet() {
        StateMachineConfig<State, Trigger, Context> config = new StateMachineConfig<>();

        StateReference<State, Context> reference = new StateReference<>(State.B);
        StateMachine<State, Trigger, Context> sm = new StateMachine<>(reference, reference, config);

        config.setTriggerParameters(Trigger.X, String.class, int.class);
        config.setTriggerParameters(Trigger.X, String.class);
    }
}

package com.github.oxo42.stateless4j;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NonEnumTests {

    public static final String StateA = "StateA";
    public static final String StateB = "StateB";
    public static final String StateC = "StateC";

    public static final String TriggerX = "TriggerX";
    public static final String TriggerY = "TriggerY";

    public static final Long Context1 = 1000l;

    @Test
    public void CanUseReferenceTypeMarkers() {
        RunSimpleTest(new String[]{StateA, StateB, StateC},
                new String[]{TriggerX, TriggerY},
                new Long[]{Context1});
    }

    @Test
    public void CanUseValueTypeMarkers() {
        RunSimpleTest(State.values(), Trigger.values(), Context.values());
    }

    <S, T, C> void RunSimpleTest(S[] states, T[] transitions, C[] contexts) {
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

}

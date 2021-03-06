package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Func2;
import com.github.oxo42.stateless4j.delegates.Func3;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DynamicTriggerTests {

    @Test
    public void DestinationStateIsDynamic() {
        StateMachineConfig<State, Trigger, Context> config = new StateMachineConfig<>();
        config.configure(State.A).permitDynamic(Trigger.X, new Func2<Context, State>() {

            @Override
            public State call(Context context) {
                return State.B;
            }
        });

        StateReference<State, Context> reference = new StateReference<>(State.A);
        StateMachine<State, Trigger, Context> sm = new StateMachine<>(reference, reference, config);
        sm.fire(Trigger.X, Context.M);

        assertEquals(State.B, sm.getState(Context.M));
    }

    @Test
    public void DestinationStateIsCalculatedBasedOnTriggerParameters() {
        StateMachineConfig<State, Trigger, Context> config = new StateMachineConfig<>();
        TriggerWithParameters1<Integer, Trigger> trigger = config.setTriggerParameters(
                Trigger.X, Integer.class);
        config.configure(State.A).permitDynamic(trigger, new Func3<Integer, Context, State>() {
            @Override
            public State call(Integer i, Context c) {
                return i == 1 ? State.B : State.C;
            }
        });

        StateReference<State, Context> reference = new StateReference<>(State.A);
        StateMachine<State, Trigger, Context> sm = new StateMachine<>(reference, reference, config);
        sm.fire(trigger, Context.M, 1);

        assertEquals(State.B, sm.getState(Context.M));
    }
}

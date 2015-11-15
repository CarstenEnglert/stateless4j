package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.FuncCondition;
import com.github.oxo42.stateless4j.triggers.IgnoredTriggerBehaviour;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IgnoredTriggerBehaviourTests {

    public static final FuncCondition<Context> returnTrue = new FuncCondition<Context>() {

        @Override
        public boolean check(Context context) {
            return true;
        }
    };

    public static final FuncCondition<Context> returnFalse = new FuncCondition<Context>() {

        @Override
        public boolean check(Context context) {
            return false;
        }
    };

    @Test
    public void StateRemainsUnchanged() {
        IgnoredTriggerBehaviour<State, Trigger, Context> ignored = new IgnoredTriggerBehaviour<>(Trigger.X, returnTrue);
        assertFalse(ignored.resultsInTransitionFrom(State.B, Context.M, new Object[0], new OutVar<State>()));
    }

    @Test
    public void ExposesCorrectUnderlyingTrigger() {
        IgnoredTriggerBehaviour<State, Trigger, Context> ignored = new IgnoredTriggerBehaviour<>(Trigger.X, returnTrue);
        assertEquals(Trigger.X, ignored.getTrigger());
    }

    @Test
    public void WhenGuardConditionFalse_IsGuardConditionMetIsFalse() {
        IgnoredTriggerBehaviour<State, Trigger, Context> ignored = new IgnoredTriggerBehaviour<>(Trigger.X, returnFalse);
        assertFalse(ignored.isGuardConditionMet(Context.M));
    }

    @Test
    public void WhenGuardConditionTrue_IsGuardConditionMetIsTrue() {
        IgnoredTriggerBehaviour<State, Trigger, Context> ignored = new IgnoredTriggerBehaviour<>(Trigger.X, returnTrue);
        assertTrue(ignored.isGuardConditionMet(Context.M));
    }
}

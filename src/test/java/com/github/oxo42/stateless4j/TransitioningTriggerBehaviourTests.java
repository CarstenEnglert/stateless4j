package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.transitions.TransitioningTriggerBehaviour;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransitioningTriggerBehaviourTests {

    @Test
    public void TransitionsToDestinationState() {
        TransitioningTriggerBehaviour<State, Trigger, Context> transitioning = new TransitioningTriggerBehaviour<>(
                Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnTrue);
        OutVar<State> destination = new OutVar<>();
        assertTrue(transitioning.resultsInTransitionFrom(State.B, new Object[0], destination));
        assertEquals(State.C, destination.get());
    }
}

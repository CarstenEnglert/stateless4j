package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.transitions.Transition;
import com.github.oxo42.stateless4j.triggers.IgnoredTriggerBehaviour;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class StateRepresentationTests {

    Transition<State, Trigger, Context> actualTransition = null;
    boolean executed = false;
    int order = 0, subOrder = 0, superOrder = 0;

    @Test
    public void UponEntering_EnteringActionsExecuted() {
        StateRepresentation<State, Trigger, Context> stateRepresentation = CreateRepresentation(State.B);
        Transition<State, Trigger, Context> transition = new Transition<>(State.A, State.B, Trigger.X, Context.M);
        actualTransition = null;
        stateRepresentation.addEntryAction(new Action2<Transition<State, Trigger, Context>, Object[]>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> t, Object[] a) {
                actualTransition = t;
            }
        });
        stateRepresentation.enter(transition);
        assertEquals(transition, actualTransition);
    }

    @Test
    public void UponLeaving_EnteringActionsNotExecuted() {
        StateRepresentation<State, Trigger, Context> stateRepresentation = CreateRepresentation(State.B);
        Transition<State, Trigger, Context> transition = new Transition<>(State.A, State.B, Trigger.X, Context.M);
        actualTransition = null;
        stateRepresentation.addEntryAction(new Action2<Transition<State, Trigger, Context>, Object[]>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> t, Object[] a) {
                actualTransition = t;
            }
        });
        stateRepresentation.exit(transition);
        assertNull(actualTransition);
    }

    @Test
    public void UponLeaving_LeavingActionsExecuted() {

        StateRepresentation<State, Trigger, Context> stateRepresentation = CreateRepresentation(State.A);
        Transition<State, Trigger, Context> transition = new Transition<>(State.A, State.B, Trigger.X, Context.M);
        actualTransition = null;
        stateRepresentation.addExitAction(new Action1<Transition<State, Trigger, Context>>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> t) {
                actualTransition = t;
            }
        });
        stateRepresentation.exit(transition);
        assertEquals(transition, actualTransition);
    }

    @Test
    public void UponEntering_LeavingActionsNotExecuted() {
        StateRepresentation<State, Trigger, Context> stateRepresentation = CreateRepresentation(State.A);
        Transition<State, Trigger, Context> transition = new Transition<>(State.A, State.B, Trigger.X, Context.M);
        actualTransition = null;
        stateRepresentation.addExitAction(new Action1<Transition<State, Trigger, Context>>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> t) {
                actualTransition = t;
            }
        });
        stateRepresentation.enter(transition);
        assertNull(actualTransition);
    }

    @Test
    public void IncludesUnderlyingState() {
        StateRepresentation<State, Trigger, Context> stateRepresentation = CreateRepresentation(State.B);
        assertTrue(stateRepresentation.includes(State.B));
    }

    @Test
    public void DoesNotIncludeUnrelatedState() {
        StateRepresentation<State, Trigger, Context> stateRepresentation = CreateRepresentation(State.B);
        assertFalse(stateRepresentation.includes(State.C));
    }

    @Test
    public void IncludesSubstate() {
        StateRepresentation<State, Trigger, Context> stateRepresentation = CreateRepresentation(State.B);
        stateRepresentation.addSubstate(CreateRepresentation(State.C));
        assertTrue(stateRepresentation.includes(State.C));
    }

    @Test
    public void DoesNotIncludeSuperstate() {
        StateRepresentation<State, Trigger, Context> stateRepresentation = CreateRepresentation(State.B);
        stateRepresentation.setSuperstate(CreateRepresentation(State.C));
        assertFalse(stateRepresentation.includes(State.C));
    }

    @Test
    public void IsIncludedInUnderlyingState() {
        StateRepresentation<State, Trigger, Context> stateRepresentation = CreateRepresentation(State.B);
        assertTrue(stateRepresentation.isIncludedIn(State.B));
    }

    @Test
    public void IsNotIncludedInUnrelatedState() {
        StateRepresentation<State, Trigger, Context> stateRepresentation = CreateRepresentation(State.B);
        assertFalse(stateRepresentation.isIncludedIn(State.C));
    }

    @Test
    public void IsNotIncludedInSubstate() {
        StateRepresentation<State, Trigger, Context> stateRepresentation = CreateRepresentation(State.B);
        stateRepresentation.addSubstate(CreateRepresentation(State.C));
        assertFalse(stateRepresentation.isIncludedIn(State.C));
    }

    @Test
    public void IsIncludedInSuperstate() {
        StateRepresentation<State, Trigger, Context> stateRepresentation = CreateRepresentation(State.B);
        stateRepresentation.setSuperstate(CreateRepresentation(State.C));
        assertTrue(stateRepresentation.isIncludedIn(State.C));
    }

    @Test
    public void WhenTransitioningFromSubToSuperstate_SubstateEntryActionsExecuted() {
        StateRepresentation<State, Trigger, Context> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger, Context> sub = CreateRepresentation(State.B);
        superState.addSubstate(sub);
        sub.setSuperstate(superState);

        sub.addEntryAction(new Action2<Transition<State, Trigger, Context>, Object[]>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> t, Object[] a) {
                executed = true;
            }
        });
        sub.enter(new Transition<>(superState.getUnderlyingState(), sub.getUnderlyingState(), Trigger.X, Context.M));
        assertTrue(executed);
    }

    @Test
    public void WhenTransitioningFromSubToSuperstate_SubstateExitActionsExecuted() {
        StateRepresentation<State, Trigger, Context> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger, Context> sub = CreateRepresentation(State.B);
        superState.addSubstate(sub);
        sub.setSuperstate(superState);

        executed = false;
        sub.addExitAction(new Action1<Transition<State, Trigger, Context>>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> arg1) {
                executed = true;
            }
        });
        sub.exit(new Transition<>(sub.getUnderlyingState(), superState.getUnderlyingState(), Trigger.X, Context.M));
        assertTrue(executed);
    }

    @Test
    public void WhenTransitioningToSuperFromSubstate_SuperEntryActionsNotExecuted() {
        StateRepresentation<State, Trigger, Context> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger, Context> sub = CreateRepresentation(State.B);
        superState.addSubstate(sub);
        sub.setSuperstate(superState);

        executed = false;
        superState.addEntryAction(new Action2<Transition<State, Trigger, Context>, Object[]>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> t, Object[] a) {
                executed = true;
            }
        });
        superState.enter(new Transition<>(superState.getUnderlyingState(), sub.getUnderlyingState(), Trigger.X, Context.M));
        assertFalse(executed);
    }

    @Test
    public void WhenTransitioningFromSuperToSubstate_SuperExitActionsNotExecuted() {
        StateRepresentation<State, Trigger, Context> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger, Context> sub = CreateRepresentation(State.B);
        superState.addSubstate(sub);
        sub.setSuperstate(superState);

        executed = false;
        superState.addExitAction(new Action1<Transition<State, Trigger, Context>>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> arg1) {
                executed = true;
            }
        });
        superState.exit(new Transition<>(superState.getUnderlyingState(), sub.getUnderlyingState(), Trigger.X, Context.M));
        assertFalse(executed);
    }

    @Test
    public void WhenEnteringSubstate_SuperEntryActionsExecuted() {
        StateRepresentation<State, Trigger, Context> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger, Context> sub = CreateRepresentation(State.B);
        superState.addSubstate(sub);
        sub.setSuperstate(superState);

        executed = false;
        superState.addEntryAction(new Action2<Transition<State, Trigger, Context>, Object[]>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> t, Object[] a) {
                executed = true;
            }
        });
        sub.enter(new Transition<>(State.C, sub.getUnderlyingState(), Trigger.X, Context.M));
        assertTrue(executed);
    }

    @Test
    public void WhenLeavingSubstate_SuperExitActionsExecuted() {
        StateRepresentation<State, Trigger, Context> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger, Context> sub = CreateRepresentation(State.B);
        superState.addSubstate(sub);
        sub.setSuperstate(superState);

        executed = false;
        superState.addExitAction(new Action1<Transition<State, Trigger, Context>>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> arg1) {
                executed = true;
            }
        });
        sub.exit(new Transition<>(sub.getUnderlyingState(), State.C, Trigger.X, Context.M));
        assertTrue(executed);
    }

    @Test
    public void EntryActionsExecuteInOrder() {
        final ArrayList<Integer> actual = new ArrayList<>();

        StateRepresentation<State, Trigger, Context> rep = CreateRepresentation(State.B);
        rep.addEntryAction(new Action2<Transition<State, Trigger, Context>, Object[]>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> arg1, Object[] arg2) {
                actual.add(0);

            }
        });
        rep.addEntryAction(new Action2<Transition<State, Trigger, Context>, Object[]>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> arg1, Object[] arg2) {
                actual.add(1);

            }
        });

        rep.enter(new Transition<>(State.A, State.B, Trigger.X, Context.M));

        assertEquals(2, actual.size());
        assertEquals(0, actual.get(0).intValue());
        assertEquals(1, actual.get(1).intValue());
    }

    @Test
    public void ExitActionsExecuteInOrder() {
        final List<Integer> actual = new ArrayList<>();

        StateRepresentation<State, Trigger, Context> rep = CreateRepresentation(State.B);
        rep.addExitAction(new Action1<Transition<State, Trigger, Context>>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> arg1) {
                actual.add(0);
            }
        });
        rep.addExitAction(new Action1<Transition<State, Trigger, Context>>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> arg1) {
                actual.add(1);
            }
        });

        rep.exit(new Transition<>(State.B, State.C, Trigger.X, Context.M));

        assertEquals(2, actual.size());
        assertEquals(0, actual.get(0).intValue());
        assertEquals(1, actual.get(1).intValue());
    }

    @Test
    public void WhenTransitionExists_TriggerCanBeFired() {
        StateRepresentation<State, Trigger, Context> rep = CreateRepresentation(State.B);
        assertFalse(rep.canHandle(Trigger.X, Context.M));
    }

    @Test
    public void WhenTransitionExistsButGuardConditionNotMet_TriggerCanBeFired() {
        StateRepresentation<State, Trigger, Context> rep = CreateRepresentation(State.B);
        rep.addTriggerBehaviour(new IgnoredTriggerBehaviour<State, Trigger, Context>(Trigger.X, IgnoredTriggerBehaviourTests.returnFalse));
        assertFalse(rep.canHandle(Trigger.X, Context.M));
    }

    @Test
    public void WhenTransitionDoesNotExist_TriggerCannotBeFired() {
        StateRepresentation<State, Trigger, Context> rep = CreateRepresentation(State.B);
        rep.addTriggerBehaviour(new IgnoredTriggerBehaviour<State, Trigger, Context>(Trigger.X, IgnoredTriggerBehaviourTests.returnTrue));
        assertTrue(rep.canHandle(Trigger.X, Context.M));
    }

    @Test
    public void WhenTransitionExistsInSupersate_TriggerCanBeFired() {
        StateRepresentation<State, Trigger, Context> rep = CreateRepresentation(State.B);
        rep.addTriggerBehaviour(new IgnoredTriggerBehaviour<State, Trigger, Context>(Trigger.X, IgnoredTriggerBehaviourTests.returnTrue));
        StateRepresentation<State, Trigger, Context> sub = CreateRepresentation(State.C);
        sub.setSuperstate(rep);
        rep.addSubstate(sub);
        assertTrue(sub.canHandle(Trigger.X, Context.M));
    }

    @Test
    public void WhenEnteringSubstate_SuperstateEntryActionsExecuteBeforeSubstate() {
        StateRepresentation<State, Trigger, Context> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger, Context> sub = CreateRepresentation(State.B);
        superState.addSubstate(sub);
        sub.setSuperstate(superState);

        order = 0;
        subOrder = 0;
        superOrder = 0;
        superState.addEntryAction(new Action2<Transition<State, Trigger, Context>, Object[]>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> arg1, Object[] arg2) {
                superOrder = order++;
            }
        });
        sub.addEntryAction(new Action2<Transition<State, Trigger, Context>, Object[]>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> arg1, Object[] arg2) {
                subOrder = order++;
            }
        });
        sub.enter(new Transition<>(State.C, sub.getUnderlyingState(), Trigger.X, Context.M));
        assertTrue(superOrder < subOrder);
    }

    @Test
    public void WhenExitingSubstate_SubstateEntryActionsExecuteBeforeSuperstate() {
        StateRepresentation<State, Trigger, Context> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger, Context> sub = CreateRepresentation(State.B);
        superState.addSubstate(sub);
        sub.setSuperstate(superState);

        order = 0;
        subOrder = 0;
        superOrder = 0;
        superState.addExitAction(new Action1<Transition<State, Trigger, Context>>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> arg1) {
                superOrder = order++;
            }
        });
        sub.addExitAction(new Action1<Transition<State, Trigger, Context>>() {

            @Override
            public void doIt(Transition<State, Trigger, Context> arg1) {
                subOrder = order++;
            }
        });
        sub.exit(new Transition<>(sub.getUnderlyingState(), State.C, Trigger.X, Context.M));
        assertTrue(subOrder < superOrder);
    }

    StateRepresentation<State, Trigger, Context> CreateRepresentation(State state) {
        return new StateRepresentation<>(state);
    }
}

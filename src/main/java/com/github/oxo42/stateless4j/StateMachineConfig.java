package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Func2;
import com.github.oxo42.stateless4j.transitions.TransitioningTriggerBehaviour;
import com.github.oxo42.stateless4j.triggers.TriggerBehaviour;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters3;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The state machine configuration.
 * Potentially reusable, if defined actions are generic enough or are only applied to the provided context.
 *
 * @param <S> The type used to represent the states
 * @param <T> The type used to represent the triggers that cause state transitions
 * @param <C> The type used to represent the context in which the state machine is being applied
 */
public class StateMachineConfig<S, T, C> {

    private final Map<S, StateRepresentation<S, T, C>> stateConfiguration = new HashMap<>();
    private final Map<T, TriggerWithParameters<S, T>> triggerConfiguration = new HashMap<>();
    
    /**
     * Return StateRepresentation for the specified state. May return null.
     *
     * @param state The state
     * @return StateRepresentation for the specified state, or null.
     */
    public StateRepresentation<S, T, C> getRepresentation(S state) {
        return stateConfiguration.get(state);
    }

    /**
     * Return StateRepresentation for the specified state. Creates representation if it does not exist.
     *
     * @param state The state
     * @return StateRepresentation for the specified state.
     */
    private StateRepresentation<S, T, C> getOrCreateRepresentation(S state) {
        StateRepresentation<S, T, C> result = stateConfiguration.get(state);
        if (result == null) {
            result = new StateRepresentation<>(state);
            stateConfiguration.put(state, result);
        }

        return result;
    }

    public TriggerWithParameters<S, T> getTriggerConfiguration(T trigger) {
        return triggerConfiguration.get(trigger);
    }

    /**
     * Begin configuration of the entry/exit actions and allowed transitions
     * when the state machine is in a particular state
     *
     * @param state The state to configure
     * @return A configuration object through which the state can be configured
     */
    public StateConfiguration<S, T, C> configure(S state) {
        return new StateConfiguration<>(getOrCreateRepresentation(state), new Func2<S, StateRepresentation<S, T, C>>() {

            @Override
            public StateRepresentation<S, T, C> call(S arg0) {
                return getOrCreateRepresentation(arg0);
            }
        });
    }

    private void saveTriggerConfiguration(TriggerWithParameters<S, T> trigger) {
        if (triggerConfiguration.containsKey(trigger.getTrigger())) {
            throw new IllegalStateException("Parameters for the trigger '" + trigger + "' have already been configured.");
        }

        triggerConfiguration.put(trigger.getTrigger(), trigger);
    }

    /**
     * Specify the arguments that must be supplied when a specific trigger is fired
     *
     * @param trigger The underlying trigger value
     * @param classe  Class argument
     * @param <TArg>  Type of the first trigger argument
     * @return An object that can be passed to the fire() method in order to fire the parameterised trigger
     */
    public <TArg> TriggerWithParameters1<TArg, S, T> setTriggerParameters(T trigger, Class<TArg> classe) {
        TriggerWithParameters1<TArg, S, T> configuration = new TriggerWithParameters1<>(trigger, classe);
        saveTriggerConfiguration(configuration);
        return configuration;
    }

    /**
     * Specify the arguments that must be supplied when a specific trigger is fired
     *
     * @param trigger The underlying trigger value
     * @param classe0 Class argument
     * @param classe1 Class argument
     * @param <TArg0> Type of the first trigger argument
     * @param <TArg1> Type of the second trigger argument
     * @return An object that can be passed to the fire() method in order to fire the parameterised trigger
     */
    public <TArg0, TArg1> TriggerWithParameters2<TArg0, TArg1, S, T> setTriggerParameters(T trigger, Class<TArg0> classe0, Class<TArg1> classe1) {
        TriggerWithParameters2<TArg0, TArg1, S, T> configuration = new TriggerWithParameters2<>(trigger, classe0, classe1);
        saveTriggerConfiguration(configuration);
        return configuration;
    }

    /**
     * Specify the arguments that must be supplied when a specific trigger is fired
     *
     * @param trigger The underlying trigger value
     * @param classe0 Class argument
     * @param classe1 Class argument
     * @param classe2 Class argument
     * @param <TArg0> Type of the first trigger argument
     * @param <TArg1> Type of the second trigger argument
     * @param <TArg2> Type of the third trigger argument
     * @return An object that can be passed to the fire() method in order to fire the parameterised trigger
     */
    public <TArg0, TArg1, TArg2> TriggerWithParameters3<TArg0, TArg1, TArg2, S, T> setTriggerParameters(T trigger, Class<TArg0> classe0, Class<TArg1> classe1, Class<TArg2> classe2) {
        TriggerWithParameters3<TArg0, TArg1, TArg2, S, T> configuration = new TriggerWithParameters3<>(trigger, classe0, classe1, classe2);
        saveTriggerConfiguration(configuration);
        return configuration;
    }

    public void generateDotFileInto(final OutputStream dotFile) throws IOException {
        try (OutputStreamWriter w = new OutputStreamWriter(dotFile, "UTF-8")) {
            PrintWriter writer = new PrintWriter(w);
            writer.write("digraph G {\n");
            OutVar<S> destination = new OutVar<>();
            for (Map.Entry<S, StateRepresentation<S, T, C>> entry : this.stateConfiguration.entrySet()) {
                Map<T, List<TriggerBehaviour<S, T, C>>> behaviours = entry.getValue().getTriggerBehaviours();
                for (List<TriggerBehaviour<S, T, C>> behaviour : behaviours.values()) {
                    for (TriggerBehaviour<S, T, C> triggerBehaviour : behaviour) {
                        if (triggerBehaviour instanceof TransitioningTriggerBehaviour) {
                            destination.set(null);
                            triggerBehaviour.resultsInTransitionFrom(null, null, null, destination);
                            writer.write(String.format("\t%s -> %s;\n", entry.getKey(), destination));
                        }
                    }
                }
            }
            writer.write("}");
        }
    }

}

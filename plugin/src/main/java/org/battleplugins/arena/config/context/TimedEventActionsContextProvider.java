package org.battleplugins.arena.config.context;

import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.config.SingularValueParser;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.event.action.EventActionType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Context provider for timed event actions
 */
public class TimedEventActionsContextProvider implements ContextProvider<List<EventAction>> {

    @Override
    public List<EventAction> provideInstance(
            @Nullable Path sourceFile,
            ArenaOption option,
            Class<?> type,
            ConfigurationSection configuration,
            String name,
            @Nullable Object scope) throws ParseException {

        if (!List.class.isAssignableFrom(type)) {
            throw new ParseException("Expected " + type.getName() + " to be assignable from List when loading timed event actions!")
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Section", configuration.getName())
                    .cause(ParseException.Cause.INVALID_TYPE)
                    .type(EventAction.class)
                    .sourceFile(sourceFile);
        }

        List<String> actions = configuration.getStringList(name);
        if (option.required() && actions.isEmpty()) {
            throw new ParseException("Required action list " + name + " is empty in configuration section " + configuration.getName())
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Section", configuration.getName())
                    .cause(ParseException.Cause.MISSING_VALUE)
                    .type(EventAction.class)
                    .userError()
                    .sourceFile(sourceFile);
        }

        List<EventAction> eventActions = new ArrayList<>();
        for (String actionStr : actions) {
            try {
                SingularValueParser.ArgumentBuffer buffer = SingularValueParser.parseNamed(
                        actionStr,
                        SingularValueParser.BraceStyle.CURLY,
                        ';'
                );

                if (!buffer.hasNext()) {
                    throw new ParseException("No actions found for EventAction")
                            .context("Section", configuration.getName())
                            .context("Provided action", actionStr)
                            .cause(ParseException.Cause.INVALID_VALUE)
                            .type(EventActionType.class)
                            .userError()
                            .sourceFile(sourceFile);
                }

                SingularValueParser.Argument root = buffer.pop();
                if (!root.key().equals("root")) {
                    throw new ParseException("Expected root key for EventActionType, got " + root.key())
                            .context("Section", configuration.getName())
                            .context("Provided key", root.key())
                            .cause(ParseException.Cause.INTERNAL_ERROR)
                            .type(EventActionType.class)
                            .sourceFile(sourceFile);
                }

                EventActionType<?> actionType = EventActionType.get(root.value());
                if (actionType == null) {
                    throw new ParseException("Unrecognized event action detected (" + root.value() + ") when loading configuration section " + configuration.getName())
                            .context("Section", configuration.getName())
                            .context("Provided action", root.value())
                            .context("Valid actions", EventActionType.values().stream().map(EventActionType::getName).toList().toString())
                            .cause(ParseException.Cause.INVALID_VALUE)
                            .type(EventActionType.class)
                            .userError()
                            .sourceFile(sourceFile);
                }

                Map<String, String> params = new LinkedHashMap<>();
                while (buffer.hasNext()) {
                    SingularValueParser.Argument argument = buffer.pop();
                    params.put(argument.key(), argument.value());
                }

                EventAction action = actionType.create(params);
                eventActions.add(action);

            } catch (ParseException e) {
                throw e.sourceFile(sourceFile);
            } catch (IllegalArgumentException e) {
                throw new ParseException("Failed to create event action with params", e)
                        .context("Section", configuration.getName())
                        .context("Action string", actionStr)
                        .context("Reason", e.getMessage())
                        .cause(ParseException.Cause.INTERNAL_ERROR)
                        .type(EventActionType.class)
                        .userError()
                        .sourceFile(sourceFile);
            }
        }

        return eventActions;
    }
}
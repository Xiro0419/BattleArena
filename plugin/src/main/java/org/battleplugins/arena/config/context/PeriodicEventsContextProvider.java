package org.battleplugins.arena.config.context;

import org.battleplugins.arena.competition.event.TimedEventManager;
import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.ParseException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 周期性事件配置解析器
 */
public class PeriodicEventsContextProvider implements ContextProvider<Map<String, TimedEventManager.PeriodicEvent>> {

    @Override
    public Map<String, TimedEventManager.PeriodicEvent> provideInstance(
            @Nullable Path sourceFile,
            ArenaOption option,
            Class<?> type,
            ConfigurationSection configuration,
            String name,
            @Nullable Object scope) throws ParseException {

        if (!Map.class.isAssignableFrom(type)) {
            throw new ParseException("Expected " + type.getName() + " to be assignable from Map when loading periodic events!")
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Section", configuration.getName())
                    .cause(ParseException.Cause.INVALID_TYPE)
                    .type(TimedEventManager.PeriodicEvent.class)
                    .sourceFile(sourceFile);
        }

        ConfigurationSection configurationSection = configuration.getConfigurationSection(name);
        if (option.required() && configurationSection == null) {
            throw new ParseException("Failed to find configuration section " + name + " in configuration section " + configuration.getName())
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Section", configuration.getName())
                    .cause(ParseException.Cause.MISSING_SECTION)
                    .type(TimedEventManager.PeriodicEvent.class)
                    .userError()
                    .sourceFile(sourceFile);
        } else if (!option.required() && configurationSection == null) {
            return Map.of();
        }

        Map<String, TimedEventManager.PeriodicEvent> periodicEvents = new LinkedHashMap<>();
        for (String key : configurationSection.getKeys(false)) {
            ConfigurationSection section = configurationSection.getConfigurationSection(key);
            if (section == null) {
                throw new ParseException("Failed to find configuration section " + key + " in configuration section " + configurationSection.getName())
                        .context("Type", type.getName())
                        .context("Name", key)
                        .context("Section", configurationSection.getName())
                        .cause(ParseException.Cause.MISSING_SECTION)
                        .type(TimedEventManager.PeriodicEvent.class)
                        .userError()
                        .sourceFile(sourceFile);
            }

            try {
                TimedEventManager.PeriodicEvent event = ArenaConfigParser.newInstance(
                        sourceFile,
                        TimedEventManager.PeriodicEvent.class,
                        section,
                        scope
                );
                periodicEvents.put(key, event);
            } catch (ParseException e) {
                throw e.sourceFile(sourceFile);
            }
        }

        return periodicEvents;
    }
}
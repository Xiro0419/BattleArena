package org.battleplugins.arena.competition.event;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.context.TimedEventActionsContextProvider;
import org.battleplugins.arena.event.action.EventAction;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 时间触发事件系统
 */
public class TimedEventManager {
    private final Arena arena;
    private final LiveCompetition<?> competition;
    private final Map<String, TimedEvent> timedEvents = new HashMap<>();
    private final Map<String, PeriodicEvent> periodicEvents = new HashMap<>();
    private final List<BukkitTask> activeTasks = new ArrayList<>();
    private long phaseStartTime = -1;

    public TimedEventManager(Arena arena, LiveCompetition<?> competition) {
        this.arena = arena;
        this.competition = competition;
    }

    /**
     * 启动时间事件系统
     */
    public void start() {
        arena.getPlugin().info("=== TimedEventManager.start() called ===");
        arena.getPlugin().info("timedEvents size: {}", timedEvents.size());
        arena.getPlugin().info("periodicEvents size: {}", periodicEvents.size());

        this.phaseStartTime = System.currentTimeMillis();

        for (Map.Entry<String, TimedEvent> entry : timedEvents.entrySet()) {
            arena.getPlugin().info("Processing timed event: {}", entry.getKey());
            TimedEvent event = entry.getValue();

            if (event == null) {
                arena.getPlugin().warn("Event is NULL for key: {}", entry.getKey());
                continue;
            }

            if (event.getTriggerTime() == null) {
                arena.getPlugin().warn("TriggerTime is NULL for event: {}", entry.getKey());
                continue;
            }

            long delayTicks = event.triggerTime.toMillis() / 50L;
            arena.getPlugin().info("Scheduling '{}' in {} ticks", entry.getKey(), delayTicks);
        }

        // 启动所有定时事件
        for (Map.Entry<String, TimedEvent> entry : timedEvents.entrySet()) {
            TimedEvent event = entry.getValue();
            long delayTicks = event.triggerTime.toMillis() / 50L;

            BukkitTask task = Bukkit.getScheduler().runTaskLater(
                    arena.getPlugin(),
                    () -> executeTimedEvent(event),
                    delayTicks
            );
            activeTasks.add(task);
        }

        // 启动所有周期性事件
        for (Map.Entry<String, PeriodicEvent> entry : periodicEvents.entrySet()) {
            PeriodicEvent event = entry.getValue();
            long delayTicks = event.startDelay.toMillis() / 50L;
            long intervalTicks = event.interval.toMillis() / 50L;

            BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                    arena.getPlugin(),
                    () -> executePeriodicEvent(event),
                    delayTicks,
                    intervalTicks
            );
            activeTasks.add(task);
        }
    }

    /**
     * 停止所有事件
     */
    public void stop() {
        for (BukkitTask task : activeTasks) {
            if (task != null) {
                task.cancel();
            }
        }
        activeTasks.clear();
        phaseStartTime = -1;
    }

    /**
     * 注册定时事件
     */
    public void registerTimedEvent(String id, TimedEvent event) {
        timedEvents.put(id, event);
    }

    /**
     * 注册周期性事件
     */
    public void registerPeriodicEvent(String id, PeriodicEvent event) {
        periodicEvents.put(id, event);
    }

    /**
     * 执行定时事件
     */
    private void executeTimedEvent(TimedEvent event) {
        if (competition.getPlayers().isEmpty()) {
            return;
        }

        arena.getPlugin().debug("Executing timed event at {}", event.triggerTime);

        for (EventAction action : event.actions) {
            for (ArenaPlayer player : competition.getPlayers()) {
                try {
                    action.preProcess(arena, competition, player);
                    action.call(player, player);
                    action.postProcess(arena, competition, player);
                } catch (Exception e) {
                    arena.getPlugin().error("Error executing timed event action", e);
                }
            }
        }
    }

    /**
     * 执行周期性事件
     */
    private void executePeriodicEvent(PeriodicEvent event) {
        if (competition.getPlayers().isEmpty()) {
            return;
        }

        arena.getPlugin().debug("Executing periodic event with interval {}", event.interval);

        for (EventAction action : event.actions) {
            for (ArenaPlayer player : competition.getPlayers()) {
                try {
                    action.preProcess(arena, competition, player);
                    action.call(player, player);
                    action.postProcess(arena, competition, player);
                } catch (Exception e) {
                    arena.getPlugin().error("Error executing periodic event action", e);
                }
            }
        }
    }

    /**
     * 获取距离阶段开始的时间
     */
    public Duration getTimeSincePhaseStart() {
        if (phaseStartTime == -1) {
            return Duration.ZERO;
        }
        return Duration.ofMillis(System.currentTimeMillis() - phaseStartTime);
    }

    /**
     * 定时事件类
     */
    public static class TimedEvent {
        @ArenaOption(name = "trigger-time", description = "觸發時間", required = true)
        private Duration triggerTime;

        @ArenaOption(name = "trigger-from", description = "從哪個階段開始計算")
        private String triggerFrom = "ingame-start";

        @ArenaOption(
                name = "events",
                description = "要執行的事件",
                required = true,
                contextProvider = TimedEventActionsContextProvider.class
        )
        private List<EventAction> actions;

        public TimedEvent() {
        }

        public TimedEvent(Duration triggerTime, List<EventAction> actions) {
            this.triggerTime = triggerTime;
            this.actions = actions;
        }

        public Duration getTriggerTime() {
            return triggerTime;
        }

        public List<EventAction> getActions() {
            return actions;
        }
    }

    /**
     * 周期性事件类
     */
    public static class PeriodicEvent {
        @ArenaOption(name = "interval", description = "执行间隔", required = true)
        private Duration interval;

        @ArenaOption(name = "start-delay", description = "开始延迟")
        private Duration startDelay = Duration.ZERO;

        @ArenaOption(name = "active-phases", description = "激活阶段")
        private List<String> activePhases = List.of("ingame");

        @ArenaOption(name = "events", description = "要执行的事件", required = true)
        private List<EventAction> actions;

        public PeriodicEvent() {
        }

        public PeriodicEvent(Duration interval, Duration startDelay, List<EventAction> actions) {
            this.interval = interval;
            this.startDelay = startDelay;
            this.actions = actions;
        }

        public Duration getInterval() {
            return interval;
        }

        public Duration getStartDelay() {
            return startDelay;
        }

        public List<EventAction> getActions() {
            return actions;
        }
    }
}
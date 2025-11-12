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
import java.util.*;

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

    // 新增：事件执行状态追踪
    private final Set<String> executedTimedEvents = new HashSet<>();
    private final Map<String, EventExecutionInfo> eventExecutionInfo = new HashMap<>();

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
        this.executedTimedEvents.clear();
        this.eventExecutionInfo.clear();

        // 启动所有定时事件
        for (Map.Entry<String, TimedEvent> entry : timedEvents.entrySet()) {
            String eventId = entry.getKey();
            TimedEvent event = entry.getValue();
            long delayTicks = event.triggerTime.toMillis() / 50L;

            // 记录事件信息
            EventExecutionInfo info = new EventExecutionInfo(
                    EventType.TIMED,
                    event.triggerTime,
                    phaseStartTime + event.triggerTime.toMillis(),
                    false
            );
            eventExecutionInfo.put(eventId, info);

            BukkitTask task = Bukkit.getScheduler().runTaskLater(
                    arena.getPlugin(),
                    () -> executeTimedEvent(eventId, event),
                    delayTicks
            );
            activeTasks.add(task);
        }

        // 启动所有周期性事件
        for (Map.Entry<String, PeriodicEvent> entry : periodicEvents.entrySet()) {
            String eventId = entry.getKey();
            PeriodicEvent event = entry.getValue();
            long delayTicks = event.startDelay.toMillis() / 50L;
            long intervalTicks = event.interval.toMillis() / 50L;

            // 记录事件信息
            EventExecutionInfo info = new EventExecutionInfo(
                    EventType.PERIODIC,
                    event.interval,
                    phaseStartTime + event.startDelay.toMillis(),
                    false
            );
            info.setInterval(event.interval);
            info.setStartDelay(event.startDelay);
            eventExecutionInfo.put(eventId, info);

            BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                    arena.getPlugin(),
                    () -> executePeriodicEvent(eventId, event),
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
        executedTimedEvents.clear();
        eventExecutionInfo.clear();
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
    private void executeTimedEvent(String eventId, TimedEvent event) {
        if (competition.getPlayers().isEmpty()) {
            return;
        }

        arena.getPlugin().debug("Executing timed event: {}", eventId);

        // 标记为已执行
        executedTimedEvents.add(eventId);
        EventExecutionInfo info = eventExecutionInfo.get(eventId);
        if (info != null) {
            info.setExecuted(true);
            info.recordExecution();
        }

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
    private void executePeriodicEvent(String eventId, PeriodicEvent event) {
        if (competition.getPlayers().isEmpty()) {
            return;
        }

        arena.getPlugin().debug("Executing periodic event: {}", eventId);

        // 更新执行信息
        EventExecutionInfo info = eventExecutionInfo.get(eventId);
        if (info != null) {
            info.setExecuted(true);
            info.recordExecution();
        }

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
     * 获取所有定时事件信息
     */
    public Map<String, TimedEvent> getTimedEvents() {
        return Collections.unmodifiableMap(timedEvents);
    }

    /**
     * 获取所有周期性事件信息
     */
    public Map<String, PeriodicEvent> getPeriodicEvents() {
        return Collections.unmodifiableMap(periodicEvents);
    }

    /**
     * 检查定时事件是否已执行
     */
    public boolean isTimedEventExecuted(String eventId) {
        return executedTimedEvents.contains(eventId);
    }

    /**
     * 获取事件执行信息
     */
    public EventExecutionInfo getEventExecutionInfo(String eventId) {
        return eventExecutionInfo.get(eventId);
    }

    /**
     * 获取所有事件执行信息
     */
    public Map<String, EventExecutionInfo> getAllEventExecutionInfo() {
        return Collections.unmodifiableMap(eventExecutionInfo);
    }

    /**
     * 获取下一个即将触发的定时事件
     */
    public Map.Entry<String, TimedEvent> getNextTimedEvent() {
        long currentTime = System.currentTimeMillis();
        return timedEvents.entrySet().stream()
                .filter(entry -> !executedTimedEvents.contains(entry.getKey()))
                .min(Comparator.comparing(entry ->
                        phaseStartTime + entry.getValue().triggerTime.toMillis() - currentTime))
                .orElse(null);
    }

    /**
     * 获取当前活跃的周期性事件
     */
    public List<String> getActivePeriodicEvents() {
        List<String> active = new ArrayList<>();
        for (Map.Entry<String, PeriodicEvent> entry : periodicEvents.entrySet()) {
            EventExecutionInfo info = eventExecutionInfo.get(entry.getKey());
            if (info != null && info.isExecuted()) {
                active.add(entry.getKey());
            }
        }
        return active;
    }

    /**
     * 获取阶段开始时间
     */
    public long getPhaseStartTime() {
        return phaseStartTime;
    }

    /**
     * 事件类型枚举
     */
    public enum EventType {
        TIMED,
        PERIODIC
    }

    /**
     * 事件执行信息类
     */
    public static class EventExecutionInfo {
        private final EventType type;
        private final Duration triggerTime;
        private final long scheduledExecutionTime;
        private boolean executed;
        private long lastExecutionTime = -1;
        private int executionCount = 0;

        // 周期性事件特有
        private Duration interval;
        private Duration startDelay;

        public EventExecutionInfo(EventType type, Duration triggerTime,
                                  long scheduledExecutionTime, boolean executed) {
            this.type = type;
            this.triggerTime = triggerTime;
            this.scheduledExecutionTime = scheduledExecutionTime;
            this.executed = executed;
        }

        public void recordExecution() {
            this.lastExecutionTime = System.currentTimeMillis();
            this.executionCount++;
        }

        public EventType getType() {
            return type;
        }

        public Duration getTriggerTime() {
            return triggerTime;
        }

        public long getScheduledExecutionTime() {
            return scheduledExecutionTime;
        }

        public boolean isExecuted() {
            return executed;
        }

        public void setExecuted(boolean executed) {
            this.executed = executed;
        }

        public long getLastExecutionTime() {
            return lastExecutionTime;
        }

        public int getExecutionCount() {
            return executionCount;
        }

        public Duration getInterval() {
            return interval;
        }

        public void setInterval(Duration interval) {
            this.interval = interval;
        }

        public Duration getStartDelay() {
            return startDelay;
        }

        public void setStartDelay(Duration startDelay) {
            this.startDelay = startDelay;
        }

        public long getNextExecutionTime() {
            if (type == EventType.PERIODIC && lastExecutionTime > 0 && interval != null) {
                return lastExecutionTime + interval.toMillis();
            }
            return scheduledExecutionTime;
        }
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
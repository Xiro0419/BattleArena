package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.event.TimedEventManager;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 启动时间事件系统动作
 */
public class StartTimedEventsAction extends EventAction {
    private static final Map<Competition<?>, TimedEventManager> eventManagers = new WeakHashMap<>();

    public StartTimedEventsAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        // 不需要在玩家级别处理
    }

    @Override
    public void postProcess(Arena arena, Competition<?> competition, Resolvable resolvable) {
        if (!(competition instanceof LiveCompetition<?> liveCompetition)) {
            arena.getPlugin().warn("Competition is not a LiveCompetition, cannot start timed events");
            return;
        }

        try {
            // 检查是否已经创建了事件管理器
            if (!eventManagers.containsKey(competition)) {
                TimedEventManager manager = new TimedEventManager(arena, liveCompetition);

                // 从 Arena 配置加载定时事件
                Map<String, TimedEventManager.TimedEvent> timedEvents = arena.getTimedEvents();
                arena.getPlugin().info("Loading {} timed events from arena configuration", timedEvents.size());
                for (Map.Entry<String, TimedEventManager.TimedEvent> entry : timedEvents.entrySet()) {
                    manager.registerTimedEvent(entry.getKey(), entry.getValue());
                    arena.getPlugin().info("Registered timed event: {}", entry.getKey());
                }

                // 从 Arena 配置加载周期性事件
                Map<String, TimedEventManager.PeriodicEvent> periodicEvents = arena.getPeriodicEvents();
                arena.getPlugin().info("Loading {} periodic events from arena configuration", periodicEvents.size());
                for (Map.Entry<String, TimedEventManager.PeriodicEvent> entry : periodicEvents.entrySet()) {
                    manager.registerPeriodicEvent(entry.getKey(), entry.getValue());
                    arena.getPlugin().info("Registered periodic event: {}", entry.getKey());
                }

                eventManagers.put(competition, manager);
                arena.getPlugin().info("Created TimedEventManager for competition");
            }

            // 启动事件系统
            TimedEventManager manager = eventManagers.get(competition);
            manager.start();

            arena.getPlugin().info("Started timed event system successfully");
        } catch (Exception e) {
            arena.getPlugin().error("Error starting timed events", e);
        }
    }

    /**
     * 获取竞赛的事件管理器
     */
    public static TimedEventManager getEventManager(Competition<?> competition) {
        return eventManagers.get(competition);
    }
}
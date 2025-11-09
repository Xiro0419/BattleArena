package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;

/**
 * 启动周期事件系统
 * 注意：周期事件实际上在 StartTimedEventsAction 中一起启动
 */
public class StartPeriodicEventsAction extends EventAction {

    public StartPeriodicEventsAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        // 不需要在玩家级别处理
    }

    @Override
    public void postProcess(Arena arena, Competition<?> competition, Resolvable resolvable) {
        // 周期事件在 StartTimedEventsAction 中已经一起启动
        arena.getPlugin().debug("Periodic events are managed by TimedEventManager");
    }
}
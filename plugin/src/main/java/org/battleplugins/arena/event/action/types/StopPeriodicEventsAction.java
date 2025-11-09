package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;

public class StopPeriodicEventsAction extends EventAction {

    public StopPeriodicEventsAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        // 周期事件通过 TimedEventManager 统一管理
    }

    @Override
    public void postProcess(Arena arena, Competition<?> competition, Resolvable resolvable) {
        // 周期事件在 stop-timed-events 时一起停止
        arena.getPlugin().debug("Periodic events stopped with timed events");
    }
}
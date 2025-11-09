package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.event.TimedEventManager;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;

/**
 * 停止时间事件系统动作
 */
public class StopTimedEventsAction extends EventAction {

    public StopTimedEventsAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        // 不需要在玩家级别处理
    }

    @Override
    public void postProcess(Arena arena, Competition<?> competition, Resolvable resolvable) {
        TimedEventManager manager = StartTimedEventsAction.getEventManager(competition);
        if (manager != null) {
            manager.stop();
            arena.getPlugin().debug("Stopped timed event system");
        }
    }
}
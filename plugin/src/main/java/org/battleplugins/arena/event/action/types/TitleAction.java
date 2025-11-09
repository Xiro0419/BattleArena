package org.battleplugins.arena.event.action.types;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.config.DurationParser;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.time.Duration;
import java.util.Map;

/**
 * 显示标题动作
 */
public class TitleAction extends EventAction {
    private static final String TITLE_KEY = "title";
    private static final String SUBTITLE_KEY = "subtitle";
    private static final String FADE_IN_KEY = "fade-in";
    private static final String STAY_KEY = "stay";
    private static final String FADE_OUT_KEY = "fade-out";

    public TitleAction(Map<String, String> params) {
        super(params, TITLE_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        String titleText = this.get(TITLE_KEY);
        String subtitleText = this.getOrDefault(SUBTITLE_KEY, "");

        // 解析标题和副标题
        Component title = resolvable.resolve().resolveToComponent(
                MiniMessage.miniMessage().deserialize(titleText)
        );
        Component subtitle = Component.empty();
        if (!subtitleText.isEmpty()) {
            subtitle = resolvable.resolve().resolveToComponent(
                    MiniMessage.miniMessage().deserialize(subtitleText)
            );
        }

        // 解析时间参数
        Duration fadeIn = parseDuration(this.getOrDefault(FADE_IN_KEY, "0.5s"));
        Duration stay = parseDuration(this.getOrDefault(STAY_KEY, "3s"));
        Duration fadeOut = parseDuration(this.getOrDefault(FADE_OUT_KEY, "1s"));

        Times times = Times.times(fadeIn, stay, fadeOut);
        Title titleObject = Title.title(title, subtitle, times);

        arenaPlayer.getPlayer().showTitle(titleObject);
    }

    private Duration parseDuration(String durationStr) {
        try {
            return DurationParser.deserializeSingular(durationStr);
        } catch (ParseException e) {
            // 默认值
            return Duration.ofSeconds(1);
        }
    }
}
package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.config.DurationParser;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.util.Map;

/**
 * 应用药水效果动作
 */
public class ApplyEffectAction extends EventAction {
    private static final String EFFECT_KEY = "effect";
    private static final String DURATION_KEY = "duration";
    private static final String AMPLIFIER_KEY = "amplifier";
    private static final String AMBIENT_KEY = "ambient";
    private static final String PARTICLES_KEY = "particles";

    public ApplyEffectAction(Map<String, String> params) {
        super(params, EFFECT_KEY, DURATION_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        String effectName = this.get(EFFECT_KEY);
        String durationStr = this.get(DURATION_KEY);
        int amplifier = Integer.parseInt(this.getOrDefault(AMPLIFIER_KEY, "0"));
        boolean ambient = Boolean.parseBoolean(this.getOrDefault(AMBIENT_KEY, "true"));
        boolean particles = Boolean.parseBoolean(this.getOrDefault(PARTICLES_KEY, "true"));

        // 解析效果类型
        PotionEffectType effectType = PotionEffectType.getByKey(
                NamespacedKey.minecraft(effectName.toLowerCase())
        );

        if (effectType == null) {
            arenaPlayer.getArena().getPlugin().warn(
                    "Invalid potion effect type: {}", effectName
            );
            return;
        }

        // 解析持续时间
        Duration duration;
        try {
            duration = DurationParser.deserializeSingular(durationStr);
        } catch (ParseException e) {
            arenaPlayer.getArena().getPlugin().warn(
                    "Invalid duration format: {}", durationStr
            );
            return;
        }

        // 创建并应用效果
        int durationTicks = (int) (duration.toMillis() / 50);
        PotionEffect effect = new PotionEffect(
                effectType,
                durationTicks,
                amplifier,
                ambient,
                particles
        );

        arenaPlayer.getPlayer().addPotionEffect(effect);
    }
}
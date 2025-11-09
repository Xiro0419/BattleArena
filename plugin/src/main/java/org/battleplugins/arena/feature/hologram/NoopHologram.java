package org.battleplugins.arena.feature.hologram;

import net.kyori.adventure.text.Component;
import org.battleplugins.arena.competition.LiveCompetition;
import org.bukkit.Location;

import java.util.List;

class NoopHologram implements Hologram {
    private final LiveCompetition<?> competition;
    private final Location location;
    
    public NoopHologram(LiveCompetition<?> competition, Location location) {
        this.competition = competition;
        this.location = location;
    }

    @Override
    public LiveCompetition<?> getCompetition() {
        return this.competition;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public List<Component> getLines() {
        return List.of();
    }

    @Override
    public void setLines(Component... lines) {
        // no-op
    }

    @Override
    public void addLine(Component line) {
        // no-op
    }

    @Override
    public void removeLine(int index) {
        // no-op
    }
}

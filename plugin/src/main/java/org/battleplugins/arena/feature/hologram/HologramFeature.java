package org.battleplugins.arena.feature.hologram;

import net.kyori.adventure.text.Component;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.feature.FeatureInstance;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Main entrypoint for holograms. Implementing plugins
 * should implement this interface to provide hologram
 * support.
 */
@ApiStatus.Experimental
public interface HologramFeature extends FeatureInstance {

    /**
     * Creates a {@link Hologram} at the given location with the given lines.
     *
     * @param competition the competition to create the hologram for
     * @param location the location to create the hologram at
     * @param lines the lines of the hologram
     * @return the created hologram
     */
    Hologram createHologram(LiveCompetition<?> competition, Location location, Component... lines);

    /**
     * Removes a {@link Hologram} from a competition.
     *
     * @param hologram the hologram to remove
     */
    void removeHologram(Hologram hologram);

    /**
     * Gets all holograms created by this feature.
     *
     * @return all holograms created by this feature
     */
    List<Hologram> getHolograms();

    @Override
    default Listener createListener() {
        return new HologramListener(this);
    }
}

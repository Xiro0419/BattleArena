package org.battleplugins.arena.feature.hologram;

import net.kyori.adventure.text.Component;
import org.battleplugins.arena.competition.LiveCompetition;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Represents a hologram in an {@link LiveCompetition}.
 * <p>
 * Holograms are tied to an active competition, and will
 * automatically be removed when the competition ends.
 */
@ApiStatus.Experimental
public interface Hologram {

    /**
     * Gets the {@link LiveCompetition} this hologram is associated with.
     *
     * @return the live competition this hologram is associated with
     */
    LiveCompetition<?> getCompetition();

    /**
     * Gets the {@link Location} of this hologram.
     *
     * @return the location of this hologram
     */
    Location getLocation();

    /**
     * Gets the lines of this hologram.
     *
     * @return the lines of this hologram
     */
    List<Component> getLines();

    /**
     * Sets the lines of this hologram.
     *
     * @param lines the lines to set
     */
    void setLines(Component... lines);

    /**
     * Adds a line to the hologram.
     *
     * @param line the line to add
     */
    void addLine(Component line);

    /**
     * Removes a line from the hologram.
     *
     * @param index the index of the line to remove
     */
    void removeLine(int index);
}

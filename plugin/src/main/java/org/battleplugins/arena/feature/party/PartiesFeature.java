package org.battleplugins.arena.feature.party;

import org.battleplugins.arena.feature.FeatureInstance;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Main entrypoint for parties. Implementing plugins
 * should implement this interface to provide party
 * support.
 */
@ApiStatus.Experimental
public interface PartiesFeature extends FeatureInstance {

    /**
     * Gets the party of the given player UUID.
     *
     * @param uuid the uuid of the party
     * @return the party of the given player UUID
     */
    @Nullable
    Party getParty(UUID uuid);
}

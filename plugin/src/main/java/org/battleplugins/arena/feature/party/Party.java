package org.battleplugins.arena.feature.party;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Represents a party.
 */
public interface Party {

    /**
     * Gets the leader of the party.
     *
     * @return the leader of the party
     */
    @Nullable
    PartyMember getLeader();

    /**
     * Gets the members of the party.
     *
     * @return the members of the party
     */
    Set<PartyMember> getMembers();
}

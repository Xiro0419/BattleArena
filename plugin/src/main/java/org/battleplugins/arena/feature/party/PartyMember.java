package org.battleplugins.arena.feature.party;

import java.util.UUID;

/**
 * Represents a party member.
 */
public interface PartyMember {

    /**
     * Gets the name of the party member.
     *
     * @return the name of the party member
     */
    String getName();

    /**
     * Gets the unique id of the party member.
     *
     * @return the unique id of the party member
     */
    UUID getUniqueId();

    /**
     * Gets the party the member is in.
     *
     * @return the party the member is in
     */
    Party getParty();
}

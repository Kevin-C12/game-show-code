package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class TeamRosterTest {

    @Test
    void trimsAndPreservesValidNames() {
        TeamRoster roster = new TeamRoster(
            "  Merge Masters  ", List.of(" Ava ", "Noah"));

        assertEquals("Merge Masters", roster.name());
        assertEquals(List.of("Ava", "Noah"), roster.members());
    }

    @Test
    void rejectsBlankTeamOrMemberNames() {
        assertThrows(IllegalArgumentException.class,
            () -> new TeamRoster(" ", List.of("Ava")));
        assertThrows(IllegalArgumentException.class,
            () -> new TeamRoster("Merge Masters", List.of("Ava", " ")));
    }

    @Test
    void rejectsDuplicateMemberNamesIgnoringCase() {
        assertThrows(IllegalArgumentException.class,
            () -> new TeamRoster("Merge Masters", List.of("Ava", " ava ")));
    }

    @Test
    void rejectsRosterOutsideOneToEightMembers() {
        assertThrows(IllegalArgumentException.class,
            () -> new TeamRoster("Merge Masters", List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new TeamRoster("Merge Masters", List.of(
                "1", "2", "3", "4", "5", "6", "7", "8", "9")));
    }
}

package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MatchupSchedulerTest {

    @Test
    void eachMemberAppearsOnceBeforeTheirTeamRepeats() {
        MatchupScheduler scheduler = scheduler(
            List.of("Ava", "Noah", "Mia"),
            List.of("Leo", "Zoe", "Eli"));
        Set<String> teamOne = new HashSet<>();
        Set<String> teamTwo = new HashSet<>();

        for (int match = 0; match < 3; match++) {
            Matchup matchup = scheduler.next();
            teamOne.add(matchup.team1Member());
            teamTwo.add(matchup.team2Member());
        }

        assertEquals(Set.of("Ava", "Noah", "Mia"), teamOne);
        assertEquals(Set.of("Leo", "Zoe", "Eli"), teamTwo);
    }

    @Test
    void unequalTeamsCycleIndependently() {
        MatchupScheduler scheduler = scheduler(
            List.of("Ava", "Noah"),
            List.of("Leo", "Zoe", "Eli"));
        Matchup first = scheduler.next();
        Matchup second = scheduler.next();
        Matchup third = scheduler.next();

        assertEquals(2, Set.of(
            first.team1Member(), second.team1Member()).size());
        assertEquals(3, Set.of(
            first.team2Member(), second.team2Member(), third.team2Member()).size());
    }

    @Test
    void oneMemberTeamAlwaysProducesAValidMatchup() {
        MatchupScheduler scheduler = scheduler(
            List.of("Ava"), List.of("Leo"));

        for (int match = 0; match < 4; match++) {
            assertEquals(new Matchup("Ava", "Leo"), scheduler.next());
        }
    }

    private MatchupScheduler scheduler(
            List<String> teamOne, List<String> teamTwo) {
        return new MatchupScheduler(
            new TeamRoster("Team One", teamOne),
            new TeamRoster("Team Two", teamTwo),
            new Random(42));
    }
}

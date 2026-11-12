package com.example;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/** Selects one member per team without repeats until that roster cycles. */
public final class MatchupScheduler {

    private final TeamRotation teamOne;
    private final TeamRotation teamTwo;

    public MatchupScheduler(
            TeamRoster teamOne, TeamRoster teamTwo, Random random) {
        Objects.requireNonNull(random, "random");
        this.teamOne = new TeamRotation(
            Objects.requireNonNull(teamOne, "teamOne").members(), random);
        this.teamTwo = new TeamRotation(
            Objects.requireNonNull(teamTwo, "teamTwo").members(), random);
    }

    public Matchup next() {
        return new Matchup(teamOne.next(), teamTwo.next());
    }

    private static final class TeamRotation {
        private final List<String> members;
        private final Random random;
        private final Deque<String> remaining = new ArrayDeque<>();
        private String lastSelected;

        private TeamRotation(List<String> members, Random random) {
            this.members = members;
            this.random = random;
        }

        private String next() {
            if (remaining.isEmpty()) {
                refill();
            }
            lastSelected = remaining.removeFirst();
            return lastSelected;
        }

        private void refill() {
            List<String> shuffled = new ArrayList<>(members);
            Collections.shuffle(shuffled, random);
            if (shuffled.size() > 1 && shuffled.get(0).equals(lastSelected)) {
                Collections.swap(shuffled, 0, 1);
            }
            remaining.addAll(shuffled);
        }
    }
}

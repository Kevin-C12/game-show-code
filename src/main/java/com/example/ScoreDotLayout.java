package com.example;

import java.util.ArrayList;
import java.util.List;

/** Maps round scores to a dot bar filled inward from both edges. */
public final class ScoreDotLayout {

    private ScoreDotLayout() {
    }

    public static List<Integer> owners(
            int dotCount, int teamOneScore, int teamTwoScore) {
        if (dotCount < 1 || teamOneScore < 0 || teamTwoScore < 0
                || teamOneScore + teamTwoScore > dotCount) {
            throw new IllegalArgumentException("Scores must fit the dot bar");
        }

        List<Integer> owners = new ArrayList<>(dotCount);
        for (int index = 0; index < dotCount; index++) {
            if (index < teamOneScore) {
                owners.add(1);
            } else if (index >= dotCount - teamTwoScore) {
                owners.add(2);
            } else {
                owners.add(0);
            }
        }
        return List.copyOf(owners);
    }
}

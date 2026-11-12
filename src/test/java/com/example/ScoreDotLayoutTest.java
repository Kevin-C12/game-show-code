package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class ScoreDotLayoutTest {

    @Test
    void redFillsFromTheLeftAndBlueFillsFromTheRight() {
        assertEquals(
            List.of(1, 1, 0, 0, 0, 0, 0, 2, 2),
            ScoreDotLayout.owners(9, 2, 2));
    }

    @Test
    void supportsACompleteBestOfNineRound() {
        assertEquals(
            List.of(1, 1, 1, 1, 0, 2, 2, 2, 2),
            ScoreDotLayout.owners(9, 4, 4));
    }

    @Test
    void rejectsScoresThatDoNotFitTheDotBar() {
        assertThrows(IllegalArgumentException.class,
            () -> ScoreDotLayout.owners(9, 5, 5));
    }
}

package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BuzzTimingTest {

    @Test
    void convertsRevealToBuzzNanosecondsIntoElapsedMilliseconds() {
        assertEquals(1842, BuzzTiming.elapsedMillis(
            10_000_000_000L, 11_842_999_999L));
    }

    @Test
    void rejectsBuzzTimestampsBeforeQuestionReveal() {
        assertThrows(IllegalArgumentException.class,
            () -> BuzzTiming.elapsedMillis(200, 199));
    }
}

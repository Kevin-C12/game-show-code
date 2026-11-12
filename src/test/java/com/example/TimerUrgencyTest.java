package com.example;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TimerUrgencyTest {

    @Test
    void becomesCriticalOnlyDuringTheFinalFiveActiveAnswerSeconds() {
        assertFalse(TimerUrgency.isCritical(20, true, true));
        assertFalse(TimerUrgency.isCritical(6, true, true));
        assertTrue(TimerUrgency.isCritical(5, true, true));
        assertTrue(TimerUrgency.isCritical(1, true, true));
        assertTrue(TimerUrgency.isCritical(0, true, true));
        assertFalse(TimerUrgency.isCritical(5, true, false));
        assertFalse(TimerUrgency.isCritical(5, false, true));
    }
}

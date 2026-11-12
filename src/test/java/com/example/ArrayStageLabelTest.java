package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ArrayStageLabelTest {

    @Test
    void fixedRoundsNameTheStateBeforeTheRequestedStep() {
        assertEquals("ORIGINAL LIST:", ArrayStageLabel.forQuestion(1, 1));
        assertEquals("1ST STEP:", ArrayStageLabel.forQuestion(2, 2));
        assertEquals("2ND STEP:", ArrayStageLabel.forQuestion(3, 3));
    }

    @Test
    void mixedRoundsAlwaysIdentifyTheOriginalList() {
        assertEquals("ORIGINAL LIST:", ArrayStageLabel.forQuestion(4, 3));
        assertEquals("ORIGINAL LIST:", ArrayStageLabel.forQuestion(5, 5));
    }

    @Test
    void rejectsInvalidRoundOrStepNumbers() {
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> ArrayStageLabel.forQuestion(0, 1));
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> ArrayStageLabel.forQuestion(1, 0));
    }
}

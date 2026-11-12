package com.example;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AnswerInputTest {

    @Test
    void permitsSignedMultiDigitEdits() {
        assertTrue(AnswerInput.isPotentialInteger(""));
        assertTrue(AnswerInput.isPotentialInteger("-"));
        assertTrue(AnswerInput.isPotentialInteger("-125"));
        assertTrue(AnswerInput.isPotentialInteger("2048"));
        assertFalse(AnswerInput.isPotentialInteger("12a"));
        assertFalse(AnswerInput.isPotentialInteger("--2"));
        assertFalse(AnswerInput.isPotentialInteger("+4"));
    }

    @Test
    void advancesOnlyForACompleteJavaInteger() {
        assertFalse(AnswerInput.isCompleteInteger(""));
        assertFalse(AnswerInput.isCompleteInteger("-"));
        assertTrue(AnswerInput.isCompleteInteger("-125"));
        assertTrue(AnswerInput.isCompleteInteger("2048"));
        assertFalse(AnswerInput.isCompleteInteger("999999999999999999999"));
    }
}

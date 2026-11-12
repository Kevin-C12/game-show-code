package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FeedbackFlowTest {

    @Test
    void wrongAnswersAndStrikeoutsWaitForTheHost() {
        assertTrue(FeedbackFlow.requiresManualContinue(GameEngine.Result.WRONG));
        assertTrue(FeedbackFlow.requiresManualContinue(
            GameEngine.Result.MATCH_FORFEITED));
    }

    @Test
    void correctAnswersContinueAutomatically() {
        assertFalse(FeedbackFlow.requiresManualContinue(
            GameEngine.Result.STEP_CORRECT));
        assertFalse(FeedbackFlow.requiresManualContinue(GameEngine.Result.CORRECT));
        assertFalse(FeedbackFlow.requiresManualContinue(GameEngine.Result.MATCH_WON));
    }

    @Test
    void continueButtonNamesTheDestination() {
        assertEquals("NEXT QUESTION", FeedbackFlow.continueLabel(false));
        assertEquals("NEXT ROUND", FeedbackFlow.continueLabel(true));
    }

    @Test
    void resolvedArraysUseAnAccurateCaption() {
        assertEquals("CORRECT:",
            FeedbackFlow.answerCaption(GameEngine.Result.CORRECT));
        assertEquals("CORRECT ANSWER:",
            FeedbackFlow.answerCaption(GameEngine.Result.WRONG));
        assertEquals("CORRECT ANSWER:",
            FeedbackFlow.answerCaption(GameEngine.Result.MATCH_FORFEITED));
    }
}

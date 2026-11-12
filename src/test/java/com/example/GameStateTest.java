package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameStateTest {

    @Test
    void fifthCorrectAnswerWinsTheMatch() {
        GameState state = new GameState();
        state.setCurrentPlayer(1);

        for (int answer = 0; answer < 5; answer++) {
            state.recordCorrect();
        }

        assertEquals(5, state.getTeamWins(1));
        assertEquals(0, state.getTeamWins(2));
        assertEquals(1, state.getWinner());
        assertTrue(state.isMatchOver());
    }

    @Test
    void wrongAnswerAddsStrikeWithoutClaimingDot() {
        GameState state = new GameState();
        state.setCurrentPlayer(2);

        state.recordWrong();

        assertEquals(0, state.getTeamWins(1));
        assertEquals(0, state.getTeamWins(2));
        assertEquals(1, state.getTeamStrikes(2));
        assertFalse(state.isMatchOver());
    }

    @Test
    void thirdStrikeForfeitsMatchEvenWhileLeadingFourToZero() {
        GameState state = new GameState();
        state.setCurrentPlayer(1);
        for (int answer = 0; answer < 4; answer++) {
            state.recordCorrect();
        }

        state.recordWrong();
        state.recordWrong();
        state.recordWrong();

        assertEquals(4, state.getTeamWins(1));
        assertEquals(3, state.getTeamStrikes(1));
        assertEquals(2, state.getWinner());
        assertTrue(state.isMatchOver());
    }

    @Test
    void resetClearsAllMatchState() {
        GameState state = new GameState();
        state.setCurrentPlayer(1);
        state.recordCorrect();
        state.recordWrong();

        state.resetMatch();

        assertEquals(0, state.getCurrentPlayer());
        assertEquals(0, state.getTeamWins(1));
        assertEquals(0, state.getTeamWins(2));
        assertEquals(0, state.getTeamStrikes(1));
        assertEquals(0, state.getTeamStrikes(2));
        assertEquals(0, state.getWinner());
        assertFalse(state.isMatchOver());
    }
}

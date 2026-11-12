
package com.example;

/** Match score, strikes, and active-team state for a two-team game. */
public class GameState {

    private static final int WINS_TO_TAKE_MATCH = 5;
    private static final int STRIKES_TO_FORFEIT = 3;

    private int currentPlayer;
    private final int[] teamWins = new int[2];
    private final int[] teamStrikes = new int[2];
    private int winner;

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getTeamWins(int team) {
        return teamWins[indexFor(team)];
    }

    public int getTeamStrikes(int team) {
        return teamStrikes[indexFor(team)];
    }

    public int getWinner() {
        return winner;
    }

    public boolean isMatchOver() {
        return winner != 0;
    }

    public void recordCorrect() {
        ensureAnswerOwner();
        if (isMatchOver()) {
            throw new IllegalStateException("The match is already over");
        }
        int teamIndex = indexFor(currentPlayer);
        teamWins[teamIndex]++;
        if (teamWins[teamIndex] == WINS_TO_TAKE_MATCH) {
            winner = currentPlayer;
        }
    }

    public void recordWrong() {
        ensureAnswerOwner();
        if (isMatchOver()) {
            throw new IllegalStateException("The match is already over");
        }
        int teamIndex = indexFor(currentPlayer);
        teamStrikes[teamIndex]++;
        if (teamStrikes[teamIndex] == STRIKES_TO_FORFEIT) {
            winner = currentPlayer == 1 ? 2 : 1;
        }
    }

    public void setCurrentPlayer(int player) {
        if (player != 1 && player != 2) {
            throw new IllegalArgumentException("Player must be 1 or 2");
        }
        currentPlayer = player;
    }

    public void clearCurrentPlayer() {
        currentPlayer = 0;
    }

    public void resetMatch() {
        clearCurrentPlayer();
        teamWins[0] = 0;
        teamWins[1] = 0;
        teamStrikes[0] = 0;
        teamStrikes[1] = 0;
        winner = 0;
    }

    private void ensureAnswerOwner() {
        if (currentPlayer == 0) {
            throw new IllegalStateException("No team owns this answer attempt");
        }
    }

    private int indexFor(int team) {
        if (team != 1 && team != 2) {
            throw new IllegalArgumentException("Team must be 1 or 2");
        }
        return team - 1;
    }
}

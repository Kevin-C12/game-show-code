
package com.example;

public class GameState {

    private int currentTeam = 1;

    private int team1Points = 0;
    private int team2Points = 0;

    private int team1Strikes = 0;
    private int team2Strikes = 0;

    public int getCurrentTeam() {
        return currentTeam;
    }

    public int getTeam1Points() {
        return team1Points;
    }

    public int getTeam2Points() {
        return team2Points;
    }

    // Get strikes for current team
    public int getStrikes() {
        return currentTeam == 1
            ? team1Strikes : team2Strikes;
    }

    public int getTeam1Strikes() {
        return team1Strikes;
    }

    public int getTeam2Strikes() {
        return team2Strikes;
    }

    // Award points to current team
    public void addPoints(int points) {
        if (currentTeam == 1) {
            team1Points += points;
        } else {
            team2Points += points;
        }
    }

    // Add a strike to current team
    public void addStrike() {
        if (currentTeam == 1) {
            team1Strikes++;
        } else {
            team2Strikes++;
        }
    }

    // Switch teams without resetting strikes
    public void switchTeam() {
        currentTeam = currentTeam == 1 ? 2 : 1;
    }

    // Reset current team's strikes
    public void resetStrikes() {
        if (currentTeam == 1) {
            team1Strikes = 0;
        } else {
            team2Strikes = 0;
        }
    }
}

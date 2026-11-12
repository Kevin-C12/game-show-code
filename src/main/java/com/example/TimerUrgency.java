package com.example;

/** Determines when the answer clock should show its critical warning. */
public final class TimerUrgency {

    private TimerUrgency() {
    }

    public static boolean isCritical(
            int secondsLeft, boolean playing, boolean buzzed) {
        return playing && buzzed && secondsLeft >= 0 && secondsLeft <= 5;
    }
}

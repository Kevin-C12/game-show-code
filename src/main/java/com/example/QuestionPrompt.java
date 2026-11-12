package com.example;

/** Builds concise question copy for a numbered sorting step. */
public final class QuestionPrompt {

    private QuestionPrompt() {
    }

    public static String forStep(int step, String algorithm) {
        if (step < 1) {
            throw new IllegalArgumentException("Step must be positive");
        }
        if (algorithm == null || algorithm.isBlank()) {
            throw new IllegalArgumentException("Algorithm is required");
        }
        return "What is the " + step + suffix(step)
            + " step using " + algorithm + "?";
    }

    private static String suffix(int value) {
        int lastTwo = value % 100;
        if (lastTwo >= 11 && lastTwo <= 13) {
            return "th";
        }
        return switch (value % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }
}

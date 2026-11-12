package com.example;

/** Labels the array state shown before a sorting-step question. */
public final class ArrayStageLabel {

    private ArrayStageLabel() {
    }

    public static String forQuestion(int seriesRound, int targetStep) {
        if (seriesRound < 1 || seriesRound > 5) {
            throw new IllegalArgumentException("Round must be between 1 and 5");
        }
        if (targetStep < 1) {
            throw new IllegalArgumentException("Step must be positive");
        }
        if (seriesRound >= 4 || targetStep == 1) {
            return "ORIGINAL LIST:";
        }

        int previousStep = targetStep - 1;
        return previousStep + suffix(previousStep) + " STEP:";
    }

    private static String suffix(int value) {
        int lastTwo = value % 100;
        if (lastTwo >= 11 && lastTwo <= 13) {
            return "TH";
        }
        return switch (value % 10) {
            case 1 -> "ST";
            case 2 -> "ND";
            case 3 -> "RD";
            default -> "TH";
        };
    }
}

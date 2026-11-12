package com.example;

/** Defines whether answer feedback advances automatically. */
public final class FeedbackFlow {

    private FeedbackFlow() {
    }

    public static boolean requiresManualContinue(GameEngine.Result result) {
        return result == GameEngine.Result.WRONG
            || result == GameEngine.Result.MATCH_FORFEITED;
    }

    public static String continueLabel(boolean roundComplete) {
        return roundComplete ? "NEXT ROUND" : "NEXT QUESTION";
    }

    public static String answerCaption(GameEngine.Result result) {
        return requiresManualContinue(result) ? "CORRECT ANSWER:" : "CORRECT:";
    }
}

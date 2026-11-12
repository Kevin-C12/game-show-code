package com.example;

/** Validation rules shared by answer fields and their focus navigation. */
final class AnswerInput {

    private AnswerInput() {
    }

    static boolean isPotentialInteger(String text) {
        return text != null && text.matches("-?\\d*");
    }

    static boolean isCompleteInteger(String text) {
        if (text == null || text.isBlank() || "-".equals(text)) {
            return false;
        }
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}

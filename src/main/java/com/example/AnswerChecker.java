
package com.example;

import com.google.gson.JsonArray;
import javafx.scene.control.TextField;
import java.util.List;

public class AnswerChecker {

    // Checks the player's answer against the correct sorting step
    public static boolean checkAnswer(
            List<TextField> answers,
            JsonArray correctAnswer) {

        // Check that the array lengths match
        if (answers.size() != correctAnswer.size()) {
            return false;
        }

        // Compare each answer
        for (int i = 0; i < answers.size(); i++) {

            try {
                int playerAnswer = Integer.parseInt(
                    answers.get(i).getText().trim()
                );

                int correctNumber =
                    correctAnswer.get(i).getAsInt();

                if (playerAnswer != correctNumber) {
                    return false;
                }

            } catch (NumberFormatException e) {
                return false;
            }
        }

        // All numbers match
        return true;
    }
}

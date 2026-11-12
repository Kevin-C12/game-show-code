package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class QuestionPromptTest {

    @ParameterizedTest
    @CsvSource({
        "1, insertion sort, What is the 1st step using insertion sort?",
        "2, selection sort, What is the 2nd step using selection sort?",
        "3, merge sort, What is the 3rd step using merge sort?",
        "4, insertion sort, What is the 4th step using insertion sort?",
        "11, merge sort, What is the 11th step using merge sort?",
        "12, merge sort, What is the 12th step using merge sort?",
        "13, merge sort, What is the 13th step using merge sort?",
        "21, selection sort, What is the 21st step using selection sort?",
        "22, insertion sort, What is the 22nd step using insertion sort?"
    })
    void includesTheAlgorithmInOrdinalStepQuestions(
            int step, String algorithm, String expected) {
        assertEquals(expected, QuestionPrompt.forStep(step, algorithm));
    }
}

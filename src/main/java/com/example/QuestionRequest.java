package com.example;

/** Describes the algorithm and step mode for the next question. */
public record QuestionRequest(String sortId, boolean randomTargetStep) {
}

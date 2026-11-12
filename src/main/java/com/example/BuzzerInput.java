package com.example;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.input.KeyCode;

/** Converts local key events into one-shot player buzzes. */
final class BuzzerInput {

    private final Set<KeyCode> heldKeys = new HashSet<>();

    int press(KeyCode key, boolean typing) {
        int player = playerFor(key);
        if (player == 0 || !heldKeys.add(key) || typing) {
            return 0;
        }
        return player;
    }

    void release(KeyCode key) {
        heldKeys.remove(key);
    }

    private int playerFor(KeyCode key) {
        if (key == KeyCode.A) {
            return 1;
        }
        return key == KeyCode.L ? 2 : 0;
    }
}

package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

class BuzzerInputTest {

    @Test
    void mapsAAndLToTheTwoPlayers() {
        BuzzerInput input = new BuzzerInput();

        assertEquals(1, input.press(KeyCode.A, false));
        assertEquals(2, input.press(KeyCode.L, false));
        assertEquals(0, input.press(KeyCode.SPACE, false));
    }

    @Test
    void heldKeyRegistersOnlyOnceUntilReleased() {
        BuzzerInput input = new BuzzerInput();

        assertEquals(1, input.press(KeyCode.A, false));
        assertEquals(0, input.press(KeyCode.A, false));
        input.release(KeyCode.A);
        assertEquals(1, input.press(KeyCode.A, false));
    }

    @Test
    void buzzerKeyPressedWhileTypingStaysSuppressedUntilRelease() {
        BuzzerInput input = new BuzzerInput();

        assertEquals(0, input.press(KeyCode.L, true));
        assertEquals(0, input.press(KeyCode.L, false));
        input.release(KeyCode.L);
        assertEquals(2, input.press(KeyCode.L, false));
    }
}

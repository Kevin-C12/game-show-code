package com.example;

import com.sun.jna.Library;
import com.sun.jna.Native;

/** Reads the two physical Shift keys while the game window has focus. */
final class MacShiftKeys {
    private static final int HID_SYSTEM_STATE = 1;
    private static final short LEFT_SHIFT = 0x38;
    private static final short RIGHT_SHIFT = 0x3c;

    private interface CoreGraphics extends Library {
        CoreGraphics INSTANCE = Native.load("CoreGraphics", CoreGraphics.class);

        boolean CGEventSourceKeyState(int stateID, short keyCode);
    }

    private MacShiftKeys() {
    }

    static boolean leftDown() {
        return CoreGraphics.INSTANCE.CGEventSourceKeyState(
            HID_SYSTEM_STATE, LEFT_SHIFT);
    }

    static boolean rightDown() {
        return CoreGraphics.INSTANCE.CGEventSourceKeyState(
            HID_SYSTEM_STATE, RIGHT_SHIFT);
    }
}

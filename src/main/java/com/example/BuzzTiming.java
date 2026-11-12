package com.example;

/** Monotonic reveal-to-buzz timing helpers. */
public final class BuzzTiming {

    private BuzzTiming() {
    }

    public static long elapsedMillis(long revealedAtNanos, long buzzedAtNanos) {
        if (buzzedAtNanos < revealedAtNanos) {
            throw new IllegalArgumentException(
                "Buzz timestamp cannot precede question reveal");
        }
        return (buzzedAtNanos - revealedAtNanos) / 1_000_000L;
    }
}

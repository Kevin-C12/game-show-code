package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GameViewportTest {

    @BeforeAll
    static void startJavaFx() throws InterruptedException {
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(started::countDown);
        assertTrue(started.await(5, TimeUnit.SECONDS));
    }

    @AfterAll
    static void stopJavaFx() {
        Platform.exit();
    }

    @Test
    void oversizedGameContentRemainsVerticallyReachable() throws Exception {
        Method factory = assertDoesNotThrow(() -> App.class.getDeclaredMethod(
            "createGameViewport", javafx.scene.Node.class));
        factory.setAccessible(true);
        VBox content = new VBox();
        content.setPrefHeight(900);
        AtomicReference<ScrollPane> result = new AtomicReference<>();
        AtomicReference<Throwable> failure = new AtomicReference<>();
        CountDownLatch finished = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                result.set((ScrollPane) factory.invoke(null, content));
            } catch (Throwable throwable) {
                failure.set(throwable);
            } finally {
                finished.countDown();
            }
        });

        assertTrue(finished.await(5, TimeUnit.SECONDS));
        assertEquals(null, failure.get());
        ScrollPane viewport = result.get();
        assertSame(content, viewport.getContent());
        assertTrue(viewport.isFitToWidth());
        assertFalse(viewport.isFitToHeight());
        assertEquals(
            ScrollPane.ScrollBarPolicy.AS_NEEDED,
            viewport.getVbarPolicy());
        assertEquals(
            ScrollPane.ScrollBarPolicy.NEVER,
            viewport.getHbarPolicy());
    }
}

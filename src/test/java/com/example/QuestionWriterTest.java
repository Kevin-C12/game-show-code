package com.example;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class QuestionWriterTest {

    @TempDir
    Path directory;

    @Test
    void writesTheExistingQuestionSchemaAndReturnsTheCreatedFile()
            throws Exception {
        int[] original = {-8, 12, 3};
        Map<String, int[]> steps = new LinkedHashMap<>();
        steps.put("step1", new int[] {-8, 3, 12});
        steps.put("step2", new int[] {-8, 3, 12});

        Class<?> writer = assertDoesNotThrow(
            () -> Class.forName("com.example.QuestionWriter"));
        Method method = writer.getDeclaredMethod(
            "writeNext", Path.class, String.class, int[].class, Map.class);
        Path written = (Path) method.invoke(
            null, directory, "mergeSort", original, steps);

        assertEquals("mergeSort1.json", written.getFileName().toString());
        JsonObject json = JsonParser.parseString(Files.readString(written))
            .getAsJsonObject();
        assertEquals("mergeSort", json.get("sort").getAsString());
        assertArrayEquals(original, toArray(json.getAsJsonArray("unOrderedList")));
        assertArrayEquals(
            new int[] {-8, 3, 12},
            toArray(json.getAsJsonObject("sortSteps").getAsJsonArray("step1")));
    }

    private int[] toArray(com.google.gson.JsonArray values) {
        int[] result = new int[values.size()];
        for (int index = 0; index < values.size(); index++) {
            result[index] = values.get(index).getAsInt();
        }
        return result;
    }
}

package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.google.gson.GsonBuilder;

/** Writes generated sort questions using the schema consumed by the game. */
public final class QuestionWriter {

    private QuestionWriter() {
    }

    public static Path writeNext(
            Path folder,
            String sortId,
            int[] original,
            Map<String, int[]> steps) throws IOException {
        Files.createDirectories(folder);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sort", sortId);
        data.put("unOrderedList", original.clone());
        data.put("sortSteps", steps);

        Path file = folder.resolve(sortId + nextNumber(folder, sortId) + ".json");
        String json = new GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(data);
        Files.writeString(file, json);
        return file;
    }

    private static int nextNumber(Path folder, String sortId) throws IOException {
        Pattern namePattern = Pattern.compile(
            Pattern.quote(sortId) + "(\\d+)\\.json");

        try (Stream<Path> files = Files.list(folder)) {
            return files
                .map(path -> namePattern.matcher(path.getFileName().toString()))
                .filter(Matcher::matches)
                .mapToInt(matcher -> Integer.parseInt(matcher.group(1)))
                .max()
                .orElse(0) + 1;
        }
    }
}

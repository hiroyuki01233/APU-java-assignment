package com.java_assignment.group.Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A generic repository for simple text file operations.
 * The file is assumed to store one record per line (e.g. in CSV format).
 * T is the model type.
 */
public class TxtModelRepository<T> {
    private final String filePath;
    private final Function<String, T> parser;
    private final Function<T, String> serializer;

    /**
     * Constructor.
     *
     * @param filePath   The file path to the text file.
     * @param parser     A function that converts a text line into a model instance.
     * @param serializer A function that converts a model instance into a text line.
     */
    public TxtModelRepository(String filePath, Function<String, T> parser, Function<T, String> serializer) {
        this.filePath = filePath;
        this.parser = parser;
        this.serializer = serializer;
    }

    /**
     * Reads all records from the text file and converts them to model objects.
     *
     * @return a List of models.
     * @throws IOException if reading fails.
     */
    public List<T> readAll() throws IOException {
        List<T> models = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines (optional)
                if (line.trim().isEmpty()) {
                    continue;
                }
                T model = parser.apply(line);
                models.add(model);
            }
        }
        return models;
    }

    /**
     * Writes the given list of models to the file.
     * If append is true, the records are added to the end of the file.
     *
     * @param models the list of models to write.
     * @param append true to append to the file, false to overwrite.
     * @throws IOException if writing fails.
     */
    public void writeAll(List<T> models, boolean append) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, append))) {
            for (T model : models) {
                String line = serializer.apply(model);
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Appends a single model record to the file.
     *
     * @param model the model to append.
     * @throws IOException if writing fails.
     */
    public void append(T model) throws IOException {
        writeAll(List.of(model), true);
    }
}

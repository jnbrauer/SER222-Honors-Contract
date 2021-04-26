package com.jnbrauer.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class for writing CSV logs.
 *
 * Credit: https://www.baeldung.com/java-csv
 *
 * @author Jude Brauer
 */
public class CSVWriter {
    private final String filename;
    private final List<String[]> lines;

    /**
     * Initialize the writer.
     * @param filename name of CSV file to write to.
     */
    public CSVWriter(String filename) {
        this.filename = filename;
        this.lines = new LinkedList<>();
    }

    /**
     * Add a line to the CSV file.
     * @param line line to add.
     */
    public void addLine(String[] line) {
        lines.add(line);
    }

    /**
     * Write all added lines to the file.
     * @throws IOException
     */
    public void write() throws IOException {
        File file = new File(filename);

        PrintWriter writer = new PrintWriter(file);
        lines.stream().map(this::lineToString).forEach(writer::println);
        writer.close();
    }

    private String lineToString(String[] line) {
        return String.join(",", line);
    }
}

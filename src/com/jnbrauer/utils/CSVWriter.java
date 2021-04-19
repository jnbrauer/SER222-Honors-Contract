package com.jnbrauer.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class CSVWriter {
    private final String filename;
    private final List<String[]> lines;

    public CSVWriter(String filename) {
        this.filename = filename;
        this.lines = new LinkedList<>();
    }

    public void addLine(String[] line) {
        lines.add(line);
    }

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

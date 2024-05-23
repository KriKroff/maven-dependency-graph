package com.github.krikroff.dependencygraph.plugin.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

public class GraphOutputWriter implements AutoCloseable {

    private final Writer writer;

    public GraphOutputWriter(OutputStream output) {
        this.writer = new BufferedWriter(new PrintWriter(output));
    }

    public void write(String value) {
        try {
            writer.write(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        writer.close();
    }
}

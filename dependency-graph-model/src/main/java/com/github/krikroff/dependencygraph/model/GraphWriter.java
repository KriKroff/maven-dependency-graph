package com.github.krikroff.dependencygraph.model;

import java.io.OutputStream;

public interface GraphWriter {

    void writeGraph(OutputStream output, Graph graph);

}

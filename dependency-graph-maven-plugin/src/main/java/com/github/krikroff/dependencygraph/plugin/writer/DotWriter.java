package com.github.krikroff.dependencygraph.plugin.writer;

import com.github.krikroff.dependencygraph.model.Graph;
import com.github.krikroff.dependencygraph.model.GraphEdge;
import com.github.krikroff.dependencygraph.model.GraphNode;
import com.github.krikroff.dependencygraph.model.GraphWriter;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

@Named("dot")
@Singleton
public class DotWriter implements GraphWriter {

    @Override
    public void writeGraph(OutputStream output, Graph graph) {
        try (RuntimeWriter writer = new RuntimeWriter(new BufferedWriter(new PrintWriter(output)))) {
            writer.write("digraph G {\n");

            writer.write("// NODES\n");
            graph.getNodes().forEach(node -> writeNode(writer, node));
            writer.write("// EDGES\n");
            graph.getEdges().forEach(edge -> writeEdge(writer, edge));

            writer.write("}\n");
        } catch (Exception e) {
            throw new RuntimeException("Error writing graph", e);
        }
    }

    private void writeEdge(RuntimeWriter writer, GraphEdge edge) {
        writer.write("\t" + DotEscaper.escape(edge.getSourceIdentifier()) + " -> " + DotEscaper.escape(edge.getTargetIdentifier()));
        writeAttributes(writer, edge.getAdditionalProperties());
        writer.write("\n");
    }

    private void writeNode(RuntimeWriter writer, GraphNode node) {
        writer.write("\t" + DotEscaper.escape(node.getIdentifier()));
        writeAttributes(writer, node.getAdditionalProperties());
        writer.write("\n");
    }

    private void writeAttributes(RuntimeWriter writer, Map<String, Object> additionalProperties) {
        if (!additionalProperties.isEmpty()) {
            writer.write(" [");
            additionalProperties.forEach((key, value) ->
                    writer.write(DotEscaper.escape(key) + "=" + DotEscaper.escape(value) + " ")
            );
            writer.write("]");
        }
    }

    private static class RuntimeWriter implements AutoCloseable {
        private final Writer writer;

        private RuntimeWriter(Writer writer) {
            this.writer = writer;
        }

        private void write(String value) {
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
}

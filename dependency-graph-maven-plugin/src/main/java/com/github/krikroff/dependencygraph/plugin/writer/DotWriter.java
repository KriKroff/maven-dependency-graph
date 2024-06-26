package com.github.krikroff.dependencygraph.plugin.writer;

import com.github.krikroff.dependencygraph.model.Graph;
import com.github.krikroff.dependencygraph.model.GraphEdge;
import com.github.krikroff.dependencygraph.model.GraphNode;
import com.github.krikroff.dependencygraph.model.GraphWriter;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.OutputStream;
import java.util.Map;

@Named("dot")
@Singleton
public class DotWriter implements GraphWriter {

    @Override
    public void writeGraph(OutputStream output, Graph graph) {
        try (GraphOutputWriter writer = new GraphOutputWriter(output)) {
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

    private void writeEdge(GraphOutputWriter writer, GraphEdge edge) {
        writer.write("\t" + WriterEscaper.escape(edge.getSourceIdentifier()) + " -> " + WriterEscaper.escape(edge.getTargetIdentifier()));
        writeAttributes(writer, edge.getAdditionalProperties());
        writer.write("\n");
    }

    private void writeNode(GraphOutputWriter writer, GraphNode node) {
        writer.write("\t" + WriterEscaper.escape(node.getIdentifier()));
        writeAttributes(writer, node.getAdditionalProperties());
        writer.write("\n");
    }

    private void writeAttributes(GraphOutputWriter writer, Map<String, Object> additionalProperties) {
        if (!additionalProperties.isEmpty()) {
            writer.write(" [");
            additionalProperties.forEach((key, value) ->
                    writer.write(WriterEscaper.escape(key) + "=" + WriterEscaper.escape(value) + " ")
            );
            writer.write("]");
        }
    }
}

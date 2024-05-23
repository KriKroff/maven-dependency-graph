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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Named("graphml")
@Singleton
public class GraphmlWriter implements GraphWriter {

    private static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
            + "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xmlns:y=\"http://www.yworks.com/xml/graphml\" "
            + "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n";

    @Override
    public void writeGraph(OutputStream output, Graph graph) {
        try (RuntimeWriter writer = new RuntimeWriter(new BufferedWriter(new PrintWriter(output)))) {
            writer.write(HEADER);
            writeAttributeKeys(writer, graph);
            writer.write("<graph id=\"dependencies\" edgedefault=\"directed\">\n");

            graph.getNodes().forEach(node -> writeNode(writer, node));
            AtomicInteger edgeId = new AtomicInteger(0);
            graph.getEdges().forEach(edge -> writeEdge(writer, edge, edgeId.getAndIncrement()));

            writer.write("</graph>\n");
            writer.write("</graphml>\n");
        } catch (Exception e) {
            throw new RuntimeException("Error writing graph", e);
        }
    }

    private void writeAttributeKeys(RuntimeWriter writer, Graph graph) {
        Map<String, Object> nodeAttributes = graph.getNodes().stream()
                .map(GraphNode::getAdditionalProperties)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

        Map<String, Object> edgeAttributes = graph.getEdges().stream()
                .map(GraphEdge::getAdditionalProperties)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

        nodeAttributes.forEach((key, value) -> writeAttributeKey(writer, "node", key, value));
        edgeAttributes.forEach((key, value) -> writeAttributeKey(writer, "edge", key, value));
    }

    private void writeAttributeKey(RuntimeWriter writer, String type, String key, Object value) {
        String attrType = getAttributeType(value);
        writer.write("  <key for=\"" + type + "\" id=" + WriterEscaper.escape(key) + " attr.name=" + WriterEscaper.escape(key) + " attr.type=\"" + attrType + "\"/>\n");
    }

    private String getAttributeType(Object value) {
        return value instanceof Boolean ? "boolean" :
                value instanceof Integer ? "int" :
                        value instanceof Double ? "double" :
                                value instanceof Float ? "float" : "string";
    }

    private void writeNode(RuntimeWriter writer, GraphNode node) {
        writer.write("<node id=" + WriterEscaper.escape(node.getIdentifier()));
        if (node.getAdditionalProperties().isEmpty()) {
            writer.write("/>\n");
        } else {
            writer.write(">\n");
            writeAttributes(writer, node.getAdditionalProperties());
            writer.write("</node>\n");
        }
    }

    private void writeEdge(RuntimeWriter writer, GraphEdge edge, int edgeId) {
        writer.write("  <edge id=\"e" + edgeId + "\" source=" + WriterEscaper.escape(edge.getSourceIdentifier()) + " target=" + WriterEscaper.escape(edge.getTargetIdentifier()));
        if (edge.getAdditionalProperties().isEmpty()) {
            writer.write("/>\n");
        } else {
            writer.write(">\n");
            writeAttributes(writer, edge.getAdditionalProperties());
            writer.write("</edge>\n");
        }
    }

    private void writeAttributes(RuntimeWriter writer, Map<String, Object> additionalProperties) {
        additionalProperties.forEach((key, value) ->
                writer.write("  <data key=" + WriterEscaper.escape(key) + ">" + value.toString() + "</data>\n")
        );
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

package com.github.krikroff.dependencygraph.model;

import java.util.List;

public class Graph {

    private final List<GraphNode> nodes;
    private final List<GraphEdge> edges;

    public Graph(List<GraphNode> nodes, List<GraphEdge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }
}

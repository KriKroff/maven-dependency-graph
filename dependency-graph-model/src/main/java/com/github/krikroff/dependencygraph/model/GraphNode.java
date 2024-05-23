package com.github.krikroff.dependencygraph.model;

import java.util.Map;

public interface GraphNode {
    String getIdentifier();

    Map<String, Object> getAdditionalProperties();
}

package com.github.krikroff.dependencygraph.model;

import java.util.Map;

public interface GraphEdge {
    String getSourceIdentifier();
    String getTargetIdentifier();
    Map<String, Object> getAdditionalProperties();
}

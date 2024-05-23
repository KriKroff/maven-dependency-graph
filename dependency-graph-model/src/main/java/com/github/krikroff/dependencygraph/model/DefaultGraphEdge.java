package com.github.krikroff.dependencygraph.model;

import java.util.Collections;
import java.util.Map;

public class DefaultGraphEdge implements GraphEdge {
    private final String sourceIdentifier;
    private final String targetIdentifier;

    public DefaultGraphEdge(String sourceIdentifier, String targetIdentifier) {
        this.sourceIdentifier = sourceIdentifier;
        this.targetIdentifier = targetIdentifier;
    }

    @Override
    public String getSourceIdentifier() {
        return sourceIdentifier;
    }

    @Override
    public String getTargetIdentifier() {
        return targetIdentifier;
    }

    @Override
    public Map<String, Object> getAdditionalProperties() {
        return Collections.emptyMap();
    }

}

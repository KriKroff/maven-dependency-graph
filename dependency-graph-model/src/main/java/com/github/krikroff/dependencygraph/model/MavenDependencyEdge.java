package com.github.krikroff.dependencygraph.model;

import java.util.HashMap;
import java.util.Map;

public class MavenDependencyEdge extends DefaultGraphEdge {
    private final String scope;

    public MavenDependencyEdge(String sourceIdentifier, String targetIdentifier, String scope) {
        super(sourceIdentifier, targetIdentifier);
        this.scope = scope;
    }

    @Override
    public Map<String, Object> getAdditionalProperties() {
        Map<String, Object> additionalProperties = new HashMap<>();
        if (scope != null) {
            additionalProperties.put("scope", scope);
        }
        return additionalProperties;
    }
}

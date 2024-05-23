package com.github.krikroff.dependencygraph.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MavenProjectNode implements GraphNode {

    private final MavenProjectIdentifier mavenProjectIdentifier;

    private final boolean external;

    public MavenProjectNode(MavenProjectIdentifier mavenProjectIdentifier, boolean external) {
        this.mavenProjectIdentifier = mavenProjectIdentifier;
        this.external = external;
    }

    @Override
    public String getIdentifier() {
        return mavenProjectIdentifier.getGroupId() + ":" + mavenProjectIdentifier.getArtifactId();
    }

    @Override
    public Map<String, Object> getAdditionalProperties() {
        Map<String, Object> additionalProperties = new HashMap<>();

        additionalProperties.put("external", external);

        return additionalProperties;
    }
}

package com.github.krikroff.dependencygraph.model;

public class MavenProjectIdentifier {

    private final String groupId;
    private final String artifactId;

    public MavenProjectIdentifier(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }
}

package com.github.krikroff.dependencygraph.plugin.model;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import java.util.Objects;

public class ArtifactIdentifier {

    private final String groupId;
    private final String artifactId;


    public ArtifactIdentifier(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public ArtifactIdentifier(MavenProject mavenProject) {
        this.groupId = mavenProject.getGroupId();
        this.artifactId = mavenProject.getArtifactId();
    }

    public String getIdentifier() {
        return groupId + ":" + artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ArtifactIdentifier that = (ArtifactIdentifier) object;
        return Objects.equals(groupId, that.groupId) && Objects.equals(artifactId, that.artifactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId);
    }

}
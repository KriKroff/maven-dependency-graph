package com.github.krikroff.dependencygraph.plugin;

import com.github.krikroff.dependencygraph.model.Graph;
import com.github.krikroff.dependencygraph.model.GraphEdge;
import com.github.krikroff.dependencygraph.model.GraphNode;
import com.github.krikroff.dependencygraph.model.MavenDependencyEdge;
import com.github.krikroff.dependencygraph.model.MavenProjectIdentifier;
import com.github.krikroff.dependencygraph.model.MavenProjectNode;
import com.github.krikroff.dependencygraph.plugin.model.ArtifactIdentifier;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mojo(name = "maven-dependencies", aggregator = true, inheritByDefault = false, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
@Execute(phase = LifecyclePhase.INITIALIZE)
public class MavenDependenciesMojo extends AbstractMojo {

    @Parameter(property = "include-external-dependencies", defaultValue = "false")
    protected boolean includeExternalDependencies;

    @Parameter(property = "included-scopes", defaultValue = "compile")
    protected String scopes;

    @Override
    protected Graph executeAnalysis() {
        Set<String> includedScopes = getScopes();

        Set<ArtifactIdentifier> artifactIdentifiers = this.project.getCollectedProjects().stream()
                .filter(p -> "jar".equals(p.getPackaging()))
                .map(ArtifactIdentifier::new)
                .collect(Collectors.toSet());

        this.project.getCollectedProjects().stream()
                .map(ArtifactIdentifier::new)
                .forEach(artifactIdentifiers::add);

        List<ArtifactDependency> dependencies = this.project.getCollectedProjects()
                .stream()
                .flatMap(p -> {
                            if (p.getDependencies() == null) {
                                return Stream.of();
                            }
                            ArtifactIdentifier sourceIdentifier = new ArtifactIdentifier(p);
                            return p.getDependencies().stream()
                                    .filter(d -> includedScopes.contains(d.getScope()))
                                    .map(d -> new ArtifactDependency(
                                            sourceIdentifier,
                                            new ArtifactIdentifier(d.getGroupId(), d.getArtifactId()),
                                            d.getScope()
                                    ))
                                    .filter(dependency -> includeExternalDependencies || artifactIdentifiers.contains(dependency.getTarget()));
                        }
                ).collect(Collectors.toList());

        return toGraph(artifactIdentifiers, dependencies);
    }

    private Graph toGraph(Set<ArtifactIdentifier> artifactIdentifiers, List<ArtifactDependency> dependencies) {
        List<GraphEdge> edges = dependencies.stream()
                .map(dependency -> new MavenDependencyEdge(
                        dependency.getSource().getIdentifier(),
                        dependency.getTarget().getIdentifier(),
                        dependency.getScope()
                )).collect(Collectors.toList());

        Set<ArtifactIdentifier> combinedArtifacts = Stream.concat(
                artifactIdentifiers.stream(), // All nodes, even if not referenced
                dependencies.stream().map(ArtifactDependency::getTarget) // Potential external nodes
        ).collect(Collectors.toSet());

        List<GraphNode> nodes = combinedArtifacts.stream()
                .map(a -> new MavenProjectNode(
                        new MavenProjectIdentifier(
                                a.getGroupId(),
                                a.getArtifactId()),
                                artifactIdentifiers.contains(a)
                        )
                )
                .collect(Collectors.toList());

        return new Graph(nodes, edges);
    }

    public Set<String> getScopes() {
        return Arrays.stream(scopes.split(",")).map(String::trim).collect(Collectors.toSet());
    }

    private static class ArtifactDependency {
        private final ArtifactIdentifier source;
        private final ArtifactIdentifier target;
        private final String scope;

        public ArtifactDependency(ArtifactIdentifier source, ArtifactIdentifier target, String scope) {
            this.source = source;
            this.target = target;
            this.scope = scope;
        }

        public ArtifactIdentifier getSource() {
            return source;
        }

        public ArtifactIdentifier getTarget() {
            return target;
        }

        public String getScope() {
            return scope;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            ArtifactDependency that = (ArtifactDependency) object;
            return Objects.equals(source, that.source) && Objects.equals(target, that.target) && Objects.equals(scope, that.scope);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, target, scope);
        }
    }
}

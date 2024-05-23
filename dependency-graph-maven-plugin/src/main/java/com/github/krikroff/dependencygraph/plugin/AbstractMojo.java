package com.github.krikroff.dependencygraph.plugin;

import com.github.krikroff.dependencygraph.model.Graph;
import com.github.krikroff.dependencygraph.model.GraphWriter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import javax.inject.Inject;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public abstract class AbstractMojo extends org.apache.maven.plugin.AbstractMojo {

    @Inject
    protected PlexusContainer plexusContainer;

    /**
     * The Maven project to analyze.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;


    @Parameter(property = "format", defaultValue = "dot")
    protected String format;

    @Parameter(property = "dependency-graph.skip", defaultValue = "false")
    protected boolean skip;

    @Parameter(property = "output-file")
    private File outputFile;


    // Mojo methods -----------------------------------------------------------

    /*
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException {
        if (isSkip()) {
            getLog().info("Skipping plugin execution");
            return;
        }

        executeInternal();
    }

    protected void executeInternal() {
        Graph graph = executeAnalysis();
        writeGraph(graph);
    }

    protected abstract Graph executeAnalysis();

    protected void writeGraph(Graph graph) {
        try (OutputStream out = getOutputStream()) {
            getWriter(format).writeGraph(out, graph);
        } catch (Exception e) {
            throw new RuntimeException("Cannot write graph", e);
        }
    }

    protected OutputStream getOutputStream() {
        try {
            if (outputFile != null) {
                Files.createDirectories(outputFile.getParentFile().toPath());
                return new BufferedOutputStream(Files.newOutputStream(outputFile.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
            } else {
                return System.out;
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot write to file " + outputFile, e);
        }
    }

    protected GraphWriter getWriter(String outputType) {
        try {
            return plexusContainer.lookup(GraphWriter.class, format);
        } catch (ComponentLookupException e) {
            throw new RuntimeException("Cannot resolve writer " + outputType, e);
        }
    }

    protected final boolean isSkip() {
        return skip;
    }

}

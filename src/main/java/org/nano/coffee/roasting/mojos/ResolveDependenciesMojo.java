package org.nano.coffee.roasting.mojos;


import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.dependency.CopyDependenciesMojo;
import org.apache.maven.plugin.dependency.UnpackDependenciesMojo;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

/**
 * Copy project dependencies to the <tt>target/web</tt> folder.
 * The location can be changed using the <tt>webDir</tt> option
 *
 * @goal resolve-dependencies
 * @requiresDependencyResolution test
 */
public class ResolveDependenciesMojo extends AbstractRoastingCoffeeMojo {



    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @component
     */
    protected ArtifactFactory factory;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @component
     */
    protected ArtifactResolver resolver;

    /**
     * Artifact collector, needed to resolve dependencies.
     *
     * @component role="org.apache.maven.artifact.resolver.ArtifactCollector"
     * @required
     * @readonly
     */
    protected ArtifactCollector artifactCollector;

    /**
     * @component role="org.apache.maven.artifact.metadata.ArtifactMetadataSource"
     * hint="maven"
     * @required
     * @readonly
     */
    protected ArtifactMetadataSource artifactMetadataSource;

    /**
     * Location of the local repository.
     *
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    private ArtifactRepository local;

    /**
     * List of Remote Repositories used by the resolver
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    protected List<ArtifactRepository> remoteRepos;


    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Resolving JavaScript / CSS and Web dependencies");
        CopyJSDependenciesMojo js = new CopyJSDependenciesMojo();
        js.execute();
        CopyJSDependenciesMojo css = new CopyJSDependenciesMojo();
        css.execute();

        CopyWebDependenciesMojo web = new CopyWebDependenciesMojo();
        web.execute();
    }

    private class CopyJSDependenciesMojo extends CopyDependenciesMojo {

        public CopyJSDependenciesMojo() {
            super();
            project = ResolveDependenciesMojo.this.project;
            setFactory(factory);
            setResolver(resolver);
            setArtifactCollector(artifactCollector);
            setArtifactMetadataSource(artifactMetadataSource);
            setLocal(local);
            setRemoteRepos(remoteRepos);
            setOutputDirectory(getLibDirectory());
            setUseRepositoryLayout(false);
            setLog(getLog());
            setCopyPom(false);
            silent = false;
            overWriteIfNewer = true;
            overWriteSnapshots = true;
            overWriteReleases = false;
            excludeTransitive = false;
            excludeScope = "provided";
            includeTypes = "js";
        }
    }

    // TODO Unpack zip dependencies ?

    private class CopyWebDependenciesMojo extends UnpackDependenciesMojo {

        public CopyWebDependenciesMojo() {
            super();
            project = ResolveDependenciesMojo.this.project;
            setFactory(factory);
            setResolver(resolver);
            setArtifactCollector(artifactCollector);
            setArtifactMetadataSource(artifactMetadataSource);
            setLocal(local);
            setRemoteRepos(remoteRepos);
            setOutputDirectory(getLibDirectory());
            setUseRepositoryLayout(false);
            setLog(getLog());
            setCopyPom(false);
            silent = false;
            overWriteIfNewer = true;
            overWriteSnapshots = true;
            overWriteReleases = false;
            excludeTransitive = false;
            excludeScope = "provided";
            includeTypes = "zip";
        }
    }
}

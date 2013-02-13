/*
 * Copyright 2013 OW2 Nanoko Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nanoko.coffee.mill.mojos.processResources;


import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.dependency.CopyDependenciesMojo;
import org.apache.maven.plugin.dependency.UnpackDependenciesMojo;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Copy `js` project dependencies to the <tt>target/libs</tt> folder.
 * Copy `css` project dependencies to the <tt>target/web</tt> folder.
 * The location can be changed using the <tt>webDir</tt> and <tt>libsDir</tt> option
 *
 * @goal resolve-dependencies
 * @requiresDependencyResolution test
 */
public class ResolveDependenciesMojo extends AbstractCoffeeMillMojo {
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
        stripMinClassifier();

        CopyCSSDependenciesMojo css = new CopyCSSDependenciesMojo();
        css.execute();

        CopyWebDependenciesMojo web = new CopyWebDependenciesMojo();
        web.execute();
    }

    private void stripMinClassifier() {
        if (!getLibDirectory().isDirectory()) {
            // Nothing to do.
            return;
        }

        Collection<File> files = FileUtils.listFiles(getLibDirectory(), new String[]{"js"}, true);
        for (File file : files) {
            if (file.getName().endsWith("-min.js")) {
                File newFile = new File(file.getParent(), file.getName().replace("-min.js", ".js"));
                file.renameTo(newFile);
            }
        }
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
            stripVersion = true;
            silent = false;
            overWriteIfNewer = true;
            overWriteSnapshots = true;
            overWriteReleases = false;
            excludeTransitive = false;
            excludeScope = "provided";
            includeTypes = "js";
        }
    }

    private class CopyCSSDependenciesMojo extends CopyDependenciesMojo {

        public CopyCSSDependenciesMojo() {
            super();
            project = ResolveDependenciesMojo.this.project;
            setFactory(factory);
            setResolver(resolver);
            setArtifactCollector(artifactCollector);
            setArtifactMetadataSource(artifactMetadataSource);
            setLocal(local);
            setRemoteRepos(remoteRepos);
            setOutputDirectory(getWorkDirectory());
            setUseRepositoryLayout(false);
            setLog(getLog());
            setCopyPom(false);
            stripVersion = true;
            silent = false;
            overWriteIfNewer = true;
            overWriteSnapshots = true;
            overWriteReleases = false;
            excludeTransitive = false;
            excludeScope = "provided";
            includeTypes = "css";
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

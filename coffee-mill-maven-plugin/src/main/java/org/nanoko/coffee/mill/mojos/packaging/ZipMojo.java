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

package org.nanoko.coffee.mill.mojos.packaging;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;

import java.io.File;

/**
 * Build a zip file including the all assets, generated files and dependencies.
 * @goal build-zip
 */
public class ZipMojo extends AbstractCoffeeMillMojo {

    /**
     * The Zip archiver.
     * @component role="org.codehaus.plexus.archiver.Archiver" roleHint="zip"
     */
    protected ZipArchiver zipArchiver;

    /**
     * @parameter default-value="false"
     */
    public boolean skipArchiveCreation;

    /**
     * @parameter default-value="true"
     */
    public boolean attachArchive;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipArchiveCreation) {
            getLog().info("Archive creation skipped");
            return;
        }
        String finalName = project.getBuild().getFinalName() + ".zip";
        File output = new File(buildDirectory, finalName );
        try {
            if (getWorkDirectory().isDirectory()) {
                zipArchiver.addDirectory( getWorkDirectory(), "" );
            }
            if (getLibDirectory().isDirectory()) {
                zipArchiver.addDirectory( getLibDirectory(), "" );
            }
            zipArchiver.setDestFile( output );
            zipArchiver.createArchive();

            if (attachArchive) {
                if (project.getFile() == null) {
                    project.setFile(output);
                } else {
                    projectHelper.attachArtifact(project, "zip", "dist", output);
                }
            }

        } catch( Exception e ) {
            throw new MojoExecutionException( "Could not zip", e );
        }

    }
}

package org.nano.coffee.roasting.processors;

import org.apache.maven.plugin.MojoExecutionException;

public interface Processor {

    public void process() throws MojoExecutionException;

}

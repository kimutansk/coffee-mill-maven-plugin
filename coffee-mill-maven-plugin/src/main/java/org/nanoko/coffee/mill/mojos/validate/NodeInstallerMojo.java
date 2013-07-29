package org.nanoko.coffee.mill.mojos.validate;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.utils.node.NPM;
import org.nanoko.coffee.mill.utils.node.NodeManager;

import java.io.IOException;

/**
 * Mojo installing node in the maven local repository as well as npm.
 *
 * @goal install-node
 */
public class NodeInstallerMojo extends AbstractCoffeeMillMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            node.installIfNotInstalled();
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}

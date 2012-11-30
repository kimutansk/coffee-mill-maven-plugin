package org.nanoko.coffee.mill.mojos.others;

import org.eclipse.jetty.server.handler.ResourceHandler;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: clement
 * Date: 16/08/12
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryHandler extends ResourceHandler {
    private String prefix;

    public DirectoryHandler(File workDirectory) {
        setResourceBase(workDirectory.getAbsolutePath());
    }
}

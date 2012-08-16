package org.nano.coffee.roasting.mojos.others;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: clement
 * Date: 16/08/12
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryHandler extends ResourceHandler {
    public DirectoryHandler(File workDirectory) {
        setResourceBase(workDirectory.getAbsolutePath());
    }
}

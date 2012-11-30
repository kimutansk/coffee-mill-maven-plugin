package org.nanoko.coffee.mill.mojos.others;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.TestMojo;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.nanoko.coffee.mill.utils.JasmineUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handler serving the Jasmine Runner.
 */
public class JasmineHandler extends ResourceHandler {
    AbstractJasmineMojo jasmine;
    private JasmineRunnerGenerator createsManualRunner;
    private WatchMojo watchMojo;

    public JasmineHandler(WatchMojo mojo) {
        this.watchMojo = mojo;
        jasmine = new TestMojo();
        JasmineUtils.prepareJasmineMojo(watchMojo, jasmine, watchMojo.javascriptAggregation);
        createsManualRunner = new JasmineRunnerGenerator(mojo, jasmine);
    }

    private void createManualSpecRunnerIfNecessary(String target) throws IOException {
        if ("/jasmine".equals(target)) {
            watchMojo.getLog().info("Generating Jasmine Runner");
            createsManualRunner.create();
        }
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        createManualSpecRunnerIfNecessary(target);
        if ("/jasmine".equals(target)) {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            response.getWriter().println(createsManualRunner.getHtml());
        } else {
            super.handle(target, baseRequest, request, response);
        }

    }


}

/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.browser;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.noraui.Runner;
import com.github.noraui.browser.steps.BrowserSteps;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.utils.Context;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class AuthUT {

    private BrowserSteps bs = null;
    private TestServer testServer = new TestServer();

    @Before
    public void prepare() throws TechnicalException {
        testServer.start();
        Context.getInstance().initializeEnv("demoExcel.properties");
        Context.getInstance().initializeRobot(Runner.class);
        Context.getApplication(Context.DEMO_KEY).addUrlPage("PROTECTED", "http://localhost:8000/protected");
        Context.getApplication(Context.DEMO_KEY).addUrlPage("UNPROTECTED", "http://localhost:8000/unprotected");
        bs = new BrowserSteps();
    }

    @Test
    public void testPageWithoutAuthentication() {
        try {
            Auth.clear();
            Auth.setAuthenticationType("");
            bs.goToUrl("UNPROTECTED");
            Assert.assertTrue("The requested page content must respond 'OK'.",
                    Context.getDriver().getPageSource().contains("<head></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">OK</pre></body>"));
        } catch (Exception e) {
            Assert.fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testPageBehindAuthenticationWithWrongCredentials() {
        try {
            Auth.clear();
            System.setProperty(Auth.UID, "admin");
            System.setProperty(Auth.PASSWORD, "wrongpass");
            Auth.setAuthenticationType(Auth.authenticationTypes.BASIC.toString());
            bs.goToUrl("PROTECTED");
            Assert.assertTrue("The requested page content must not respond 'OK'.",
                    Context.getDriver().getPageSource().contains("<head></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\"></pre></body>")
                            || Context.getDriver().getPageSource().contains("<html><head></head><body></body></html>"));
        } catch (Exception e) {
            Assert.fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testPageBehindAuthentication() {
        try {
            Auth.clear();
            System.setProperty(Auth.UID, "admin");
            System.setProperty(Auth.PASSWORD, "admin");
            Auth.setAuthenticationType(Auth.authenticationTypes.BASIC.toString());
            bs.goToUrl("PROTECTED");
            Assert.assertTrue("The requested page content must respond 'OK'.",
                    Context.getDriver().getPageSource().contains("<head></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">OK</pre></body>"));
        } catch (Exception e) {
            Assert.fail("Exception thrown: " + e.getMessage());
        }
    }

    @After
    public void teardown() {
        testServer.stop();
        Context.clear();
    }

}

@SuppressWarnings("restriction")
class TestServer {

    private HttpServer server;

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (Exception e) {
            Assert.fail("Exception thrown: " + e.getMessage());
        }
        server.createContext("/unprotected", new TestHttpHandler());

        HttpContext protectedContext = server.createContext("/protected", new TestHttpHandler());
        protectedContext.setAuthenticator(new BasicAuthenticator("get") {

            @Override
            public boolean checkCredentials(String user, String pwd) {
                return user.equals("admin") && pwd.equals("admin");
            }

        });
        server.setExecutor(null);
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public HttpServer getServer() {
        return server;
    }

    class TestHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            t.getRequestBody();
            String response = "OK";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

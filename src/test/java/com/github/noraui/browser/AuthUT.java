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
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.Runner;
import com.github.noraui.browser.steps.BrowserSteps;
import com.github.noraui.exception.FailureException;
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
    private static final TestServer testServer = new TestServer();

    public static void main(String[] args) {
        testServer.start();
    }

    @SuppressWarnings("deprecation")
    @Before
    public void prepare() throws TechnicalException {
        testServer.start();
        Context.getInstance().initializeEnv("demoExcel.properties");
        Context.getInstance().initializeRobot(Runner.class);
        Context.getApplication(Context.BAKERY_KEY).addUrlPage("PROTECTED", "http://127.0.0.1:8000/protected");
        Context.getApplication(Context.BAKERY_KEY).addUrlPage("UNPROTECTED", "http://127.0.0.1:8000/unprotected");
        Context.getApplication(Context.BAKERY_KEY).addUrlPage("COOKIEPROTECTED", "http://127.0.0.1:8000/cookieprotected");
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
        } catch (final Exception e) {
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

            // Set a tiny timeout to force webdriver to crash after authentication failure
            Context.getDriver().manage().timeouts().pageLoadTimeout(1, TimeUnit.SECONDS);
            bs.goToUrl("PROTECTED");
            final boolean notOKResponse = Context.getDriver().getPageSource().contains("<head></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\"></pre></body>")
                    || Context.getDriver().getPageSource().contains("<html><head></head><body></body></html>")
                    || Context.getDriver().getPageSource().contains("<div class=\"error-code\" jscontent=\"errorCode\" jstcache=\"18\">HTTP ERROR 401</div>");
            Assert.assertTrue("The requested page content must not respond 'OK'.", notOKResponse);
        } catch (final Exception e) {
            Assert.assertTrue("Exception thrown after authentication failure should be an instance of 'exception.FailureException' !", e instanceof FailureException);
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
        } catch (final Exception e) {
            Assert.fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testPageBehindCookieAuthentication() {
        try {
            Auth.clear();
            System.setProperty(Auth.SESSION_COOKIE, "auth=ok,path=/");
            final Cookie c = Auth.getAuthenticationCookie("http://127.0.0.1");
            bs.goToUrl("UNPROTECTED");
            Auth.loadAuthenticationCookie(c);
            bs.goToUrl("COOKIEPROTECTED");
            Assert.assertTrue("The requested page content must respond 'OK'.",
                    Context.getDriver().getPageSource().contains("<head></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">OK</pre></body>"));
        } catch (final Exception e) {
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
        } catch (final Exception e) {
            Assert.fail("Exception thrown: " + e.getMessage());
        }
        server.createContext("/unprotected", new TestHttpHandler());

        final HttpContext protectedContext = server.createContext("/protected", new TestHttpHandler());

        protectedContext.setAuthenticator(new BasicAuthenticator("get") {

            @Override
            public boolean checkCredentials(String user, String pwd) {
                return user.equals("admin") && pwd.equals("admin");
            }

        });

        server.createContext("/cookieprotected", new CookieTestHttpHandler());

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
            final String response = "OK";
            t.sendResponseHeaders(200, response.length());
            final OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    class CookieTestHttpHandler implements HttpHandler {

        /**
         * Specific LOGGER
         */
        private final Logger logger = LoggerFactory.getLogger(CookieTestHttpHandler.class);

        @Override
        public void handle(HttpExchange t) throws IOException {
            final Set<Entry<String, List<String>>> headers = t.getRequestHeaders().entrySet();
            final OutputStream os = t.getResponseBody();
            for (final Entry<String, List<String>> h : headers) {
                logger.debug("key:[{}] and value:[{}]", h.getKey(), h.getValue());
                if ("Cookie".equals(h.getKey()) && h.getValue().contains("auth=ok")) {
                    t.getRequestBody();
                    final String response = "OK";
                    t.sendResponseHeaders(200, response.length());
                    os.write(response.getBytes());
                    break;
                }
            }
            os.close();
        }
    }
}

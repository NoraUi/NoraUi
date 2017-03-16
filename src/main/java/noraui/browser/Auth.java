package noraui.browser;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;

import noraui.exception.TechnicalException;
import noraui.utils.Context;

public class Auth {

    private static final Logger logger = Logger.getLogger(Auth.class);

    public static final String SESSION_COOKIE = "cookie";

    public static final String UID = "uid";

    public static final String PASSWORD = "password";

    /**
     * Static context instance.
     */
    private static Auth instance;

    private User currentUser;

    /**
     * Is current user connected ?
     */
    private boolean isConnected;

    /**
     * Authentication cookie
     */
    private Cookie authCookie;

    /**
     * Constructor.
     */
    public Auth() {
        this.currentUser = setCredential(System.getProperty(UID), System.getProperty(PASSWORD));
        this.isConnected = false;
        this.authCookie = null;
    }

    /**
     * Get auth singleton.
     *
     * @return auth instance
     */
    public static Auth getInstance() {
        if (instance == null) {
            instance = new Auth();
        }
        return instance;
    }

    /**
     * Save the value of current user's authentication (true if connected, false otherwise)
     *
     * @param isConnected
     *            value of authentication
     */
    public static void setConnected(boolean isConnected) {
        getInstance().isConnected = isConnected;
    }

    /**
     * Returns true if current user is connected, false otherwise
     *
     * @return true if current user is connected, false otherwise
     */
    public static boolean isConnected() {
        return getInstance().isConnected;
    }

    /**
     * Gets current user login if direct authentication is used.
     *
     * @return the current user's login
     */
    public static String getLogin() {
        return getInstance().currentUser.login;
    }

    /**
     * Gets current user password if direct authentication is used.
     *
     * @return the current user's password
     */
    public static String getPassword() {
        return getInstance().currentUser.password;
    }

    /**
     * Returns a Cookie object by retrieving data from -Dcookie maven parameter.
     *
     * @param domainUrl
     *            is url of guardian domain
     * @return a Cookie with a name, value, domain and path.
     * @throws TechnicalException
     *             with a message (no screenshot, no exception)
     */
    public static Cookie getAuthenticationCookie(String domainUrl) throws TechnicalException {
        if (getInstance().authCookie == null) {
            String cookieStr = System.getProperty(SESSION_COOKIE);
            try {
                if (cookieStr != null && !"".equals(cookieStr)) {
                    int indexValue = cookieStr.indexOf("=");
                    int indexPath = cookieStr.indexOf(",path=");
                    String cookieName = cookieStr.substring(0, indexValue);
                    String cookieValue = cookieStr.substring(indexValue + 1, indexPath);
                    String cookieDomain = new URI(domainUrl).getHost().replaceAll("self.", "");
                    String cookiePath = cookieStr.substring(indexPath + 6);
                    getInstance().authCookie = new Cookie.Builder(cookieName, cookieValue).domain(cookieDomain).path(cookiePath).build();
                    logger.debug("New cookie created: " + cookieName + "=" + cookieValue + " on domain " + cookieDomain + cookiePath);
                }
            } catch (URISyntaxException e) {
                throw new TechnicalException("Wrong URI syntax (URISyntaxException)", e);
            }
        }
        return getInstance().authCookie;
    }

    /**
     * Loads authentication cookie in the current web browsers. The way is different for phantomjs.
     *
     * @param browser
     *            the name of the current browser
     */
    public static void loadAuthentication(String browser) {
        if (DriverFactory.PHANTOM.equals(browser)) {
            JavascriptExecutor js = (JavascriptExecutor) Context.getDriver();
            js.executeScript("document.cookie = \"" + getInstance().authCookie.getName() + "=" + getInstance().authCookie.getValue() + ";path=" + getInstance().authCookie.getPath() + ";domain="
                    + getInstance().authCookie.getDomain() + "\"");
        } else {
            Context.getDriver().manage().addCookie(getInstance().authCookie);
        }

    }

    private User setCredential(String login, String password) {
        return new User(login, password);
    }

    public class User {

        /**
         * Login used by Jenkins.
         */
        private String login;

        /**
         * Password of login used by Jenkins.
         */
        private String password;

        public User(String login, String password) {
            super();
            this.login = login;
            this.password = password;
        }

    }
}

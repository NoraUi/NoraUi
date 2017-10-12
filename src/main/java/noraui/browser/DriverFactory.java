package noraui.browser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import noraui.exception.TechnicalException;
import noraui.utils.Context;
import noraui.utils.Messages;
import noraui.utils.Utilities;
import noraui.utils.Utilities.OperatingSystem;
import noraui.utils.Utilities.SystemArchitecture;

public class DriverFactory {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);

    /** Default web drivers implicit wait **/
    public static final long IMPLICIT_WAIT = 500;

    public static final String PHANTOM = "phantom";
    public static final String IE = "ie";
    public static final String CHROME = "chrome";
    public static final String HTMLUNIT = "htmlunit";
    public static final String DEFAULT_DRIVER = PHANTOM;

    /** Selenium drivers. **/
    private final Map<String, WebDriver> drivers;

    public DriverFactory() {
        drivers = new HashMap<>();
    }

    /**
     * Get selenium driver. Drivers are lazy loaded.
     *
     * @return selenium driver
     */
    public WebDriver getDriver() {
        // Driver's name is retrieved by system properties
        String driverName = Context.getBrowser();
        driverName = driverName != null ? driverName : DEFAULT_DRIVER;
        WebDriver driver = null;
        if (!drivers.containsKey(driverName)) {
            try {
                driver = generateWebDriver(driverName);
            } catch (final TechnicalException e) {
                logger.error(e.getMessage());
            }
        } else {
            driver = drivers.get(driverName);
        }
        return driver;
    }

    /**
     * Clear loaded drivers
     */
    public void clear() {
        for (final WebDriver wd : drivers.values()) {
            wd.quit();
        }
        drivers.clear();
    }

    /**
     * Generate a phantomJs webdriver.
     *
     * @return a phantomJs webdriver
     * @throws TechnicalException
     *             if an error occured when Webdriver setExecutable to true.
     */
    private WebDriver generatePhantomJsDriver() throws TechnicalException {
        logger.info("Driver phantomjs");
        final String pathWebdriver = DriverFactory.getPath(Driver.PHANTOMJS);
        if (!new File(pathWebdriver).setExecutable(true)) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_WEBDRIVER_SET_EXECUTABLE));
        }
        final DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, pathWebdriver);
        caps.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
        caps.setJavascriptEnabled(true);
        final LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + "Accept-Language", "fr-FR");
        String proxy = "";
        if (Context.getProxy().getProxyType() != ProxyType.UNSPECIFIED && Context.getProxy().getProxyType() != ProxyType.AUTODETECT) {
            proxy = Context.getProxy().getHttpProxy();
        }
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS,
                new String[] { "--proxy=" + proxy, "--web-security=no", "--ignore-ssl-errors=true", "--ssl-protocol=tlsv1", "--webdriver-loglevel=NONE" });
        return new PhantomJSDriver(caps);
    }

    /**
     * Generate an ie webdriver. Unable to use it with a proxy. Causes a crash.
     *
     * @return an ie webdriver
     * @throws TechnicalException
     *             if an error occured when Webdriver setExecutable to true.
     */
    private WebDriver generateIEDriver() throws TechnicalException {
        logger.info("Driver ie");
        final String pathWebdriver = DriverFactory.getPath(Driver.IE);
        if (!new File(pathWebdriver).setExecutable(true)) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_WEBDRIVER_SET_EXECUTABLE));
        }
        final DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
        capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
        capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
        capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
        capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        capabilities.setCapability("disable-popup-blocking", true);
        capabilities.setJavascriptEnabled(true);
        if (Context.getProxy().getProxyType() != ProxyType.UNSPECIFIED && Context.getProxy().getProxyType() != ProxyType.AUTODETECT) {
            capabilities.setCapability(CapabilityType.PROXY, Context.getProxy());
        }
        System.setProperty(Driver.IE.getDriverName(), pathWebdriver);
        return new InternetExplorerDriver(capabilities);
    }

    /**
     * Generate a chrome webdriver.
     *
     * @return a chrome webdriver
     * @throws TechnicalException
     *             if an error occured when Webdriver setExecutable to true.
     */
    private WebDriver generateGoogleChromeDriver() throws TechnicalException {
        logger.info("Driver chrome");
        final String pathWebdriver = DriverFactory.getPath(Driver.CHROME);
        if (!new File(pathWebdriver).setExecutable(true)) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_WEBDRIVER_SET_EXECUTABLE));
        }
        final DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
        final LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        if (Context.getProxy().getProxyType() != ProxyType.UNSPECIFIED && Context.getProxy().getProxyType() != ProxyType.AUTODETECT) {
            capabilities.setCapability(CapabilityType.PROXY, Context.getProxy());
        }

        System.setProperty(Driver.CHROME.getDriverName(), pathWebdriver);
        return new ChromeDriver(capabilities);
    }

    /**
     * Generate a htmlunit webdriver.
     *
     * @return a htmlunit webdriver
     */
    private WebDriver generateHtmlUnitDriver() {
        logger.info("Driver htmlunit");
        final DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
        capabilities.setJavascriptEnabled(true);
        if (Context.getProxy().getProxyType() != ProxyType.UNSPECIFIED && Context.getProxy().getProxyType() != ProxyType.AUTODETECT) {
            capabilities.setCapability(CapabilityType.PROXY, Context.getProxy());
        }
        return new HtmlUnitDriver(capabilities);
    }

    /**
     * Generate selenium webdriver. By default a phantomJS driver is generate
     *
     * @param driverName
     * @return
     * @throws TechnicalException
     *             if an error occured when Webdriver setExecutable to true.
     */
    private WebDriver generateWebDriver(String driverName) throws TechnicalException {
        WebDriver driver;
        if (IE.equals(driverName)) {
            driver = generateIEDriver();
        } else if (CHROME.equals(driverName)) {
            driver = generateGoogleChromeDriver();
        } else if (HTMLUNIT.equals(driverName)) {
            driver = generateHtmlUnitDriver();
        } else {
            driver = generatePhantomJsDriver();
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.MILLISECONDS);
        drivers.put(driverName, driver);
        return driver;

    }

    public static String getPath(Driver currentDriver) {
        final OperatingSystem currentOperatingSystem = OperatingSystem.getCurrentOperatingSystem();
        String format = "";
        if ("webdriver.ie.driver".equals(currentDriver.driverName)) {
            format = Utilities.setProperty(Context.getWebdriversProperties(currentDriver.driverName), "src/test/resources/drivers/%s/internetexplorer/%s/IEDriverServer%s");
        } else if ("phantomjs.binary.path".equals(currentDriver.driverName)) {
            format = Utilities.setProperty(Context.getWebdriversProperties(currentDriver.driverName), "src/test/resources/drivers/%s/phantomjs/%s/phantomjs%s");
        } else if ("webdriver.chrome.driver".equals(currentDriver.driverName)) {
            format = Utilities.setProperty(Context.getWebdriversProperties(currentDriver.driverName), "src/test/resources/drivers/%s/googlechrome/%s/chromedriver%s");
        }
        return String.format(format, currentOperatingSystem.getOperatingSystemDir(), SystemArchitecture.getCurrentSystemArchitecture().getSystemArchitectureName(),
                currentOperatingSystem.getSuffixBinary());
    }

    public enum Driver {
        IE("webdriver.ie.driver"), PHANTOMJS("phantomjs.binary.path"), CHROME("webdriver.chrome.driver");
        private String driverName;

        Driver(String driverName) {
            this.driverName = driverName;
        }

        String getDriverName() {
            return driverName;
        }
    }
}

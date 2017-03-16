package noraui.browser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import noraui.utils.Context;
import noraui.utils.Utilities;
import noraui.utils.Utilities.OperatingSystem;
import noraui.utils.Utilities.SystemArchitecture;

public class DriverFactory {

    private static Logger logger = Logger.getLogger(DriverFactory.class);

    /** Default web drivers implicit wait **/
    public static final long IMPLICIT_WAIT = 500;

    public static final String PHANTOM = "phantom";
    public static final String IE = "ie";
    public static final String CHROME = "chrome";
    public static final String DEFAULT_DRIVER = PHANTOM;

    /** Selenium drivers. **/
    private Map<String, WebDriver> drivers;

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
        WebDriver driver;
        if (!drivers.containsKey(driverName)) {
            driver = generateWebDriver(driverName);
        } else {
            driver = drivers.get(driverName);
        }
        return driver;
    }

    /**
     * Clear loaded drivers
     */
    public void clear() {
        for (WebDriver wd : drivers.values()) {
            wd.quit();
        }
        drivers.clear();
    }

    /**
     * Generate a phantomJs webdriver.
     *
     * @return a phantomJs webdriver
     */
    private WebDriver generatePhantomJsDriver() {
        logger.info("Driver phantomjs");
        String pathWebdriver = DriverFactory.getPath(Driver.PHANTOMJS);
        new File(pathWebdriver).setExecutable(true);
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, pathWebdriver);
        caps.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
        caps.setJavascriptEnabled(true);
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + "Accept-Language", "fr-FR");
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS,
                new String[] { "--proxy=" + Context.getProxy(), "--web-security=no", "--ignore-ssl-errors=true", "--ssl-protocol=tlsv1", "--webdriver-loglevel=NONE" });
        return new PhantomJSDriver(caps);
    }

    /**
     * Generate an ie webdriver.
     *
     * @deprecated It should not be used in production and it is very slow for developments.
     * @return an ie webdriver
     */
    @Deprecated
    private WebDriver generateIEDriver() {
        logger.info("Driver ie");
        String pathWebdriver = DriverFactory.getPath(Driver.IE);
        new File(pathWebdriver).setExecutable(true);
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
        capabilities.setCapability("requireWindowFocus", true);
        capabilities.setCapability(CapabilityType.PROXY, Context.getProxy());
        System.setProperty(Driver.IE.getDriverName(), pathWebdriver);
        return new InternetExplorerDriver(capabilities);
    }

    /**
     * Generate a chrome webdriver.
     *
     * @return a chrome webdriver
     */
    private WebDriver generateGoogleChromeDriver() {
        logger.info("Driver chrome");
        String pathWebdriver = DriverFactory.getPath(Driver.CHROME);
        new File(pathWebdriver).setExecutable(true);
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
        capabilities.setCapability("requireWindowFocus", true);
        if (!Context.getProxy().isEmpty()) {
            Proxy proxy = new Proxy();
            proxy.setHttpProxy(Context.getProxy());
            capabilities.setCapability(CapabilityType.PROXY, proxy);
        }
        System.setProperty(Driver.CHROME.getDriverName(), pathWebdriver);
        return new ChromeDriver(capabilities);
    }

    /**
     * Generate selenium webdriver. By default a phantomJS driver is generate
     *
     * @param driverName
     * @return
     */
    private WebDriver generateWebDriver(String driverName) {
        WebDriver driver;
        if ("ie".equals(driverName)) {
            driver = generateIEDriver();
        } else if ("chrome".equals(driverName)) {
            driver = generateGoogleChromeDriver();
        } else {
            driver = generatePhantomJsDriver();
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.MILLISECONDS);
        drivers.put(driverName, driver);
        return driver;

    }

    public static String getPath(Driver currentDriver) {
        OperatingSystem currentOperatingSystem = OperatingSystem.getCurrentOperatingSystem();
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

package noraui.browser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.beust.jcommander.internal.Nullable;

public class WindowManager {

    /**
     * Main window.
     */
    private String mainWindow;

    /**
     * Windows.
     */
    private HashMap<String, String> windows;

    public WindowManager() {
        this.windows = new HashMap<>();
        this.mainWindow = "";
    }

    public String getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(String window) {
        mainWindow = window;
    }

    public Map<String, String> getWindows() {
        return windows;
    }

    public String getWindow(String key) {
        return windows.get(key);
    }

    public void addWindow(String key, String windowHandle) {
        windows.put(key, windowHandle);
    }

    public void removeWindow(String key) {
        windows.remove(key);
    }

    /**
     * Clear loaded windows
     */
    public void clear() {
        windows.clear();
    }

    /**
     * @param currentHandles
     *            is list of opened windows.
     * @return a string with new Window Opens (GUID)
     */
    public static ExpectedCondition<String> newWindowOpens(final Set<String> currentHandles) {
        return new ExpectedCondition<String>() {
            
            /**
             * {@inheritDoc}
             */
            @Override
            public String apply(@Nullable WebDriver driver) {
                if (driver != null && !currentHandles.equals(driver.getWindowHandles())) {
                    for (String s : driver.getWindowHandles()) {
                        if (!currentHandles.contains(s)) {
                            return s;
                        }
                    }
                }
                return null;
            }
        };
    }

    /**
     * Click can be done without alert on Element.
     *
     * @param element
     *            is target WebElement.
     * @return true or false
     */
    public static ExpectedCondition<Boolean> clickCanBeDoneWithoutAlertOnElement(final WebElement element) {
        return new ExpectedCondition<Boolean>() {
            
            /**
             * {@inheritDoc}
             */
            @Override
            public Boolean apply(@Nullable WebDriver driver) {
                try {
                    element.click();
                    return true;
                } catch (UnhandledAlertException e) {
                    driver.switchTo().alert().dismiss();
                } catch (ElementNotVisibleException e) {
                }
                return false;
            }
        };
    }

    /**
     * click can be done without alert on element located.
     *
     * @param locator
     *            is the selenium locator
     * @return true or false
     */
    public static ExpectedCondition<Boolean> clickCanBeDoneWithoutAlertOnElementLocated(final By locator) {
        return new ExpectedCondition<Boolean>() {
            /**
             * {@inheritDoc}
             */
            @Override
            public Boolean apply(@Nullable WebDriver driver) {
                try {
                    driver.findElement(locator).click();
                    return true;
                } catch (UnhandledAlertException e) {
                    driver.switchTo().alert().dismiss();
                } catch (ElementNotVisibleException e) {
                }
                return false;
            }
        };
    }
}

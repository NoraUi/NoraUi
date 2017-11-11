package noraui.utils;

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import noraui.Runner;
import noraui.application.page.Page;
import noraui.application.page.demo.DemoPage;
import noraui.cucumber.injector.NoraUiInjector;
import noraui.cucumber.injector.NoraUiInjectorSource;
import noraui.exception.TechnicalException;

public class UtilitiesUT {

    public static final String DEMO_PAGE_NAME = "demo.DemoPage";

    DemoPage demoPage;

    @Before
    public void setUp() throws TechnicalException {
        NoraUiInjector.resetInjector();
        new NoraUiInjectorSource().getInjector();
        demoPage = (DemoPage) Page.getInstance(DEMO_PAGE_NAME);
    }

    @After
    public void tearDown() {
        NoraUiInjector.resetInjector();
    }

    @Test
    public void testGetLocatorValue() {
        // prepare mock
        Context.iniFiles = new HashMap<>();
        Context.initApplicationDom(Runner.class.getClassLoader(), "V1", demoPage.getApplication());

        // run test
        String value = Utilities.getSelectorValue(demoPage.getApplication(), demoPage.getPageKey() + demoPage.xpathContainPercentChar, 1);
        Assert.assertEquals("OK", ".//input[@name='%%Surrogate_LstPrestComp']/following-sibling::label[1]/input", value);
    }

}

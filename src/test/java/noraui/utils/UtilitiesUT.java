package noraui.utils;

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;

import noraui.Runner;
import noraui.application.page.demo.DemoPage;
import noraui.cucumber.injector.NoraUiInjector;
import noraui.cucumber.injector.NoraUiInjectorSource;
import noraui.exception.TechnicalException;

public class UtilitiesUT {

    @Inject
    DemoPage demoPage;

    @Before
    public void setUp() throws TechnicalException {
        NoraUiInjector.resetInjector();
        new NoraUiInjectorSource().getInjector();
    }

    @After
    public void tearDown() {
        NoraUiInjector.resetInjector();
    }

    @Test
    public void testGetLocatorValue() {
        // prepare mock
        Context.iniFiles = new HashMap<>();
        Context.initApplicationDom(Runner.class.getClassLoader(), "V1", this.demoPage.getApplication());

        // run test
        String value = Utilities.getSelectorValue(this.demoPage.getApplication(), this.demoPage.getPageKey() + this.demoPage.xpathContainPercentChar, 1);
        Assert.assertEquals("OK", ".//input[@name='%%Surrogate_LstPrestComp']/following-sibling::label[1]/input", value);
    }

}

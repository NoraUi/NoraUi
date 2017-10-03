package noraui.utils;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import noraui.Runner;
import noraui.application.page.demo.DemoPage;

public class UtilitiesUT {

    Utilities utilities;
    DemoPage demoPage;

    @Before
    public void setUp() {
        utilities = new Utilities();
        demoPage = new DemoPage();
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

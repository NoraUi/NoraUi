package noraui.application.page;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import noraui.application.page.demo.DemoPage;
import noraui.cucumber.injector.NoraUiInjectorSource;
import noraui.exception.TechnicalException;

public class PageUT {

    public static final String DEMO_PAGE_NAME = "demo.DemoPage";

    @Before
    public void setUp() {
        new NoraUiInjectorSource().getInjector();
    }

    @Test
    public void checkGuiceSingletonTest() throws TechnicalException {
        DemoPage demoPage1 = (DemoPage) Page.getInstance(DEMO_PAGE_NAME);
        DemoPage demoPage2 = (DemoPage) Page.getInstance(DEMO_PAGE_NAME);
        Assert.assertEquals(demoPage1, demoPage2);
    }

}

/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.page;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.noraui.application.page.demo.DemoPage;
import com.github.noraui.cucumber.injector.NoraUiInjector;
import com.github.noraui.cucumber.injector.NoraUiInjectorSource;
import com.github.noraui.exception.TechnicalException;

public class PageUT {

    public static final String DEMO_PAGE_NAME = "demo.DemoPage";

    @Before
    public void setUp() {
        NoraUiInjector.resetInjector();
        new NoraUiInjectorSource().getInjector();
    }

    @After
    public void tearDown() {
        NoraUiInjector.resetInjector();
    }

    @Test
    public void checkGuiceSingletonTest() throws TechnicalException {
        DemoPage demoPage1 = (DemoPage) Page.getInstance(DEMO_PAGE_NAME);
        DemoPage demoPage2 = (DemoPage) Page.getInstance(DEMO_PAGE_NAME);
        Assert.assertEquals(demoPage1, demoPage2);
    }

}

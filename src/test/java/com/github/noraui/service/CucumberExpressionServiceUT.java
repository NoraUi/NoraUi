package com.github.noraui.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.noraui.cucumber.injector.NoraUiInjector;
import com.github.noraui.cucumber.injector.NoraUiInjectorSource;
import com.github.noraui.exception.TechnicalException;
import com.google.gson.Gson;
import com.google.inject.Inject;

public class CucumberExpressionServiceUT {
    
    @Inject
    private CucumberExpressionService cucumberExpressionService;
    
    @Before
    public void setUp() throws TechnicalException {
        NoraUiInjector.resetInjector();
        new NoraUiInjectorSource().getInjector().injectMembers(this);
    }

    @After
    public void tearDown() {
        NoraUiInjector.resetInjector();
    }

    @Test
    public void testCucumberExpressionMatch() {
        String res = new Gson().toJson(cucumberExpressionService.match("I expect to have {string} with the text {string}(\\?)", "I expect to have 'foo' with the text 'bar'?"));
        Assert.assertEquals("[\"foo\",\"bar\"]", res);
        res = new Gson().toJson(cucumberExpressionService.match("I expect to have {string} with the text {string}(\\?)", "I expect to have 'foo' with the text 'bar'"));
        Assert.assertEquals("[\"foo\",\"bar\"]", res);
        res = new Gson().toJson(cucumberExpressionService.match("I wait {int} second(s)(\\?)", "I wait 1 second?"));
        Assert.assertEquals("[1]", res);
        res = new Gson().toJson(cucumberExpressionService.match("I wait {int} second(s)(\\?)", "I wait 1 second"));
        Assert.assertEquals("[1]", res);
        res = new Gson().toJson(cucumberExpressionService.match("I wait {int} second(s)(\\?)", "I wait 2 seconds?"));
        Assert.assertEquals("[2]", res);
        res = new Gson().toJson(cucumberExpressionService.match("I wait {int} second(s)(\\?)", "I wait 2 seconds"));
        Assert.assertEquals("[2]", res);
        res = new Gson().toJson(cucumberExpressionService.match("I wait {int} second(s)(\\?)", "I wait 2 seconds!")); //false
        Assert.assertEquals("null", res);
        res = new Gson().toJson(cucumberExpressionService.match("If {string} matches {string}, While {string} respects {string} I do with {int} max tries:", "If 'foo' matches '.+', While 'bakery.DemoPage-big_title' respects 'This is a demo for NORAUI.*' I do with 3 max tries:"));
        Assert.assertEquals("[\"foo\",\".+\",\"bakery.DemoPage-big_title\",\"This is a demo for NORAUI.*\",3]", res);
    }

}

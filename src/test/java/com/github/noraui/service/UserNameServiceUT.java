/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.github.noraui.cucumber.injector.NoraUiInjector;
import com.github.noraui.cucumber.injector.NoraUiInjectorSource;
import com.github.noraui.exception.TechnicalException;
import com.google.inject.Inject;

public class UserNameServiceUT {

    @Inject
    private UserNameService userNameService;

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
    public void testFindOptionByIgnoreCaseText() {
        final WebElement peterOption = mockOption("Peter");
        final WebElement stephaneOption = mockOption("Stéphane le cléac'h");
        final WebElement noelOption = mockOption("Noël");
        final WebElement katyOption = mockOption("Céline");
        final WebElement bradOption = mockOption("Brad ");
        final WebElement pierreOption = mockOption(" Pierre");
        final WebElement jeanChristopheOption = mockOption("Jean Christophe CAROTTE");
        final List<WebElement> options = Arrays.asList(peterOption, stephaneOption, noelOption, katyOption, bradOption, pierreOption, jeanChristopheOption);

        final WebElement element = Mockito.mock(WebElement.class);
        Mockito.when(element.getTagName()).thenReturn("select");
        Mockito.when(element.findElements(By.tagName("option"))).thenReturn(options);
        Select select = new Select(element);

        Assert.assertEquals("KO", -1, userNameService.findOptionByIgnoreCaseText("fake", select));

        Assert.assertEquals("OK", 0, userNameService.findOptionByIgnoreCaseText("Peter", select));
        Assert.assertEquals("OK", 0, userNameService.findOptionByIgnoreCaseText(" Peter", select));
        Assert.assertEquals("OK", 0, userNameService.findOptionByIgnoreCaseText("Peter ", select));
        Assert.assertEquals("OK", 0, userNameService.findOptionByIgnoreCaseText("peter", select));
        Assert.assertEquals("OK", 0, userNameService.findOptionByIgnoreCaseText(" peter", select));
        Assert.assertEquals("OK", 0, userNameService.findOptionByIgnoreCaseText("peter ", select));

        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText("Stéphane le cléac'h", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText("Stephane le cleac'h", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText("Stéphane le cléac h", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText("Stephane le cléac h", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText("Stéphane le cléac'h ", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText("Stephane le cleac'h ", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText("Stéphane le cléac h ", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText("Stephane le cléac h ", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" Stéphane le cléac'h", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" Stephane le cleac'h", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" Stéphane le cléac h", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" Stephane le cléac h", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" Stéphane le cléac'h ", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" Stephane le cleac'h ", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" Stéphane le cléac h ", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" Stephane le cléac h ", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText("STÉPHANE LE CLÉAC'H ", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" STEPHANE LE CLEAC'H", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" STÉPHANE LE CLÉAC'H ", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" STÉPHANE LE CLÉAC H ", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText("LE CLÉAC'H STÉPHANE", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" LE CLÉAC H STÉPHANE", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText("LE CLÉAC'H STÉPHANE ", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" LE CLÉAC'H STÉPHANE", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" LE CLÉAC'H STÉPHANE ", select));
        Assert.assertEquals("OK", 1, userNameService.findOptionByIgnoreCaseText(" LE CLÉAC H STÉPHANE ", select));

        Assert.assertEquals("OK", 2, userNameService.findOptionByIgnoreCaseText("Noël", select));
        Assert.assertEquals("OK", 2, userNameService.findOptionByIgnoreCaseText("Noel", select));

        Assert.assertEquals("OK", 3, userNameService.findOptionByIgnoreCaseText("Celine", select));
        Assert.assertEquals("OK", 3, userNameService.findOptionByIgnoreCaseText(" Celine", select));
        Assert.assertEquals("OK", 3, userNameService.findOptionByIgnoreCaseText("Celine ", select));
        Assert.assertEquals("OK", 3, userNameService.findOptionByIgnoreCaseText(" Celine ", select));
        Assert.assertEquals("OK", 3, userNameService.findOptionByIgnoreCaseText("Céline", select));
        Assert.assertEquals("OK", 3, userNameService.findOptionByIgnoreCaseText(" Céline", select));
        Assert.assertEquals("OK", 3, userNameService.findOptionByIgnoreCaseText("Céline ", select));
        Assert.assertEquals("OK", 3, userNameService.findOptionByIgnoreCaseText(" Céline ", select));

        Assert.assertEquals("OK", 4, userNameService.findOptionByIgnoreCaseText("Brad", select));
        Assert.assertEquals("OK", 4, userNameService.findOptionByIgnoreCaseText(" Brad", select));
        Assert.assertEquals("OK", 4, userNameService.findOptionByIgnoreCaseText("Brad ", select));
        Assert.assertEquals("OK", 4, userNameService.findOptionByIgnoreCaseText(" Brad ", select));

        Assert.assertEquals("OK", 5, userNameService.findOptionByIgnoreCaseText("Pierre", select));
        Assert.assertEquals("OK", 5, userNameService.findOptionByIgnoreCaseText(" Pierre", select));
        Assert.assertEquals("OK", 5, userNameService.findOptionByIgnoreCaseText("Pierre ", select));
        Assert.assertEquals("OK", 5, userNameService.findOptionByIgnoreCaseText(" Pierre ", select));

        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText("Jean Christophe CAROTTE", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText("JEAN CHRISTOPHE CAROTTE", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText("Jean-Christophe CAROTTE", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText("jean christophe carotte", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText("CAROTTE JEAN-CHRISTOPHE", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText("carotte jean-christophe", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText("carotte jean christophe", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText("CAROTTE JEAN-CHRISTOPHE ", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText("carotte jean-christophe ", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText("carotte jean christophe ", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText(" CAROTTE JEAN-CHRISTOPHE", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText(" carotte jean-christophe", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText(" carotte jean christophe", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText(" CAROTTE JEAN-CHRISTOPHE ", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText(" carotte jean-christophe ", select));
        Assert.assertEquals("OK", 6, userNameService.findOptionByIgnoreCaseText(" carotte jean christophe ", select));
    }

    private WebElement mockOption(String name) {
        final WebElement option = Mockito.mock(WebElement.class, name);
        Mockito.when(option.getText()).thenReturn(name);
        return option;
    }

}

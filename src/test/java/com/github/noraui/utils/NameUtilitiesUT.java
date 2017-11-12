package com.github.noraui.utils;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.github.noraui.utils.NameUtilities;

public class NameUtilitiesUT {

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

        Assert.assertEquals("KO", -1, NameUtilities.findOptionByIgnoreCaseText("fake", select));

        Assert.assertEquals("OK", 0, NameUtilities.findOptionByIgnoreCaseText("Peter", select));
        Assert.assertEquals("OK", 0, NameUtilities.findOptionByIgnoreCaseText(" Peter", select));
        Assert.assertEquals("OK", 0, NameUtilities.findOptionByIgnoreCaseText("Peter ", select));
        Assert.assertEquals("OK", 0, NameUtilities.findOptionByIgnoreCaseText("peter", select));
        Assert.assertEquals("OK", 0, NameUtilities.findOptionByIgnoreCaseText(" peter", select));
        Assert.assertEquals("OK", 0, NameUtilities.findOptionByIgnoreCaseText("peter ", select));

        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText("Stéphane le cléac'h", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText("Stephane le cleac'h", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText("Stéphane le cléac h", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText("Stephane le cléac h", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText("Stéphane le cléac'h ", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText("Stephane le cleac'h ", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText("Stéphane le cléac h ", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText("Stephane le cléac h ", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" Stéphane le cléac'h", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" Stephane le cleac'h", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" Stéphane le cléac h", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" Stephane le cléac h", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" Stéphane le cléac'h ", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" Stephane le cleac'h ", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" Stéphane le cléac h ", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" Stephane le cléac h ", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText("STÉPHANE LE CLÉAC'H ", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" STEPHANE LE CLEAC'H", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" STÉPHANE LE CLÉAC'H ", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" STÉPHANE LE CLÉAC H ", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText("LE CLÉAC'H STÉPHANE", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" LE CLÉAC H STÉPHANE", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText("LE CLÉAC'H STÉPHANE ", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" LE CLÉAC'H STÉPHANE", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" LE CLÉAC'H STÉPHANE ", select));
        Assert.assertEquals("OK", 1, NameUtilities.findOptionByIgnoreCaseText(" LE CLÉAC H STÉPHANE ", select));

        Assert.assertEquals("OK", 2, NameUtilities.findOptionByIgnoreCaseText("Noël", select));
        Assert.assertEquals("OK", 2, NameUtilities.findOptionByIgnoreCaseText("Noel", select));

        Assert.assertEquals("OK", 3, NameUtilities.findOptionByIgnoreCaseText("Celine", select));
        Assert.assertEquals("OK", 3, NameUtilities.findOptionByIgnoreCaseText(" Celine", select));
        Assert.assertEquals("OK", 3, NameUtilities.findOptionByIgnoreCaseText("Celine ", select));
        Assert.assertEquals("OK", 3, NameUtilities.findOptionByIgnoreCaseText(" Celine ", select));
        Assert.assertEquals("OK", 3, NameUtilities.findOptionByIgnoreCaseText("Céline", select));
        Assert.assertEquals("OK", 3, NameUtilities.findOptionByIgnoreCaseText(" Céline", select));
        Assert.assertEquals("OK", 3, NameUtilities.findOptionByIgnoreCaseText("Céline ", select));
        Assert.assertEquals("OK", 3, NameUtilities.findOptionByIgnoreCaseText(" Céline ", select));

        Assert.assertEquals("OK", 4, NameUtilities.findOptionByIgnoreCaseText("Brad", select));
        Assert.assertEquals("OK", 4, NameUtilities.findOptionByIgnoreCaseText(" Brad", select));
        Assert.assertEquals("OK", 4, NameUtilities.findOptionByIgnoreCaseText("Brad ", select));
        Assert.assertEquals("OK", 4, NameUtilities.findOptionByIgnoreCaseText(" Brad ", select));

        Assert.assertEquals("OK", 5, NameUtilities.findOptionByIgnoreCaseText("Pierre", select));
        Assert.assertEquals("OK", 5, NameUtilities.findOptionByIgnoreCaseText(" Pierre", select));
        Assert.assertEquals("OK", 5, NameUtilities.findOptionByIgnoreCaseText("Pierre ", select));
        Assert.assertEquals("OK", 5, NameUtilities.findOptionByIgnoreCaseText(" Pierre ", select));

        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText("Jean Christophe CAROTTE", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText("JEAN CHRISTOPHE CAROTTE", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText("Jean-Christophe CAROTTE", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText("jean christophe carotte", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText("CAROTTE JEAN-CHRISTOPHE", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText("carotte jean-christophe", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText("carotte jean christophe", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText("CAROTTE JEAN-CHRISTOPHE ", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText("carotte jean-christophe ", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText("carotte jean christophe ", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText(" CAROTTE JEAN-CHRISTOPHE", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText(" carotte jean-christophe", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText(" carotte jean christophe", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText(" CAROTTE JEAN-CHRISTOPHE ", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText(" carotte jean-christophe ", select));
        Assert.assertEquals("OK", 6, NameUtilities.findOptionByIgnoreCaseText(" carotte jean christophe ", select));
    }

    private WebElement mockOption(String name) {
        final WebElement option = Mockito.mock(WebElement.class, name);
        Mockito.when(option.getText()).thenReturn(name);
        return option;
    }

}

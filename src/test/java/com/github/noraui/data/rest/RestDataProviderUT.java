package com.github.noraui.data.rest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;

import com.github.noraui.data.rest.RestDataProvider;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.exception.data.WebServicesException;

public class RestDataProviderUT {

    @Test
    public void testConstructorIsPublic() throws Exception {
        Constructor<RestDataProvider> constructor = RestDataProvider.class.getDeclaredConstructor(String.class, String.class, String.class);
        Assert.assertTrue(Modifier.isPublic(constructor.getModifiers()));
        constructor.setAccessible(true);
    }

    @Test
    public void testWriteXxxxxResult() throws WebServicesException, TechnicalException {
        RestDataProvider restDataProvider = new RestDataProvider(RestDataProvider.types.JSON.toString(), "http://localhost", "8084");
        restDataProvider.prepare("hello");
        Assert.assertTrue(true);

        Assert.assertEquals(9, restDataProvider.getNbLines());
        restDataProvider.writeFailedResult(1, "UT Failed Message");
        restDataProvider.writeSuccessResult(2);
        restDataProvider.writeWarningResult(3, "UT Warning Message");
        restDataProvider.writeDataResult("title", 4, "UT title");
    }
}

/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data.rest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.noraui.exception.HttpServiceException;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.exception.data.WebServicesException;
import com.github.noraui.service.HttpService;

public class RestDataProviderUT {

    @Test
    public void testConstructorIsPublic() throws Exception {
        Constructor<RestDataProvider> constructor = RestDataProvider.class.getDeclaredConstructor(String.class, String.class, String.class);
        Assert.assertTrue(Modifier.isPublic(constructor.getModifiers()));
        constructor.setAccessible(true);
    }

    @Test
    public void testWriteXxxxxResult() throws WebServicesException, TechnicalException, HttpServiceException {
        final HttpService httpServiceMock = Mockito.mock(HttpService.class);
        Mockito.when(httpServiceMock.get("http://localhost:8084/noraui/api/hello/columns"))
                .thenReturn("{\"columns\":[\"author\",\"zip\",\"city\",\"element\",\"element2\",\"date\",\"title\"],\"rows\":null}");

        Mockito.when(httpServiceMock.get("http://localhost:8084/noraui/api/", "hello/nbLines")).thenReturn("8");

        Mockito.when(httpServiceMock.post("http://localhost:8084/noraui/api/hello/column/7/line/1", "UT Failed Message")).thenReturn(
                "{\"columns\":[\"author\",\"zip\",\"city\",\"element\",\"element2\",\"date\",\"title\"],\"rows\":[{\"columns\":[\"Jenkins T1\",\"35000\",\"Rennes\",\"smile\",\"smile\",\"MY TITLE\",\"\"],\"errorStepIndex\":-1},{\"columns\":[\"Jenkins T2\",\"75000\",\"Paris\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":24},{\"columns\":[\"Jenkins T3\",\"56100\",\"Lorient\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":18},{\"columns\":[\"Jenkins T4\",\"35000\",\"Rennes\",\"noExistElement\",\"smile\",\"\",\"\"],\"errorStepIndex\":-1},{\"columns\":[\"Jenkins T5\",\"35000\",\"Rennes\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":29},{\"columns\":[\"Jenkins T6\",\"35000\",\"\",\"\",\"\",\"\",\"\"],\"errorStepIndex\":2},{\"columns\":[\"Jenkins T7\",\"35000\",\"Rennes\",\"\",\"\",\"\",\"\"],\"errorStepIndex\":3},{\"columns\":[\"Jenkins T8\",\"\",\"Rennes\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":44}]}");

        Mockito.when(httpServiceMock.post("http://localhost:8084/noraui/api/hello/column/7/line/2", "Succès")).thenReturn(
                "{\"columns\":[\"author\",\"zip\",\"city\",\"element\",\"element2\",\"date\",\"title\"],\"rows\":[{\"columns\":[\"Jenkins T1\",\"35000\",\"Rennes\",\"smile\",\"smile\",\"MY TITLE\",\"\"],\"errorStepIndex\":-1},{\"columns\":[\"Jenkins T2\",\"75000\",\"Paris\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":24},{\"columns\":[\"Jenkins T3\",\"56100\",\"Lorient\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":18},{\"columns\":[\"Jenkins T4\",\"35000\",\"Rennes\",\"noExistElement\",\"smile\",\"\",\"\"],\"errorStepIndex\":-1},{\"columns\":[\"Jenkins T5\",\"35000\",\"Rennes\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":29},{\"columns\":[\"Jenkins T6\",\"35000\",\"\",\"\",\"\",\"\",\"\"],\"errorStepIndex\":2},{\"columns\":[\"Jenkins T7\",\"35000\",\"Rennes\",\"\",\"\",\"\",\"\"],\"errorStepIndex\":3},{\"columns\":[\"Jenkins T8\",\"\",\"Rennes\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":44}]}");

        Mockito.when(httpServiceMock.post("http://localhost:8084/noraui/api/hello/column/7/line/3", "UT Warning Message")).thenReturn(
                "{\"columns\":[\"author\",\"zip\",\"city\",\"element\",\"element2\",\"date\",\"title\"],\"rows\":[{\"columns\":[\"Jenkins T1\",\"35000\",\"Rennes\",\"smile\",\"smile\",\"MY TITLE\",\"\"],\"errorStepIndex\":-1},{\"columns\":[\"Jenkins T2\",\"75000\",\"Paris\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":24},{\"columns\":[\"Jenkins T3\",\"56100\",\"Lorient\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":18},{\"columns\":[\"Jenkins T4\",\"35000\",\"Rennes\",\"noExistElement\",\"smile\",\"\",\"\"],\"errorStepIndex\":-1},{\"columns\":[\"Jenkins T5\",\"35000\",\"Rennes\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":29},{\"columns\":[\"Jenkins T6\",\"35000\",\"\",\"\",\"\",\"\",\"\"],\"errorStepIndex\":2},{\"columns\":[\"Jenkins T7\",\"35000\",\"Rennes\",\"\",\"\",\"\",\"\"],\"errorStepIndex\":3},{\"columns\":[\"Jenkins T8\",\"\",\"Rennes\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":44}]}");

        Mockito.when(httpServiceMock.post("http://localhost:8084/noraui/api/hello/column/6/line/4", "UT title")).thenReturn(
                "{\"columns\":[\"author\",\"zip\",\"city\",\"element\",\"element2\",\"date\",\"title\"],\"rows\":[{\"columns\":[\"Jenkins T1\",\"35000\",\"Rennes\",\"smile\",\"smile\",\"MY TITLE\",\"\"],\"errorStepIndex\":-1},{\"columns\":[\"Jenkins T2\",\"75000\",\"Paris\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":24},{\"columns\":[\"Jenkins T3\",\"56100\",\"Lorient\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":18},{\"columns\":[\"Jenkins T4\",\"35000\",\"Rennes\",\"noExistElement\",\"smile\",\"\",\"UT title\"],\"errorStepIndex\":-1},{\"columns\":[\"Jenkins T5\",\"35000\",\"Rennes\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":29},{\"columns\":[\"Jenkins T6\",\"35000\",\"\",\"\",\"\",\"\",\"\"],\"errorStepIndex\":2},{\"columns\":[\"Jenkins T7\",\"35000\",\"Rennes\",\"\",\"\",\"\",\"\"],\"errorStepIndex\":3},{\"columns\":[\"Jenkins T8\",\"\",\"Rennes\",\"smile\",\"smile\",\"\",\"\"],\"errorStepIndex\":44}]}");

        RestDataProvider restDataProvider = new RestDataProvider(RestDataProvider.types.JSON.toString(), "http://localhost", "8084");
        restDataProvider.setHttpService(httpServiceMock);

        restDataProvider.prepare("hello");
        Assert.assertTrue(true);

        Assert.assertEquals(9, restDataProvider.getNbLines());
        restDataProvider.writeFailedResult(1, "UT Failed Message");
        restDataProvider.writeSuccessResult(2);
        restDataProvider.writeWarningResult(3, "UT Warning Message");
        restDataProvider.writeDataResult("title", 4, "UT title");
    }
}

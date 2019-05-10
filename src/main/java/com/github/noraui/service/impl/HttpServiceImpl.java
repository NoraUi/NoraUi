/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.exception.HttpServiceException;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.service.HttpService;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.google.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Singleton
public class HttpServiceImpl implements HttpService {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(HttpServiceImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String url) throws HttpServiceException, TechnicalException {
        logger.debug("HttpService GET on url: {}", url);
        Response response;
        try {
            response = getClient().newCall(new Request.Builder().url(new URL(url)).header("Accept", "application/json").build()).execute();
            String jsonResponse = response.body().string();
            logger.info("JSON response is: {}", jsonResponse);
            response.close();
            return jsonResponse;
        } catch (IOException e) {
            logger.error(Messages.getMessage(HttpServiceException.HTTP_SERVICE_ERROR_MESSAGE), e);
            throw new HttpServiceException(Messages.getMessage(HttpServiceException.HTTP_SERVICE_ERROR_MESSAGE), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String baseUrl, String uri) throws HttpServiceException, TechnicalException {
        return get(baseUrl + uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String post(String url, String json) throws HttpServiceException, TechnicalException {
        logger.debug("HttpService POST on url: {}", url);
        Response response;
        try {
            response = getClient().newCall(new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json"), json)).build()).execute();
            String jsonResponse = response.body().string();
            logger.info("JSON response is: {}", jsonResponse);
            response.close();
            return jsonResponse;
        } catch (IOException e) {
            logger.error(Messages.getMessage(HttpServiceException.HTTP_SERVICE_ERROR_MESSAGE), e);
            throw new HttpServiceException(Messages.getMessage(HttpServiceException.HTTP_SERVICE_ERROR_MESSAGE), e);
        }
    }

    /**
     * @return
     */
    private OkHttpClient getClient() {
        OkHttpClient client;
        org.openqa.selenium.Proxy proxy = Context.getProxy();
        if (proxy != null && proxy.getHttpProxy() != null && !"".equals(proxy.getHttpProxy())) {
            String[] p = proxy.getHttpProxy().split(":");
            client = new OkHttpClient.Builder().proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(p[0], Integer.parseInt(p[1])))).build();
        } else {
            client = new OkHttpClient.Builder().build();
        }
        return client;
    }

}

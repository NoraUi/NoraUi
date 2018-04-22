/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service;

import com.github.noraui.exception.HttpServiceException;
import com.github.noraui.exception.TechnicalException;

public interface HttpService {

    /**
     * @param url
     *            is full url
     * @return json string
     * @throws HttpServiceException
     * @throws TechnicalException
     */
    String get(String url) throws HttpServiceException, TechnicalException;

    /**
     * @param baseUrl
     *            beginning of the url
     * @param uri
     *            end of the url
     * @return json string
     * @throws HttpServiceException
     * @throws TechnicalException
     */
    String get(String baseUrl, String uri) throws HttpServiceException, TechnicalException;

    /**
     * @param url
     *            is full url
     * @param json
     *            body string
     * @return json string
     * @throws HttpServiceException
     * @throws TechnicalException
     */
    String post(String url, String json) throws HttpServiceException, TechnicalException;

}

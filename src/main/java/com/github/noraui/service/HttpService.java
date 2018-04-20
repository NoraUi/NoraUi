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

    String get(String url) throws HttpServiceException, TechnicalException;

    String get(String baseUrl, String uri) throws HttpServiceException, TechnicalException;

    String post(String url, String json) throws HttpServiceException, TechnicalException;

}

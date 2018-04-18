/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service;

import com.github.noraui.exception.TechnicalException;

public interface CryptoService {

    String getPrefix();

    String encrypt(String text) throws TechnicalException;

    String decrypt(String encrypted) throws TechnicalException;

    String encrypt(String cryptoKey, String text) throws TechnicalException;

    String decrypt(String cryptoKey, String encrypted) throws TechnicalException;

}

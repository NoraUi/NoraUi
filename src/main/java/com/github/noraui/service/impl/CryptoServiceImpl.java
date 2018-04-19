/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service.impl;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.exception.TechnicalException;
import com.github.noraui.service.CryptoService;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;
import com.google.inject.Singleton;

@Singleton
public class CryptoServiceImpl implements CryptoService {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(Utilities.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPrefix() {
        return "℗:";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encrypt(String text) throws TechnicalException {
        return encrypt(Context.getCryptoKey(), text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String decrypt(String encrypted) throws TechnicalException {
        return decrypt(Context.getCryptoKey(), encrypted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encrypt(String cryptoKey, String text) throws TechnicalException {
        Key aesKey = null;
        if (cryptoKey != null && !"".equals(cryptoKey)) {
            do {
                cryptoKey = cryptoKey + cryptoKey;
            } while (cryptoKey.length() < 16);
            aesKey = new SecretKeySpec(cryptoKey.substring(0, 16).getBytes(), "AES");
        }
        if (aesKey == null) {
            logger.error(TechnicalException.TECHNICAL_ERROR_MESSAGE_DECRYPT_CONFIGURATION_EXCEPTION);
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DECRYPT_CONFIGURATION_EXCEPTION));
        }
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return getPrefix() + Base64.encodeBase64String(cipher.doFinal(text.getBytes()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            logger.error(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_ENCRYPT_EXCEPTION), e);
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_ENCRYPT_EXCEPTION), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String decrypt(String cryptoKey, String encrypted) throws TechnicalException {
        if (!encrypted.startsWith(getPrefix())) {
            logger.error(TechnicalException.TECHNICAL_ERROR_MESSAGE_DECRYPT_EXCEPTION);
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DECRYPT_EXCEPTION));
        }
        Key aesKey = null;
        if (cryptoKey != null && !"".equals(cryptoKey)) {
            aesKey = buildKey16char(cryptoKey);
        }
        if (aesKey == null) {
            logger.error(TechnicalException.TECHNICAL_ERROR_MESSAGE_DECRYPT_CONFIGURATION_EXCEPTION);
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DECRYPT_CONFIGURATION_EXCEPTION));
        }
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(Base64.decodeBase64(encrypted.substring(getPrefix().length(), encrypted.length()))));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DECRYPT_EXCEPTION), e);
        }
    }

    /**
     * @param cryptoKey
     * @return
     */
    private Key buildKey16char(String cryptoKey) {
        Key aesKey;
        StringBuilder cryptoKeyBuilder = new StringBuilder();
        cryptoKeyBuilder.append(cryptoKey);
        do {
            cryptoKeyBuilder.append(cryptoKey);
        } while (cryptoKeyBuilder.toString().length() < 16);
        aesKey = new SecretKeySpec(cryptoKeyBuilder.toString().substring(0, 16).getBytes(), "AES");
        return aesKey;
    }

}

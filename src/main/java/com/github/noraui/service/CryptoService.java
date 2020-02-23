/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service;

import com.github.noraui.exception.TechnicalException;

public interface CryptoService {

    /**
     * @return prefix use by NoraUi ("℗:").
     */
    String getPrefix();

    /**
     * encrypt given text as plainText, use cryptoKey as string in context and return encrypted plainText.
     * 
     * @param text
     *            is plain text.
     * @return encrypted plainText.
     * @throws TechnicalException
     *             is thrown if you have a technical error (NoSuchAlgorithmException or NoSuchPaddingException or InvalidKeyException or IllegalBlockSizeException or BadPaddingException, ...) in
     *             NoraUi.
     */
    String encrypt(String text) throws TechnicalException;

    /**
     * decrypt given text as plainText (encrypted), use cryptoKey as string in context and return decrypted plainText.
     * 
     * @param text
     *            is encrypted plain text.
     * @return decrypted plainText.
     * @throws TechnicalException
     *             is thrown if you have a technical error (NoSuchAlgorithmException or NoSuchPaddingException or InvalidKeyException or IllegalBlockSizeException or BadPaddingException, ...) in
     *             NoraUi.
     */
    String decrypt(String text) throws TechnicalException;

    /**
     * encrypt given cryptoKey as string and text as plainText and return encrypted plainText.
     * 
     * @param cryptoKey
     *            as string.
     * @param text
     *            as string.
     * @return encrypted plainText.
     * @throws TechnicalException
     *             is thrown if you have a technical error (NoSuchAlgorithmException or NoSuchPaddingException or InvalidKeyException or IllegalBlockSizeException or BadPaddingException, ...) in
     *             NoraUi.
     */
    String encrypt(String cryptoKey, String text) throws TechnicalException;

    /**
     * decrypt given cryptoKey as string and text as plainText (encrypted) and return decrypted plainText.
     * 
     * @param cryptoKey
     *            as string.
     * @param encrypted
     *            as string.
     * @return decrypted plainText.
     * @throws TechnicalException
     *             is thrown if you have a technical error (NoSuchAlgorithmException or NoSuchPaddingException or InvalidKeyException or IllegalBlockSizeException or BadPaddingException, ...) in
     *             NoraUi.
     */
    String decrypt(String cryptoKey, String encrypted) throws TechnicalException;

}

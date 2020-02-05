/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.exception;

public class HttpServiceException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1032342565354635929L;

    public static final String HTTP_SERVICE_ERROR_MESSAGE = "HTTP_SERVICE_ERROR_MESSAGE";

    /**
     * Build an object <code>HttpServiceException</code>.
     */
    public HttpServiceException() {
        super();
    }

    /**
     * Build an object <code>HttpServiceException</code>.
     *
     * @param message
     *            error message.
     * @param source
     *            exception source.
     */
    public HttpServiceException(String message, Throwable source) {
        super(message, source);
    }

    /**
     * Build an object <code>HttpServiceException</code>.
     *
     * @param message
     *            error message.
     */
    public HttpServiceException(String message) {
        super(message);
    }

    /**
     * Build an object <code>HttpServiceException</code>.
     *
     * @param source
     *            exception source.
     */
    public HttpServiceException(Throwable source) {
        super(source);
    }

}
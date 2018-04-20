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
     * Construit un objet <code>HttpServiceException</code>.
     */
    public HttpServiceException() {
        super();
    }

    /**
     * Construit un objet <code>HttpServiceException</code>.
     *
     * @param message
     *            le message d'erreur
     * @param source
     *            l'exception source
     */
    public HttpServiceException(String message, Throwable source) {
        super(message, source);
    }

    /**
     * Construit un objet <code>HttpServiceException</code>.
     *
     * @param message
     *            le message d'erreur
     */
    public HttpServiceException(String message) {
        super(message);
    }

    /**
     * Construit un objet <code>HttpServiceException</code>.
     *
     * @param source
     *            l'exception source
     */
    public HttpServiceException(Throwable source) {
        super(source);
    }

}
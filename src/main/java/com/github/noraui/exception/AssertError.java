/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.exception;

public class AssertError extends java.lang.AssertionError {

    /**
     * 
     */
    private static final long serialVersionUID = -5952513628066098655L;

    public AssertError(String message) {
        super(message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }
}

/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.exception.data;

public class DatabaseException extends Exception {

    public static final String TECHNICAL_ERROR_MESSAGE_DATABASE_EXCEPTION = "TECHNICAL_ERROR_MESSAGE_DATABASE_EXCEPTION";
    public static final String TECHNICAL_ERROR_MESSAGE_UNKNOWN_DATABASE_TYPE = "TECHNICAL_ERROR_MESSAGE_UNKNOWN_DATABASE_TYPE";

    /**
     *
     */
    private static final long serialVersionUID = -2117982245895292335L;

    public DatabaseException() {
        super();
    }

    public DatabaseException(String message) {
        super(message);
    }

}

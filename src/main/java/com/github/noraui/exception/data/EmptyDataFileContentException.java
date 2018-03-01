/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.exception.data;

public class EmptyDataFileContentException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -3626482231036913842L;

    public static final String EMPTY_DATA_FILE_CONTENT_ERROR_MESSAGE = "EMPTY_DATA_FILE_CONTENT_ERROR_MESSAGE";

    public EmptyDataFileContentException() {
        super();
    }

    public EmptyDataFileContentException(String message) {
        super(message);
    }

}

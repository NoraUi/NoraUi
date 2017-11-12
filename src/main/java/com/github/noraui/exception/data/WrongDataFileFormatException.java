package com.github.noraui.exception.data;

public class WrongDataFileFormatException extends Exception {

    /**
     * serialUid
     */
    private static final long serialVersionUID = 4190215494417846482L;

    public static final String WRONG_RESULT_COLUMN_NAME_ERROR_MESSAGE = "WRONG_RESULT_COLUMN_NAME_ERROR_MESSAGE";

    public WrongDataFileFormatException() {
        super();
    }

    public WrongDataFileFormatException(String message) {
        super(message);
    }

}

package com.github.noraui.exception.data;

public class WebServicesException extends Exception {

    public static final String TECHNICAL_ERROR_MESSAGE_WEB_SERVICES_EXCEPTION = "TECHNICAL_ERROR_MESSAGE_WEB_SERVICES_EXCEPTION";
    public static final String TECHNICAL_ERROR_MESSAGE_UNKNOWN_WEB_SERVICES_TYPE = "TECHNICAL_ERROR_MESSAGE_UNKNOWN_WEB_SERVICES_TYPE";

    /**
     *
     */
    private static final long serialVersionUID = -2117982245895292335L;

    public WebServicesException() {
        super();
    }

    public WebServicesException(String message) {
        super(message);
    }

}

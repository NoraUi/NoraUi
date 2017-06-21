package noraui.exception.data;

public class WebServicesException extends Exception {

    public static final String TECHNICAL_ERROR_MESSAGE_WEB_SERVICES_EXCEPTION = "Error on opening connection with WebSrecices.";
    public static final String TECHNICAL_ERROR_MESSAGE_UNKNOWN_WEB_SERVICES_TYPE = "Unknown WebSrecices type (%s).";

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

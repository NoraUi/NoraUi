package noraui.exception.data;

public class DatabaseException extends Exception {

    public static final String TECHNICAL_ERROR_MESSAGE_DATABASE_EXCEPTION = "Error on opening connection with database.";
    public static final String TECHNICAL_ERROR_MESSAGE_UNKNOWN_DATABASE_TYPE = "Unknown database type (%s).";

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

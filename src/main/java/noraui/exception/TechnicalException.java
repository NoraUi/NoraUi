package noraui.exception;

public class TechnicalException extends Exception {

    /**
     * serialUid
     */
    private static final long serialVersionUID = -8316324009963694711L;

    public static final String TECHNICAL_ERROR_MESSAGE = "TECHNICAL_ERROR_MESSAGE";
    public static final String TECHNICAL_SUBSTEP_ERROR_MESSAGE = "Erreur lors de l'exécution de la sous-étape: ";
    public static final String TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION = "Wrong data file (IOException, EmptyDataFileContentException or WrongDataFileFormatException)";
    public static final String TECHNICAL_ERROR_MESSAGE_CHECKSUM_IO_EXCEPTION = "NoraUi Security Exception (NoSuchAlgorithmException or IOException)";
    public static final String TECHNICAL_ERROR_MESSAGE_NORAUI_INJECTOR_SOURCE_ALREADY_EXISTS = "noraUiInjectorSource already exists.";
    public static final String TECHNICAL_ERROR_MESSAGE_WEBDRIVER_SET_EXECUTABLE = "Error when Webdriver setExecutable to true";
    public static final String TECHNICAL_EXPECTED_ACTUAL_SIZE_DIFFERENT = "Wrong conditions definition (expected and actual sizes are different).";
    public static final String TECHNICAL_EXPECTED_EXCEL_EXTENTION_ERROR = "If you are using Excel as a dataProvider, you must choose one of the following formats: xls, xlsx, or xlsm.";
    public static final String TECHNICAL_EXPECTED_AT_LEAST_AN_ID_COLUMN_IN_EXAMPLES = "Your examples must contain at least an |<id>| as their first column. Ex: |1|somedata|otherdata|...";

    /**
     * Construit un objet <code>TechnicalException</code>.
     */
    public TechnicalException() {
        super();
    }

    /**
     * Construit un objet <code>TechnicalException</code>.
     *
     * @param message
     *            le message d'erreur
     * @param source
     *            l'exception source
     */
    public TechnicalException(String message, Throwable source) {
        super(message, source);
    }

    /**
     * Construit un objet <code>TechnicalException</code>.
     *
     * @param message
     *            le message d'erreur
     */
    public TechnicalException(String message) {
        super(message);
    }

    /**
     * Construit un objet <code>TechnicalException</code>.
     *
     * @param source
     *            l'exception source
     */
    public TechnicalException(Throwable source) {
        super(source);
    }

}
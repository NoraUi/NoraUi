package com.github.noraui.exception;

public class TechnicalException extends Exception {

    /**
     * serialUid
     */
    private static final long serialVersionUID = -8316324009963694711L;

    public static final String TECHNICAL_ERROR_MESSAGE = "TECHNICAL_ERROR_MESSAGE";
    public static final String TECHNICAL_SUBSTEP_ERROR_MESSAGE = "TECHNICAL_SUBSTEP_ERROR_MESSAGE";
    public static final String TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION = "TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION";
    public static final String TECHNICAL_ERROR_MESSAGE_CHECKSUM_IO_EXCEPTION = "TECHNICAL_ERROR_MESSAGE_CHECKSUM_IO_EXCEPTION";
    public static final String TECHNICAL_ERROR_MESSAGE_NORAUI_INJECTOR_SOURCE_ALREADY_EXISTS = "TECHNICAL_ERROR_MESSAGE_NORAUI_INJECTOR_SOURCE_ALREADY_EXISTS";
    public static final String TECHNICAL_ERROR_MESSAGE_WEBDRIVER_SET_EXECUTABLE = "TECHNICAL_ERROR_MESSAGE_WEBDRIVER_SET_EXECUTABLE";
    public static final String TECHNICAL_ERROR_STEP_UNDEFINED = "TECHNICAL_ERROR_STEP_UNDEFINED";
    public static final String TECHNICAL_EXPECTED_ACTUAL_SIZE_DIFFERENT = "TECHNICAL_EXPECTED_ACTUAL_SIZE_DIFFERENT";
    public static final String TECHNICAL_EXPECTED_EXCEL_EXTENTION_ERROR = "TECHNICAL_EXPECTED_EXCEL_EXTENTION_ERROR";
    public static final String TECHNICAL_EXPECTED_AT_LEAST_AN_ID_COLUMN_IN_EXAMPLES = "TECHNICAL_EXPECTED_AT_LEAST_AN_ID_COLUMN_IN_EXAMPLES";
    public static final String TECHNICAL_ERROR_MESSAGE_FUSION_PROCESSOR = "TECHNICAL_ERROR_MESSAGE_FUSION_PROCESSOR";

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
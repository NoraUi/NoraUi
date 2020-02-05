/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
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
    public static final String TECHNICAL_ERROR_MESSAGE_WEBDRIVER_SET_EXECUTABLE = "TECHNICAL_ERROR_MESSAGE_WEBDRIVER_SET_EXECUTABLE";
    public static final String TECHNICAL_ERROR_STEP_UNDEFINED = "TECHNICAL_ERROR_STEP_UNDEFINED";
    public static final String TECHNICAL_EXPECTED_ACTUAL_SIZE_DIFFERENT = "TECHNICAL_EXPECTED_ACTUAL_SIZE_DIFFERENT";
    public static final String TECHNICAL_EXPECTED_EXCEL_EXTENTION_ERROR = "TECHNICAL_EXPECTED_EXCEL_EXTENTION_ERROR";
    public static final String TECHNICAL_EXPECTED_AT_LEAST_AN_ID_COLUMN_IN_EXAMPLES = "TECHNICAL_EXPECTED_AT_LEAST_AN_ID_COLUMN_IN_EXAMPLES";
    public static final String TECHNICAL_ERROR_MESSAGE_FUSION_PROCESSOR = "TECHNICAL_ERROR_MESSAGE_FUSION_PROCESSOR";
    public static final String TECHNICAL_ERROR_MESSAGE_ENCRYPT_EXCEPTION = "TECHNICAL_ERROR_MESSAGE_ENCRYPT_EXCEPTION";
    public static final String TECHNICAL_ERROR_MESSAGE_DECRYPT_EXCEPTION = "TECHNICAL_ERROR_MESSAGE_DECRYPT_EXCEPTION";
    public static final String TECHNICAL_ERROR_MESSAGE_DECRYPT_CONFIGURATION_EXCEPTION = "TECHNICAL_ERROR_MESSAGE_DECRYPT_CONFIGURATION_EXCEPTION";
    public static final String TECHNICAL_IO_EXCEPTION = "IOException {}";

    /**
     * Build an object <code>TechnicalException</code>.
     */
    public TechnicalException() {
        super();
    }

    /**
     * Build an object <code>TechnicalException</code>.
     *
     * @param message
     *            error message.
     * @param source
     *            exception source.
     */
    public TechnicalException(String message, Throwable source) {
        super(message, source);
    }

    /**
     * Build an object <code>TechnicalException</code>.
     *
     * @param message
     *            error message.
     */
    public TechnicalException(String message) {
        super(message);
    }

    /**
     * Build an object <code>TechnicalException</code>.
     *
     * @param source
     *            exception source.
     */
    public TechnicalException(Throwable source) {
        super(source);
    }

}
package noraui.utils;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import noraui.exception.TechnicalException;

public class Messages {

    private static ResourceBundle messagesBundle = null;

    private static final String FAIL_MESSAGE_FORMAT_STRING = "FAIL_MESSAGE_FORMAT_STRING";

    /**
     * Success message
     */
    public static final String SUCCESS_MESSAGE = "SUCCESS_MESSAGE";

    /**
     * Default error/warning message
     */
    public static final String FAIL_MESSAGE_DEFAULT = "FAIL_MESSAGE_DEFAULT";
    public static final String WARNING_MESSAGE_DEFAULT = "WARNING_MESSAGE_DEFAULT";
    public static final String SUCCESS_MESSAGE_WITH_RESERVE = "SUCCESS_MESSAGE_WITH_RESERVE";
    public static final String NOT_RUN_MESSAGE = "NOT_RUN_MESSAGE";

    /**
     * Errors of Gherkin and Cucumber.
     */
    public static final String SCENARIO_ERROR_MESSAGE_TYPE_NOT_IMPLEMENTED = "SCENARIO_ERROR_MESSAGE_TYPE_NOT_IMPLEMENTED";
    public static final String SCENARIO_ERROR_MESSAGE_ILLEGAL_TAB_FORMAT = "SCENARIO_ERROR_MESSAGE_ILLEGAL_TAB_FORMAT";

    /**
     * Functional fail messages.
     */
    public static final String FAIL_MESSAGE_UNKNOWN_CREDENTIALS = "FAIL_MESSAGE_UNKNOWN_CREDENTIALS";
    public static final String FAIL_MESSAGE_LOGOUT = "FAIL_MESSAGE_LOGOUT";
    public static final String FAIL_MESSAGE_LIST = "FAIL_MESSAGE_LIST";
    public static final String FAIL_MESSAGE_TEXT = "FAIL_MESSAGE_TEXT";
    public static final String FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT = "FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT";
    public static final String FAIL_MESSAGE_ELEMENT_STILL_VISIBLE = "FAIL_MESSAGE_ELEMENT_STILL_VISIBLE";
    public static final String FAIL_MESSAGE_UNABLE_TO_OPEN_A_NEW_WINDOW = "FAIL_MESSAGE_UNABLE_TO_OPEN_A_NEW_WINDOW";

    /**
     * Functional fail messages with parameters.
     */
    public static final String FAIL_MESSAGE_UNABLE_TO_OPEN_APPLICATION = "FAIL_MESSAGE_UNABLE_TO_OPEN_APPLICATION";
    public static final String FAIL_MESSAGE_UNABLE_TO_OPEN_PAGE = "FAIL_MESSAGE_UNABLE_TO_OPEN_PAGE";
    public static final String FAIL_MESSAGE_ERROR_ON_INPUT = "FAIL_MESSAGE_ERROR_ON_INPUT";
    public static final String FAIL_MESSAGE_ERROR_CLEAR_ON_INPUT = "FAIL_MESSAGE_ERROR_CLEAR_ON_INPUT";

    public static final String FAIL_MESSAGE_UNEXPECTED_DATE = "FAIL_MESSAGE_UNEXPECTED_DATE";
    public static final String DATE_GREATER_THAN_TODAY = "DATE_GREATER_THAN_TODAY";
    public static final String FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK = "FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK";
    public static final String FAIL_MESSAGE_UNABLE_TO_PASS_OVER_ELEMENT = "FAIL_MESSAGE_UNABLE_TO_PASS_OVER_ELEMENT";
    public static final String FAIL_MESSAGE_EMPTY_MANDATORY_FIELD = "FAIL_MESSAGE_EMPTY_MANDATORY_FIELD";
    public static final String FAIL_MESSAGE_WRONG_DATE_FORMAT = "FAIL_MESSAGE_WRONG_DATE_FORMAT";
    public static final String FAIL_MESSAGE_UNABLE_TO_SWITCH_WINDOW = "FAIL_MESSAGE_UNABLE_TO_SWITCH_WINDOW";
    public static final String FAIL_MESSAGE_UNABLE_TO_SWITCH_FRAME = "FAIL_MESSAGE_UNABLE_TO_SWITCH_FRAME";
    public static final String FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE = "FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE";
    public static final String FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON = "FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON";
    public static final String FAIL_MESSAGE_UNABLE_TO_CLOSE_APP = "FAIL_MESSAGE_UNABLE_TO_CLOSE_APP";
    public static final String FAIL_MESSAGE_VALUE_NOT_AVAILABLE_IN_THE_LIST = "FAIL_MESSAGE_VALUE_NOT_AVAILABLE_IN_THE_LIST";
    public static final String FAIL_MESSAGE_WRONG_PAGE = "FAIL_MESSAGE_WRONG_PAGE";
    public static final String FAIL_MESSAGE_EMPTY_DATA = "FAIL_MESSAGE_EMPTY_DATA";
    public static final String FAIL_MESSAGE_EMPTY_DATAS = "FAIL_MESSAGE_EMPTY_DATAS";
    public static final String FAIL_MESSAGE_WRONG_EXPECTED_VALUE = "FAIL_MESSAGE_WRONG_EXPECTED_VALUE";
    public static final String FAIL_MESSAGE_UNABLE_TO_EVALUATE_XPATH = "FAIL_MESSAGE_UNABLE_TO_EVALUATE_XPATH";
    public static final String FAIL_MESSAGE_UNABLE_TO_CHECK_ELEMENT = "FAIL_MESSAGE_UNABLE_TO_CHECK_ELEMENT";
    public static final String FAIL_MESSAGE_EMPTY_FIELD = "FAIL_MESSAGE_EMPTY_FIELD";
    public static final String FAIL_MESSAGE_NOT_FOUND_ON_ALERT = "FAIL_MESSAGE_NOT_FOUND_ON_ALERT";
    public static final String FAIL_MESSAGE_ALERT_NOT_FOUND = "FAIL_MESSAGE_ALERT_NOT_FOUND";
    public static final String FAIL_MESSAGE_ALERT_FOUND = "FAIL_MESSAGE_ALERT_FOUND";
    public static final String FAIL_MESSAGE_UNABLE_TO_WRITE_MESSAGE_IN_RESULT_FILE = "FAIL_MESSAGE_UNABLE_TO_WRITE_MESSAGE_IN_RESULT_FILE";

    /**
     * Format given message with provided arguments
     *
     * @param templateMessage
     *            Message to display before formatting
     * @param args
     *            Arguments
     * @return a String containing the message well formatted.
     * @throws TechnicalException
     *             if data input is wrong.
     */
    public static String format(String templateMessage, Object... args) throws TechnicalException {
        if (null != templateMessage && countWildcardsOccurrences(templateMessage, "%s") == args.length) {
            try {
                return String.format(templateMessage, args);
            } catch (final Exception e) {
                throw new TechnicalException(getMessage(FAIL_MESSAGE_FORMAT_STRING), e);
            }
        } else {
            throw new TechnicalException(getMessage(FAIL_MESSAGE_FORMAT_STRING));
        }
    }

    public static String getMessage(String key) {
        if (messagesBundle == null) {
            if (Context.getLocale() == null) {
                return ResourceBundle.getBundle("i18n/messages", Locale.getDefault()).getString(key);
            }
            messagesBundle = ResourceBundle.getBundle("i18n/messages", Context.getLocale());
        }
        return messagesBundle.getString(key);
    }

    /**
     * Count the number of occurrences of a given wildcard.
     *
     * @param templateMessage
     *            Input string
     * @param occurrence
     *            String searched.
     * @return The number of occurrences.
     */
    private static int countWildcardsOccurrences(String templateMessage, String occurrence) {
        if (templateMessage != null && occurrence != null) {
            final Pattern pattern = Pattern.compile(occurrence);
            final Matcher matcher = pattern.matcher(templateMessage);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            return count;
        } else {
            return 0;
        }
    }

}

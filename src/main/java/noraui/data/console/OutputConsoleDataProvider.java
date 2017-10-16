package noraui.data.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import noraui.data.CommonDataProvider;
import noraui.data.DataOutputProvider;
import noraui.exception.TechnicalException;
import noraui.utils.Messages;

/**
 * This DataOutputProvider can be used to display run results through the default console.
 *
 * @author nhallouin
 */
public class OutputConsoleDataProvider extends CommonDataProvider implements DataOutputProvider {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(OutputConsoleDataProvider.class);

    private static final String OUTPUT_CONSOLE_DATA_PROVIDER_USED = "OUTPUT_CONSOLE_DATA_PROVIDER_USED";
    private static final String OUTPUT_CONSOLE_DATA_PROVIDER_FAILED_AT_LINE = "OUTPUT_CONSOLE_DATA_PROVIDER_FAILED_AT_LINE";
    private static final String OUTPUT_CONSOLE_DATA_PROVIDER_WARNING_AT_LINE = "OUTPUT_CONSOLE_DATA_PROVIDER_WARNING_AT_LINE";
    private static final String OUTPUT_CONSOLE_DATA_PROVIDER_SUCCESS_AT_LINE = "OUTPUT_CONSOLE_DATA_PROVIDER_SUCCESS_AT_LINE";
    private static final String OUTPUT_CONSOLE_DATA_PROVIDER_RESULT_AT_LINE = "OUTPUT_CONSOLE_DATA_PROVIDER_RESULT_AT_LINE";

    public OutputConsoleDataProvider() {
        super();
        logger.info(Messages.getMessage(OUTPUT_CONSOLE_DATA_PROVIDER_USED));
    }

    @Override
    public void prepare(String scenario) throws TechnicalException {
        // Nothing to prepare for this Output provider
    }

    @Override
    public void writeFailedResult(int line, String value) throws TechnicalException {
        logger.error(Messages.getMessage(OUTPUT_CONSOLE_DATA_PROVIDER_FAILED_AT_LINE), line, value);
    }

    @Override
    public void writeWarningResult(int line, String value) throws TechnicalException {
        logger.warn(Messages.getMessage(OUTPUT_CONSOLE_DATA_PROVIDER_WARNING_AT_LINE), line, value);
    }

    @Override
    public void writeSuccessResult(int line) throws TechnicalException {
        logger.info(Messages.getMessage(OUTPUT_CONSOLE_DATA_PROVIDER_SUCCESS_AT_LINE), line);

    }

    @Override
    public void writeDataResult(String column, int line, String value) throws TechnicalException {
        logger.info(Messages.getMessage(OUTPUT_CONSOLE_DATA_PROVIDER_RESULT_AT_LINE), line, column, value);
    }

}

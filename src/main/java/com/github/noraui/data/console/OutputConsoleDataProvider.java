/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.data.CommonDataProvider;
import com.github.noraui.data.DataOutputProvider;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.utils.Messages;

/**
 * This DataOutputProvider can be used to display run results through the default console.
 *
 * @author nhallouin
 */
public class OutputConsoleDataProvider extends CommonDataProvider implements DataOutputProvider {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OutputConsoleDataProvider.class);

    private static final String OUTPUT_CONSOLE_DATA_PROVIDER_USED = "OUTPUT_CONSOLE_DATA_PROVIDER_USED";
    private static final String OUTPUT_CONSOLE_DATA_PROVIDER_FAILED_AT_LINE = "OUTPUT_CONSOLE_DATA_PROVIDER_FAILED_AT_LINE";
    private static final String OUTPUT_CONSOLE_DATA_PROVIDER_WARNING_AT_LINE = "OUTPUT_CONSOLE_DATA_PROVIDER_WARNING_AT_LINE";
    private static final String OUTPUT_CONSOLE_DATA_PROVIDER_SUCCESS_AT_LINE = "OUTPUT_CONSOLE_DATA_PROVIDER_SUCCESS_AT_LINE";
    private static final String OUTPUT_CONSOLE_DATA_PROVIDER_RESULT_AT_LINE = "OUTPUT_CONSOLE_DATA_PROVIDER_RESULT_AT_LINE";

    public OutputConsoleDataProvider() {
        super();
        LOGGER.info(Messages.getMessage(OUTPUT_CONSOLE_DATA_PROVIDER_USED));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(String scenario) throws TechnicalException {
        // Nothing to prepare for this Output provider
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeFailedResult(int line, String value) {
        LOGGER.error(Messages.getMessage(OUTPUT_CONSOLE_DATA_PROVIDER_FAILED_AT_LINE), line, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeWarningResult(int line, String value) {
        LOGGER.warn(Messages.getMessage(OUTPUT_CONSOLE_DATA_PROVIDER_WARNING_AT_LINE), line, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeSuccessResult(int line) {
        LOGGER.info(Messages.getMessage(OUTPUT_CONSOLE_DATA_PROVIDER_SUCCESS_AT_LINE), line);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeDataResult(String column, int line, String value) {
        LOGGER.info(Messages.getMessage(OUTPUT_CONSOLE_DATA_PROVIDER_RESULT_AT_LINE), line, column, value);
    }

}

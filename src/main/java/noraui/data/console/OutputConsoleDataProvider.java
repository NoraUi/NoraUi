package noraui.data.console;

import noraui.data.CommonDataProvider;
import noraui.data.DataOutputProvider;
import noraui.exception.TechnicalException;

/**
 * This DataOutputProvider can be used to display run results throught the default console.
 * 
 * @author nhallouin
 */
public class OutputConsoleDataProvider extends CommonDataProvider implements DataOutputProvider {

    public OutputConsoleDataProvider() {
        super();
        logger.info("Output data provider used is CONSOLE");
    }

    @Override
    public void prepare(String scenario) throws TechnicalException {
        // Nothing to prepare for this Output provider
    }

    @Override
    public void writeFailedResult(int line, String value) throws TechnicalException {
        logger.error(String.format("----- FAILED at line %d >  %s -----", line,  value));
    }

    @Override
    public void writeWarningResult(int line, String value) throws TechnicalException {
        logger.warn(String.format("----- WARNING at line %d > %s -----", line,  value));
    }

    @Override
    public void writeSuccessResult(int line) throws TechnicalException {
        logger.info(String.format("----- SUCCESS at line %d -----",line));

    }

    @Override
    public void writeDataResult(String column, int line, String value) throws TechnicalException {
        logger.info(String.format("----- DATA RESULT at line %d > [%s] = %s -----", line, column, value));
    }

}

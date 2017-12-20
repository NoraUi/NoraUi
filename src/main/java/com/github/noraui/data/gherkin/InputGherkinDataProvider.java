/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data.gherkin;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.data.CommonDataProvider;
import com.github.noraui.data.DataInputProvider;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.exception.data.EmptyDataFileContentException;
import com.github.noraui.gherkin.GherkinFactory;
import com.github.noraui.model.Model;
import com.github.noraui.utils.Messages;

/**
 * This DataInputProvider can be used if you want to provide Gherkin example by
 * your own. Scenario initiator inserts will be skipped.
 *
 * @author nhallouin
 */
public class InputGherkinDataProvider extends CommonDataProvider implements DataInputProvider {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(InputGherkinDataProvider.class);

    private static final String GHERKIN_INPUT_DATA_PROVIDER_USED = "GHERKIN_INPUT_DATA_PROVIDER_USED";

    private String[] examples = new String[] {};

    public InputGherkinDataProvider() {
        logger.info(Messages.getMessage(GHERKIN_INPUT_DATA_PROVIDER_USED));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(String scenario) throws TechnicalException {
        examples = GherkinFactory.getExamples(scenario);
        try {
            initColumns();
        } catch (EmptyDataFileContentException e) {
            logger.error(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION), e);
            System.exit(-1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNbLines() throws TechnicalException {
        return examples.length;
    }

    /**
     * Gets prepared Gherkin examples.
     *
     * @return an array of examples
     */
    public String[] getExamples() {
        return examples;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readValue(String column, int line) throws TechnicalException {
        if (examples.length > 0) {
            final String[] lineContent = readLine(line, true);
            final int i = columns.indexOf(column) + 1;
            if (i > 0 && null != lineContent && lineContent.length > i) {
                return lineContent[i];
            } else {
                return "";
            }

        }
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] readLine(int line, boolean readResult) throws TechnicalException {
        if (examples.length > 0 && examples.length > line) {
            final String[] lineContent = examples[line].split("\\|", -1);
            if (lineContent.length < 3) {
                throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_EXPECTED_AT_LEAST_AN_ID_COLUMN_IN_EXAMPLES));
            }
            return Arrays.copyOfRange(lineContent, 2, readResult ? lineContent.length : lineContent.length - 1);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Model> getModel(String modelPackages) throws TechnicalException {
        return null;
    }

    private void initColumns() throws EmptyDataFileContentException {
        columns = new ArrayList<>();
        if (examples.length > 1) {
            final String[] cols = examples[0].split("\\|", -1);
            for (int i = 1; i < cols.length - 1; i++) {
                columns.add(cols[i]);
            }
        } else {
            throw new EmptyDataFileContentException(Messages.getMessage(EmptyDataFileContentException.EMPTY_DATA_FILE_CONTENT_ERROR_MESSAGE));
        }
        if (columns.size() < 2) {
            throw new EmptyDataFileContentException(Messages.getMessage(EmptyDataFileContentException.EMPTY_DATA_FILE_CONTENT_ERROR_MESSAGE));
        }
        resultColumnName = columns.get(columns.size() - 1);
        if (!isResultColumnNameAuthorized(resultColumnName)) {
            resultColumnName = Messages.getMessage(ResultColumnNames.RESULT_COLUMN_NAME);
        }
    }
}

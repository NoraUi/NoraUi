package noraui.data.gherkin;

import java.util.Arrays;

import noraui.data.CommonDataProvider;
import noraui.data.DataInputProvider;
import noraui.exception.TechnicalException;
import noraui.gherkin.GherkinFactory;
import noraui.model.Model;

/**
 * This DataInputProvider can be used if you want to provide Gherkin example by
 * your own. Scenario initiator inserts will be skipped.
 *
 * @author nhallouin
 */
public class InputGherkinDataProvider extends CommonDataProvider implements DataInputProvider {
    private String[] examples = new String[] {};

    public InputGherkinDataProvider() {
        logger.info("Input data provider used is GHERKIN");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(String scenario) throws TechnicalException {
        examples = GherkinFactory.getExamples(scenario);

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
            String[] columns = examples[0].split("\\|", -1);
            for (int i = 1; i < columns.length - 1; i++) {
                if (columns[i].equals(column)) {
                    String[] lineContent = readLine(line, true);
                    if (null != lineContent && lineContent.length > i) {
                        return lineContent[i];
                    } else {
                        return "";
                    }
                }
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
            String[] lineContent = examples[line].split("\\|", -1);
            if (lineContent.length < 3) {
                throw new TechnicalException(TechnicalException.TECHNICAL_EXPECTED_AT_LEAST_AN_ID_COLUMN_IN_EXAMPLES);
            }
            return Arrays.copyOfRange(lineContent, 2, (readResult) ? lineContent.length + 1 : lineContent.length);
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
}

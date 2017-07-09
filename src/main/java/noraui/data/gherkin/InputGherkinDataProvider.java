package noraui.data.gherkin;

import noraui.data.CommonDataProvider;
import noraui.data.DataInputProvider;
import noraui.exception.TechnicalException;
import noraui.gherkin.GherkinFactory;
import noraui.model.Model;

/**
 * This DataInputProvider can be used if you want to provide Gherkin example by your own.
 * Scenario initiator inserts will be skipped.
 * 
 * @author nhallouin
 */
public class InputGherkinDataProvider extends CommonDataProvider implements DataInputProvider {
    private int nbLines = 0;

    public InputGherkinDataProvider() {
        logger.info("Input data provider used is GHERKIN");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(String scenario) throws TechnicalException {
        this.nbLines = GherkinFactory.getNumberOfGherkinExamples(scenario);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNbLines() throws TechnicalException {
        return nbLines;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readValue(String column, int line) throws TechnicalException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] readLine(int line, boolean readResult) throws TechnicalException {
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

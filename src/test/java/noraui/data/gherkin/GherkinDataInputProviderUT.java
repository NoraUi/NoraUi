package noraui.data.gherkin;

import org.junit.Assert;
import org.junit.Test;

import noraui.exception.TechnicalException;

public class GherkinDataInputProviderUT {

    @Test
    public void testConstructor() {
        try {
            InputGherkinDataProvider provider = new InputGherkinDataProvider();
            Assert.assertTrue("nbLines must be equals to 0.", provider.getNbLines() == 0);
        } catch (TechnicalException e) {
            Assert.fail("Technical exception in testConstructor(): " + e.getMessage());
        }
    }

    @Test
    public void testNullMethods() {
        try {
            InputGherkinDataProvider provider = new InputGherkinDataProvider();
            Assert.assertTrue("getModel method must return null.", provider.getModel(null) == null);
            Assert.assertTrue("readLine method must return null.", provider.readLine(0, false) == null);
            Assert.assertTrue("readValue method must return null.", provider.readValue(null, 0) == null);
        } catch (TechnicalException e) {
            Assert.fail("Technical exception in testNullMethods(): " + e.getMessage());
        }

    }
}

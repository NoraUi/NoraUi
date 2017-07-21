package noraui.data.csv;

import static noraui.data.DataProvider.NAME_OF_RESULT_COLUMN;
import static noraui.utils.Constants.DATA_IN;
import static noraui.utils.Constants.DATA_OUT;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;

import noraui.exception.TechnicalException;
import noraui.utils.Messages;

public class CsvDataProviderUT {

    @Test
    public void testConstructorIsPublic() throws Exception {
        Constructor<CsvDataProvider> constructor = CsvDataProvider.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPublic(constructor.getModifiers()));
        constructor.setAccessible(true);
    }

    @Test
    public void test() throws TechnicalException {
        CsvDataProvider csvDataProvider = new CsvDataProvider();
        csvDataProvider.setDataInPath("src/test/resources" + DATA_IN);
        csvDataProvider.setDataOutPath("src/test/resources" + DATA_OUT);
        csvDataProvider.prepare("hello");
        Assert.assertTrue(true);

        Assert.assertEquals(9, csvDataProvider.getNbLines());

        csvDataProvider.writeFailedResult(1, "UT Failed Message");
        Assert.assertEquals("UT Failed Message", csvDataProvider.readValue(NAME_OF_RESULT_COLUMN, 1));

        csvDataProvider.writeSuccessResult(2);
        Assert.assertEquals(Messages.SUCCESS_MESSAGE, csvDataProvider.readValue(NAME_OF_RESULT_COLUMN, 2));

        csvDataProvider.writeWarningResult(3, "UT Warning Message");
        Assert.assertEquals("UT Warning Message", csvDataProvider.readValue(NAME_OF_RESULT_COLUMN, 3));
    }
}

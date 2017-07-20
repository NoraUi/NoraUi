package noraui.data.csv;

import static noraui.utils.Constants.DATA_IN;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;

import noraui.exception.TechnicalException;

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
        csvDataProvider.prepare("hello");
        Assert.assertTrue(true);
        Assert.assertEquals(9, csvDataProvider.getNbLines());
    }
}

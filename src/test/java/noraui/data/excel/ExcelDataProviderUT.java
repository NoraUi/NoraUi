package noraui.data.excel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;

import noraui.data.excel.ExcelDataProvider;
import noraui.data.excel.InputExcelDataProvider;
import noraui.data.excel.OutputExcelDataProvider;

public class ExcelDataProviderUT {

    @Test
    public void testInputConstructorIsPublic() throws Exception {
        Constructor<InputExcelDataProvider> constructor = InputExcelDataProvider.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPublic(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testOutputConstructorIsPublic() throws Exception {
        Constructor<OutputExcelDataProvider> constructor = OutputExcelDataProvider.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPublic(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testConstructorIsPublic() throws Exception {
        Constructor<ExcelDataProvider> constructor = ExcelDataProvider.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPublic(constructor.getModifiers()));
        constructor.setAccessible(true);
    }

}

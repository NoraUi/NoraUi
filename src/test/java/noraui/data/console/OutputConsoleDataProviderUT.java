package noraui.data.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import noraui.exception.TechnicalException;

public class OutputConsoleDataProviderUT {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }

    @Test
    public void testConstructorIsPublic() throws Exception {
        Constructor<OutputConsoleDataProvider> constructor = OutputConsoleDataProvider.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPublic(constructor.getModifiers()));
        constructor.setAccessible(true);
    }

    @Test
    public void testWriteXxxxxResult() throws TechnicalException, IOException {
        OutputConsoleDataProvider outputConsoleDataProvider = new OutputConsoleDataProvider();
        outputConsoleDataProvider.prepare("hello");
        Assert.assertTrue(true);

        outputConsoleDataProvider.writeFailedResult(1, "UT Failed Message");
        Assert.assertTrue(outContent.toString().endsWith("----- FAILED at line 1 >  UT Failed Message -----\n"));

        outputConsoleDataProvider.writeSuccessResult(2);
        Assert.assertTrue(outContent.toString().endsWith("----- SUCCESS at line 2 -----\n"));

        outputConsoleDataProvider.writeWarningResult(3, "UT Warning Message");
        Assert.assertTrue(outContent.toString().endsWith("----- WARNING at line 3 > UT Warning Message -----\n"));

        outputConsoleDataProvider.writeDataResult("title", 4, "UT title");
        Assert.assertTrue(outContent.toString().endsWith("----- DATA RESULT at line 4 > [title] = UT title -----\n"));
    }
}

package noraui.gherkin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;

import noraui.gherkin.GherkinFactory;

public class GherkinFactoryUT {

    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<GherkinFactory> constructor = GherkinFactory.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}

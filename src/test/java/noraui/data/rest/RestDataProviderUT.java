package noraui.data.rest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;

public class RestDataProviderUT {

    @Test
    public void testConstructorIsPublic() throws Exception {
        Constructor<RestDataProvider> constructor = RestDataProvider.class.getDeclaredConstructor(String.class, String.class, String.class);
        Assert.assertTrue(Modifier.isPublic(constructor.getModifiers()));
        constructor.setAccessible(true);
    }

}

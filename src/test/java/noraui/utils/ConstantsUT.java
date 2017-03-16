package noraui.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;

import noraui.utils.Constants;

public class ConstantsUT {

    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testCheckValidateDate() {
        Assert.assertFalse("La date est fause, janvier n'a jamais 32 jours.", "32/01/2016".matches(Constants.DATE_FORMAT_REG_EXP));
        Assert.assertFalse("La date est fause, février n'a jamais 30 jours.", "30/02/2016".matches(Constants.DATE_FORMAT_REG_EXP));
        Assert.assertFalse("La date est fause, 2017 n'est pas une année bissextile donc 28 jours", "29/02/2017".matches(Constants.DATE_FORMAT_REG_EXP));
        Assert.assertFalse("La date est fause, novembre à 30 jours.", "31/11/2016".matches(Constants.DATE_FORMAT_REG_EXP));
        Assert.assertFalse("La date est fause, 16 au lieu de 2016.", "30/11/16".matches(Constants.DATE_FORMAT_REG_EXP));

        Assert.assertTrue("La date est bonne.", "31/01/2016".matches(Constants.DATE_FORMAT_REG_EXP));
        Assert.assertTrue("La date est bonne, 2016 est une année bissextile donc 29 jours", "29/02/2016".matches(Constants.DATE_FORMAT_REG_EXP));
        Assert.assertTrue("La date est bonne.", "28/02/2016".matches(Constants.DATE_FORMAT_REG_EXP));
        Assert.assertTrue("La date est bonne.", "28/02/2017".matches(Constants.DATE_FORMAT_REG_EXP));
        Assert.assertTrue("La date est bonne.", "30/11/2016".matches(Constants.DATE_FORMAT_REG_EXP));
    }

}

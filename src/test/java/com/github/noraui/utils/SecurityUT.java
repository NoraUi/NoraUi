package com.github.noraui.utils;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.github.noraui.exception.TechnicalException;
import com.github.noraui.utils.Security;

public class SecurityUT {

    @Test
    public void testCreateSha1CheckSumFile() {
        Security security = new Security();
        try {
            security.createSha1CheckSumFile(
                    new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "demoExcel.properties"));
            Assert.assertTrue(true);
        } catch (TechnicalException e) {
            Assert.assertFalse("Erreur car il y a une erreur de type TechnicalException", true);
        }
    }

}

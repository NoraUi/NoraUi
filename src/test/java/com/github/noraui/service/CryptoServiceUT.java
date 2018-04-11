/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service;

import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.noraui.cucumber.injector.NoraUiInjector;
import com.github.noraui.cucumber.injector.NoraUiInjectorSource;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.utils.Context;
import com.google.inject.Inject;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class })
@PowerMockIgnore({ "javax.crypto.*", "javax.net.ssl.*" })
public class CryptoServiceUT {

    @Inject
    private CryptoService cryptoService;

    @Before
    public void setUp() throws TechnicalException {
        NoraUiInjector.resetInjector();
        new NoraUiInjectorSource().getInjector().injectMembers(this);
    }

    @After
    public void tearDown() {
        NoraUiInjector.resetInjector();
    }

    @Test
    public void testEncrypt() throws TechnicalException {
        PowerMockito.mockStatic(Context.class);
        when(Context.getCryptoKey()).thenReturn("my-secret");
        Assert.assertEquals("℗:AMAapQjwjKaAGUkO6rbttg==", cryptoService.encrypt("foot"));
    }

    @Test
    public void testDecrypt() throws TechnicalException {
        PowerMockito.mockStatic(Context.class);
        when(Context.getCryptoKey()).thenReturn("my-secret");
        Assert.assertEquals("foot", cryptoService.decrypt("℗:AMAapQjwjKaAGUkO6rbttg=="));
    }

    @Test
    public void testErrorConfig() {
        try {
            PowerMockito.mockStatic(Context.class);
            when(Context.getLocale()).thenReturn(new Locale("en"));
            cryptoService.encrypt("foo");
            Assert.assertFalse(true);
        } catch (TechnicalException e) {
            Assert.assertEquals("/!\\ Technical problem. You need to configure crypto.key parameter. /!\\", e.getMessage());
        }
    }

}

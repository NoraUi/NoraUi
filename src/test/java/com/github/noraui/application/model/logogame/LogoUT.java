package com.github.noraui.application.model.logogame;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.github.noraui.application.model.logogame.Logo;
import com.github.noraui.application.model.logogame.Logos;
import com.github.noraui.model.ModelList;

public class LogoUT {

    @Test
    public void checkLogoSerializeTest() {
        // prepare mock
        Logo logo = new Logo();
        logo.setBrand("amazon");

        // run test
        Assert.assertEquals("{\"brand\":\"amazon\"}", logo.serialize());
    }

    @Test
    public void checkLogoDeserializeTest() {
        // run test
        Logo logo = new Logo();
        logo.deserialize("{\"brand\":\"amazon\"}");
        Assert.assertEquals("amazon", logo.getBrand());
    }

    @Test
    public void checkLogoSerializeListTest() {
        // prepare mock
        Logo amazon = new Logo();
        amazon.setBrand("amazon");

        Logo redbull = new Logo();
        redbull.setBrand("redbull");

        Logos logos = new Logos();
        logos.add(amazon);
        logos.add(redbull);

        // run test
        Assert.assertEquals("[{\"brand\":\"amazon\"},{\"brand\":\"redbull\"}]", logos.serialize());
    }

    @Test
    public void checkLogoDeserializeListTest() {
        // run test
        Logos logos = new Logos();
        logos.deserialize("[{\"brand\":\"amazon\"},{\"brand\":\"redbull\"}]");
        Assert.assertEquals("amazon", logos.get(0).getBrand());
        Assert.assertEquals("redbull", logos.get(1).getBrand());
    }

    @Test
    public void checkGetModelListTest() throws InstantiationException, IllegalAccessException {
        // prepare mock
        Logo amazon = new Logo();
        amazon.setBrand("amazon");

        Logo redbull = new Logo();
        redbull.setBrand("redbull");

        Class<? extends ModelList> c = amazon.getModelList();
        ModelList cl = c.newInstance();
        cl.addModel(amazon);
        cl.addModel(redbull);

        // run test
        Assert.assertEquals("[{\"brand\":\"amazon\"},{\"brand\":\"redbull\"}]", cl.serialize());
    }

    @Test
    public void checkDeleteLogosAndAddLogosTest() {
        // prepare mock
        Logo a = new Logo();
        a.setBrand("amazon");
        Logo b = new Logo();
        b.setBrand("citroen");
        Logo c = new Logo();
        c.setBrand("burgerking");
        Logo d = new Logo();
        d.setBrand("ebay");

        Logos logosInGame = new Logos();
        logosInGame.add(a);
        logosInGame.add(b);
        logosInGame.add(c);

        Logos logos = new Logos();
        logos.add(b);
        logos.add(c);
        logos.add(d);

        // run test
        Logos lInGame = (Logos) logosInGame.clone();
        lInGame.subtract(logos);
        Assert.assertEquals(1, lInGame.size());
        Assert.assertEquals("amazon", lInGame.get(0).getBrand());

        Logos l = (Logos) logos.clone();
        l.subtract(logosInGame);
        Assert.assertEquals(1, l.size());
        Assert.assertEquals("ebay", l.get(0).getBrand());

        Assert.assertEquals("amazon", logosInGame.get(0).getBrand());
        Assert.assertEquals("citroen", logosInGame.get(1).getBrand());
        Assert.assertEquals("burgerking", logosInGame.get(2).getBrand());
        Collections.sort(logosInGame);
        Assert.assertEquals("amazon", logosInGame.get(0).getBrand());
        Assert.assertEquals("burgerking", logosInGame.get(1).getBrand());
        Assert.assertEquals("citroen", logosInGame.get(2).getBrand());

    }
}

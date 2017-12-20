/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.model.demo;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.github.noraui.model.ModelList;

public class ArticleUT {

    @Test
    public void checkArticleSerializeTest() {
        // prepare mock
        Article article = new Article();
        article.setTitle("Football");
        article.setText("Football is a family of team sports...");
        article.setAuthor("wikipedia");
        article.setNote(8);

        // run test
        Assert.assertEquals("{\"title\":\"Football\",\"text\":\"Football is a family of team sports...\",\"author\":\"wikipedia\",\"note\":8}", article.serialize());
    }

    @Test
    public void checkArticleDeserializeTest() {
        // run test
        Article article = new Article();
        article.deserialize("{\"title\":\"Football\",\"text\":\"Football is a family of team sports...\",\"author\":\"wikipedia\",\"note\":8}");
        Assert.assertEquals("Football", article.getTitle());
        Assert.assertEquals("Football is a family of team sports...", article.getText());
        Assert.assertEquals("wikipedia", article.getAuthor());
        Assert.assertEquals(8, article.getNote());
    }

    @Test
    public void checkArticleSerializeListTest() {
        // prepare mock
        Article football = new Article();
        football.setTitle("Football");
        football.setText("Football is a family of team sports...");
        football.setAuthor("wikipedia");
        football.setNote(8);

        Article judo = new Article();
        judo.setTitle("Judo");
        judo.setText("Judo was created as a physical, mental and moral pedagogy in Japan, in 1882, by Kanō Jigorō...");
        judo.setAuthor("wikipedia");
        judo.setNote(10);

        Articles articles = new Articles();
        articles.add(football);
        articles.add(judo);

        // run test
        Assert.assertEquals(
                "[{\"title\":\"Football\",\"text\":\"Football is a family of team sports...\",\"author\":\"wikipedia\",\"note\":8},{\"title\":\"Judo\",\"text\":\"Judo was created as a physical, mental and moral pedagogy in Japan, in 1882, by Kanō Jigorō...\",\"author\":\"wikipedia\",\"note\":10}]",
                articles.serialize());
    }

    @Test
    public void checkArticleDeserializeListTest() {
        // run test
        Articles articles = new Articles();
        articles.deserialize(
                "[{\"title\":\"Football\",\"text\":\"Football is a family of team sports...\",\"author\":\"wikipedia\",\"note\":8},{\"title\":\"Judo\",\"text\":\"Judo was created as a physical, mental and moral pedagogy in Japan, in 1882, by Kanō Jigorō...\",\"author\":\"wikipedia\",\"note\":10}]");
        Assert.assertEquals("Football", articles.get(0).getTitle());
        Assert.assertEquals("Football is a family of team sports...", articles.get(0).getText());
        Assert.assertEquals("wikipedia", articles.get(0).getAuthor());
        Assert.assertEquals(8, articles.get(0).getNote());

        Assert.assertEquals("Judo", articles.get(1).getTitle());
        Assert.assertEquals("Judo was created as a physical, mental and moral pedagogy in Japan, in 1882, by Kanō Jigorō...", articles.get(1).getText());
        Assert.assertEquals("wikipedia", articles.get(1).getAuthor());
        Assert.assertEquals(10, articles.get(1).getNote());
    }

    @Test
    public void checkgetModelListTest() throws InstantiationException, IllegalAccessException {
        // prepare mock
        Article football = new Article();
        football.setTitle("Football");
        football.setText("Football is a family of team sports...");
        football.setAuthor("wikipedia");
        football.setNote(8);

        Article judo = new Article();
        judo.setTitle("Judo");
        judo.setText("Judo was created as a physical, mental and moral pedagogy in Japan, in 1882, by Kanō Jigorō...");
        judo.setAuthor("wikipedia");
        judo.setNote(10);

        Class<? extends ModelList> c = football.getModelList();
        ModelList cl = c.newInstance();
        cl.addModel(football);
        cl.addModel(judo);

        // run test
        Assert.assertEquals(
                "[{\"title\":\"Football\",\"text\":\"Football is a family of team sports...\",\"author\":\"wikipedia\",\"note\":8},{\"title\":\"Judo\",\"text\":\"Judo was created as a physical, mental and moral pedagogy in Japan, in 1882, by Kanō Jigorō...\",\"author\":\"wikipedia\",\"note\":10}]",
                cl.serialize());
    }

    @Test
    public void checkDeleteWorkPartiesAndAddWorkPartiesTest() {
        // prepare mock
        Article a = new Article();
        a.setTitle("A");
        a.setNote(3);
        a.setText("stub1");
        Article b = new Article();
        b.setTitle("B");
        b.setNote(2);
        b.setText("stub2");
        Article c = new Article();
        c.setTitle("C");
        c.setNote(1);
        c.setText("stub3");
        Article d = new Article();
        d.setTitle("D");
        d.setNote(10);
        d.setText("stub4");

        Articles workPartiesInSalto = new Articles();
        workPartiesInSalto.add(a);
        workPartiesInSalto.add(b);
        workPartiesInSalto.add(c);

        Articles workParties = new Articles();
        workParties.add(b);
        workParties.add(c);
        workParties.add(d);

        // run test
        Articles wpInSalto = (Articles) workPartiesInSalto.clone();
        wpInSalto.subtract(workParties);
        Assert.assertEquals(1, wpInSalto.size());
        Assert.assertEquals("A", wpInSalto.get(0).getTitle());

        Articles wp = (Articles) workParties.clone();
        wp.subtract(workPartiesInSalto);
        Assert.assertEquals(1, wp.size());
        Assert.assertEquals("D", wp.get(0).getTitle());

        Assert.assertEquals(3, workPartiesInSalto.get(0).getNote());
        Assert.assertEquals(2, workPartiesInSalto.get(1).getNote());
        Assert.assertEquals(1, workPartiesInSalto.get(2).getNote());
        Collections.sort(workPartiesInSalto);
        Assert.assertEquals(1, workPartiesInSalto.get(0).getNote());
        Assert.assertEquals(2, workPartiesInSalto.get(1).getNote());
        Assert.assertEquals(3, workPartiesInSalto.get(2).getNote());

    }
}

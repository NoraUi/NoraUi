package noraui.application.model.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import noraui.annotation.Column;
import noraui.application.model.DemoModel;

public class Article extends DemoModel implements Comparable<Article> {

    @Expose(serialize = false, deserialize = false)
    private Integer wid;

    @Expose
    @Column(name = "Titre")
    private String title;

    @Expose
    @Column(name = "Texte")
    private String text;

    @Expose
    @Column(name = "Auteur")
    private String author;

    @Expose
    @Column(name = "Note")
    private int note;

    // constructor by default for serialize/deserialize
    public Article() {
        this.wid = -1;
        this.title = "";
        this.text = "";
        this.author = "";
        this.note = -1;
    }

    public Article(String wid, String title, String text, String author, String note) {
        this.wid = Integer.parseInt(wid);
        this.title = title;
        this.text = text;
        this.author = author;
        this.note = (int) Double.parseDouble(note);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deserialize(String jsonString) {
        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        final Gson gson = builder.create();
        Article w = gson.fromJson(jsonString, Article.class);
        this.wid = w.wid;
        this.title = w.title;
        this.text = w.text;
        this.author = w.author;
        this.note = w.note;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Articles> getModelList() {
        return Articles.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (title == null ? 0 : title.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Article other = (Article) obj;
        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Article other) {
        return ((Integer) this.note).compareTo(other.note);
    }

    public Integer getWid() {
        return wid;
    }

    public void setWid(Integer wid) {
        this.wid = wid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

}
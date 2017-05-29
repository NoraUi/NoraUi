package noraui.application.model.logogame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import noraui.annotation.Column;
import noraui.application.model.DemoModel;

public class Logo extends DemoModel implements Comparable<Logo> {

    @Expose(serialize = false, deserialize = false)
    private Integer wid;

    @Expose
    @Column(name = "brand")
    private String brand;

    @Column(name = "score")
    private String score;

    // constructor by default for serialize/deserialize
    public Logo() {
        this.wid = -1;
        this.brand = "";
        this.score = "";
    }

    public Logo(String wid, String brand, String score) {
        this.wid = Integer.parseInt(wid);
        this.brand = brand;
        this.score = score;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deserialize(String jsonString) {
        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        final Gson gson = builder.create();
        Logo w = gson.fromJson(jsonString, Logo.class);
        this.wid = w.wid;
        this.brand = w.brand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Logos> getModelList() {
        return Logos.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (brand == null ? 0 : brand.hashCode());
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
        Logo other = (Logo) obj;
        if (brand == null) {
            if (other.brand != null) {
                return false;
            }
        } else if (!brand.equals(other.brand)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Logo other) {
        return this.brand.compareTo(other.brand);
    }

    @Override
    public String toString() {
        return "{wid:" + wid + ", brand:\"" + brand + "\", score:\"" + score + "\"}";
    }

    public Integer getWid() {
        return wid;
    }

    public void setWid(Integer wid) {
        this.wid = wid;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

}
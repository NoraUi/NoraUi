package noraui.application.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import noraui.model.Model;

public abstract class DemoModel implements Model {
    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize() {
        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        builder.disableHtmlEscaping();
        final Gson gson = builder.create();
        return gson.toJson(this);
    }

}

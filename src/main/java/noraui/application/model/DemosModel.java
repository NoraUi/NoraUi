package noraui.application.model;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import noraui.model.ModelList;

public abstract class DemosModel<T> extends ArrayList<T> implements ModelList {

    /**
     *
     */
    private static final long serialVersionUID = 257533336402277981L;

    protected DemosModel() {
        super();
    }

    protected DemosModel(DemosModel<T> inputList) {
        super(inputList);
    }

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

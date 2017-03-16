package noraui.application.model.logogame;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import noraui.application.model.DemosModel;
import noraui.model.Model;
import noraui.model.ModelList;

public class Logos extends DemosModel<Logo> implements ModelList {

    /**
     *
     */
    private static final long serialVersionUID = 9002528163560746878L;

    public Logos() {
        super();
    }

    public Logos(DemosModel<Logo> inputList) {
        super(inputList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deserialize(String jsonString) {
        Type listType = new TypeToken<ArrayList<Logo>>() {
        }.getType();

        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        final Gson gson = builder.create();

        List<Logo> list = gson.fromJson(jsonString, listType);
        this.addAll(list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelList addModel(Model m) {
        super.add((Logo) m);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void subtract(ModelList list) {
        Iterator<?> iterator = ((Logos) list).iterator();
        while (iterator.hasNext()) {
            this.remove(iterator.next());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getIds() {
        List<Integer> result = new ArrayList<>();
        for (Logo article : this) {
            result.add(article.getWid());
        }
        return result;
    }

}

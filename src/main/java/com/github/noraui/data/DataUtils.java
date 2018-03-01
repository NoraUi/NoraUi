/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.noraui.exception.TechnicalException;
import com.github.noraui.model.Model;
import com.github.noraui.model.ModelList;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;

public class DataUtils {

    private DataUtils() {
    }

    public static Constructor<Model> getModelConstructor(Class<Model> model, final String[] headers) {
        Constructor<Model> modelConstructor = null;
        for (final Constructor<?> c : model.getConstructors()) {
            if (c.getParameterCount() == headers.length) {
                modelConstructor = (Constructor<Model>) c;
                break;
            }
        }
        return modelConstructor;
    }

    public static Map<String, ModelList> fusionProcessor(Class<Model> model, Constructor<Model> modelConstructor) throws TechnicalException {
        final Map<String, ModelList> fusionedData = new LinkedHashMap<>();
        try {
            final Class<? extends ModelList> modelListClass = model.newInstance().getModelList();
            String[] example = Context.getDataInputProvider().readLine(1, false);
            int i = 2;
            do {
                final String key = example[0];
                final Object[] data = addStringToBeginningOfObjectArray(String.valueOf(i - 1), Arrays.copyOfRange(example, 1, example.length));
                if (fusionedData.containsKey(key)) {
                    fusionedData.put(key, fusionedData.get(key).addModel(modelConstructor.newInstance(data)));
                } else {
                    fusionedData.put(key, modelListClass.newInstance().addModel(modelConstructor.newInstance(data)));
                }
                example = Context.getDataInputProvider().readLine(i, false);
                i++;
            } while (example != null);
        } catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_FUSION_PROCESSOR), e);
        }
        return fusionedData;
    }

    private static Object[] addStringToBeginningOfObjectArray(String s, Object[] data) {
        final List<Object> list = new ArrayList<>(Arrays.asList(data));
        list.add(0, s);
        return list.toArray(new Object[list.size()]);
    }

}

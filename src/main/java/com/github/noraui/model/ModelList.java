/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.model;

import java.util.List;

public interface ModelList extends SerializableModel {

    /**
     * Adds a Model in ModelList
     * 
     * @param m
     *            is a noraui.model.Model Object
     * @return The noraui.model.ModelList with the added noraui.model.Model
     */
    ModelList addModel(Model m);

    /**
     * Subtracts a Model of ModelList
     * 
     * @param list
     *            is a noraui.model.ModelList Object
     */
    void subtract(ModelList list);

    /**
     * @return an array of NoraUi ids
     */
    List<Integer> getIds();

}

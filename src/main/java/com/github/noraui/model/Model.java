/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.model;

public interface Model extends SerializableModel {

    /**
     * @return chidren class of noraui.model.ModelList
     */
    Class<? extends ModelList> getModelList();

}

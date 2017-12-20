/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.model;

public interface SerializableModel {

    /**
     * @return a string with a Object serialized
     */
    String serialize();

    /**
     * @param string
     *            is a string with a Object serialized
     */
    void deserialize(String string);

}

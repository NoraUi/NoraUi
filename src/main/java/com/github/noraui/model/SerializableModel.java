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

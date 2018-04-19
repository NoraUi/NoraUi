/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data;

import java.util.List;

public class DataIndex {

    private int dataLine;
    private List<Integer> indexes;

    public DataIndex(int dataLine, List<Integer> indexes) {
        this.dataLine = dataLine;
        this.indexes = indexes;
    }

    public int getDataLine() {
        return dataLine;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }

}

/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data;

import java.util.List;

public class DataIndex {

    private int dataLine;
    private List<Integer> indexes;

    public DataIndex(int dataLine, List<Integer> index) {
        this.dataLine = dataLine;
        this.indexes = index;
    }

    public int getDataLine() {
        return dataLine;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }

}

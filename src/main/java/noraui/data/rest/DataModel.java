package noraui.data.rest;

import java.util.List;

// @XmlRootElement
public class DataModel {

    private List<String> columns;

    private List<Row> rows;

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

}

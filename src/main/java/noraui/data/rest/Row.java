package noraui.data.rest;

import java.util.ArrayList;
import java.util.List;

public class Row {

    private List<String> columns;
    private int errorStepIndex;
    private String result;

    public Row() {
        this.columns = new ArrayList<>();
        this.errorStepIndex = -1;
    }

    public Row(List<String> rows) {
        this.columns = rows;
        this.errorStepIndex = -1;
    }

    public Row(List<String> rows, int errorStepIndex) {
        this.columns = rows;
        this.errorStepIndex = errorStepIndex;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public int getErrorStepIndex() {
        return errorStepIndex;
    }

    public void setErrorStepIndex(int errorStepIndex) {
        this.errorStepIndex = errorStepIndex;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}

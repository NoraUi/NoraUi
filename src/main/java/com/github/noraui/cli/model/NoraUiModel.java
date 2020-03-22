/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli.model;

import java.util.ArrayList;
import java.util.List;

public class NoraUiModel {

    private String name;
    private List<NoraUiField> fields;
    private List<NoraUiResult> results;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NoraUiField> getFields() {
        return fields;
    }

    /**
     * @return all fields in a String and separated by a space.
     */
    public String getFieldsString() {
        if (fields != null && !fields.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (NoraUiField noraUiField : fields) {
                sb.append(noraUiField.getName()).append(" ");
            }
            sb.deleteCharAt(sb.length());
            return sb.toString();
        }
        return "";
    }

    public void setFields(List<NoraUiField> fields) {
        this.fields = fields;
    }

    /**
     * @param fields
     *            is fields of model (String separated by a space).
     */
    public void setFields(String fields) {
        List<NoraUiField> r = new ArrayList<>();
        if (fields != null) {
            String[] fieldList = fields.split(" ");
            for (String element : fieldList) {
                r.add(new NoraUiField(element));
            }
            this.fields = r;
        } else {
            this.fields = null;
        }
    }

    public List<NoraUiResult> getResults() {
        return results;
    }

    /**
     * @return all results in a String and separated by a space.
     */
    public String getResultsString() {
        if (results != null && !results.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (NoraUiResult noraUiResult : results) {
                sb.append(noraUiResult.getName()).append(" ");
            }
            sb.deleteCharAt(sb.length());
            return sb.toString();
        }
        return "";
    }

    public void setResults(List<NoraUiResult> results) {
        this.results = results;
    }

    /**
     * @param results
     *            is all results in a String and separated by a space.
     */
    public void setResults(String results) {
        List<NoraUiResult> r = new ArrayList<>();
        if (results != null) {
            String[] resultList = results.split(" ");
            for (String element : resultList) {
                r.add(new NoraUiResult(element));
            }
            this.results = r;
        } else {
            this.results = null;
        }

    }

}

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

    public String getFieldsString() {
        if (fields != null && fields.size() > 0) {
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

    public void setFields(String fields) {
        List<NoraUiField> r = new ArrayList<>();
        if (fields != null) {
            String[] fieldList = fields.split(" ");
            for (String element : fieldList) {
                r.add(new NoraUiField(element));
            }
            this.fields = r;
        }
        this.fields = null;
    }

    public List<NoraUiResult> getResults() {
        return results;
    }

    public String getResultsString() {
        if (results != null && results.size() > 0) {
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

    public void setResults(String results) {
        List<NoraUiResult> r = new ArrayList<>();
        if (results != null) {
            String[] resultList = results.split(" ");
            for (String element : resultList) {
                r.add(new NoraUiResult(element));
            }
            this.results = r;
        }
        this.results = null;
    }

}

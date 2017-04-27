package noraui.gherkin;

import noraui.utils.Context;

public class GherkinStepCondition {

    private String key;
    private String expected;
    private String actual;

    public GherkinStepCondition() {
    }
    
    public GherkinStepCondition(String key, String expected, String actual) {
        this.key = key;
        this.expected = expected;
        this.actual = actual;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public boolean checkCondition() {
        String actual_ = Context.getValue(this.actual) != null ? Context.getValue(this.actual) : this.actual;
        if (actual_ == null || !actual_.matches("(?i)" + this.expected)) {
            return false;
        }
        return true;
    }

}

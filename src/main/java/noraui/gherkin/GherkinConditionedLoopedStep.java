package noraui.gherkin;

public class GherkinConditionedLoopedStep extends GherkinStepCondition {
    private String step;

    public GherkinConditionedLoopedStep(String key, String step, String expected, String actual) {
        super(key, expected, actual);
        this.step = step;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

}

package com.github.noraui.main;

import com.github.noraui.utils.Context;

public class ScenarioInitiatorRunner {

    public static void main(String[] args) {
        Context.getInstance().initializeEnv("demoExcel.properties");
        new ScenarioInitiator().start(args);
    }
}

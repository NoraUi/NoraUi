package com.github.noraui.cli;

public abstract class AbstractNoraUiCli {

    protected String getJavaClassHeaders(String noraRobotName) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**").append(System.lineSeparator());
        sb.append(" * " + noraRobotName + " generated free by NoraUi Organization https://github.com/NoraUi").append(System.lineSeparator());
        sb.append(" * " + noraRobotName + " is licensed under the license BSD.").append(System.lineSeparator());
        sb.append(" * ").append(System.lineSeparator());
        sb.append(" * CAUTION: " + noraRobotName + " use NoraUi library. This project is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE").append(System.lineSeparator());
        sb.append(" */").append(System.lineSeparator());
        return sb.toString();
    }

}

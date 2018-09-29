/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.indus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.noraui.utils.UnitTest4CLIContext;

import com.github.noraui.indus.MavenRunCounter;
import com.github.noraui.utils.Context;

public class UnitTest4CLICounter {

    public static void main(String[] args) {
        List<String> manager = new ArrayList<>();

        List<String> scenarioBlacklist = new ArrayList<>();
        // you can add not run scenario here
        // scenarioBlacklist.add("---  you can add not run scenario here ---");

        List<String> versionControlSystemsBlacklist = new ArrayList<>();
        versionControlSystemsBlacklist.add(".svn");

        UnitTest4CLIContext.getInstance().initializeEnv("***Robot.properties");

        MavenRunCounter mavenRunCounter = new MavenRunCounter();
        List<MavenRunCounter.Counter> counters = mavenRunCounter.count(versionControlSystemsBlacklist, scenarioBlacklist, manager, new File(Context.getResourcesPath() + "/steps"));
        mavenRunCounter.print(counters, args[0]);
    }

}

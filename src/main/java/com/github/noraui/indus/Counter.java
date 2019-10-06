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

import org.slf4j.Logger;

import com.github.noraui.exception.TechnicalException;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;

@Loggable
public class Counter {

    static Logger log;

    private static final String COUNTER_USAGE_WARNING_MESSAGE = "COUNTER_USAGE_WARNING_MESSAGE";

    private Counter() {
    }

    public static void main(String[] args) throws TechnicalException {
        if (args.length == 1) {
            Context.getInstance().initializeEnv("demo" + args[0] + ".properties");
            final List<String> manager = new ArrayList<>();
            final List<String> scenarioBlacklist = new ArrayList<>();
            final List<String> versionControlSystemsBlacklist = new ArrayList<>();

            versionControlSystemsBlacklist.add(".svn");
            versionControlSystemsBlacklist.add(".gitignore");

            if (args[0].contains("RestJson")) {
                scenarioBlacklist.add("sampleRESTAPI");
            }
            scenarioBlacklist.add("loginLogout");

            final MavenRunCounter mavenRunCounter = new MavenRunCounter();
            final List<MavenRunCounter.Counter> counters = mavenRunCounter.count(versionControlSystemsBlacklist, scenarioBlacklist, manager, new File(Context.getResourcesPath() + "/steps"));
            mavenRunCounter.print(counters, args[0]);
            Context.clear();
        } else {
            log.warn(Messages.getMessage(COUNTER_USAGE_WARNING_MESSAGE));
        }

    }

}

package noraui.indus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import noraui.utils.Context;
import noraui.utils.Messages;

public class Counter {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(Counter.class);

    private static final String COUNTER_USAGE_WARNING_MESSAGE = "COUNTER_USAGE_WARNING_MESSAGE";

    private Counter() {
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            final List<String> manager = new ArrayList<>();
            final List<String> scenarioBlacklist = new ArrayList<>();
            final List<String> versionControlSystemsBlacklist = new ArrayList<>();

            versionControlSystemsBlacklist.add(".svn");

            Context.getInstance().initializeEnv("demo" + args[0] + ".properties");

            if (args[0].contains("Db") || args[0].contains("RestJson") || args[0].contains("Gherkin")) {
                scenarioBlacklist.add("blog");
                scenarioBlacklist.add("bonjour");
                scenarioBlacklist.add("jouerAuJeuDesLogos");
                scenarioBlacklist.add("playToLogoGame");
            }

            final MavenRunCounter mavenRunCounter = new MavenRunCounter();
            final List<MavenRunCounter.Counter> counters = mavenRunCounter.count(versionControlSystemsBlacklist, scenarioBlacklist, manager, new File(Context.getResourcesPath() + "/steps"));
            mavenRunCounter.print(counters, args[0]);
            Context.clear();
        } else {
            logger.warn(Messages.getMessage(COUNTER_USAGE_WARNING_MESSAGE));
        }

    }

}

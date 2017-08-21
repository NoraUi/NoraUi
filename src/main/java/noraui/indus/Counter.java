package noraui.indus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import noraui.utils.Context;

public class Counter {

    private static final Logger logger = Logger.getLogger(Counter.class);

    private Counter() {
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            List<String> manager = new ArrayList<>();
            List<String> scenarioBlacklist = new ArrayList<>();
            List<String> versionControlSystemsBlacklist = new ArrayList<>();

            versionControlSystemsBlacklist.add(".svn");

            Context.getInstance().initializeEnv("demo" + args[0] + ".properties");

            if (args[0].contains("Db") || args[0].contains("RestJson") || args[0].contains("Gherkin")) {
                scenarioBlacklist.add("blog");
                scenarioBlacklist.add("bonjour");
                scenarioBlacklist.add("jouerAuJeuDesLogos");
                scenarioBlacklist.add("playToLogoGame");
            }

            MavenRunCounter mavenRunCounter = new MavenRunCounter();
            List<MavenRunCounter.Counter> counters = mavenRunCounter.count(versionControlSystemsBlacklist, scenarioBlacklist, manager, new File(Context.getResourcesPath() + "/steps"));
            mavenRunCounter.print(counters, args[0]);
            Context.clear();
        } else {
            logger.warn("Usage: Counter main must be launched with 1 parameters ==> \"Counter\" <type_of_counter>");
        }

    }

}

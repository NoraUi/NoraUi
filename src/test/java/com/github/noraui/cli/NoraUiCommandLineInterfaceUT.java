/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.junit.runners.MethodSorters;

import com.github.noraui.utils.UnitTest4CLIContext;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NoraUiCommandLineInterfaceUT {

    @Rule
    public final StandardOutputStreamLog stdOutLog = new StandardOutputStreamLog();
    private NoraUiCommandLineInterface cli;

    @Before
    public void setUp() {
        cli = new NoraUiCommandLineInterface();
        // mock Application Object here.
        cli.setApplication(new Application("src" + File.separator + "test"));
        cli.setScenario(new Scenario("src" + File.separator + "test"));
        cli.setModel(new Model("src" + File.separator + "test"));
    }

    @Test
    public void testCliStep001_InteractiveModeIsfalseWithoutFeatureArg() {
        String[] args = { "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When interactiveMode is false, you need use -f"));
    }

    @Test
    public void testCliStep002_AddApplicationWhenInteractiveModeIsfalseWithoutAppNameAndUrlArg() {
        String[] args = { "-f", "1", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to add an application with interactiveMode is false, you need use -a and -u"));
    }

    @Test
    public void testCliStep003_AddApplicationWhenInteractiveModeIsfalse() {
        String[] args = { "-f", "1", "-a", "google", "-u", "http://www.google.fr", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Add a new application named [google] with this url: [http://www.google.fr]"));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep004_AddScenarioWhenInteractiveModeIsfalseWithoutAppNameAndUrlArg() {
        String[] args = { "-f", "2", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to add a scenario with interactiveMode is false, you need use -a, -s and -d"));
    }

    @Test
    public void testCliStep005AddScenarioWhenInteractiveModeIsfalse() {
        String[] args = { "-f", "2", "-a", "google", "-s", "loginSample", "-d", "description for login to google sample", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Add a new scenario named [loginSample] on [google] application with this description: [description for login to google sample]"));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep006_AddModelWhenInteractiveModeIsfalseWithoutAppNameAndUrlArg() {
        String[] args = { "-f", "3", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to add a model with interactiveMode is false, you need use -a, -m, -fi and -re (optional)"));
    }

    @Test
    public void testCliStep007_AddModelWhenInteractiveModeIsfalseWithoutResults() {
        String[] args = { "-f", "3", "-a", "google", "-m", "foo", "-fi", "field1 field2", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Add a new model named [foo] in application named [google]"));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep008_AddModelWhenInteractiveModeIsfalse() {
        String[] args = { "-f", "3", "-a", "google", "-m", "moo", "-fi", "field1 field2", "-re", "result1", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Add a new model named [moo] in application named [google]"));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep009_RemoveModelWhenInteractiveModeIsfalseWithoutAppNameAndUrlArg() {
        String[] args = { "-f", "6", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to remove a model with interactiveMode is false, you need use -a and -m"));
    }

    @Test
    public void testCliStep010_RemoveModelWhenInteractiveModeIsfalse() {
        String[] args = { "-f", "6", "-a", "google", "-m", "foo", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Remove model named [foo] in application named [google]"));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep011_RemoveScenarioWhenInteractiveModeIsfalseWithoutAppNameAndUrlArg() {
        String[] args = { "-f", "5", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to remove a scenario with interactiveMode is false, you need use -s"));
    }

    @Test
    public void testCliStep012_RemoveScenarioWhenInteractiveModeIsfalse() {
        String[] args = { "-f", "5", "-s", "loginSample", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Remove a scenario named [loginSample]."));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep013_RemoveApplicationWhenInteractiveModeIsfalseWithoutAppNameAndUrlArg() {
        String[] args = { "-f", "4", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to remove an application with interactiveMode is false, you need use -a"));
    }

    @Test
    public void testCliStep014_RemoveApplicationWhenInteractiveModeIsfalse() {
        String[] args = { "-f", "4", "-a", "google", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Remove application named [google]."));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

}

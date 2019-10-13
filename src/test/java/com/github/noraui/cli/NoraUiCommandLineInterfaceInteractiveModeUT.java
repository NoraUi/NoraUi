/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli;

import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.junit.runners.MethodSorters;

import com.github.noraui.exception.TechnicalException;
import com.github.noraui.indus.UnitTest4CLICounter;
import com.github.noraui.utils.UnitTest4CLIContext;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NoraUiCommandLineInterfaceInteractiveModeUT {

    @Rule
    public final SystemErrRule stdErrLog = new SystemErrRule();
    
    @Rule
    public final SystemOutRule stdOutLog = new SystemOutRule().enableLog();
    
    @Rule
    public final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

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
    public void testCliStep000_DisplayHelp() throws TechnicalException {
        systemInMock.provideLines("0");

        String[] args = { "-h" };
        cli.runCli(UnitTest4CLIContext.class, UnitTest4CLICounter.class, args);

        StringBuilder expectedCliAsk = new StringBuilder();
        expectedCliAsk.append("-h: Display this help").append(System.lineSeparator());
        expectedCliAsk.append("--verbose: Add debug informations in console.").append(System.lineSeparator());
        expectedCliAsk.append("--update: Use NoraUi CLI files for update your robot.").append(System.lineSeparator());
        expectedCliAsk.append("-f: features").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append("-s: Scenario Name").append(System.lineSeparator());
        expectedCliAsk.append("-u: Url").append(System.lineSeparator());
        expectedCliAsk.append("-d: Description").append(System.lineSeparator());
        expectedCliAsk.append("-k: Crypto key").append(System.lineSeparator());
        expectedCliAsk.append("-a: Application Name").append(System.lineSeparator());
        expectedCliAsk.append("-m: Model Name").append(System.lineSeparator());
        expectedCliAsk.append("-fi: Field list of model").append(System.lineSeparator());
        expectedCliAsk.append("-re: Result list of model").append(System.lineSeparator());
        expectedCliAsk.append(
                "-interactiveMode: (boolean) When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the previously listed parameters. When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command line.")
                .append(System.lineSeparator());
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("Exit NoraUi Command Line Interface with success.").append(System.lineSeparator());

        Assert.assertTrue(stdOutLog.getLog().contains(expectedCliAsk));
    }

    @Test
    public void testCliStep001_DisplayStatus() throws TechnicalException {
        systemInMock.provideLines("9", "0");

        cli.runCli(UnitTest4CLIContext.class, UnitTest4CLICounter.class);

        StringBuilder expectedCliAsk = new StringBuilder();
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("NoraUi Command Line Interface finished with success.").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("Exit NoraUi Command Line Interface with success.").append(System.lineSeparator());

        Assert.assertTrue(stdOutLog.getLog().contains(expectedCliAsk));
    }

    @Test
    public void testCliStep002_AddApplication() throws TechnicalException {
        systemInMock.provideLines("1", "google", "http://www.google.fr", "0");

        cli.runCli(UnitTest4CLIContext.class, UnitTest4CLICounter.class);

        StringBuilder expectedCliAsk = new StringBuilder();
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append("Enter application name:").append(System.lineSeparator());
        expectedCliAsk.append("Enter url:").append(System.lineSeparator());
        expectedCliAsk.append("Add a new application named [google] with this url: [http://www.google.fr]").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("NoraUi Command Line Interface finished with success.").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("Exit NoraUi Command Line Interface with success.").append(System.lineSeparator());

        Assert.assertTrue(stdOutLog.getLog().contains(expectedCliAsk));
    }

    @Test
    public void testCliStep003_AddScenario() throws TechnicalException {
        systemInMock.provideLines("2", "1", "loginSample", "description for login to google sample", "0");

        cli.runCli(UnitTest4CLIContext.class, UnitTest4CLICounter.class);

        StringBuilder expectedCliAsk = new StringBuilder();
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append("Enter index application number:").append(System.lineSeparator());
        expectedCliAsk.append("    1) google").append(System.lineSeparator());
        expectedCliAsk.append("Enter scenario name:").append(System.lineSeparator());
        expectedCliAsk.append("Enter description:").append(System.lineSeparator());
        expectedCliAsk.append("Add a new scenario named [loginSample] on [google] application with this description: [description for login to google sample]").append(System.lineSeparator());
        expectedCliAsk.append("dataProvider.in.type is [EXCEL]").append(System.lineSeparator());
        expectedCliAsk.append("dataProvider.out.type is [EXCEL]").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("NoraUi Command Line Interface finished with success.").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("Exit NoraUi Command Line Interface with success.").append(System.lineSeparator());

        Assert.assertTrue(stdOutLog.getLog().contains(expectedCliAsk));
    }

    @Test
    public void testCliStep004_AddModel() throws TechnicalException {
        systemInMock.provideLines("3", "1", "foo", "field1 field2", "result1", "0");

        cli.runCli(UnitTest4CLIContext.class, UnitTest4CLICounter.class);

        StringBuilder expectedCliAsk = new StringBuilder();
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append("Enter index application number:").append(System.lineSeparator());
        expectedCliAsk.append("    1) google").append(System.lineSeparator());
        expectedCliAsk.append("Enter model name:").append(System.lineSeparator());
        expectedCliAsk.append("Enter field list:").append(System.lineSeparator());
        expectedCliAsk.append("Enter result list (optional):").append(System.lineSeparator());
        expectedCliAsk.append("Add a new model named [foo] in application named [google]").append(System.lineSeparator());
        expectedCliAsk.append("field: [field1]").append(System.lineSeparator());
        expectedCliAsk.append("field: [field2]").append(System.lineSeparator());
        expectedCliAsk.append("result: [result1]").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("NoraUi Command Line Interface finished with success.").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("Exit NoraUi Command Line Interface with success.").append(System.lineSeparator());

        Assert.assertTrue(stdOutLog.getLog().contains(expectedCliAsk));
    }

    @Test
    public void testCliStep005_DisplayStatus() throws TechnicalException {
        systemInMock.provideLines("9", "0");

        cli.runCli(UnitTest4CLIContext.class, UnitTest4CLICounter.class);

        StringBuilder expectedCliAsk = new StringBuilder();
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append("Application: [google]").append(System.lineSeparator());
        expectedCliAsk.append(" - url: [http://www.google.fr]").append(System.lineSeparator());
        expectedCliAsk.append(" - model: [foo]").append(System.lineSeparator());
        expectedCliAsk.append("   - field: [field1]").append(System.lineSeparator());
        expectedCliAsk.append("   - field: [field2]").append(System.lineSeparator());
        expectedCliAsk.append("   - result: [result1]").append(System.lineSeparator());
        expectedCliAsk.append("Scenario: [loginSample]").append(System.lineSeparator());
        expectedCliAsk.append(" - description: [description for login to google sample]").append(System.lineSeparator());
        expectedCliAsk.append(" - application: [google]").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("NoraUi Command Line Interface finished with success.").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("Exit NoraUi Command Line Interface with success.").append(System.lineSeparator());

        Assert.assertTrue(stdOutLog.getLog().contains(expectedCliAsk));
    }

    @Test
    public void testCliStep006_RemoveModel() throws TechnicalException {
        // 2 is google and 1 is foo
        systemInMock.provideLines("6", "2", "1", "0");

        cli.runCli(UnitTest4CLIContext.class, UnitTest4CLICounter.class);

        StringBuilder expectedCliAsk = new StringBuilder();
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append("Enter index application number:").append(System.lineSeparator());
        expectedCliAsk.append("    1) demo").append(System.lineSeparator());
        expectedCliAsk.append("    2) google").append(System.lineSeparator());
        expectedCliAsk.append("Enter index model number:").append(System.lineSeparator());
        expectedCliAsk.append("    1) foo").append(System.lineSeparator());
        expectedCliAsk.append("Remove model named [foo] in application named [google]").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("NoraUi Command Line Interface finished with success.").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("Exit NoraUi Command Line Interface with success.").append(System.lineSeparator());

        Assert.assertTrue(stdOutLog.getLog().contains(expectedCliAsk));
    }

    @Test
    public void testCliStep007_RemoveScenario() throws TechnicalException {
        systemInMock.provideLines("5", "1", "0");

        cli.runCli(UnitTest4CLIContext.class, UnitTest4CLICounter.class);

        StringBuilder expectedCliAsk = new StringBuilder();
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append("Enter index scenario number:").append(System.lineSeparator());
        expectedCliAsk.append("    1) loginSample").append(System.lineSeparator());
        expectedCliAsk.append("    2) hello").append(System.lineSeparator());
        expectedCliAsk.append("    3) bonjour").append(System.lineSeparator());
        expectedCliAsk.append("    4) blog").append(System.lineSeparator());
        expectedCliAsk.append("    5) loginLogout").append(System.lineSeparator());
        expectedCliAsk.append("    6) sampleRESTAPI").append(System.lineSeparator());
        expectedCliAsk.append("Remove a scenario named [loginSample].").append(System.lineSeparator());
        expectedCliAsk.append("dataProvider.in.type is [EXCEL]").append(System.lineSeparator());
        expectedCliAsk.append("dataProvider.out.type is [EXCEL]").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("NoraUi Command Line Interface finished with success.").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("Exit NoraUi Command Line Interface with success.").append(System.lineSeparator());

        Assert.assertTrue(stdOutLog.getLog().contains(expectedCliAsk));
    }

    @Test
    public void testCliStep008_RemoveApplication() throws TechnicalException {
        systemInMock.provideLines("4", "1", "0");

        cli.runCli(UnitTest4CLIContext.class, UnitTest4CLICounter.class);

        StringBuilder expectedCliAsk = new StringBuilder();
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append("Enter index application number:").append(System.lineSeparator());
        expectedCliAsk.append("    1) google").append(System.lineSeparator());
        expectedCliAsk.append("Remove application named [google].").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("NoraUi Command Line Interface finished with success.").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("Exit NoraUi Command Line Interface with success.").append(System.lineSeparator());

        Assert.assertTrue(stdOutLog.getLog().contains(expectedCliAsk));
    }

    @Test
    public void testCliStep009_DisplayStatus() throws TechnicalException {
        systemInMock.provideLines("9", "0");

        cli.runCli(UnitTest4CLIContext.class, UnitTest4CLICounter.class);

        StringBuilder expectedCliAsk = new StringBuilder();
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("NoraUi Command Line Interface finished with success.").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("What do you want ?").append(System.lineSeparator());
        expectedCliAsk.append("    0 => exit NoraUi CLI").append(System.lineSeparator());
        expectedCliAsk.append("    1 => add new application").append(System.lineSeparator());
        expectedCliAsk.append("    2 => add new scenario").append(System.lineSeparator());
        expectedCliAsk.append("    3 => add new model").append(System.lineSeparator());
        expectedCliAsk.append("    4 => remove application").append(System.lineSeparator());
        expectedCliAsk.append("    5 => remove scenario").append(System.lineSeparator());
        expectedCliAsk.append("    6 => remove model").append(System.lineSeparator());
        expectedCliAsk.append("    7 => encrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    8 => decrypt data").append(System.lineSeparator());
        expectedCliAsk.append("    9 => status").append(System.lineSeparator());
        expectedCliAsk.append(System.lineSeparator());
        expectedCliAsk.append("Exit NoraUi Command Line Interface with success.").append(System.lineSeparator());

        Assert.assertTrue(stdOutLog.getLog().contains(expectedCliAsk));
    }
}

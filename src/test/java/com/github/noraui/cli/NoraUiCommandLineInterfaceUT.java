/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.junit.runners.MethodSorters;

import com.github.noraui.cli.model.NoraUiApplicationFile;
import com.github.noraui.cli.model.NoraUiCliFile;
import com.github.noraui.cli.model.NoraUiField;
import com.github.noraui.cli.model.NoraUiModel;
import com.github.noraui.cli.model.NoraUiResult;
import com.github.noraui.cli.model.NoraUiScenarioFile;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.utils.UnitTest4CLIContext;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NoraUiCommandLineInterfaceUT {

    @Rule
    public final StandardOutputStreamLog stdOutLog = new StandardOutputStreamLog();
    private NoraUiCommandLineInterface cli;

    @Before
    public void setUp() throws IOException {
        cli = new NoraUiCommandLineInterface();
        // mock Application Object here.
        cli.setApplication(new Application("src" + File.separator + "test"));
        cli.setScenario(new Scenario("src" + File.separator + "test"));
        cli.setModel(new Model("src" + File.separator + "test"));
        FileUtils.forceMkdir(new File(".noraui"));
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(new File(".noraui"));
    }

    @Test
    public void testNoraUiCliFiles() {
        NoraUiCliFile noraUiCliFile = new NoraUiCliFile();
        List<NoraUiApplicationFile> applicationFiles = new ArrayList<>();
        NoraUiApplicationFile noraUiApplicationFile = new NoraUiApplicationFile();
        noraUiApplicationFile.setName("stubapp");
        noraUiApplicationFile.setUrl("https://noraui.github.io/demo/logogame/v3/#/login");
        List<NoraUiModel> noraUiModels = new ArrayList<>();
        NoraUiModel noraUiModel = new NoraUiModel();
        noraUiModel.setName("foo");
        List<NoraUiField> fields = new ArrayList<>();
        NoraUiField aaa = new NoraUiField();
        aaa.setName("aaa");
        NoraUiField bbb = new NoraUiField();
        bbb.setName("bbb");
        fields.add(aaa);
        fields.add(bbb);
        noraUiModel.setFields(fields);
        List<NoraUiResult> results = new ArrayList<>();
        NoraUiResult ccc = new NoraUiResult();
        ccc.setName("ccc");
        results.add(ccc);
        noraUiModel.setResults(results);
        noraUiModels.add(noraUiModel);
        noraUiApplicationFile.setModels(noraUiModels);
        applicationFiles.add(noraUiApplicationFile);
        noraUiCliFile.setApplicationFiles(applicationFiles);

        List<NoraUiScenarioFile> scenarioFiles = new ArrayList<>();
        NoraUiScenarioFile scenario = new NoraUiScenarioFile();
        scenario.setName("stubSenario");
        scenario.setDescription("stubDescription");
        scenario.setApplication("stubapp");
        scenarioFiles.add(scenario);
        noraUiCliFile.setScenarioFiles(scenarioFiles);

        cli.writeNoraUiCliFiles(noraUiCliFile, true);
        NoraUiCliFile result = cli.readNoraUiCliFiles(true);
        Assert.assertTrue(result.getApplicationFiles().size() == 1);
        Assert.assertEquals(result.getApplicationFiles().get(0).getName(), "stubapp");
        Assert.assertEquals(result.getApplicationFiles().get(0).getUrl(), "https://noraui.github.io/demo/logogame/v3/#/login");
        Assert.assertTrue(result.getApplicationFiles().get(0).getModels().size() == 1);
        Assert.assertEquals(result.getApplicationFiles().get(0).getModels().get(0).getName(), "foo");
        Assert.assertTrue(result.getApplicationFiles().get(0).getModels().get(0).getFields().size() == 2);
        Assert.assertEquals(result.getApplicationFiles().get(0).getModels().get(0).getFields().get(0).getName(), "aaa");
        Assert.assertEquals(result.getApplicationFiles().get(0).getModels().get(0).getFields().get(1).getName(), "bbb");
        Assert.assertTrue(result.getApplicationFiles().get(0).getModels().get(0).getResults().size() == 1);
        Assert.assertEquals(result.getApplicationFiles().get(0).getModels().get(0).getResults().get(0).getName(), "ccc");

        Assert.assertTrue(result.getScenarioFiles().size() == 1);
        Assert.assertEquals(result.getScenarioFiles().get(0).getName(), "stubSenario");
        Assert.assertEquals(result.getScenarioFiles().get(0).getDescription(), "stubDescription");
        Assert.assertEquals(result.getScenarioFiles().get(0).getApplication(), "stubapp");
    }

    @Test
    public void testCliStep001_InteractiveModeIsfalseWithoutFeatureArg() throws TechnicalException {
        String[] args = { "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When interactiveMode is false, you need use -f"));
    }

    @Test
    public void testCliStep002_AddApplicationWhenInteractiveModeIsfalseWithoutAppNameAndUrlArg() throws TechnicalException {
        String[] args = { "-f", "1", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to add an application with interactiveMode is false, you need use -a and -u"));
    }

    @Test
    public void testCliStep003_AddApplicationWhenInteractiveModeIsfalse() throws TechnicalException {
        String[] args = { "-f", "1", "-a", "google", "-u", "http://www.google.fr", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Add a new application named [google] with this url: [http://www.google.fr]"));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep004_AddScenarioWhenInteractiveModeIsfalseWithoutAppNameAndUrlArg() throws TechnicalException {
        String[] args = { "-f", "2", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to add a scenario with interactiveMode is false, you need use -a, -s and -d"));
    }

    @Test
    public void testCliStep005AddScenarioWhenInteractiveModeIsfalse() throws TechnicalException {
        String[] args = { "-f", "2", "-a", "google", "-s", "loginSample", "-d", "description for login to google sample", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Add a new scenario named [loginSample] on [google] application with this description: [description for login to google sample]"));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep006_AddModelWhenInteractiveModeIsfalseWithoutAppNameAndUrlArg() throws TechnicalException {
        String[] args = { "-f", "3", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to add a model with interactiveMode is false, you need use -a, -m, -fi and -re (optional)"));
    }

    @Test
    public void testCliStep007_AddModelWhenInteractiveModeIsfalseWithoutResults() throws TechnicalException {
        String[] args = { "-f", "3", "-a", "google", "-m", "foo", "-fi", "field1 field2", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Add a new model named [foo] in application named [google]"));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep008_AddModelWhenInteractiveModeIsfalse() throws TechnicalException {
        String[] args = { "-f", "3", "-a", "google", "-m", "moo", "-fi", "field1 field2", "-re", "result1", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Add a new model named [moo] in application named [google]"));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep009_RemoveModelWhenInteractiveModeIsfalseWithoutAppNameAndUrlArg() throws TechnicalException {
        String[] args = { "-f", "6", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to remove a model with interactiveMode is false, you need use -a and -m"));
    }

    @Test
    public void testCliStep010_RemoveModelWhenInteractiveModeIsfalse() throws TechnicalException {
        String[] args = { "-f", "6", "-a", "google", "-m", "foo", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Remove model named [foo] in application named [google]"));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep011_RemoveScenarioWhenInteractiveModeIsfalseWithoutAppNameAndUrlArg() throws TechnicalException {
        String[] args = { "-f", "5", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to remove a scenario with interactiveMode is false, you need use -s"));
    }

    @Test
    public void testCliStep012_RemoveScenarioWhenInteractiveModeIsfalse() throws TechnicalException {
        String[] args = { "-f", "5", "-s", "loginSample", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Remove a scenario named [loginSample]."));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep013_RemoveApplicationWhenInteractiveModeIsfalseWithoutAppNameAndUrlArg() throws TechnicalException {
        String[] args = { "-f", "4", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to remove an application with interactiveMode is false, you need use -a"));
    }

    @Test
    public void testCliStep014_RemoveApplicationWhenInteractiveModeIsfalse() throws TechnicalException {
        String[] args = { "-f", "4", "-a", "google", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Remove application named [google]."));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep015_EncrypteWhenInteractiveModeIsfalseWithoutDescriptionAndCryptoKeyArg() throws TechnicalException {
        String[] args = { "-f", "7", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to encrypt data with interactiveMode is false, you need use -d and -k"));
    }

    @Test
    public void testCliStep016_Encrypte() throws TechnicalException {
        String[] args = { "-f", "7", "-d", "password", "-k", "my-secret", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Encrypt a data [password] with this crypto key: [my-secret]"));
        Assert.assertTrue(stdOutLog.getLog().contains("Encrypted value is ℗:7y+CKIH1Zd5RVORZ0PAQBA=="));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

    @Test
    public void testCliStep017_DecrypteWhenInteractiveModeIsfalseWithoutDescriptionAndCryptoKeyArg() throws TechnicalException {
        String[] args = { "-f", "8", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("When you want to decrypt data with interactiveMode is false, you need use -d and -k"));
    }

    @Test
    public void testCliStep018_Decrypte() throws TechnicalException {
        String[] args = { "-f", "8", "-d", "℗:7y+CKIH1Zd5RVORZ0PAQBA==", "-k", "my-secret", "-interactiveMode", "false", "--verbose" };
        cli.runCli(UnitTest4CLIContext.class, args);
        Assert.assertTrue(stdOutLog.getLog().contains("Decrypt a data [℗:7y+CKIH1Zd5RVORZ0PAQBA==] with this crypto key: [my-secret]"));
        Assert.assertTrue(stdOutLog.getLog().contains("Decrypted value is password"));
        Assert.assertTrue(stdOutLog.getLog().contains("NoraUi Command Line Interface finished with success."));
        Assert.assertTrue(stdOutLog.getLog().contains("Exit NoraUi Command Line Interface with success."));
    }

}

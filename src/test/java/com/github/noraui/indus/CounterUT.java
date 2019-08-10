/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.indus;

import org.junit.Assert;
import org.junit.Test;

public class CounterUT {

    @Test
    public void generateExpected1() {
        MavenRunCounter mrc = new MavenRunCounter();
        Assert.assertEquals("[Excel] > <EXPECTED_RESULTS_1>8 Scenarios (6 failed, 2 passed)</EXPECTED_RESULTS_1>", mrc.generateExpected1("Excel", 6, 8));
        Assert.assertEquals("[Excel] > <EXPECTED_RESULTS_1>5 Scenarios (5 passed)</EXPECTED_RESULTS_1>", mrc.generateExpected1("Excel", 0, 5));
        Assert.assertEquals("[Excel] > <EXPECTED_RESULTS_1>5 Scenarios (5 failed)</EXPECTED_RESULTS_1>", mrc.generateExpected1("Excel", 5, 5));
        Assert.assertEquals("[Excel] > <EXPECTED_RESULTS_1>0 Scenario</EXPECTED_RESULTS_1>", mrc.generateExpected1("Excel", 0, 0));
    }

    @Test
    public void generateExpected2() {
        MavenRunCounter mrc = new MavenRunCounter();
        Assert.assertEquals("[Excel] > <EXPECTED_RESULTS_2>536 Steps (6 failed, 282 skipped, 248 passed)</EXPECTED_RESULTS_2>", mrc.generateExpected2("Excel", 544, 6, 282, 8));
        Assert.assertEquals("[Excel] > <EXPECTED_RESULTS_2>536 Steps (536 passed)</EXPECTED_RESULTS_2>", mrc.generateExpected2("Excel", 544, 0, 0, 8));
        Assert.assertEquals("[Excel] > <EXPECTED_RESULTS_2>0 Step</EXPECTED_RESULTS_2>", mrc.generateExpected2("Excel", 0, 0, 0, 0));
    }

    @Test
    public void testCsvCounter() throws Exception {
        testCounterMain("Csv");
    }

    @Test
    public void testRestJsonCounter() throws Exception {
        testCounterMain("RestJson");
    }

    @Test
    public void testGherkinCounter() throws Exception {
        testCounterMain("Gherkin");
    }

    @Test
    public void testExcelCounter() throws Exception {
        testCounterMain("Excel");
    }

    @Test
    public void testDbPostgreCounter() throws Exception {
        testCounterMain("DbPostgre");
    }

    @Test
    public void testDbMySqlCounter() throws Exception {
        testCounterMain("DbMySql");
    }

    private void testCounterMain(String type) {
        String res = "";
        try {
            Counter.main(new String[] { type });
        } catch (Exception e) {
            res = "An error occured during Counter.main(\"" + type + "\"): " + e.getMessage();
        }
        Assert.assertEquals(res, "", res);
    }

    // @Test
    // public void testCount() throws Exception {
    // Counter.main(new String[] { "DbOracle" });
    // }

}

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
    }

    @Test
    public void generateExpected2() {
        MavenRunCounter mrc = new MavenRunCounter();
        Assert.assertEquals("[Excel] > <EXPECTED_RESULTS_2>536 Steps (6 failed, 282 skipped, 248 passed)</EXPECTED_RESULTS_2>", mrc.generateExpected2("Excel", 544, 6, 282, 8));
        Assert.assertEquals("[Excel] > <EXPECTED_RESULTS_2>536 Steps (536 passed)</EXPECTED_RESULTS_2>", mrc.generateExpected2("Excel", 544, 0, 0, 8));
    }

    @Test
    public void testCsvCounter() throws Exception {
        Counter.main(new String[] { "Csv" });
    }

    @Test
    public void testRestJsonCounter() throws Exception {
        Counter.main(new String[] { "RestJson" });
    }

    @Test
    public void testGherkinCounter() throws Exception {
        Counter.main(new String[] { "Gherkin" });
    }

    @Test
    public void testExcelCounter() throws Exception {
        Counter.main(new String[] { "Excel" });
    }

    @Test
    public void testDbPostgreCounter() throws Exception {
        Counter.main(new String[] { "DbPostgre" });
    }

    @Test
    public void testDbMySqlCounter() throws Exception {
        Counter.main(new String[] { "DbMySql" });
    }

    // @Test
    // public void testCount() throws Exception {
    // Counter.main(new String[] { "DbOracle" });
    // }

}

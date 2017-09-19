package noraui.indus;

import org.junit.Test;

public class CounterUT {

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

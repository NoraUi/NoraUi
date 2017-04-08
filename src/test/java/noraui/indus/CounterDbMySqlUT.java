package noraui.indus;

import org.junit.Test;

public class CounterDbMySqlUT {

    @Test
    public void testCount() throws Exception {
        Counter.main(new String[] { "DbMySql" });
    }

}

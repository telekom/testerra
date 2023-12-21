package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created on 11.03.2022
 *
 * @author mgn
 */
public class DataProviderPassedTests extends TesterraTest implements Loggable {

    // Data provider is executed

    @DataProvider(name = "dpAssertPassed", parallel = true)
    public Object[][] dpAssertPassedMethod() {
        int size = 2;
        Object[][] objects = new Object[size][1];
        for (int i = 0; i < size; i++) {
            objects[i][0] = "" + (i + 1);
        }
        return objects;
    }

    @Test(dataProvider = "dpAssertPassed")
    public void testDpAssertPassed1(String dp) throws Exception {
        log().info("Test case 1 passed: " + dp);
    }

    @Test(dataProvider = "dpAssertPassed")
    public void testDpAssertPassed2(String dp) throws Exception {
        log().info("Test case 2 passed: " + dp);
    }

}

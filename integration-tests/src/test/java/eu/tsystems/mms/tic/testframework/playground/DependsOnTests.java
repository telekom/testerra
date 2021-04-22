package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created on 22.04.2021
 *
 * @author mgn
 */
public class DependsOnTests extends AbstractWebDriverTest {

    @Test
    public void testCaseSingle() {
        Assert.assertTrue(true);
    }

    @Test
    public void testCaseOne() {
        Assert.assertTrue(true);
    }

    @Test(dependsOnMethods = "testCaseOne")
    public void testCaseTwo() {
        Assert.assertTrue(true);
    }

    @Test(dependsOnMethods = "testCaseTwo")
    public void testCaseThree() {
        Assert.assertTrue(true);
    }

}

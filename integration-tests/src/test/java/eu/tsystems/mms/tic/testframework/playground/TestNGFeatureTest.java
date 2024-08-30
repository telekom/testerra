package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created on 2024-08-30
 *
 * @author mgn
 */
public class TestNGFeatureTest extends TesterraTest {

    @BeforeMethod
    public void before_method() {

    }

    @BeforeMethod
    public void beofre_method_failed() {
        Assert.fail();
    }

    @Test
    public void T01_testng_passed_test() {

    }

    @Test(dependsOnMethods = "T01_testng_passed_test")
    public void T02_testng_depends_method() {

    }

    @Test
    public void T03_testng_failed_test() {
        Assert.fail();
    }

    @AfterMethod
    public void after_method() {

    }

}

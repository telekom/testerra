package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

public class AbstractTest extends TesterraTest implements Loggable {

    @BeforeSuite
    public void beforeSuite() {
        log().info("Before suite");
    }

    @BeforeTest
    public void beforeTest() {
        log().info("Before test");
    }

    @AfterTest
    public void afterTest() {
        log().info("After test");
    }

}

package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

public class AbstractTest extends TesterraTest implements Loggable {

    @BeforeGroups(groups = "group1")
    public void beforeGroup1() {

    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        log().info("Before suite");
    }
//
//    @BeforeTest(alwaysRun = true)
//    public void beforeTest() {
//        log().info("Before test");
//    }

//    @AfterTest
//    public void afterTest() {
//        log().info("After test");
//    }

    @AfterGroups(groups = "group1")
    public void afterGroup1() {

    }

}

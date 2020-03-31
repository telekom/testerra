package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import org.testng.Assert;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public class ReportTestUnderTestBeforeScenarios extends AbstractTest {

    @BeforeSuite(groups = {SystemTestsGroup.SYSTEMTESTSFILTER8})
    public void beforeSuiteFailed() {
        Assert.assertTrue(false);
    }

    @BeforeSuite(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void beforeSuitePassed() {
        Assert.assertTrue(true);
    }

    @BeforeTest(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9, SystemTestsGroup.BEFORE_TEST_FAILED})
    public void beforeTestFailed() {
        Assert.assertTrue(false);
    }

    @BeforeTest(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void beforeTestPassed() {
        Assert.assertTrue(true);
    }

    @BeforeGroups(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9, SystemTestsGroup.BEFORE_GROUPS_FAILED})
    public void beforeGroupsFailed() {
        Assert.assertTrue(false);
    }

    @BeforeGroups(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void beforeGroupsPassed() {
        Assert.assertTrue(true);
    }

    @BeforeClass(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9, SystemTestsGroup.BEFORE_CLASS_FAILED})
    public void beforeClassFailed() {
        Assert.assertTrue(false);
    }

    @BeforeClass(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void beforeClassPassed() {
        Assert.assertTrue(true);
    }

    @BeforeMethod(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9, SystemTestsGroup.BEFORE_METHOD_FAILED})
    public void beforeMethodFailed(Method method) {
        Assert.assertTrue(false);
    }

    @BeforeMethod(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void beforeMethodPassed(Method method) {
        Assert.assertTrue(true);
    }

    /* CONTROL METHODS */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER8, SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void controlMethodAfterBeforeScenarioPassed() {
        Assert.assertTrue(true);
    }

    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER8, SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void controlMethodAfterBeforeScenarioFailed() {
        Assert.assertTrue(false);
    }


}

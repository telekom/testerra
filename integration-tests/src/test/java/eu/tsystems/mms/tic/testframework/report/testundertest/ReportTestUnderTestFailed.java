package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.report.general.TestsUnderTestGroup;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

/**
 * Created by jlma on 07.11.2016.
 */
@FennecClassContext("My_Context")
public class ReportTestUnderTestFailed extends AbstractTest {


    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_FailedMinor1() throws Exception {
        TestStep.begin("Test-Step-1");
        TestStep.begin("Test-Step-2");
        TestStep.begin("Test-Step-3");
        NonFunctionalAssert.assertTrue(false);
        throw new Exception();
    }
    @Test
    public void test_FailedMinor2() throws Exception {

        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedMinor3() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedMinor4() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedMinor5() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedMinor6() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedMinor7() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedMinor8() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedMinor9() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedMinor10() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedInheritedMinor1() throws Exception {
        //TODO add step
        //TestStep.begin("Test-Step-4");
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedInheritedMinor2() throws Exception {
        WebDriver webDriver = WebDriverManager.getWebDriver();
        webDriver.get("http://192.168.60.239");
        //webDriver.close();
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedInheritedMinor3() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedInheritedMinor4() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedInheritedMinor5() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedInheritedMinor6() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }

}

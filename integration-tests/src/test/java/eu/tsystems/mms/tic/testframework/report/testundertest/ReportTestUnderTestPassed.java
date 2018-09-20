package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by sagu on 04.11.2016.
 */

public class ReportTestUnderTestPassed extends AbstractTest {

	/**
	 *Passed
	 */

	@Test
	public void test_TestStatePassed1() {
		TestStep.begin("Test-Step-1");
		TestStep.begin("Test-Step-2");
		TestStep.begin("Test-Step-3");
		Assert.assertTrue(true);
	}
	@Test
	@Fails(ticketId = 1, description = "Does not actually fail.")
	public void test_TestStatePassed2() {
		Assert.assertTrue(true);
	}
	@Test
	public void test_TestStatePassed3() {Assert.assertTrue(true);}
	@Test
	public void test_TestStatePassed4() { Assert.assertTrue(2==2); }
	@Test
	public void test_TestStatePassed5() { Assert.assertTrue(2==2); }
	@Test
	public void test_TestStatePassed6() { Assert.assertTrue(2==2); }
	@Test
	public void test_TestStatePassed7() { Assert.assertTrue(2==2); }

	@Test
	public void test_PassedMinor1()
    {
        TestStep.begin("Test-Step-1");
        TestStep.begin("Test-Step-2");
        TestStep.begin("Test-Step-3");
        NonFunctionalAssert.assertTrue(false);
	}
	@Test
	public void test_PassedMinor2() {
		NonFunctionalAssert.assertTrue(false);
	}
	@Test
	public void test_PassedMinor3() {
		NonFunctionalAssert.assertTrue(6<3);
	}
	@Test
	public void test_PassedMinor4() {
		NonFunctionalAssert.assertTrue(false);
	}
	@Test
	public void test_PassedMinor5() {
		NonFunctionalAssert.assertTrue(false);
	}
	@Test
	public void test_PassedMinor6() {
		NonFunctionalAssert.assertTrue(false);
	}
	@Test
	public void test_PassedMinor7() {
		NonFunctionalAssert.assertTrue(false);
	}
	@Test
	public void test_PassedMinor8() {
		NonFunctionalAssert.assertTrue(false);
	}
	@Test
	public void test_PassedInheritedMinor1() {
		TestStep.begin("Test-Step-1");
		TestStep.begin("Test-Step-2");
		TestStep.begin("Test-Step-3");
        NonFunctionalAssert.assertTrue(6<3);
	}
	@Test
	public void test_PassedInheritedMinor2() {
		NonFunctionalAssert.assertTrue(6<3);
	}
	@Test
	public void test_PassedInheritedMinor3() {
		NonFunctionalAssert.assertTrue(6<3);
	}
	@Test
	public void test_PassedInheritedMinor4() {
		NonFunctionalAssert.assertTrue(6<3);
	}



}

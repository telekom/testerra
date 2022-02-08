/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.report.general.TestsUnderTestGroup;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

@TestClassContext(name = "My_Context")
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
        TestStep.begin("Test-Step-4");
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
    @Test
    public void test_FailedInheritedMinor2() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();
        driver.get("https://the-internet.herokuapp.com/");
        // Please don't delete this again without letting us know why.
        // It is needed to create a screenshot for this test method.
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

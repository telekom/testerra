/*
 * Testerra
 *
 * (C) 2021, Martin Großmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.test.execution;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.Retry;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ExpectedFailsTests extends TesterraTest implements TestStatusTest {

    public boolean failedExpectedIsValid(MethodContext methodContext) {
        return true;
    }

    public boolean failedExpectedIsNotValid(MethodContext methodContext) {
        return false;
    }

    @Test(groups = {"ExpectedFailsTests"})
    @Fails
    @Retry(maxRetries = 2)
    public void test_retriedExpectedFailed() {
        Assert.fail();
    }

    @Test(groups = {"ExpectedFailsTests"})
    @Fails
    public void test_expectedFailed() {
        Assert.fail();
    }

    @Test(groups = {"ExpectedFailsTests"})
    @Fails(description = "not failing anymore")
    public void test_expectedFailedPassed() {
        Assert.assertTrue(true);
    }

    @Test(groups = {"ExpectedFailsTests"})
    @Fails(validator = "failedExpectedIsValid")
    public void test_validExpectedFailed_withMethod() {
        Assert.fail();
    }

    @Test(groups = {"ExpectedFailsTests"})
    @Fails(validator = "failedExpectedIsNotValid")
    public void test_invalidExpectedFailed_withMethod() {
        Assert.fail();
    }

    @Test(groups = {"ExpectedFailsTests"})
    @Fails(validatorClass = ExpectedFailedValidator.class, validator = "failedExpectedIsValid")
    public void test_validExpectedFailed_withClass() {
        Assert.fail();
    }

    @Test(groups = {"ExpectedFailsTests"})
    @Fails(validatorClass = ExpectedFailedValidator.class, validator = "failedExpectedIsNotValid")
    public void test_invalidExpectedFailed_withClass() {
        Assert.fail();
    }

    @Test(dependsOnGroups = "ExpectedFailsTests", alwaysRun = true)
    public void test_validateTestStatuses() {
        List<MethodContext> retriedExpectedFailed = findMethodContexts("test_retriedExpectedFailed").collect(Collectors.toList());
        Assert.assertEquals(retriedExpectedFailed.size(), 3);
        Assert.assertEquals(retriedExpectedFailed.get(0).getStatus(), Status.RETRIED);
        Assert.assertEquals(retriedExpectedFailed.get(1).getStatus(), Status.RETRIED);
        Assert.assertEquals(retriedExpectedFailed.get(2).getStatus(), Status.FAILED_EXPECTED);

        assertMethodStatus("test_expectedFailed", Status.FAILED_EXPECTED);
        assertMethodStatus("test_expectedFailedPassed", Status.PASSED);
        assertMethodStatus("test_validExpectedFailed_withMethod", Status.FAILED_EXPECTED);
        assertMethodStatus("test_invalidExpectedFailed_withMethod", Status.FAILED);
        assertMethodStatus("test_validExpectedFailed_withClass", Status.FAILED_EXPECTED);
        assertMethodStatus("test_invalidExpectedFailed_withClass", Status.FAILED);
    }

    private void assertMethodStatus(String name, Status status) {
        Optional<MethodContext> optionalMethodContext = findMethodContexts(name).findFirst();
        Assert.assertTrue(optionalMethodContext.isPresent());

        MethodContext methodContext = optionalMethodContext.get();
        Assert.assertEquals(methodContext.getStatus(), status);
    }
}

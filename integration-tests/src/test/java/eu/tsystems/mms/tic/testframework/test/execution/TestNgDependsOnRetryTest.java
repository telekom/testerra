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

import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Testclass to test the behaviour of TestNG in case of a retried test if it is a precondition of another test
 *
 * @author mgn
 */
public class TestNgDependsOnRetryTest extends TesterraTest {

    AtomicInteger counter = new AtomicInteger(0);

    @Test(priority = 1, groups = "SEQUENTIAL")
    public void testCaseOne() {
        this.counter.incrementAndGet();
        if (counter.get() == 1) {
            // Message is already defined in test.properties
            Assert.assertTrue(false, "test_FailedToPassedHistoryWithRetry");
        } else {
            Assert.assertTrue(true);
        }

    }

    @Test(dependsOnMethods = "testCaseOne", priority = 2, groups = "SEQUENTIAL")
    public void testCaseTwo() {
        this.counter.incrementAndGet();
        Assert.assertTrue(true);
    }

    @Test(priority = 999, groups = "SEQUENTIAL")
    public void testCaseThree() {
        Assert.assertEquals(this.counter.get(), 3, "testCaseTwo should executed after retried 'dependsOn' method.");
    }

    @Test(dependsOnMethods = "testCaseOne")
    public void test_retriedTestCaseData() {
        Stream<MethodContext> methodContexts = findMethodContext("testCaseOne");

        List<MethodContext> collected = methodContexts
                .sorted(Comparator.comparingInt(MethodContext::getRetryCounter))
                .collect(Collectors.toList());

        Assert.assertEquals(collected.size(), 2);

        collected.forEach(methodContext -> {
            Assert.assertNotEquals(methodContext.getRetryCounter(), 0);
        });

        MethodContext failedMethod = collected.get(0);
        MethodContext passedMethod = collected.get(1);

        Assert.assertEquals(failedMethod.getStatus(), Status.FAILED);
        Assert.assertEquals(passedMethod.getStatus(), Status.PASSED);

        Assert.assertEquals(1, failedMethod.readRelatedMethodContexts().count());
        Assert.assertEquals(1, passedMethod.readDependsOnMethodContexts().count());
    }

    private Stream<MethodContext> findMethodContext(String methodName) {
        MethodContext currentMethodContext = ExecutionContextController.getCurrentMethodContext();
        return currentMethodContext.getClassContext().readMethodContexts()
                .filter(methodContext -> methodContext.getName().equals(methodName));
    }

}

/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems MMS GmbH, Deutsche Telekom AG
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

package io.testerra.test.logging;

import eu.tsystems.mms.tic.testframework.annotations.NoRetry;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.test.execution.TestStatusTest;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoggingTest extends TesterraTest implements Loggable, TestStatusTest {

    @Test(groups = "LOGS")
    public void test_regularLog() {
        log().info("Regular log");
    }

    @Test(groups = "LOGS", dependsOnMethods = "test_regularLog")
    public void test_regularLogInMethodContext() {
        Optional<MethodContext> optionalMethodContext = findMethodContexts("test_regularLog").findFirst();
        Assert.assertTrue(optionalMethodContext.isPresent());

        log().info("Send me to method context", optionalMethodContext.get());
    }

    @Test(groups = "LOGS", dependsOnMethods = "test_regularLog")
    public void test_promptLogInMethodContext() {
        Optional<MethodContext> optionalMethodContext = findMethodContexts("test_regularLog").findFirst();
        Assert.assertTrue(optionalMethodContext.isPresent());

        log().info("Prompt me to method context", Loggable.prompt, optionalMethodContext.get());
    }

    @Test(groups = "LOGS")
    public void test_promptLog() {
        log().warn("Warn me", Loggable.prompt);
        log().error("Tell me more!", Loggable.prompt);
    }

    @Test(groups = "LOGS")
    public void test_promptOutSideMethodContext() {
        new Thread(() -> log().info("Prompt outside method context", Loggable.prompt)).start();
    }

    @Test(dependsOnGroups = "LOGS")
    @NoRetry
    public void test_logsInMethodContexts() {
        Optional<MethodContext> optionalMethodContext = findMethodContexts("test_regularLog").findFirst();
        Assert.assertTrue(optionalMethodContext.isPresent());

        final List<LogMessage> collect = optionalMethodContext.get().readTestSteps()
                .flatMap(TestStep::readActions)
                .flatMap(testStepAction -> testStepAction.readEntries(LogMessage.class))
                .collect(Collectors.toList());

        Assert.assertEquals(5, collect.size());

        Assert.assertTrue(collect.stream().anyMatch(logMessage -> logMessage.getMessage().equals("Send me to method context")));
        Optional<LogMessage> promptLog = collect.stream().filter(logMessage -> logMessage.getMessage().equals("Prompt me to method context")).findFirst();
        Assert.assertTrue(promptLog.isPresent());
        Assert.assertTrue(promptLog.get().isPrompt());

        Assert.assertTrue(ExecutionContextController.getCurrentExecutionContext().readMethodContextLessLogs().anyMatch(logMessage -> logMessage.getMessage().equals("Prompt outside method context")));
    }
}

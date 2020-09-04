/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */

package eu.tsystems.mms.tic.testframework.report;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.events.ExecutionAbortEvent;
import eu.tsystems.mms.tic.testframework.events.ExecutionFinishEvent;
import eu.tsystems.mms.tic.testframework.events.FinalizeExecutionEvent;
import eu.tsystems.mms.tic.testframework.internal.MethodRelations;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Listener for the end of the test execution.
 * It finalizes the {@link ExecutionContext} and posts the {@link FinalizeExecutionEvent}.
 * NOTE: It is package private to prevent miss usage.
 */
final class ExecutionEndListener implements
        ExecutionFinishEvent.Listener,
        ExecutionAbortEvent.Listener,
        Loggable
{

    private static Map<String, List<MethodContext>> sortTestMethodContainerMap(final Map<String, List<MethodContext>> inputMap) {

        final Map<List, String> reverseOrigMap = new HashMap<>();
        final List<List> listOfLists = new LinkedList<>();
        for (String key : inputMap.keySet()) {
            List<MethodContext> list = inputMap.get(key);
            listOfLists.add(list);
            reverseOrigMap.put(list, key);
        }
        listOfLists.sort((o1, o2) -> o2.size() - o1.size());
        final Map<String, List<MethodContext>> newMap = new LinkedHashMap<>();
        for (List list : listOfLists) {
            newMap.put(reverseOrigMap.get(list), list);
        }
        return newMap;
    }

    // check if failed tests have an expected failed with the same root cause and a message about it to the failed test
    private static void addMatchingExpectedFailedMessage(Map<String, List<MethodContext>> failureAspects) {
        List<MethodContext> expectedFailedMethodContexts =
                failureAspects.values().stream()
                        //only one context per expected failed required
                        .map(e -> e.get(0))
                        .filter(MethodContext::isExpectedFailed)
                        .collect(Collectors.toList());

        List<MethodContext> unexpectedFailedMethodContexts =
                failureAspects.values().stream()
                        .flatMap(Collection::stream)
                        .filter(methodContext -> !methodContext.isExpectedFailed())
                        .collect(Collectors.toList());

        unexpectedFailedMethodContexts.forEach(
                context -> {
                    final Optional<MethodContext> methodContext = findMatchingMethodContext(context, expectedFailedMethodContexts);

                    if (methodContext.isPresent()) {

                        final Fails failsAnnotation =
                                methodContext.get().testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Fails.class);
                        String additionalErrorMessage = "Failure aspect matches known issue:";

                        if (StringUtils.isNotBlank(failsAnnotation.description())) {
                            additionalErrorMessage += " Description: " + failsAnnotation.description();
                        }

                        if (failsAnnotation.ticketId() > 0) {
                            additionalErrorMessage += " Ticket: " + failsAnnotation.ticketId();
                        }

                        context.errorContext().additionalErrorMessage = additionalErrorMessage;
                    }
                });
    }

    //find method context of expected failed test where it's underlying cause matches the cause of the given context
    private static Optional<MethodContext> findMatchingMethodContext(MethodContext context,
                                                                     List<MethodContext> methodContexts) {
        return methodContexts.stream()
                .filter(expectedFailedMethodContext ->
                        expectedFailedMethodContext.isExpectedFailed()
                                && context.errorContext().getThrowable().getMessage() != null
                                && expectedFailedMethodContext.errorContext().getThrowable().getCause().getMessage() != null
                                && expectedFailedMethodContext.errorContext().getThrowable().getCause().getMessage()
                                .equals(context.errorContext().getThrowable().getMessage()))
                .findFirst();
    }

    @Override
    @Subscribe
    public void onExecutionAbort(ExecutionAbortEvent event) {
        ExecutionContextController.getCurrentExecutionContext().crashed = true;
        finalizeExecutionContext();
    }

    @Override
    @Subscribe
    public void onExecutionFinish(ExecutionFinishEvent event) {
        // set the testRunFinished flag
        ExecutionContextController.testRunFinished = true;
        finalizeExecutionContext();
    }

    private void finalizeExecutionContext() {
        MethodRelations.flushAll();

        ExecutionContext currentExecutionContext = ExecutionContextController.getCurrentExecutionContext();
        currentExecutionContext.endTime = new Date();

        /*
        get ALL ClassContexts
         */
        final List<ClassContext> allClassContexts =
                new ArrayList<>(currentExecutionContext.getMethodStatsPerClass(true, false).keySet());

        /*
         * Build maps for exit points and failure aspects
         */
        log().trace("Build maps for exit points and failure aspects...");
        Map<String, List<MethodContext>> exitPoints = new TreeMap<>();
        Map<String, List<MethodContext>> failureAspects = new TreeMap<>();
        int unknownCounter = 0;
        // scan
        for (ClassContext classContext : allClassContexts) {
            for (MethodContext methodContext : classContext.methodContexts) {
                /*
                 * Every failed method that is not flagged config or retry
                 */
                if (methodContext.isFailed() && !methodContext.isConfigMethod() && !methodContext.isRetry()) {
                    /*
                    get exit points (this is the fingerprint)
                     */
                    final String fingerprint = methodContext.errorContext().errorFingerprint;
                    final String failuresMapKey;
                    if (StringUtils.isStringEmpty(fingerprint)) {
                        // fingerprint unknown -> "others"
                        unknownCounter++;
                        failuresMapKey = "unknown exit #" + unknownCounter;
                    } else {
                        // fingerprint found
                        failuresMapKey = fingerprint;
                    }

                    // push info into map
                    if (!exitPoints.containsKey(failuresMapKey)) {
                        exitPoints.put(failuresMapKey, new LinkedList<>());
                    }
                    exitPoints.get(failuresMapKey).add(methodContext);

                    /*
                    get failure aspects (this is the error message)
                     */
                    final String readableMessage = methodContext.errorContext().getReadableErrorMessage();
                    if (!failureAspects.containsKey(readableMessage)) {
                        failureAspects.put(readableMessage, new LinkedList<>());
                    }
                    failureAspects.get(readableMessage).add(methodContext);
                }
            }
        }

        /*
        Sort exitPoints
         */
        exitPoints = sortTestMethodContainerMap(exitPoints);

        /*
        Sort failureAspects
         */
        failureAspects = sortTestMethodContainerMap(failureAspects);

        /*
        check if failed tests have an expected failed with the same root cause and a message about it to the failed test
         */
        addMatchingExpectedFailedMessage(failureAspects);

        /*
        Store
         */
        currentExecutionContext.exitPoints = exitPoints;
        currentExecutionContext.failureAspects = failureAspects;

        TesterraListener.getEventBus().post(new ContextUpdateEvent().setContext(currentExecutionContext));

        FinalizeExecutionEvent event = new FinalizeExecutionEvent()
            .setExecutionContext(currentExecutionContext)
            .setMethodStatsPerClass(allClassContexts);

        TesterraListener.getEventBus().post(event);
    }
}

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
 */

package eu.tsystems.mms.tic.testframework.adapters;

import com.google.common.net.MediaType;
import com.google.gson.Gson;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.ClickPathEvent;
import eu.tsystems.mms.tic.testframework.internal.IDUtils;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.BuildInformation;
import eu.tsystems.mms.tic.testframework.report.model.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.ContextValues;
import eu.tsystems.mms.tic.testframework.report.model.ErrorContext;
import eu.tsystems.mms.tic.testframework.report.model.FailureCorridorValue;
import eu.tsystems.mms.tic.testframework.report.model.File;
import eu.tsystems.mms.tic.testframework.report.model.MethodType;
import eu.tsystems.mms.tic.testframework.report.model.ClickPathEventType;
import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.LogMessageType;
import eu.tsystems.mms.tic.testframework.report.model.TestStep;
import eu.tsystems.mms.tic.testframework.report.model.TestStepAction;
import eu.tsystems.mms.tic.testframework.report.model.ResultStatusType;
import eu.tsystems.mms.tic.testframework.report.model.RunConfig;
import eu.tsystems.mms.tic.testframework.report.model.ScriptSource;
import eu.tsystems.mms.tic.testframework.report.model.ScriptSourceLine;
import eu.tsystems.mms.tic.testframework.report.model.SessionContext;
import eu.tsystems.mms.tic.testframework.report.model.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.StackTraceCause;
import eu.tsystems.mms.tic.testframework.report.model.SuiteContext;
import eu.tsystems.mms.tic.testframework.report.model.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.TestContext;
import eu.tsystems.mms.tic.testframework.report.model.TestStepActionEntry;
import eu.tsystems.mms.tic.testframework.report.model.context.AbstractContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

public class ContextExporter implements Loggable {
    private static final Map<TestStatusController.Status, ResultStatusType> STATUS_MAPPING = new LinkedHashMap<>();
    private final Report report = TesterraListener.getReport();
    private final Gson jsonEncoder = new Gson();

    private Map<String,Object> getAnnotationParameters(Annotation annotation) {
        Method[] methods = annotation.annotationType().getMethods();
        Map<String,Object> params = new HashMap<>();
        for (Method method : methods) {
            if (method.getDeclaringClass() == annotation.annotationType()) { //this filters out built-in methods, like hashCode etc
                try {
                    Object value = method.invoke(annotation);
                    if (value == null) continue;

                    if (value.getClass().isArray()) {
                        Object[] values = (Object[])value;
                        if (values.length == 0) continue;
                        value = Arrays.asList(values);
                    } else {
                        value = value.toString();
                        if (((String) value).isEmpty()) continue;
                    }
                    params.put(method.getName(), value);
                } catch (Exception e) {
                    log().error("Unable to retrieve annotation parameter", e);
                }
            }
        }
        return params;
    }

    public MethodContext.Builder buildMethodContext(eu.tsystems.mms.tic.testframework.report.model.context.MethodContext methodContext) {
        MethodContext.Builder builder = MethodContext.newBuilder();

        ContextValues.Builder contextValuesBuilder = buildContextValues(methodContext);


        methodContext.getTestNgResult().ifPresent(iTestResult -> {
            String testName = methodContext.getName();
            String methodName = iTestResult.getMethod().getMethodName();
            // When the context name differs from the method name
            if (!testName.equals(methodName)) {
                // Set the context name to the method name
                contextValuesBuilder.setName(methodName);
                // And the test name to the actual generated test name
                builder.setTestName(testName);
            }
        });

        builder.setContextValues(contextValuesBuilder);

        map(methodContext.getStatus(), this::getMappedStatus, builder::setResultStatus);
        map(methodContext.getMethodType(), type -> MethodType.valueOf(type.name()), builder::setMethodType);
        List<Object> parameterValues = methodContext.getParameterValues();
        for (int i = 0; i < parameterValues.size(); ++i) {
            builder.putParameters(methodContext.getParameters()[i].getName(), parameterValues.get(i).toString());
        }

        methodContext.readAnnotations()
                // Skip TestNG annotations
                .filter(annotation -> !annotation.annotationType().getName().startsWith("org.testng"))
                .forEach(annotation -> {
                    builder.putAnnotations(annotation.annotationType().getName(), this.jsonEncoder.toJson(getAnnotationParameters(annotation)));
                });

        apply(methodContext.getRetryCounter(), builder::setRetryNumber);
        apply(methodContext.methodRunIndex, builder::setMethodRunIndex);

        apply(methodContext.priorityMessage, builder::setPriorityMessage);
        apply(methodContext.threadName, builder::setThreadName);

        // test steps
        methodContext.readTestSteps().forEach(testStep -> builder.addTestSteps(buildTestStep(testStep)));
        //value(methodContext.failedStep, MethodContextExporter::createTestStep, builder::setFailedStep);
        builder.setFailedStepIndex(methodContext.getLastFailedTestStepIndex());

        Class failureCorridorClass = methodContext.getFailureCorridorClass();
        if (failureCorridorClass.equals(FailureCorridor.High.class)) {
            builder.setFailureCorridorValue(FailureCorridorValue.FCV_HIGH);
        } else if (failureCorridorClass.equals(FailureCorridor.Mid.class)) {
            builder.setFailureCorridorValue(FailureCorridorValue.FCV_MID);
        } else {
            builder.setFailureCorridorValue(FailureCorridorValue.FCV_LOW);
        }
        builder.setClassContextId(methodContext.getClassContext().getId());
        forEach(methodContext.infos, builder::addInfos);
        methodContext.readRelatedMethodContexts().forEach(m -> builder.addRelatedMethodContextIds(m.getId()));
        methodContext.readDependsOnMethodContexts().forEach(m -> builder.addDependsOnMethodContextIds(m.getId()));

        // build context
        if (methodContext.hasErrorContext()) builder.setErrorContext(buildErrorContext(methodContext.getErrorContext()));
        methodContext.readSessionContexts().forEach(sessionContext -> builder.addSessionContextIds(sessionContext.getId()));

        methodContext.readCustomContexts().forEach(customContext -> {
            builder.putCustomContexts(customContext.getName(), jsonEncoder.toJson(customContext.exportToReport(report)));
        });

        return builder;
    }

    public File.Builder[] buildScreenshot(Screenshot screenshot) {
        java.io.File currentScreenshotFile = screenshot.getScreenshotFile();
        File.Builder screenshotBuilder = prepareFile(currentScreenshotFile);
        screenshotBuilder.setRelativePath(report.getRelativePath(currentScreenshotFile));
        screenshotBuilder.setMimetype(MediaType.PNG.toString());
        screenshotBuilder.putAllMeta(screenshot.getMetaData());

        java.io.File currentSourceFile = screenshot.getPageSourceFile();
        File.Builder sourceBuilder = prepareFile(currentSourceFile);
        sourceBuilder.setRelativePath(report.getRelativePath(currentSourceFile));
        sourceBuilder.setMimetype(MediaType.PLAIN_TEXT_UTF_8.toString());
        screenshotBuilder.putMeta("sourcesRefId", sourceBuilder.getId());

        File.Builder[] fileBuilders = new File.Builder[2];
        fileBuilders[0] = screenshotBuilder;
        fileBuilders[1] = sourceBuilder;
        return fileBuilders;
    }

    public File.Builder buildVideo(Video video) {
        java.io.File currentVideoFile = video.getVideoFile();
        File.Builder builder = prepareFile(currentVideoFile);
        builder.setRelativePath(report.getRelativePath(currentVideoFile));
        builder.setMimetype(MediaType.WEBM_VIDEO.toString());
        return builder;
    }

    protected File.Builder prepareFile(java.io.File source) {
        String fileId = IDUtils.getB64encXID();

        // add video data
        File.Builder builder = File.newBuilder();
        builder.setId(fileId);
        fillFileBasicData(builder, source);

        return builder;
    }

    private void fillFileBasicData(File.Builder builder, java.io.File file) {
        // timestamps
        long timestamp = System.currentTimeMillis();
        builder.setCreatedTimestamp(timestamp);
        builder.setLastModified(timestamp);

        // file size
        builder.setSize(file.length());
    }
//
//    public StackTrace.Builder prepareStackTrace(eu.tsystems.mms.tic.testframework.report.model.context.StackTrace stackTrace) {
//        StackTrace.Builder builder = StackTrace.newBuilder();
//
//        //apply(stackTrace.additionalErrorMessage, builder::setAdditionalErrorMessage);
//        map(stackTrace.stackTrace, this::prepareStackTraceCause, builder::setCause);
//
//        return builder;
//    }

    public ScriptSource.Builder buildScriptSource(eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource scriptSource) {
        ScriptSource.Builder builder = ScriptSource.newBuilder();

        apply(scriptSource.getFileName(), builder::setFileName);
        apply(scriptSource.getMethodName(), builder::setMethodName);
        scriptSource.readLines().forEach(line -> builder.addLines(buildScriptSourceLine(line)));
        builder.setMark(scriptSource.getMarkedLineNumber());

        return builder;
    }

    public ScriptSourceLine.Builder buildScriptSourceLine(eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource.Line line) {
        ScriptSourceLine.Builder builder = ScriptSourceLine.newBuilder();

        apply(line.getLine(), builder::setLine);
        apply(line.getLineNumber(), builder::setLineNumber);
//        apply(line.mark, builder::setMark);

        return builder;
    }

    public ErrorContext.Builder buildErrorContext(eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext errorContext) {
        ErrorContext.Builder builder = ErrorContext.newBuilder();

//        apply(errorContext.getReadableErrorMessage(), builder::setReadableErrorMessage);
//        apply(errorContext.getAdditionalErrorMessage(), builder::setAdditionalErrorMessage);
        traceThrowable(errorContext.getThrowable(), throwable -> {
            builder.addStackTrace(this.buildStackTraceCause(throwable));
        });
//        map(errorContext.getThrowable(), this::prepareStackTraceCause, builder::addAllCause);
//        apply(errorContext.errorFingerprint, builder::setErrorFingerprint);
        errorContext.getScriptSource().ifPresent(scriptSource -> builder.setScriptSource(this.buildScriptSource(scriptSource)));
        //errorContext.getExecutionObjectSource().ifPresent(scriptSource -> builder.setExecutionObjectSource(this.buildScriptSource(scriptSource)));
        if (errorContext.getTicketId() != null) builder.setTicketId(errorContext.getTicketId().toString());
        apply(errorContext.getDescription(), builder::setDescription);
        builder.setOptional(errorContext.isOptional());

        return builder;
    }

    public TestStep.Builder buildTestStep(eu.tsystems.mms.tic.testframework.report.model.steps.TestStep testStep) {
        TestStep.Builder builder = TestStep.newBuilder();

        apply(testStep.getName(), builder::setName);
        forEach(testStep.getTestStepActions(), testStepAction -> builder.addActions(buildTestStepAction(testStepAction)));

        return builder;
    }

    public ClickPathEvent.Builder buildClickPathEvent(eu.tsystems.mms.tic.testframework.clickpath.ClickPathEvent clickPathEvent) {
        ClickPathEvent.Builder builder = eu.tsystems.mms.tic.testframework.report.model.ClickPathEvent.newBuilder();
        switch (clickPathEvent.getType()) {
            case WINDOW:
                builder.setType(ClickPathEventType.CPET_WINDOW);
                break;
            case CLICK:
                builder.setType(ClickPathEventType.CPET_CLICK);
                break;
            case VALUE:
                builder.setType(ClickPathEventType.CPET_VALUE);
                break;
            case PAGE:
                builder.setType(ClickPathEventType.CPET_PAGE);
                break;
            case URL:
                builder.setType(ClickPathEventType.CPET_URL);
                break;
            default:
                builder.setType(ClickPathEventType.CPET_NOT_SET);
        }
        builder.setSubject(clickPathEvent.getSubject());
        builder.setSessionId(clickPathEvent.getSessionId());
        return builder;
    }

    public TestStepAction.Builder buildTestStepAction(eu.tsystems.mms.tic.testframework.report.model.steps.TestStepAction testStepAction) {
        TestStepAction.Builder actionBuilder = TestStepAction.newBuilder();

        apply(testStepAction.getName(), actionBuilder::setName);
        apply(testStepAction.getTimestamp(), actionBuilder::setTimestamp);

        testStepAction.readEntries().forEach(entry -> {
            TestStepActionEntry.Builder entryBuilder = TestStepActionEntry.newBuilder();
            if (entry instanceof eu.tsystems.mms.tic.testframework.clickpath.ClickPathEvent) {
                Optional<ClickPathEvent.Builder> optional = Optional.ofNullable(this.buildClickPathEvent((eu.tsystems.mms.tic.testframework.clickpath.ClickPathEvent) entry));
                optional.ifPresent(entryBuilder::setClickPathEvent);
            } else if (entry instanceof Screenshot) {
                File.Builder[] builders = buildScreenshot((Screenshot) entry);
                Optional<File.Builder> optional = Optional.ofNullable(builders[0]);
                optional.ifPresent(file -> entryBuilder.setScreenshotId(file.getId()));
            } else if (entry instanceof eu.tsystems.mms.tic.testframework.report.model.context.LogMessage) {
                eu.tsystems.mms.tic.testframework.report.model.context.LogMessage logEvent = (eu.tsystems.mms.tic.testframework.report.model.context.LogMessage)entry;
                Optional<LogMessage.Builder> optional = Optional.ofNullable(buildLogMessage(logEvent));
                optional.ifPresent(entryBuilder::setLogMessage);
            } else if (entry instanceof eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext) {
                eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext errorContext = (eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext)entry;
                Optional<ErrorContext.Builder> optional = Optional.ofNullable(buildErrorContext(errorContext));
                optional.ifPresent(entryBuilder::setAssertion);
            }

            if (
                    entryBuilder.hasAssertion()
                    || entryBuilder.hasLogMessage()
                    || entryBuilder.hasClickPathEvent()
                    || StringUtils.isNotBlank(entryBuilder.getScreenshotId())
            ) {
                actionBuilder.addEntries(entryBuilder);
            }
        });
        return actionBuilder;
    }

    public ContextExporter() {
        // Prepare a status map
        for (TestStatusController.Status status : TestStatusController.Status.values()) {
            ResultStatusType resultStatusType = ResultStatusType.valueOf(status.name());
            STATUS_MAPPING.put(status, resultStatusType);
        }
    }

    ResultStatusType getMappedStatus(TestStatusController.Status status) {
        return STATUS_MAPPING.get(status);
    }

    /**
     * Applies a value if not null
     */
    protected <T, R> void apply(T value, Function<T, R> function) {
        if (value != null) {
            function.apply(value);
        }
    }

    /**
     * Maps and applies a value if not null
     */
    protected<T, M, R> void map(T value, Function<T, M> map, Function<M, R> function) {
        if (value != null) {
            M m = map.apply(value);
            function.apply(m);
        }
    }

    /**
     * Iterates if not null
     */
    protected <T extends Iterable<C>, C> void forEach(T value, Consumer<C> function) {
        if (value != null) {
            value.forEach(function);
        }
    }

    protected ContextValues.Builder buildContextValues(AbstractContext context) {
        ContextValues.Builder builder = ContextValues.newBuilder();

        apply(context.getId(), builder::setId);
        //apply(context.swi, builder::setSwi);
        apply(System.currentTimeMillis(), builder::setCreated);
        apply(context.getName(), builder::setName);
        map(context.getStartTime(), Date::getTime, builder::setStartTime);
        map(context.getEndTime(), Date::getTime, builder::setEndTime);

//        if (context instanceof eu.tsystems.mms.tic.testframework.report.model.context.MethodContext) {
//            eu.tsystems.mms.tic.testframework.report.model.context.MethodContext methodContext = (eu.tsystems.mms.tic.testframework.report.model.context.MethodContext) context;
//
//            // result status
//            map(methodContext.getStatus(), this::getMappedStatus, builder::setResultStatus);
//
//            // exec status
//            if (methodContext.getStatus() == TestStatusController.Status.NO_RUN) {
//                builder.setExecStatus(ExecStatusType.RUNNING);
//            } else {
//                builder.setExecStatus(ExecStatusType.FINISHED);
//            }
//        } else if (context instanceof eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext) {
//            eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext executionContext = (eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext) context;
//            if (executionContext.crashed) {
//                /*
//                crashed state
//                 */
//                builder.setExecStatus(ExecStatusType.CRASHED);
//            } else {
//                if (TestStatusController.getTestsSkipped() == executionContext.estimatedTestMethodCount) {
//                    builder.setResultStatus(ResultStatusType.SKIPPED);
//                    builder.setExecStatus(ExecStatusType.VOID);
//                } else if (TestStatusController.getTestsFailed() + TestStatusController.getTestsSuccessful() == 0) {
//                    builder.setResultStatus(ResultStatusType.NO_RUN);
//                    builder.setExecStatus(ExecStatusType.VOID);
//
//                } else {
//                    ResultStatusType resultStatusType = STATUS_MAPPING.get(executionContext.getStatus());
//                    builder.setResultStatus(resultStatusType);
//
//                    // exec status
//                    if (executionContext.getEndTime() != null) {
//                        builder.setExecStatus(ExecStatusType.FINISHED);
//                    } else {
//                        builder.setExecStatus(ExecStatusType.RUNNING);
//                    }
//                }
//            }
//        }
        return builder;
    }

    public LogMessage.Builder buildLogMessage(eu.tsystems.mms.tic.testframework.report.model.context.LogMessage logMessage) {
        LogMessage.Builder builder = LogMessage.newBuilder();
        apply(logMessage.getLoggerName(), builder::setLoggerName);
        builder.setMessage(logMessage.getMessage());
        if (logMessage.getLogLevel() == Level.ERROR) {
            builder.setType(LogMessageType.LMT_ERROR);
        } else if (logMessage.getLogLevel() == Level.WARN) {
            builder.setType(LogMessageType.LMT_WARN);
        } else if (logMessage.getLogLevel() == Level.INFO) {
            builder.setType(LogMessageType.LMT_INFO);
        } else if (logMessage.getLogLevel() == Level.DEBUG) {
            builder.setType(LogMessageType.LMT_DEBUG);
        }
        builder.setTimestamp(logMessage.getTimestamp());
        builder.setThreadName(logMessage.getThreadName());

        traceThrowable(logMessage.getThrown(), throwable -> {
            builder.addStackTrace(this.buildStackTraceCause(throwable));
        });
        return builder;
    }

    public StackTraceCause.Builder buildStackTraceCause(Throwable throwable) {
        StackTraceCause.Builder builder = StackTraceCause.newBuilder();
        apply(throwable.getClass().getName(), builder::setClassName);
        apply(throwable.getMessage(), builder::setMessage);
        builder.addAllStackTraceElements(Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()));
//
//        if ((throwable.getCause() != null) && (throwable.getCause() != throwable)) {
//            builder.setCause(this.prepareStackTraceCause(throwable.getCause()));
//        }
        return builder;
    }

    private void traceThrowable(Throwable throwable, Consumer<Throwable> consumer) {
        while (throwable != null) {
            consumer.accept(throwable);
            throwable = throwable.getCause();
        }
    }

    public ClassContext.Builder buildClassContext(eu.tsystems.mms.tic.testframework.report.model.context.ClassContext classContext) {
        ClassContext.Builder builder = ClassContext.newBuilder();

        apply(buildContextValues(classContext), builder::setContextValues);
        apply(classContext.getTestClass().getName(), builder::setFullClassName);
        apply(classContext.getTestContext().getId(), builder::setTestContextId);
        classContext.getTestClassContext().ifPresent(testClassContext -> builder.setTestContextName(testClassContext.name()));

        return builder;
    }

    public ExecutionContext.Builder buildExecutionContext(eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext executionContext) {
        ExecutionContext.Builder builder = ExecutionContext.newBuilder();

        apply(buildContextValues(executionContext), builder::setContextValues);
//        forEach(executionContext.suiteContexts, suiteContext -> builder.addSuiteContextIds(suiteContext.getId()));
//        forEach(executionContext.mergedClassContexts, classContext -> builder.addMergedClassContextIds(classContext.id));
//        map(executionContext.exitPoints, this::createContextClip, builder::addAllExitPoints);
//        map(executionContext.failureAspects, this::createContextClip, builder::addAllFailureAscpects);
        map(executionContext.runConfig, this::buildRunConfig, builder::setRunConfig);
        executionContext.readExclusiveSessionContexts().forEach(sessionContext -> builder.addExclusiveSessionContextIds(sessionContext.getId()));
        apply(executionContext.estimatedTestMethodCount, builder::setEstimatedTestsCount);
        executionContext.readMethodContextLessLogs().forEach(logEvent -> {
            Optional<LogMessage.Builder> optional = Optional.ofNullable(buildLogMessage(logEvent));
            optional.ifPresent(builder::addLogMessages);
        });
        builder.putFailureCorridorLimits(FailureCorridorValue.FCV_HIGH_VALUE, FailureCorridor.getAllowedTestFailuresHIGH());
        builder.putFailureCorridorLimits(FailureCorridorValue.FCV_MID_VALUE, FailureCorridor.getAllowedTestFailuresMID());
        builder.putFailureCorridorLimits(FailureCorridorValue.FCV_LOW_VALUE, FailureCorridor.getAllowedTestFailuresLOW());
        return builder;
    }
//
//    private List<ContextClip> createContextClip(Map<String, List<eu.tsystems.mms.tic.testframework.report.model.context.MethodContext>> values) {
//        List<ContextClip> out = new LinkedList<>();
//        values.forEach((key, list) -> {
//            ContextClip.Builder builder = ContextClip.newBuilder();
//            builder.setKey(key);
//            list.forEach(methodContext -> builder.addMethodContextIds(methodContext.id));
//            ContextClip contextClip = builder.build();
//            out.add(contextClip);
//        });
//        return out;
//    }

    public RunConfig.Builder buildRunConfig(eu.tsystems.mms.tic.testframework.report.model.context.RunConfig runConfig) {
        RunConfig.Builder builder = RunConfig.newBuilder();

        apply(runConfig.getReportName(), builder::setReportName);
        apply(runConfig.RUNCFG, builder::setRuncfg);

        /*
        add build information
         */
        BuildInformation.Builder bi = BuildInformation.newBuilder();
        eu.tsystems.mms.tic.testframework.internal.BuildInformation buildInformation = TesterraListener.getBuildInformation();
        apply(buildInformation.buildJavaVersion, bi::setBuildJavaVersion);
        //apply(runConfig.buildInformation.buildOsArch, bi::setBuildOsName);
        apply(buildInformation.buildOsName, bi::setBuildOsName);
        apply(buildInformation.buildOsVersion, bi::setBuildOsVersion);
        apply(buildInformation.buildTimestamp, bi::setBuildTimestamp);
        apply(buildInformation.buildUserName, bi::setBuildUserName);
        apply(buildInformation.buildVersion, bi::setBuildVersion);
        builder.setBuildInformation(bi);

        return builder;
    }

    public SessionContext.Builder buildSessionContext(eu.tsystems.mms.tic.testframework.report.model.context.SessionContext sessionContext) {
        SessionContext.Builder builder = SessionContext.newBuilder();

        apply(buildContextValues(sessionContext), builder::setContextValues);
        //apply(sessionContext.getSessionKey(), builder::setSessionKey);
        //apply(sessionContext.getProvider(), builder::setProvider);
        sessionContext.getRemoteSessionId().ifPresent(builder::setSessionId);
        sessionContext.getVideo().ifPresent(video -> {
            Optional<File.Builder> optional = Optional.ofNullable(buildVideo(video));
            optional.ifPresent(fileBuilder -> builder.setVideoId(fileBuilder.getId()));
        });
        builder.setExecutionContextId(ExecutionContextController.getCurrentExecutionContext().getId());

        apply(sessionContext.getActualBrowserName(), builder::setBrowserName);
        apply(sessionContext.getActualBrowserVersion(), builder::setBrowserVersion);
        sessionContext.getCapabilities().ifPresent(map -> builder.setCapabilities(jsonEncoder.toJson(map)));
        sessionContext.getWebDriverRequest().getServerUrl().ifPresent(url -> builder.setServerUrl(url.toString()));
        sessionContext.getNodeInfo().ifPresent(nodeInfo -> builder.setNodeUrl(nodeInfo.toString()));
        return builder;
    }

    public SuiteContext.Builder buildSuiteContext(eu.tsystems.mms.tic.testframework.report.model.context.SuiteContext suiteContext) {
        SuiteContext.Builder builder = SuiteContext.newBuilder();

        apply(buildContextValues(suiteContext), builder::setContextValues);
        builder.setExecutionContextId(suiteContext.getExecutionContext().getId());

        return builder;
    }

    public TestContext.Builder buildTestContext(eu.tsystems.mms.tic.testframework.report.model.context.TestContext testContext) {
        TestContext.Builder builder = TestContext.newBuilder();

        apply(buildContextValues(testContext), builder::setContextValues);
        builder.setSuiteContextId(testContext.getSuiteContext().getId());

        return builder;
    }
}

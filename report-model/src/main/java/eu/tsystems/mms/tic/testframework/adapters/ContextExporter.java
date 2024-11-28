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
import com.google.gson.GsonBuilder;
import com.google.inject.Injector;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.internal.IdGenerator;
import eu.tsystems.mms.tic.testframework.internal.metrics.Measurable;
import eu.tsystems.mms.tic.testframework.internal.metrics.MetricsController;
import eu.tsystems.mms.tic.testframework.internal.metrics.MetricsType;
import eu.tsystems.mms.tic.testframework.internal.metrics.TimeInfo;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.ITestStatusController;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.BuildInformation;
import eu.tsystems.mms.tic.testframework.report.model.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.ClickPathEvent;
import eu.tsystems.mms.tic.testframework.report.model.ClickPathEventType;
import eu.tsystems.mms.tic.testframework.report.model.ContextValues;
import eu.tsystems.mms.tic.testframework.report.model.ErrorContext;
import eu.tsystems.mms.tic.testframework.report.model.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.FailureCorridorValue;
import eu.tsystems.mms.tic.testframework.report.model.File;
import eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext;
import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.LogMessageType;
import eu.tsystems.mms.tic.testframework.report.model.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.MethodType;
import eu.tsystems.mms.tic.testframework.report.model.MetricType;
import eu.tsystems.mms.tic.testframework.report.model.MetricsValue;
import eu.tsystems.mms.tic.testframework.report.model.ResultStatusType;
import eu.tsystems.mms.tic.testframework.report.model.RunConfig;
import eu.tsystems.mms.tic.testframework.report.model.ScriptSource;
import eu.tsystems.mms.tic.testframework.report.model.ScriptSourceLine;
import eu.tsystems.mms.tic.testframework.report.model.SessionContext;
import eu.tsystems.mms.tic.testframework.report.model.SessionMetric;
import eu.tsystems.mms.tic.testframework.report.model.StackTraceCause;
import eu.tsystems.mms.tic.testframework.report.model.SuiteContext;
import eu.tsystems.mms.tic.testframework.report.model.TestContext;
import eu.tsystems.mms.tic.testframework.report.model.TestMetrics;
import eu.tsystems.mms.tic.testframework.report.model.TestStep;
import eu.tsystems.mms.tic.testframework.report.model.TestStepAction;
import eu.tsystems.mms.tic.testframework.report.model.TestStepActionEntry;
import eu.tsystems.mms.tic.testframework.report.model.context.AbstractContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ContextExporter implements Loggable {
    private final Injector injector = Testerra.getInjector();
    private final IdGenerator idGenerator = injector.getInstance(IdGenerator.class);
    private final Map<Status, ResultStatusType> RESULT_STATUS_MAPPING = new LinkedHashMap<>();
    private final Map<Class, FailureCorridorValue> FAILURE_CORRIDOR_MAPPING = new LinkedHashMap<>();
    private final Report report = injector.getInstance(Report.class);

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

        builder.setResultStatus(this.mapResultStatus(methodContext));
        map(methodContext.getMethodType(), type -> MethodType.valueOf(type.name()), builder::setMethodType);
        List<Object> parameterValues = methodContext.getParameterValues();
        for (int i = 0; i < parameterValues.size(); ++i) {
            builder.putParameters(methodContext.getParameters()[i].getName(), parameterValues.get(i).toString());
        }

        methodContext.readAnnotations()
                .forEach(annotation -> {
                    report.getAnnotationConverter(annotation).ifPresent(annotationExporter -> {
                        builder.putAnnotations(annotation.annotationType().getName(), this.convertObjectToJson(annotationExporter.toMap(annotation)));
                    });
                });

        apply(methodContext.getRetryCounter(), builder::setRetryNumber);
        apply(methodContext.getMethodRunIndex(), builder::setMethodRunIndex);
        //methodContext.getPriorityMessage().ifPresent(builder::setPriorityMessage);
        apply(methodContext.getThreadName(), builder::setThreadName);

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
        methodContext.readRelatedMethodContexts().forEach(m -> builder.addRelatedMethodContextIds(m.getId()));
        methodContext.readDependsOnMethodContexts().forEach(m -> builder.addDependsOnMethodContextIds(m.getId()));

        // build context
        methodContext.readSessionContexts().forEach(sessionContext -> builder.addSessionContextIds(sessionContext.getId()));

        methodContext.readLayoutCheckContexts()
                .forEach(layoutCheckContext -> builder.addLayoutCheckContext(buildLayoutCheckContext(layoutCheckContext)));

        methodContext.readCustomContexts().forEach(customContext -> {
            builder.putCustomContexts(customContext.getName(), this.convertObjectToJson(customContext.exportToReport(report)));
        });

        return builder;
    }

    public File.Builder[] buildScreenshot(Screenshot screenshot) {
        File.Builder[] fileBuilders = new File.Builder[2];

        java.io.File currentScreenshotFile = screenshot.getScreenshotFile();
        File.Builder screenshotBuilder = prepareFile(currentScreenshotFile);
        screenshotBuilder.setRelativePath(report.getRelativePath(currentScreenshotFile));
        screenshotBuilder.setMimetype(MediaType.PNG.toString());
        screenshotBuilder.putAllMeta(screenshot.getMetaData());
        fileBuilders[0] = screenshotBuilder;

        screenshot.getPageSourceFile().ifPresent(currentSourceFile -> {
            File.Builder sourceBuilder = prepareFile(currentSourceFile);
            sourceBuilder.setRelativePath(report.getRelativePath(currentSourceFile));
            sourceBuilder.setMimetype(MediaType.PLAIN_TEXT_UTF_8.toString());
            screenshotBuilder.putMeta("sourcesRefId", sourceBuilder.getId());
            fileBuilders[1] = sourceBuilder;
        });
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
        String fileId = idGenerator.generate().toString();

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
//        if (errorContext.getTicketId() != null) builder.setTicketId(errorContext.getTicketId().toString());
//        apply(errorContext.getDescription(), builder::setDescription);
        builder.setOptional(errorContext.isOptional());
        apply(errorContext.getId(), builder::setId);
        return builder;
    }

    public TestStep.Builder buildTestStep(eu.tsystems.mms.tic.testframework.report.model.steps.TestStep testStep) {
        TestStep.Builder builder = TestStep.newBuilder();

        apply(testStep.getName(), builder::setName);
        testStep.readActions().forEach(testStepAction -> builder.addActions(buildTestStepAction(testStepAction)));

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
                String logMessageId = ((eu.tsystems.mms.tic.testframework.report.model.context.LogMessage) entry).getId();
                entryBuilder.setLogMessageId(logMessageId);
            } else if (entry instanceof eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext) {
                eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext errorContext = (eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext) entry;
                Optional<ErrorContext.Builder> optional = Optional.ofNullable(buildErrorContext(errorContext));
                optional.ifPresent(entryBuilder::setErrorContext);
            }

            if (
                    entryBuilder.hasErrorContext()
                            || StringUtils.isNotBlank(entryBuilder.getLogMessageId())
                            || entryBuilder.hasClickPathEvent()
                            || StringUtils.isNotBlank(entryBuilder.getScreenshotId())
            ) {
                actionBuilder.addEntries(entryBuilder);
            }
        });
        return actionBuilder;
    }

    public LayoutCheckContext.Builder buildLayoutCheckContext(eu.tsystems.mms.tic.testframework.report.model.context.LayoutCheckContext layoutCheckContext) {
        LayoutCheckContext.Builder builder = LayoutCheckContext.newBuilder();
        apply(layoutCheckContext.image, builder::setImage);
        apply(layoutCheckContext.distance, builder::setDistance);

        File.Builder[] fileBuilders = buildScreenshot(layoutCheckContext.expectedScreenshot);
        Optional<File.Builder> fileOptional = Optional.ofNullable(fileBuilders[0]);
        fileOptional.ifPresent(file -> builder.setExpectedScreenshotId(file.getId()));

        fileBuilders = buildScreenshot(layoutCheckContext.actualScreenshot);
        fileOptional = Optional.ofNullable(fileBuilders[0]);
        fileOptional.ifPresent(file -> builder.setActualScreenshotId(file.getId()));

        fileBuilders = buildScreenshot(layoutCheckContext.distanceScreenshot);
        fileOptional = Optional.ofNullable(fileBuilders[0]);
        fileOptional.ifPresent(file -> builder.setDistanceScreenshotId(file.getId()));

        if (layoutCheckContext.errorContext != null) {
            apply(layoutCheckContext.errorContext.getId(), builder::setErrorContextId);
        }

        return builder;
    }

    public ContextExporter() {
        // Prepare a status map
        RESULT_STATUS_MAPPING.put(Status.NO_RUN, ResultStatusType.NO_RUN);
        RESULT_STATUS_MAPPING.put(Status.FAILED, ResultStatusType.FAILED);
        RESULT_STATUS_MAPPING.put(Status.SKIPPED, ResultStatusType.SKIPPED);
        RESULT_STATUS_MAPPING.put(Status.PASSED, ResultStatusType.PASSED);
        RESULT_STATUS_MAPPING.put(Status.FAILED_EXPECTED, ResultStatusType.FAILED_EXPECTED);
        RESULT_STATUS_MAPPING.put(Status.REPAIRED, ResultStatusType.REPAIRED);
        RESULT_STATUS_MAPPING.put(Status.RETRIED, ResultStatusType.FAILED_RETRIED);
        RESULT_STATUS_MAPPING.put(Status.RECOVERED, ResultStatusType.PASSED_RETRY);

        FAILURE_CORRIDOR_MAPPING.put(FailureCorridor.High.class, FailureCorridorValue.FCV_HIGH);
        FAILURE_CORRIDOR_MAPPING.put(FailureCorridor.Mid.class, FailureCorridorValue.FCV_MID);
        FAILURE_CORRIDOR_MAPPING.put(FailureCorridor.Low.class, FailureCorridorValue.FCV_LOW);
    }

    private ResultStatusType mapResultStatus(eu.tsystems.mms.tic.testframework.report.model.context.MethodContext methodContext) {
        Status status = methodContext.getStatus();
        ResultStatusType resultStatusType = RESULT_STATUS_MAPPING.get(status);
        if (resultStatusType == null) {
            resultStatusType = ResultStatusType.RST_NOT_SET;
            log().error(String.format("Unable to map result status '%s' of method '%s', using '%s'", status, methodContext.getName(), resultStatusType));
        }
        return resultStatusType;
    }

    private FailureCorridorValue mapFailureCorridorClass(Class failureCorridorClass) {
        FailureCorridorValue failureCorridorValue = FAILURE_CORRIDOR_MAPPING.get(failureCorridorClass);
        if (failureCorridorValue == null) {
            failureCorridorValue = FailureCorridorValue.FCV_HIGH;
            log().warn(String.format("Mapping unknown failure corridor class '%s' to '%s'", failureCorridorClass, failureCorridorValue));
        }
        return failureCorridorValue;
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
    protected <T, M, R> void map(T value, Function<T, M> map, Function<M, R> function) {
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
        builder.setPrompt(logMessage.isPrompt());
        builder.setId(logMessage.getId());

        logMessage.getThrown().ifPresent(t -> {
            traceThrowable(t, throwable -> {
                builder.addStackTrace(this.buildStackTraceCause(throwable));
            });
        });

        return builder;
    }

    public StackTraceCause.Builder buildStackTraceCause(Throwable throwable) {
        StackTraceCause.Builder builder = StackTraceCause.newBuilder();
        apply(throwable.getClass().getName(), builder::setClassName);
        apply(throwable.getMessage(), builder::setMessage);
        builder.addAllStackTraceElements(Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()));
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
        map(executionContext.getRunConfig(), this::buildRunConfig, builder::setRunConfig);
        executionContext.readExclusiveSessionContexts().forEach(sessionContext -> builder.addExclusiveSessionContextIds(sessionContext.getId()));
        apply(executionContext.getEstimatedTestMethodCount(), builder::setEstimatedTestsCount);
        executionContext.readMethodContextLessLogs().forEach(logMessage -> builder.addLogMessageIds(logMessage.getId()));
        builder.putFailureCorridorLimits(FailureCorridorValue.FCV_HIGH_VALUE, FailureCorridor.getAllowedTestFailuresHIGH());
        builder.putFailureCorridorLimits(FailureCorridorValue.FCV_MID_VALUE, FailureCorridor.getAllowedTestFailuresMID());
        builder.putFailureCorridorLimits(FailureCorridorValue.FCV_LOW_VALUE, FailureCorridor.getAllowedTestFailuresLOW());

        ITestStatusController testStatusController = Testerra.getInjector().getInstance(ITestStatusController.class);
        Stream.of(FailureCorridor.High.class, FailureCorridor.Mid.class, FailureCorridor.Low.class).forEach(failureCorridorClass -> {
            int count = testStatusController.getFailureCorridorCount(failureCorridorClass);
            if (count > 0) {
                builder.putFailureCorridorCounts(mapFailureCorridorClass(failureCorridorClass).getNumber(), count);
            }
        });

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
        eu.tsystems.mms.tic.testframework.internal.BuildInformation buildInformation = Testerra.getBuildInformation();
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
        IExecutionContextController instance = Testerra.getInjector().getInstance(IExecutionContextController.class);

        SessionContext.Builder builder = SessionContext.newBuilder();

        apply(buildContextValues(sessionContext), builder::setContextValues);
        //apply(sessionContext.getSessionKey(), builder::setSessionKey);
        //apply(sessionContext.getProvider(), builder::setProvider);
        sessionContext.getRemoteSessionId().ifPresent(builder::setSessionId);
        sessionContext.getVideo().ifPresent(video -> {
            Optional<File.Builder> optional = Optional.ofNullable(buildVideo(video));
            optional.ifPresent(fileBuilder -> builder.setVideoId(fileBuilder.getId()));
        });
        apply(instance.getExecutionContext().getId(), builder::setExecutionContextId);
        sessionContext.getActualBrowserName().ifPresent(builder::setBrowserName);
        sessionContext.getActualBrowserVersion().ifPresent(builder::setBrowserVersion);
        sessionContext.getUserAgent().ifPresent(builder::setUserAgent);
        apply(this.convertObjectToJson(sessionContext.getWebDriverRequest().getCapabilities()), builder::setCapabilities);
        sessionContext.getWebDriverRequest().getServerUrl().ifPresent(url -> builder.setServerUrl(url.toString()));
        sessionContext.getNodeInfo().ifPresent(nodeInfo -> builder.setNodeUrl(nodeInfo.toString()));
        apply(sessionContext.getBaseUrl(), builder::setBaseUrl);
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

    public TestMetrics.Builder buildTestMetrics() {
        TestMetrics.Builder testMetricsBuilder = TestMetrics.newBuilder();
        MetricsController metricsController = Testerra.getInjector().getInstance(MetricsController.class);
        metricsController.readMetrics().forEach(entry -> {
            buildSessionContextMetrics(entry.getKey(), entry.getValue(), testMetricsBuilder);
            // Call other metrics exports here if needed
        });

        return testMetricsBuilder;
    }

    public void buildSessionContextMetrics(Measurable measurable, Map<MetricsType, TimeInfo> metrics, TestMetrics.Builder testMetricBuilder) {
        if (measurable instanceof eu.tsystems.mms.tic.testframework.report.model.context.SessionContext) {
            eu.tsystems.mms.tic.testframework.report.model.context.SessionContext sessionContext = (eu.tsystems.mms.tic.testframework.report.model.context.SessionContext) measurable;

            SessionMetric.Builder sessionMetricBuilder = SessionMetric.newBuilder();
            apply(sessionContext.getId(), sessionMetricBuilder::setSessionContextId);

            metrics.forEach((key, value) -> {
                MetricsValue.Builder metricsBuilder = MetricsValue.newBuilder();
                map(key, type -> MetricType.valueOf(type.name()), metricsBuilder::setMetricType);
                map(value.getStartTime(), Instant::toEpochMilli, metricsBuilder::setStartTimestamp);
                // There is no end time if something went wrong (e.g. an exception occurred)
                map(value.getEndTime(), Instant::toEpochMilli, metricsBuilder::setEndTimestamp);
                sessionMetricBuilder.addMetricsValues(metricsBuilder);
            });

            testMetricBuilder.addSessionMetrics(sessionMetricBuilder.build());
        }
    }

    /**
     * If caps contain java.util.logging.Level, they need to handled via a custom serializer to prevent
     * 'Unable to make field private final java.lang.String java.util.logging.Level.name accessible:'.
     */
    private String convertObjectToJson(Object o) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(java.util.logging.Level.class, new GsonCustomTypeSerializer<java.util.logging.Level>())
                .create();
        return gson.toJson(o);
    }

}

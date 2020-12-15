package eu.tsystems.mms.tic.testframework.adapters;

import com.google.common.net.MediaType;
import com.google.gson.Gson;
import eu.tsystems.mms.tic.testframework.report.model.ClickPathEvent;
import eu.tsystems.mms.tic.testframework.internal.IDUtils;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.BuildInformation;
import eu.tsystems.mms.tic.testframework.report.model.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.ContextValues;
import eu.tsystems.mms.tic.testframework.report.model.ErrorContext;
import eu.tsystems.mms.tic.testframework.report.model.ExecStatusType;
import eu.tsystems.mms.tic.testframework.report.model.FailureCorridorValue;
import eu.tsystems.mms.tic.testframework.report.model.File;
import eu.tsystems.mms.tic.testframework.report.model.FileOrBuilder;
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
import eu.tsystems.mms.tic.testframework.report.model.StackTraceCause;
import eu.tsystems.mms.tic.testframework.report.model.SuiteContext;
import eu.tsystems.mms.tic.testframework.report.model.TestContext;
import eu.tsystems.mms.tic.testframework.report.model.TestStepActionEntry;
import eu.tsystems.mms.tic.testframework.report.model.context.AbstractContext;
import eu.tsystems.mms.tic.testframework.report.model.context.CustomContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import static eu.tsystems.mms.tic.testframework.report.model.SessionContext.newBuilder;

public class ContextExporter {
    private static final Map<TestStatusController.Status, ResultStatusType> STATUS_MAPPING = new LinkedHashMap<>();
    private final Report report = new Report();
    private final Gson jsonEncoder = new Gson();
    private final java.io.File targetVideoDir = report.getFinalReportDirectory(Report.VIDEO_FOLDER_NAME);
    private final java.io.File targetScreenshotDir = report.getFinalReportDirectory(Report.SCREENSHOTS_FOLDER_NAME);
    private final java.io.File currentVideoDir = report.getFinalReportDirectory(Report.VIDEO_FOLDER_NAME);
    private final java.io.File currentScreenshotDir = report.getFinalReportDirectory(Report.SCREENSHOTS_FOLDER_NAME);
    private Consumer<File.Builder> fileBuilderConsumer;

    public ContextExporter setFileBuilderConsumer(Consumer<File.Builder> fileBuilderConsumer) {
        this.fileBuilderConsumer = fileBuilderConsumer;
        return this;
    }

    private static String annotationToString(Annotation annotation) {
        String json = "\"" + annotation.annotationType().getSimpleName() + "\"";
        json += " : { ";

        Method[] methods = annotation.annotationType().getMethods();
        List<String> params = new LinkedList<>();
        for (Method method : methods) {
            if (method.getDeclaringClass() == annotation.annotationType()) { //this filters out built-in methods, like hashCode etc
                try {
                    params.add("\"" + method.getName() + "\" : \"" + method.invoke(annotation) + "\"");
                } catch (Exception e) {
                    params.add("\"" + method.getName() + "\" : \"---error---\"");
                }
            }
        }
        json += String.join(", ", params);

        json += " }";
        return json;
    }

    private String mapArtifactsPath(String absolutePath) {
        String path = absolutePath.replace(report.getFinalReportDirectory().toString(), "");

        // replace all \ with /
        path = path.replaceAll("\\\\", "/");

        // remove leading /
        while (path.startsWith("/")) {
            path = path.substring(1);
        }

        return path;
    }

    public eu.tsystems.mms.tic.testframework.report.model.MethodContext.Builder prepareMethodContext(eu.tsystems.mms.tic.testframework.report.model.context.MethodContext methodContext) {
        eu.tsystems.mms.tic.testframework.report.model.MethodContext.Builder builder = eu.tsystems.mms.tic.testframework.report.model.MethodContext.newBuilder();

        apply(createContextValues(methodContext), builder::setContextValues);
        map(methodContext.getMethodType(), type -> MethodType.valueOf(type.name()), builder::setMethodType);
        forEach(methodContext.parameters, parameter -> builder.addParameters(parameter.toString()));
        forEach(methodContext.methodTags, annotation -> builder.addMethodTags(annotationToString(annotation)));
        apply(methodContext.retryNumber, builder::setRetryNumber);
        apply(methodContext.methodRunIndex, builder::setMethodRunIndex);

        apply(methodContext.priorityMessage, builder::setPriorityMessage);
        apply(methodContext.threadName, builder::setThreadName);

        // test steps
        methodContext.readTestSteps().forEach(testStep -> builder.addTestSteps(prepareTestStep(testStep)));
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
        if (methodContext.hasErrorContext()) builder.setErrorContext(this.prepareErrorContext(methodContext.getErrorContext()));
        methodContext.readSessionContexts().forEach(sessionContext -> builder.addSessionContextIds(sessionContext.getId()));

        List<CustomContext> customContexts = methodContext.readCustomContexts().collect(Collectors.toList());
        if (customContexts.size()>0) {
            builder.setCustomContextJson(jsonEncoder.toJson(customContexts));
        }

        methodContext.readVideos().forEach(video -> {
            final java.io.File targetVideoFile = new java.io.File(targetVideoDir, video.filename);
            final java.io.File currentVideoFile = new java.io.File(currentVideoDir, video.filename);

            final String videoId = IDUtils.getB64encXID();

            // link file
            builder.addVideoIds(videoId);

            // add video data
            final File.Builder fileBuilderVideo = File.newBuilder();
            fileBuilderVideo.setId(videoId);
            fileBuilderVideo.setRelativePath(targetVideoFile.getPath());
            fileBuilderVideo.setMimetype(MediaType.WEBM_VIDEO.toString());
            fillFileBasicData(fileBuilderVideo, currentVideoFile);
            if (this.fileBuilderConsumer != null) {
                this.fileBuilderConsumer.accept(fileBuilderVideo);
            }
        });

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

    public ScriptSource.Builder prepareScriptSource(eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource scriptSource) {
        ScriptSource.Builder builder = ScriptSource.newBuilder();

        apply(scriptSource.getFileName(), builder::setFileName);
        apply(scriptSource.getMethodName(), builder::setMethodName);
        scriptSource.readLines().forEach(line -> builder.addLines(prepareScriptSourceLine(line)));
        builder.setMark(scriptSource.getMarkedLineNumber());

        return builder;
    }

    public ScriptSourceLine.Builder prepareScriptSourceLine(eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource.Line line) {
        ScriptSourceLine.Builder builder = ScriptSourceLine.newBuilder();

        apply(line.getLine(), builder::setLine);
        apply(line.getLineNumber(), builder::setLineNumber);
//        apply(line.mark, builder::setMark);

        return builder;
    }

    public ErrorContext.Builder prepareErrorContext(eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext errorContext) {
        ErrorContext.Builder builder = ErrorContext.newBuilder();

//        apply(errorContext.getReadableErrorMessage(), builder::setReadableErrorMessage);
//        apply(errorContext.getAdditionalErrorMessage(), builder::setAdditionalErrorMessage);
        traceThrowable(errorContext.getThrowable(), throwable -> {
            builder.addStackTrace(this.prepareStackTraceCause(throwable));
        });
//        map(errorContext.getThrowable(), this::prepareStackTraceCause, builder::addAllCause);
//        apply(errorContext.errorFingerprint, builder::setErrorFingerprint);
        errorContext.getScriptSource().ifPresent(scriptSource -> builder.setScriptSource(this.prepareScriptSource(scriptSource)));
        errorContext.getExecutionObjectSource().ifPresent(scriptSource -> builder.setExecutionObjectSource(this.prepareScriptSource(scriptSource)));
        if (errorContext.getTicketId() != null) builder.setTicketId(errorContext.getTicketId().toString());
        apply(errorContext.getDescription(), builder::setDescription);
        builder.setOptional(errorContext.isOptional());

        return builder;
    }

    public TestStep.Builder prepareTestStep(eu.tsystems.mms.tic.testframework.report.model.steps.TestStep testStep) {
        TestStep.Builder builder = TestStep.newBuilder();

        apply(testStep.getName(), builder::setName);
        forEach(testStep.getTestStepActions(), testStepAction -> builder.addActions(prepareTestStepAction(testStepAction)));

        return builder;
    }

    public ClickPathEvent.Builder prepareClickPathEvent(eu.tsystems.mms.tic.testframework.clickpath.ClickPathEvent clickPathEvent) {
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

    public TestStepAction.Builder prepareTestStepAction(eu.tsystems.mms.tic.testframework.report.model.steps.TestStepAction testStepAction) {
        TestStepAction.Builder actionBuilder = TestStepAction.newBuilder();

        apply(testStepAction.getName(), actionBuilder::setName);
        apply(testStepAction.getTimestamp(), actionBuilder::setTimestamp);

        testStepAction.readEntries().forEach(entry -> {
            TestStepActionEntry.Builder entryBuilder = TestStepActionEntry.newBuilder();
            if (entry instanceof eu.tsystems.mms.tic.testframework.clickpath.ClickPathEvent) {
                Optional<ClickPathEvent.Builder> optional = Optional.ofNullable(this.prepareClickPathEvent((eu.tsystems.mms.tic.testframework.clickpath.ClickPathEvent) entry));
                optional.ifPresent(entryBuilder::setClickPathEvent);
            } else if (entry instanceof Screenshot) {
                Screenshot screenshot = (Screenshot) entry;
                // build screenshot and sources files
                final java.io.File targetScreenshotFile = new java.io.File(targetScreenshotDir, screenshot.filename);
                final java.io.File currentScreenshotFile = new java.io.File(currentScreenshotDir, screenshot.filename);

                //final java.io.File realSourceFile = new java.io.File(Report.SCREENSHOTS_DIRECTORY, screenshot.sourceFilename);
                final java.io.File targetSourceFile = new java.io.File(targetScreenshotDir, screenshot.filename);
                final java.io.File currentSourceFile = new java.io.File(currentScreenshotDir, screenshot.filename);
                final String mappedSourcePath = mapArtifactsPath(targetSourceFile.getAbsolutePath());

                final String screenshotId = IDUtils.getB64encXID();
                final String sourcesRefId = IDUtils.getB64encXID();

                // create ref link
                //builder.addScreenshotIds(screenshotId);

                // add screenshot data
                final File.Builder fileBuilderScreenshot = File.newBuilder();
                fileBuilderScreenshot.setId(screenshotId);
                fileBuilderScreenshot.setRelativePath(targetScreenshotFile.getPath());
                fileBuilderScreenshot.setMimetype(MediaType.PNG.toString());
                fileBuilderScreenshot.putAllMeta(screenshot.meta());
                fileBuilderScreenshot.putMeta("sourcesRefId", sourcesRefId);
                fillFileBasicData(fileBuilderScreenshot, currentScreenshotFile);

                if (this.fileBuilderConsumer != null) {
                    this.fileBuilderConsumer.accept(fileBuilderScreenshot);
                }

                // add sources data
                final File.Builder fileBuilderSources = File.newBuilder();
                fileBuilderSources.setId(sourcesRefId);
                fileBuilderSources.setRelativePath(mappedSourcePath);
                fileBuilderSources.setMimetype(MediaType.PLAIN_TEXT_UTF_8.toString());
                fillFileBasicData(fileBuilderSources, currentSourceFile);

                if (this.fileBuilderConsumer != null) {
                    this.fileBuilderConsumer.accept(fileBuilderSources);
                }
                entryBuilder.setScreenshotId(screenshotId);

            } else if (entry instanceof LogEvent) {
                LogEvent logEvent = (LogEvent)entry;
                Optional<LogMessage.Builder> optional = Optional.ofNullable(prepareLogEvent(logEvent));
                optional.ifPresent(entryBuilder::setLogMessage);
            } else if (entry instanceof eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext) {
                eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext errorContext = (eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext)entry;
                Optional<ErrorContext.Builder> optional = Optional.ofNullable(prepareErrorContext(errorContext));
                optional.ifPresent(entryBuilder::setAssertion);
            }

            actionBuilder.addEntries(entryBuilder);
        });
        return actionBuilder;
    }

    public ContextExporter() {
        for (TestStatusController.Status status : TestStatusController.Status.values()) {
            /*
            Status
             */
            ResultStatusType resultStatusType = ResultStatusType.valueOf(status.name());

            // add to map
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

    protected ContextValues createContextValues(AbstractContext context) {
        ContextValues.Builder builder = ContextValues.newBuilder();

        apply(context.getId(), builder::setId);
        //apply(context.swi, builder::setSwi);
        apply(System.currentTimeMillis(), builder::setCreated);
        apply(context.getName(), builder::setName);
        map(context.getStartTime(), Date::getTime, builder::setStartTime);
        map(context.getEndTime(), Date::getTime, builder::setEndTime);

        if (context instanceof MethodContext) {
            MethodContext methodContext = (MethodContext) context;

            // result status
            map(methodContext.getStatus(), this::getMappedStatus, builder::setResultStatus);

            // exec status
            if (methodContext.getStatus() == TestStatusController.Status.NO_RUN) {
                builder.setExecStatus(ExecStatusType.RUNNING);
            } else {
                builder.setExecStatus(ExecStatusType.FINISHED);
            }
        } else if (context instanceof ExecutionContext) {
            ExecutionContext executionContext = (ExecutionContext) context;
            if (executionContext.crashed) {
                /*
                crashed state
                 */
                builder.setExecStatus(ExecStatusType.CRASHED);
            } else {
                if (TestStatusController.getTestsSkipped() == executionContext.estimatedTestMethodCount) {
                    builder.setResultStatus(ResultStatusType.SKIPPED);
                    builder.setExecStatus(ExecStatusType.VOID);
                } else if (TestStatusController.getTestsFailed() + TestStatusController.getTestsSuccessful() == 0) {
                    builder.setResultStatus(ResultStatusType.NO_RUN);
                    builder.setExecStatus(ExecStatusType.VOID);

                } else {
                    ResultStatusType resultStatusType = STATUS_MAPPING.get(executionContext.getStatus());
                    builder.setResultStatus(resultStatusType);

                    // exec status
                    if (executionContext.getEndTime() != null) {
                        builder.setExecStatus(ExecStatusType.FINISHED);
                    } else {
                        builder.setExecStatus(ExecStatusType.RUNNING);
                    }
                }
            }
        }
        return builder.build();
    }

    public LogMessage.Builder prepareLogEvent(LogEvent logEvent) {
        LogMessage.Builder builder = LogMessage.newBuilder();
        builder.setLoggerName(logEvent.getLoggerName());
        builder.setMessage(logEvent.getMessage().getFormattedMessage());
        if (logEvent.getLevel() == Level.ERROR) {
            builder.setType(LogMessageType.LMT_ERROR);
        } else if (logEvent.getLevel() == Level.WARN) {
            builder.setType(LogMessageType.LMT_WARN);
        } else if (logEvent.getLevel() == Level.INFO) {
            builder.setType(LogMessageType.LMT_INFO);
        } else if (logEvent.getLevel() == Level.DEBUG) {
            builder.setType(LogMessageType.LMT_DEBUG);
        }
        builder.setTimestamp(logEvent.getTimeMillis());
        builder.setThreadName(logEvent.getThreadName());

        traceThrowable(logEvent.getThrown(), throwable -> {
            builder.addStackTrace(this.prepareStackTraceCause(throwable));
        });
        return builder;
    }

    public StackTraceCause.Builder prepareStackTraceCause(Throwable throwable) {
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

    protected void traceThrowable(Throwable throwable, Consumer<Throwable> consumer) {
        while (throwable != null) {
            consumer.accept(throwable);
            throwable = throwable.getCause();
        }
    }

    public ClassContext.Builder prepareClassContext(eu.tsystems.mms.tic.testframework.report.model.context.ClassContext classContext) {
        ClassContext.Builder builder = ClassContext.newBuilder();

        apply(createContextValues(classContext), builder::setContextValues);
        apply(classContext.getTestClass().getName(), builder::setFullClassName);
        apply(classContext.getTestContext().getId(), builder::setTestContextId);

        if (classContext.getTestClassContext() != null) {
            builder.setTestContextName(classContext.getTestClassContext().name());
        }
        return builder;
    }

    public eu.tsystems.mms.tic.testframework.report.model.ExecutionContext.Builder prepareExecutionContext(eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext executionContext) {
        eu.tsystems.mms.tic.testframework.report.model.ExecutionContext.Builder builder = eu.tsystems.mms.tic.testframework.report.model.ExecutionContext.newBuilder();

        apply(createContextValues(executionContext), builder::setContextValues);
//        forEach(executionContext.suiteContexts, suiteContext -> builder.addSuiteContextIds(suiteContext.getId()));
//        forEach(executionContext.mergedClassContexts, classContext -> builder.addMergedClassContextIds(classContext.id));
//        map(executionContext.exitPoints, this::createContextClip, builder::addAllExitPoints);
//        map(executionContext.failureAspects, this::createContextClip, builder::addAllFailureAscpects);
        map(executionContext.runConfig, this::prepareRunConfig, builder::setRunConfig);
        executionContext.readExclusiveSessionContexts().forEach(sessionContext -> builder.addExclusiveSessionContextIds(sessionContext.getId()));
        apply(executionContext.estimatedTestMethodCount, builder::setEstimatedTestsCount);
        executionContext.readMethodContextLessLogs().forEach(logEvent -> {
            Optional<LogMessage.Builder> optional = Optional.ofNullable(prepareLogEvent(logEvent));
            optional.ifPresent(builder::addLogMessages);
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

    public RunConfig.Builder prepareRunConfig(eu.tsystems.mms.tic.testframework.report.model.context.RunConfig runConfig) {
        RunConfig.Builder builder = RunConfig.newBuilder();

        apply(runConfig.getReportName(), builder::setReportName);
        apply(runConfig.RUNCFG, builder::setRuncfg);

        /*
        add build information
         */
        BuildInformation.Builder bi = BuildInformation.newBuilder();
        apply(runConfig.buildInformation.buildJavaVersion, bi::setBuildJavaVersion);
        //apply(runConfig.buildInformation.buildOsArch, bi::setBuildOsName);
        apply(runConfig.buildInformation.buildOsName, bi::setBuildOsName);
        apply(runConfig.buildInformation.buildOsVersion, bi::setBuildOsVersion);
        apply(runConfig.buildInformation.buildTimestamp, bi::setBuildTimestamp);
        apply(runConfig.buildInformation.buildUserName, bi::setBuildUserName);
        apply(runConfig.buildInformation.buildVersion, bi::setBuildVersion);
        builder.setBuildInformation(bi);

        return builder;
    }

    public SessionContext.Builder prepareSessionContext(eu.tsystems.mms.tic.testframework.report.model.context.SessionContext sessionContext) {
        SessionContext.Builder builder = newBuilder();

        apply(createContextValues(sessionContext), builder::setContextValues);
        apply(sessionContext.getSessionKey(), builder::setSessionKey);
        apply(sessionContext.getProvider(), builder::setProvider);
        apply(sessionContext.getSessionId(), builder::setSessionId);

        // translate object map to string map
        Map<String, String> newMap = new LinkedHashMap<>();
        for (String key : sessionContext.getMetaData().keySet()) {
            Object value = sessionContext.getMetaData().get(key);
            if (StringUtils.isStringEmpty(key) || value == null || StringUtils.isStringEmpty(value.toString())) {
                // ignore
            } else {
                newMap.put(key, value.toString());
            }
        }
        builder.putAllMetadata(newMap);

        return builder;
    }

    public SuiteContext.Builder prepareSuiteContext(eu.tsystems.mms.tic.testframework.report.model.context.SuiteContext suiteContext) {
        SuiteContext.Builder builder = SuiteContext.newBuilder();

        apply(createContextValues(suiteContext), builder::setContextValues);
        builder.setExecutionContextId(suiteContext.getExecutionContext().getId());

        return builder;
    }

    public TestContext.Builder prepareTestContext(eu.tsystems.mms.tic.testframework.report.model.context.TestContext testContext) {
        TestContext.Builder builder = TestContext.newBuilder();

        apply(createContextValues(testContext), builder::setContextValues);
        builder.setSuiteContextId(testContext.getSuiteContext().getId());

        return builder;
    }
}

/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.adapters;

import com.google.common.net.MediaType;
import eu.tsystems.mms.tic.testframework.internal.IDUtils;
import eu.tsystems.mms.tic.testframework.report.model.ErrorContext;
import eu.tsystems.mms.tic.testframework.report.model.FailureCorridorValue;
import eu.tsystems.mms.tic.testframework.report.model.File;
import eu.tsystems.mms.tic.testframework.report.model.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.MethodType;
import eu.tsystems.mms.tic.testframework.report.model.PClickPathEvent;
import eu.tsystems.mms.tic.testframework.report.model.PClickPathEventType;
import eu.tsystems.mms.tic.testframework.report.model.PTestStep;
import eu.tsystems.mms.tic.testframework.report.model.PTestStepAction;
import eu.tsystems.mms.tic.testframework.report.model.ScriptSource;
import eu.tsystems.mms.tic.testframework.report.model.ScriptSourceLine;
import eu.tsystems.mms.tic.testframework.report.model.StackTrace;
import eu.tsystems.mms.tic.testframework.report.model.StackTraceCause;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepAction;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

final class MethodContextExporter extends ContextExporter {
    private static Report report = new Report();

    //private static final String workspaceFolder = ExecutionContextController.getCurrentExecutionContext().metaData.get(PlatformProperties.WORKSPACE_FOLDER);

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

    public static class MethodContextData {
        eu.tsystems.mms.tic.testframework.report.model.MethodContext methodContext;
        List<File> files = new LinkedList<>();
    }

    private static String mapArtifactsPath(String absolutePath) {
        String path = absolutePath.replace(report.getFinalReportDirectory().toString(), "");

        // replace all \ with /
        path = path.replaceAll("\\\\", "/");

        // remove leading /
        while (path.startsWith("/")) {
            path = path.substring(1);
        }

        return path;
    }

    public MethodContextData prepareMethodContext(eu.tsystems.mms.tic.testframework.report.model.context.MethodContext methodContext) {
        MethodContext.Builder builder = MethodContext.newBuilder();

        value(createContextValues(methodContext), builder::setContextValues);

        valueMapping(methodContext.methodType, methodType -> MethodType.valueOf(methodType.name()), builder::setMethodType);
        valueList(methodContext.parameters, o -> "" + o, builder::addAllParameter);
        valueList(methodContext.methodTags, MethodContextExporter::annotationToString, builder::addAllMethodTag);
        value(methodContext.retryNumber, builder::setRetryNumber);
        value(methodContext.methodRunIndex, builder::setMethodRunIndex);

        value(methodContext.priorityMessage, builder::addPriorityMessage);
        value(methodContext.threadName, builder::setThreadName);

        // test steps
        valueList(methodContext.steps().getTestSteps(), testStep -> prepareTestStep(testStep).build(), builder::addAllTestSteps);
        //value(methodContext.failedStep, MethodContextExporter::createPTestStep, builder::setFailedStep);

        valueMapping(methodContext.failureCorridorValue, value -> FailureCorridorValue.valueOf(value.name()), builder::setFailureCorridorValue);
        value(methodContext.suiteContext.id, builder::setSuiteContextId);
        value(methodContext.testContextModel.id, builder::setTestContextId);
        value(methodContext.classContext.id, builder::setClassContextId);
        value(methodContext.executionContext.id, builder::setExecutionContextId);

        valueList(methodContext.infos, s -> s, builder::addAllInfo);
        valueList(methodContext.relatedMethodContexts, m -> m.id, builder::addAllRelatedMethodContext);
        valueList(methodContext.dependsOnMethodContexts, m -> m.id, builder::addAllDependsOnMethodContext);

        // build context
        MethodContextData methodContextData = new MethodContextData();
        valueMapping(methodContext, mc -> prepareErrorContext(mc.errorContext()), builder::setErrorContext);
        valueList(methodContext.nonFunctionalInfos, ec -> prepareErrorContext(ec).build(), builder::addAllNonFunctionalInfo);
        valueList(methodContext.collectedAssertions, ec -> prepareErrorContext(ec).build(), builder::addAllCollectedAssertion);
        valueList(methodContext.sessionContexts, sc -> sc.id, builder::addAllSessionContextIds);

        /**
         * The report is already moved to the target directory at this point.
         */
        final Report report = new Report();
        final java.io.File targetVideoDir = report.getFinalReportDirectory(Report.VIDEO_FOLDER_NAME);
        final java.io.File targetScreenshotDir = report.getFinalReportDirectory(Report.SCREENSHOTS_FOLDER_NAME);
        final java.io.File currentVideoDir = report.getFinalReportDirectory(Report.VIDEO_FOLDER_NAME);
        final java.io.File currentScreenshotDir = report.getFinalReportDirectory(Report.SCREENSHOTS_FOLDER_NAME);

        valueList(methodContext.videos, video -> {
            final java.io.File targetVideoFile = new java.io.File(targetVideoDir, video.filename);
            final java.io.File currentVideoFile = new java.io.File(currentVideoDir, video.filename);
            final String mappedPathVideoPath = mapArtifactsPath(targetVideoFile.getAbsolutePath());

            final String videoId = IDUtils.getB64encXID();

            // link file
            builder.addVideoIds(videoId);

            // add video data
            final File.Builder fileBuilderVideo = File.newBuilder();
            fileBuilderVideo.setId(videoId);
            fileBuilderVideo.setRelativePath(mappedPathVideoPath);
            fileBuilderVideo.setMimetype(MediaType.WEBM_VIDEO.toString());
            fillFileBasicData(fileBuilderVideo, currentVideoFile);
            methodContextData.files.add(fileBuilderVideo.build());

            return videoId;
        }, builder::addAllVideoIds);

        valueList(methodContext.screenshots, screenshot -> {
            // build screenshot and sources files
            final java.io.File targetScreenshotFile = new java.io.File(targetScreenshotDir, screenshot.filename);
            final java.io.File currentScreenshotFile = new java.io.File(currentScreenshotDir, screenshot.filename);
            final String mappedScreenshotPath = mapArtifactsPath(targetScreenshotFile.getAbsolutePath());

            //final java.io.File realSourceFile = new java.io.File(Report.SCREENSHOTS_DIRECTORY, screenshot.sourceFilename);
            final java.io.File targetSourceFile = new java.io.File(targetScreenshotDir, screenshot.filename);
            final java.io.File currentSourceFile = new java.io.File(currentScreenshotDir, screenshot.filename);
            final String mappedSourcePath = mapArtifactsPath(targetSourceFile.getAbsolutePath());

            final String screenshotId = IDUtils.getB64encXID();
            final String sourcesRefId = IDUtils.getB64encXID();

            // create ref link
            builder.addScreenshotIds(screenshotId);

            // add screenshot data
            final File.Builder fileBuilderScreenshot = File.newBuilder();
            fileBuilderScreenshot.setId(screenshotId);
            fileBuilderScreenshot.setRelativePath(mappedScreenshotPath);
            fileBuilderScreenshot.setMimetype(MediaType.PNG.toString());
            fileBuilderScreenshot.putAllMeta(screenshot.meta());
            fileBuilderScreenshot.putMeta("sourcesRefId", sourcesRefId);
            fillFileBasicData(fileBuilderScreenshot, currentScreenshotFile);
            methodContextData.files.add(fileBuilderScreenshot.build());

            // add sources data
            final File.Builder fileBuilderSources = File.newBuilder();
            fileBuilderSources.setId(sourcesRefId);
            fileBuilderSources.setRelativePath(mappedSourcePath);
            fileBuilderSources.setMimetype(MediaType.PLAIN_TEXT_UTF_8.toString());
            fillFileBasicData(fileBuilderSources, currentSourceFile);
            methodContextData.files.add(fileBuilderSources.build());
            return screenshotId;

        }, builder::addAllScreenshotIds);

//        if (methodContext.customContexts.size() > 0) {
//            builder.setCustomContextJson(toJson(methodContext.customContexts));
//        }
        methodContextData.methodContext =  builder.build();

        // return
        return methodContextData;
    }

    protected void fillFileBasicData(File.Builder builder, java.io.File file) {
//        ExecutionContext currentExecutionContext = ExecutionContextController.getCurrentExecutionContext();
//        value(currentExecutionContext.metaData.get(PlatformProperties.PROJECT_ID), builder::setProjectId);
//        value(currentExecutionContext.metaData.get(PlatformProperties.JOB_ID), builder::setJobId);

        // timestamps
        long timestamp = System.currentTimeMillis();
        builder.setCreatedTimestamp(timestamp);
        builder.setLastModified(timestamp);

        // file size
        builder.setSize(file.length());
    }

    public StackTrace.Builder prepareStackTrace(eu.tsystems.mms.tic.testframework.report.model.context.StackTrace stackTrace) {
        StackTrace.Builder builder = StackTrace.newBuilder();

        value(stackTrace.additionalErrorMessage, builder::setAdditionalErrorMessage);
        value(stackTrace.stackTrace, this::prepareStackTraceCause, builder::setCause);

        return builder;
    }

    public StackTraceCause.Builder prepareStackTraceCause(eu.tsystems.mms.tic.testframework.report.model.context.StackTrace.Cause cause) {
        StackTraceCause.Builder builder = StackTraceCause.newBuilder();

        value(cause.className, builder::setClassName);
        value(cause.message, builder::setMessage);
        value(cause.stackTraceElements, builder::addAllStackTraceElements);

        value(cause.cause, this::prepareStackTraceCause, builder::setCause);

        return builder;
    }

    public ScriptSource.Builder prepareScriptSource(eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource scriptSource) {
        ScriptSource.Builder builder = ScriptSource.newBuilder();

        value(scriptSource.fileName, builder::setFileName);
        value(scriptSource.methodName, builder::setMethodName);

        valueList(scriptSource.lines, line -> prepareScriptSourceLine(line).build(), builder::addAllLines);

        return builder;
    }

    public ScriptSourceLine.Builder prepareScriptSourceLine(eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource.Line line) {
        ScriptSourceLine.Builder builder = ScriptSourceLine.newBuilder();

        value(line.line, builder::setLine);
        value(line.lineNumber, builder::setLineNumber);
        value(line.mark, builder::setMark);

        return builder;
    }

    public ErrorContext.Builder prepareErrorContext(eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext errorContext) {
        ErrorContext.Builder builder = ErrorContext.newBuilder();

        value(errorContext.getReadableErrorMessage(), builder::setReadableErrorMessage);
        value(errorContext.getAdditionalErrorMessage(), builder::setAdditionalErrorMessage);

        valueMapping(errorContext.stackTrace, this::prepareStackTrace, builder::setStackTrace);
        value(errorContext.errorFingerprint, builder::setErrorFingerprint);

        value(errorContext.scriptSource, this::prepareScriptSource, builder::setScriptSource);
        value(errorContext.executionObjectSource, this::prepareScriptSource, builder::setExecutionObjectSource);

        return builder;
    }

    public PTestStep.Builder prepareTestStep(TestStep testStep) {
        PTestStep.Builder builder = PTestStep.newBuilder();

        value(testStep.getName(), builder::setName);

        valueList(testStep.getTestStepActions(), testStepAction -> prepareTestStepAction(testStepAction).build(), builder::addAllTestStepAction);

        return builder;
    }

    public PTestStepAction.Builder prepareTestStepAction(TestStepAction testStepAction) {
        PTestStepAction.Builder testStepBuilder = PTestStepAction.newBuilder();

        value(testStepAction.getName(), testStepBuilder::setName);
        value(testStepAction.getTimestamp(), testStepBuilder::setTimestamp);

        testStepAction.getTestStepActionEntries().forEach(testStepActionEntry -> {
            PClickPathEvent.Builder clickPathBuilder = PClickPathEvent.newBuilder();
            switch (testStepActionEntry.clickPathEvent.getType()) {
                case WINDOW:
                    clickPathBuilder.setType(PClickPathEventType.WINDOW);
                    break;
                case CLICK:
                    clickPathBuilder.setType(PClickPathEventType.CLICK);
                    break;
                case VALUE:
                    clickPathBuilder.setType(PClickPathEventType.VALUE);
                    break;
                case PAGE:
                    clickPathBuilder.setType(PClickPathEventType.PAGE);
                    break;
                case URL:
                    clickPathBuilder.setType(PClickPathEventType.URL);
                    break;
                default:
                    clickPathBuilder.setType(PClickPathEventType.NOT_SET);
            }
            clickPathBuilder.setSubject(testStepActionEntry.clickPathEvent.getSubject());
            clickPathBuilder.setSessionId(testStepActionEntry.clickPathEvent.getSessionId());
            testStepBuilder.addClickpathEvents(clickPathBuilder.build());

            testStepBuilder.addScreenshotNames(testStepActionEntry.screenshot.filename);
        });
        return testStepBuilder;
    }
}

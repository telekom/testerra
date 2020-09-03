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
import com.google.gson.Gson;
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
import java.util.function.Consumer;

public class MethodContextExporter extends ContextExporter {
    private Report report = new Report();
    private Gson jsonEncoder = new Gson();

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

    public MethodContext.Builder prepareMethodContext(eu.tsystems.mms.tic.testframework.report.model.context.MethodContext methodContext, Consumer<File.Builder> fileConsumer) {
        MethodContext.Builder builder = MethodContext.newBuilder();

        value(createContextValues(methodContext), builder::setContextValues);

        builder.setMethodType(MethodType.valueOf(methodContext.methodType.name()));
        methodContext.parameters.forEach(o -> builder.addParameters(o.toString()));
        methodContext.methodTags.forEach(annotation -> builder.addMethodTags(MethodContextExporter.annotationToString(annotation)));
        value(methodContext.retryNumber, builder::setRetryNumber);
        value(methodContext.methodRunIndex, builder::setMethodRunIndex);

        value(methodContext.priorityMessage, builder::setPriorityMessage);
        value(methodContext.threadName, builder::setThreadName);

        // test steps
        methodContext.steps().getTestSteps().forEach(testStep -> builder.addTestSteps(prepareTestStep(testStep)));
        //value(methodContext.failedStep, MethodContextExporter::createPTestStep, builder::setFailedStep);

        builder.setFailureCorridorValue(FailureCorridorValue.valueOf(methodContext.failureCorridorValue.name()));
        value(methodContext.suiteContext.id, builder::setSuiteContextId);
        value(methodContext.testContextModel.id, builder::setTestContextId);
        value(methodContext.classContext.id, builder::setClassContextId);
        value(methodContext.executionContext.id, builder::setExecutionContextId);

        methodContext.infos.forEach(s -> builder.addInfos(s));
        methodContext.relatedMethodContexts.forEach(m -> builder.addRelatedMethodContextIds(m.id));
        methodContext.dependsOnMethodContexts.forEach(m -> builder.addDependsOnMethodContextIds(m.id));

        // build context
        builder.setErrorContext(prepareErrorContext(methodContext.errorContext()));
        methodContext.nonFunctionalInfos.forEach(assertionInfo -> builder.addNonFunctionalInfos(prepareErrorContext(assertionInfo)));
        methodContext.collectedAssertions.forEach(assertionInfo -> builder.addCollectedAssertions(prepareErrorContext(assertionInfo)));
        methodContext.sessionContexts.forEach(sessionContext -> builder.addScreenshotIds(sessionContext.id));

        /**
         * The report is already moved to the target directory at this point.
         */
        final Report report = new Report();
        final java.io.File targetVideoDir = report.getFinalReportDirectory(Report.VIDEO_FOLDER_NAME);
        final java.io.File targetScreenshotDir = report.getFinalReportDirectory(Report.SCREENSHOTS_FOLDER_NAME);
        final java.io.File currentVideoDir = report.getFinalReportDirectory(Report.VIDEO_FOLDER_NAME);
        final java.io.File currentScreenshotDir = report.getFinalReportDirectory(Report.SCREENSHOTS_FOLDER_NAME);

        methodContext.videos.forEach(video -> {
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
            fileConsumer.accept(fileBuilderVideo);
        });

        methodContext.screenshots.forEach(screenshot -> {
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
//            methodContextData.files.add(fileBuilderScreenshot.build());

            // add sources data
            final File.Builder fileBuilderSources = File.newBuilder();
            fileBuilderSources.setId(sourcesRefId);
            fileBuilderSources.setRelativePath(mappedSourcePath);
            fileBuilderSources.setMimetype(MediaType.PLAIN_TEXT_UTF_8.toString());
            fillFileBasicData(fileBuilderSources, currentSourceFile);
            fileConsumer.accept(fileBuilderSources);
//            methodContextData.files.add(fileBuilderSources.build());

        });


        if (methodContext.customContexts.size() > 0) {
            builder.setCustomContextJson(jsonEncoder.toJson(methodContext.customContexts));
        }

        // return
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

    public StackTrace.Builder prepareStackTrace(eu.tsystems.mms.tic.testframework.report.model.context.StackTrace stackTrace) {
        StackTrace.Builder builder = StackTrace.newBuilder();

        value(stackTrace.additionalErrorMessage, builder::setAdditionalErrorMessage);
        builder.setCause(prepareStackTraceCause(stackTrace.stackTrace));

        return builder;
    }

    public StackTraceCause.Builder prepareStackTraceCause(eu.tsystems.mms.tic.testframework.report.model.context.StackTrace.Cause cause) {
        StackTraceCause.Builder builder = StackTraceCause.newBuilder();

        value(cause.className, builder::setClassName);
        value(cause.message, builder::setMessage);
        value(cause.stackTraceElements, builder::addAllStackTraceElements);
        builder.setCause(prepareStackTraceCause(cause.cause));

        return builder;
    }

    public ScriptSource.Builder prepareScriptSource(eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource scriptSource) {
        ScriptSource.Builder builder = ScriptSource.newBuilder();

        value(scriptSource.fileName, builder::setFileName);
        value(scriptSource.methodName, builder::setMethodName);
        scriptSource.lines.forEach(line -> builder.addLines(prepareScriptSourceLine(line)));

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
        builder.setStackTrace(prepareStackTrace(errorContext.stackTrace));
        value(errorContext.errorFingerprint, builder::setErrorFingerprint);
        builder.setScriptSource(prepareScriptSource(errorContext.scriptSource));
        builder.setExecutionObjectSource(prepareScriptSource(errorContext.executionObjectSource));

        return builder;
    }

    public PTestStep.Builder prepareTestStep(TestStep testStep) {
        PTestStep.Builder builder = PTestStep.newBuilder();

        value(testStep.getName(), builder::setName);
        testStep.getTestStepActions().forEach(testStepAction -> builder.addTestStepActions(prepareTestStepAction(testStepAction)));

        return builder;
    }

    public PTestStepAction.Builder prepareTestStepAction(TestStepAction testStepAction) {
        PTestStepAction.Builder testStepBuilder = PTestStepAction.newBuilder();

        value(testStepAction.getName(), testStepBuilder::setName);
        value(testStepAction.getTimestamp(), testStepBuilder::setTimestamp);

        testStepAction.getTestStepActionEntries().forEach(testStepActionEntry -> {
            if (testStepActionEntry.clickPathEvent != null) {
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
            }
            if (testStepActionEntry.screenshot != null) {
                testStepBuilder.addScreenshotNames(testStepActionEntry.screenshot.filename);
            }
        });
        return testStepBuilder;
    }
}

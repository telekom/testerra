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

public class MethodContextExporter extends AbstractContextExporter {
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

        apply(createContextValues(methodContext), builder::setContextValues);
        map(methodContext.methodType, type -> MethodType.valueOf(type.name()), builder::setMethodType);
        forEach(methodContext.parameters, parameter -> builder.addParameters(parameter.toString()));
        forEach(methodContext.methodTags, annotation -> builder.addMethodTags(MethodContextExporter.annotationToString(annotation)));
        apply(methodContext.retryNumber, builder::setRetryNumber);
        apply(methodContext.methodRunIndex, builder::setMethodRunIndex);

        apply(methodContext.priorityMessage, builder::setPriorityMessage);
        apply(methodContext.threadName, builder::setThreadName);

        // test steps
        forEach(methodContext.steps().getTestSteps(), testStep -> builder.addTestSteps(prepareTestStep(testStep)));
        //value(methodContext.failedStep, MethodContextExporter::createPTestStep, builder::setFailedStep);

        map(methodContext.failureCorridorValue, value -> FailureCorridorValue.valueOf(value.name()), builder::setFailureCorridorValue);
        apply(methodContext.suiteContext.id, builder::setSuiteContextId);
        apply(methodContext.testContextModel.id, builder::setTestContextId);
        apply(methodContext.classContext.id, builder::setClassContextId);
        apply(methodContext.executionContext.id, builder::setExecutionContextId);

        forEach(methodContext.infos, builder::addInfos);
        forEach(methodContext.relatedMethodContexts, m -> builder.addRelatedMethodContextIds(m.id));
        forEach(methodContext.dependsOnMethodContexts, m -> builder.addDependsOnMethodContextIds(m.id));

        // build context
        map(methodContext.errorContext(), this::prepareErrorContext, builder::setErrorContext);
        forEach(methodContext.nonFunctionalInfos, assertionInfo -> builder.addNonFunctionalInfos(prepareErrorContext(assertionInfo)));
        forEach(methodContext.collectedAssertions, assertionInfo -> builder.addCollectedAssertions(prepareErrorContext(assertionInfo)));
        forEach(methodContext.sessionContexts, sessionContext -> builder.addScreenshotIds(sessionContext.id));

        /**
         * The report is already moved to the target directory at this point.
         */
        final Report report = new Report();
        final java.io.File targetVideoDir = report.getFinalReportDirectory(Report.VIDEO_FOLDER_NAME);
        final java.io.File targetScreenshotDir = report.getFinalReportDirectory(Report.SCREENSHOTS_FOLDER_NAME);
        final java.io.File currentVideoDir = report.getFinalReportDirectory(Report.VIDEO_FOLDER_NAME);
        final java.io.File currentScreenshotDir = report.getFinalReportDirectory(Report.SCREENSHOTS_FOLDER_NAME);

        forEach(methodContext.videos, video -> {
            final java.io.File targetVideoFile = new java.io.File(targetVideoDir, video.filename);
            final java.io.File currentVideoFile = new java.io.File(currentVideoDir, video.filename);
            final String mappedPathVideoPath = mapArtifactsPath(targetVideoFile.getPath());

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

        forEach(methodContext.screenshots, screenshot -> {
            // build screenshot and sources files
            final java.io.File targetScreenshotFile = new java.io.File(targetScreenshotDir, screenshot.filename);
            final java.io.File currentScreenshotFile = new java.io.File(currentScreenshotDir, screenshot.filename);
            final String mappedScreenshotPath = mapArtifactsPath(targetScreenshotFile.getAbsolutePath());

            //final java.io.File realSourceFile = new java.io.File(Report.SCREENSHOTS_DIRECTORY, screenshot.sourceFilename);
            final java.io.File targetSourceFile = new java.io.File(targetScreenshotDir, screenshot.filename);
            final java.io.File currentSourceFile = new java.io.File(currentScreenshotDir, screenshot.filename);
            final String mappedSourcePath = mapArtifactsPath(targetSourceFile.getPath());

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
            fileConsumer.accept(fileBuilderScreenshot);
//            methodContextData.files.add(fileBuilderScreenshot.build());

            // add sources data
            final File.Builder fileBuilderSources = File.newBuilder();
            fileBuilderSources.setId(sourcesRefId);
            fileBuilderSources.setRelativePath(mappedSourcePath);
            fileBuilderSources.setMimetype(MediaType.PLAIN_TEXT_UTF_8.toString());
            fillFileBasicData(fileBuilderSources, currentSourceFile);
//            fileConsumer.accept(fileBuilderSources);
//            methodContextData.files.add(fileBuilderSources.build());

        });

        map(methodContext.customContexts, jsonEncoder::toJson, builder::setCustomContextJson);

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

        apply(stackTrace.additionalErrorMessage, builder::setAdditionalErrorMessage);
        map(stackTrace.stackTrace, this::prepareStackTraceCause, builder::setCause);

        return builder;
    }

    public StackTraceCause.Builder prepareStackTraceCause(eu.tsystems.mms.tic.testframework.report.model.context.StackTrace.Cause cause) {
        StackTraceCause.Builder builder = StackTraceCause.newBuilder();

        apply(cause.className, builder::setClassName);
        apply(cause.message, builder::setMessage);
        apply(cause.stackTraceElements, builder::addAllStackTraceElements);
        map(cause.cause, this::prepareStackTraceCause, builder::setCause);

        return builder;
    }

    public ScriptSource.Builder prepareScriptSource(eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource scriptSource) {
        ScriptSource.Builder builder = ScriptSource.newBuilder();

        apply(scriptSource.fileName, builder::setFileName);
        apply(scriptSource.methodName, builder::setMethodName);
        forEach(scriptSource.lines, line -> builder.addLines(prepareScriptSourceLine(line)));

        return builder;
    }

    public ScriptSourceLine.Builder prepareScriptSourceLine(eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource.Line line) {
        ScriptSourceLine.Builder builder = ScriptSourceLine.newBuilder();

        apply(line.line, builder::setLine);
        apply(line.lineNumber, builder::setLineNumber);
        apply(line.mark, builder::setMark);

        return builder;
    }

    public ErrorContext.Builder prepareErrorContext(eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext errorContext) {
        ErrorContext.Builder builder = ErrorContext.newBuilder();

        apply(errorContext.getReadableErrorMessage(), builder::setReadableErrorMessage);
        apply(errorContext.getAdditionalErrorMessage(), builder::setAdditionalErrorMessage);
        map(errorContext.stackTrace, this::prepareStackTrace, builder::setStackTrace);
        apply(errorContext.errorFingerprint, builder::setErrorFingerprint);
        map(errorContext.scriptSource, this::prepareScriptSource, builder::setScriptSource);
        map(errorContext.executionObjectSource, this::prepareScriptSource, builder::setExecutionObjectSource);

        return builder;
    }

    public PTestStep.Builder prepareTestStep(TestStep testStep) {
        PTestStep.Builder builder = PTestStep.newBuilder();

        apply(testStep.getName(), builder::setName);
        forEach(testStep.getTestStepActions(), testStepAction -> builder.addTestStepActions(prepareTestStepAction(testStepAction)));

        return builder;
    }

    public PTestStepAction.Builder prepareTestStepAction(TestStepAction testStepAction) {
        PTestStepAction.Builder testStepBuilder = PTestStepAction.newBuilder();

        apply(testStepAction.getName(), testStepBuilder::setName);
        apply(testStepAction.getTimestamp(), testStepBuilder::setTimestamp);

        forEach(testStepAction.getTestStepActionEntries(), testStepActionEntry -> {
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

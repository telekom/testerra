/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.BuildInformation;
import eu.tsystems.mms.tic.testframework.internal.TimingInfo;
import eu.tsystems.mms.tic.testframework.monitor.JVMMonitor;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.ReportingData;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.velocity.PublicFieldUberspect;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Formats an Html test report.
 *
 * @author mibu, mrgi
 */
public class ReportFormatter {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportFormatter.class);

    /**
     * Static initializer.
     */
    static {
        pInit();
    }

    /**
     * Init.
     */
    private static void pInit() {
        final Properties velocityConfiguration = new Properties();
        velocityConfiguration.setProperty("resource.loader", "class");
        velocityConfiguration.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityConfiguration.setProperty("runtime.log", "target/velocity.log");
        velocityConfiguration.setProperty("runtime.introspector.uberspect", PublicFieldUberspect.class.getName());
        Velocity.init(velocityConfiguration);
    }

    /**
     * Writes a new HTML formatted test log file.
     *
     * @param logFile       The log destination file.
     * @param testClassList A list of the test classes.
     * @param unrelatedLogs List of log messages that could not be mapped to a test.
     * @param template      The template file to use.
     */
    public static void createTestClassesView(final File logFile, final Collection<ClassContext> testClassList,
                                             final String template, final Collection<LogMessage> unrelatedLogs, final ReportInfo.RunInfo runInfo) {

        try {
            pCreateTestClassesView(logFile, testClassList, template, unrelatedLogs, runInfo);
        } catch (IOException e) {
            out(e);
        }
    }

    public static void createDashboardHtml(final ReportingData reportingData, final File logfile,
                                           final String template) {

        try {
            pFormatWithFailuresAndHistory(reportingData, logfile, template);
        } catch (IOException e) {
            out(e);
        }
    }

    public static void createEmailableReportHtml(final ReportingData reportingData, final File logFile, final String template) {

        Template htmlLogTemplate = Velocity.getTemplate(template, "UTF-8");
        htmlLogTemplate.setEncoding("UTF-8");
        VelocityContext context = getVelocityContext();

        context.put("failureCorridorMatched", reportingData.failureCorridorMatched);

        context.put("dashboardInfos", ReportInfo.getDashboardInfo().getInfos());
        context.put("dashboardWarnings", ReportInfo.getDashboardWarning().getInfos());

        try {
            writeHtml(logFile, htmlLogTemplate, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createTimingsHtml(final File logFile, Map<String, Long> minPerActions,
                                         Map<String, Long> avgPerActions, Map<String, Long> maxPerActions,
                                         Map<String, List<TimingInfo>> timingInfosPerAction, String template) {

        try {
            pFormatWithMeasurements(logFile, minPerActions, avgPerActions, maxPerActions, timingInfosPerAction,
                    template);
        } catch (IOException e) {
            out(e);
        }
    }

    public static void createMemoryHtml(File logFile, String template) {

        try {
            pFormatWithMemory(logFile, template);
        } catch (IOException e) {
            out(e);
        }
    }

    /**
     * Writes a new HTML formatted test log file.
     *
     * @param logFile       The log destination file.
     * @param testClassList A list of the test classes.
     * @param template      The template file to use.
     * @param unrelatedLogs List of log messages that could not be mapped to a test.
     */
    private static void pCreateTestClassesView(final File logFile, final Collection<ClassContext> testClassList,
                                               final String template, final Collection<LogMessage> unrelatedLogs, final ReportInfo.RunInfo runInfo)
            throws IOException {

        Template htmlLogTemplate = Velocity.getTemplate(template, "UTF-8");
        htmlLogTemplate.setEncoding("UTF-8");

        VelocityContext context = getVelocityContext();
        context.put("runInfo", runInfo);

        if (testClassList != null) {
            context.put("testClassList", testClassList);
        }
        if (unrelatedLogs != null) {
            context.put("unrelatedLogMessages", unrelatedLogs);
        }

        Writer logWriter = null;

        logWriter = new BufferedWriter(new FileWriter(logFile));
        htmlLogTemplate.merge(context, logWriter);
        logWriter.flush();
        logWriter.close();
    }

    /**
     * Writes a new HTML formatted test log file.
     *
     * @param logFile  .
     * @param metrics  .
     * @param template .
     */
    public static void createMetricsHtml(final File logFile, final Object metrics, final String template) {
        try {
            pFormatWithMetrics(logFile, metrics, template);
        } catch (IOException e) {
            out(e);
        }
    }

    private static void pFormatWithMetrics(final File logFile, final Object metrics, final String template) throws IOException {

        Template htmlLogTemplate;
        htmlLogTemplate = Velocity.getTemplate(template, "UTF-8");
        htmlLogTemplate.setEncoding("UTF-8");

        VelocityContext context = getVelocityContext();

        context.put("metrics", metrics);

        Writer logWriter = null;

        logWriter = new BufferedWriter(new FileWriter(logFile));
        htmlLogTemplate.merge(context, logWriter);
        logWriter.flush();
        logWriter.close();
    }

    /**
     * Writes a new HTML formatted test log file.
     *
     * @param logFile   The log destination file.
     * @param testClass A test class.
     * @param template  The template file to use.
     */
    public static void createMethodsView(
            File logFile,
            ClassContext testClass,
            String template
    ) {
        try {
            pFormatWithTestClass(logFile, testClass, template);
        } catch (Exception e) {
            out(e);
        }
    }

    private static void out(Exception e) {
        LOGGER.error("Could not create html: " + e, e);
    }

    public static void createMethodsFrame(
            File logFile,
            MethodContext methodContext,
            String template
    ) {
        try {
            pFormatWithTestMethod(logFile, methodContext, template);
        } catch (Exception e) {
            out(e);
        }
    }

    public static void createMultiMethodsHtml(
            ReportingData reportingData,
            File logFile,
            String template
    ) {
        try {
            pFormatWithFailuresAndHistory(reportingData, logFile, template);
        } catch (IOException e) {
            out(e);
        }
    }

    /**
     * Writes a new HTML formatted test log file.
     *
     * @param logFile   The log destination file.
     * @param testClass A test class.
     * @param template  The template file to use.
     */
    private static void pFormatWithTestClass(
            File logFile,
            ClassContext testClass,
            String template
    ) throws IOException {

        Template htmlLogTemplate = Velocity.getTemplate(template, "UTF-8");
        htmlLogTemplate.setEncoding("UTF-8");

        VelocityContext context = getVelocityContext();

        if (testClass != null) {
            context.put("testClass", testClass);
        }
        context.put("reportScreenshotsPreview", Flags.REPORT_SCREENSHOTS_PREVIEW);

        writeHtml(logFile, htmlLogTemplate, context);
    }

    /**
     * Writes a new HTML formatted test log file.
     *
     * @param logFile       The log destination file.
     * @param methodContext A test class.
     * @param template      The template file to use.
     */
    private static void pFormatWithTestMethod(
            File logFile,
            MethodContext methodContext,
            String template
    ) throws IOException {
        Template htmlLogTemplate = Velocity.getTemplate(template, "UTF-8");
        htmlLogTemplate.setEncoding("UTF-8");

        VelocityContext context = getVelocityContext();
        context.put("testMethod", methodContext);

        writeHtml(logFile, htmlLogTemplate, context);
    }

    static void createHtml(String vmTemplateFileInResources, File htmlOutputFile, VelocityContext velocityContext) throws IOException {
        VelocityContext baseContext = getVelocityContext();
        Object[] keys = baseContext.getKeys();
        for (Object key : keys) {
            if (key instanceof String) {
                String stringKey = (String) key;
                if (!velocityContext.containsKey(stringKey)) {
                    velocityContext.put(stringKey, baseContext.get(stringKey));
                }
            }
        }

        Template htmlLogTemplate = Velocity.getTemplate(vmTemplateFileInResources, "UTF-8");
        writeHtml(htmlOutputFile, htmlLogTemplate, velocityContext);
    }

    private static void pFormatWithMeasurements(final File logFile, Map<String, Long> minPerActions,
                                                Map<String, Long> avgPerActions, Map<String, Long> maxPerActions,
                                                Map<String, List<TimingInfo>> timingInfosPerAction, String template) throws IOException {

        Template htmlLogTemplate = Velocity.getTemplate(template, "UTF-8");
        htmlLogTemplate.setEncoding("UTF-8");

        VelocityContext context = getVelocityContext();
        context.put("tabId", "Timings");
        context.put("tabName", "Timings");
        context.put("allActions", timingInfosPerAction);
        context.put("avgPerActions", avgPerActions);
        context.put("minPerActions", minPerActions);
        context.put("maxPerActions", maxPerActions);

        writeHtml(logFile, htmlLogTemplate, context);
    }

    private static void pFormatWithMemory(final File logFile, String template) throws IOException {

        Template htmlLogTemplate = Velocity.getTemplate(template, "UTF-8");
        htmlLogTemplate.setEncoding("UTF-8");

        VelocityContext context = getVelocityContext();
        context.put("memory", JVMMonitor.getMeasurements());
        context.put("memoryTotal", JVMMonitor.getMeasurementsTotal());
        context.put("labels", JVMMonitor.getLabels());
        context.put("memoryMax", JVMMonitor.getMaxMemory());

        writeHtml(logFile, htmlLogTemplate, context);
    }

    private static void pFormatWithFailuresAndHistory(final ReportingData reportingData, final File logFile,
                                                      final String template) throws IOException {

        Template htmlLogTemplate = Velocity.getTemplate(template, "UTF-8");
        htmlLogTemplate.setEncoding("UTF-8");
        VelocityContext context = getVelocityContext();

        context.put("classContexts", reportingData.classContexts);
        context.put("dashboardInfos", ReportInfo.getDashboardInfo().getInfos());
        context.put("dashboardWarnings", ReportInfo.getDashboardWarning().getInfos());

        writeHtml(logFile, htmlLogTemplate, context);
    }

    private static VelocityContext getVelocityContext() {
        VelocityContext context = new VelocityContext();
        context.put("executionContext", ExecutionContextController.getCurrentExecutionContext());
        context.put("TesterraBuildInformation", BuildInformation.getInstance());
        context.put("reportScreenshotsPreview", Flags.REPORT_SCREENSHOTS_PREVIEW);
        context.put("reportName", ReportUtils.getReportName());

        context.put("dryrun", Flags.DRY_RUN);
        context.put("dateFormatter", new DateTool());

        context.put("filter", FilterUtils.getInstance());
        context.put("fcActive", Flags.FAILURE_CORRIDOR_ACTIVE);
        context.put("fcMatched", FailureCorridor.isCorridorMatched());
        context.put("fcH", TestStatusController.getTestsFailedHIGH());
        context.put("fcM", TestStatusController.getTestsFailedMID());
        context.put("fcL", TestStatusController.getTestsFailedLOW());
        context.put("fcHAllowed", FailureCorridor.getAllowedTestFailuresHIGH());
        context.put("fcMAllowed", FailureCorridor.getAllowedTestFailuresMID());
        context.put("fcLAllowed", FailureCorridor.getAllowedTestFailuresLOW());
        context.put("fcExpectedFailed", TestStatusController.getTestsExpectedFailed());
        context.put("fcFailedRetried", TestStatusController.getTestsFailedRetried());
        context.put("fcFailed", TestStatusController.getTestsFailed());
        context.put("fcAllFailed", TestStatusController.getAllFailed());

        context.put("topLevelTabs", ReportUtils.TOP_LEVEL_TABS);

        /*
        add all statuses
         */
        TestStatusController.Status[] values = TestStatusController.Status.values();
        for (TestStatusController.Status value : values) {
            context.put("status_" + value.name(), value);
        }

        return context;
    }

    private static void writeHtml(File htmlOutputFile, Template htmlLogTemplate, VelocityContext context) throws IOException {
        Writer logWriter;
        logWriter = new BufferedWriter(new FileWriter(htmlOutputFile));
        htmlLogTemplate.merge(context, logWriter);
        logWriter.flush();
        logWriter.close();
    }
}

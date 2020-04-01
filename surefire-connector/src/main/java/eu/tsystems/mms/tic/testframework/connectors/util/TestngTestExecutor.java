/*
 * Created on 11.11.2011
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.connectors.util;

import eu.tsystems.mms.tic.testframework.utils.MapUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.apache.maven.surefire.report.ReporterException;
import org.apache.maven.surefire.report.ReporterFactory;
import org.apache.maven.surefire.report.RunListener;
import org.apache.maven.surefire.report.SimpleReportEntry;
import org.apache.maven.surefire.testng.TestNGReporter;
import org.apache.maven.surefire.testng.conf.Configurator;
import org.apache.maven.surefire.testng.conf.TestNGMapConfigurator;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestNG;
import org.testng.TestNGException;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author mrgi Encapsulates the access to TestNG.
 */
public final class TestngTestExecutor {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestngTestExecutor.class);

    /**
     * Private constructor to hide the public one since this a static only class. Creates a new
     * <code>TestExecutionWrapper</code> object.
     */
    private TestngTestExecutor() {

    }

    /**
     * Executes the given tests using TestNG.
     *
     * @param confOptions            Options to configure TestNG
     * @param suiteFiles             The xml suite files including the tests.
     * @param reporterManagerFactory ReporterFactory which generates Reporter
     * @param syncToQC               Enables/disables the synchronization to qc.
     *
     * @throws TestSetFailedException If an exception is thrown while test execution.
     */
    public static void execute(final Properties confOptions, final List<?> suiteFiles,
                               final ReporterFactory reporterManagerFactory, final boolean syncToQC) throws TestSetFailedException {
        Properties conf = confOptions;
        final RunListener reporterManager = reporterManagerFactory.createReporter();

        final StringBuilder filenames = new StringBuilder();
        File file;
        for (int i = 0; i < suiteFiles.size(); i++) {
            file = (File) suiteFiles.get(i);
            filenames.append(file.getName() + " ");
        }
        conf.put("suitename", filenames.toString());
        startTestLogging(reporterManager, conf);
        final TestNG testNG = new TestNG(true);

        final Configurator configurator = new TestNGMapConfigurator();
        if (!syncToQC) {
            conf = removeQCSynchronizerFromListeners(conf);
        }
        configurator.configure(testNG, MapUtils.propertiesToMap(conf));

        testNG.setOutputDirectory(conf.getProperty("reportsDirectory"));
        testNG.setVerbose(0);
        final TestNGReporter reporter = new TestNGReporter(reporterManager);
        testNG.addListener((Object) reporter);

        try {
            testNG.setTestSuites(createSuiteFilePaths(suiteFiles));
            testNG.run();
        } catch (final TestSetFailedException e) {
            LOGGER.error(e.getMessage().trim());
        } catch (final TestNGException e) {
            LOGGER.error(e.getMessage().trim());
        }
        finishTestLogging(reporterManager);
    }

    /**
     * Executes the given tests using TestNG.
     *
     * @param confOptions            Options to configure TestNG
     * @param testsToRun             The tests to be executed.
     * @param reporterManagerFactory ReporterFactory which generates Reporter
     * @param syncToQC               Enables/disables the synchronization to qc.
     *
     * @throws TestSetFailedException If an exception is thrown while test execution.
     */
    public static void execute(final Properties confOptions, final TestsToRun testsToRun,
                               final ReporterFactory reporterManagerFactory, final boolean syncToQC) throws TestSetFailedException {

        final RunListener reporterManager = reporterManagerFactory.createReporter();

        final XmlSuiteWriter xsWriter = new XmlSuiteWriter(testsToRun.getMethodMap(),
                confOptions.getProperty("parallel"));
        xsWriter.createXmlSuite();
        final XmlSuite suite = xsWriter.getXmlSuite();

        confOptions.put("suitename", suite.getName());

        startTestLogging(reporterManager, confOptions);
        runTest(reporterManager, confOptions, suite, syncToQC);
        finishTestLogging(reporterManager);
    }

    /**
     * Run the Test
     *
     * @param reporterManager A listener to report.
     * @param confOptions     configuration options given by surefire
     * @param suite           The xml suite to run.
     * @param syncToQC        Enables/disables the qc synchronization.
     *
     * @throws TestSetFailedException
     * @throws TestSetFailedException thrown by TestNG.
     */
    private static void runTest(final RunListener reporterManager,
                                final Properties confOptions, final XmlSuite suite, final boolean syncToQC)
            throws TestSetFailedException {
        Properties conf = confOptions;
        final TestNG testNG = new TestNG(true);
        final Configurator configurator = new TestNGMapConfigurator();
        if (!syncToQC) {
            conf = removeQCSynchronizerFromListeners(conf);
        }
        configurator.configure(testNG, MapUtils.propertiesToMap(conf));

        testNG.setOutputDirectory(conf.getProperty("reportsDirectory"));
        testNG.setVerbose(0);
        final TestNGReporter reporter = new TestNGReporter(reporterManager);
        testNG.addListener((Object) reporter);
        final List<XmlSuite> suites = new ArrayList<XmlSuite>();
        suites.add(suite);
        testNG.setXmlSuites(suites);
        testNG.run();
    }

    /**
     * Starts the logging of the testrun.
     *
     * @param reporter A listener for reporting.
     * @param options  Properties to get testclass name.
     */
    private static void startTestLogging(final RunListener reporter, final Properties options) {
        try {
            reporter.testSetStarting(new SimpleReportEntry("", options.get("suitename").toString()));
        } catch (final ReporterException e) {
            LOGGER.error("Couldn't start reporting.");
        }
    }

    /**
     * Finish the logging of the testrun.
     *
     * @param reporter A listener for reporting.
     */
    private static void finishTestLogging(final RunListener reporter) {
        try {
            reporter.testSetCompleted(new SimpleReportEntry("", "Test finished"));
        } catch (final ReporterException e) {
            LOGGER.error("Couldn't end reporting.");
        }
    }

    /**
     * Creates an ArrayList with paths to the given suiteFiles.
     *
     * @param suiteFiles The XML suiteFiles.
     *
     * @return An ArrayList with the suite file paths.
     *
     * @throws TestSetFailedException thrown if no suite file was found or suite file was not valid.
     */
    private static List<String> createSuiteFilePaths(final List<?> suiteFiles) throws TestSetFailedException {
        final List<String> suiteFilePaths = new ArrayList<String>();

        for (final Iterator<?> i = suiteFiles.iterator(); i.hasNext(); ) {
            final File file = (File) i.next();
            if (!file.exists() || !file.isFile()) {
                throw new TestSetFailedException("Suite file is not a valid file or was not found in path:"
                        + file.getAbsolutePath());
            }
            suiteFilePaths.add(file.getAbsolutePath());
        }
        return suiteFilePaths;
    }

    /**
     * Remove QualityCenterResultSynchronizer from listener.
     *
     * @param confOptions The properties to change.
     *
     * @return new properties without the listener.
     */
    private static Properties removeQCSynchronizerFromListeners(final Properties confOptions) {
        final String listenerStr = confOptions.getProperty("listener");

        if (!StringUtils.isStringEmpty(listenerStr)) {
            final String[] listeners = listenerStr.split(",");
            final StringBuilder builder = new StringBuilder();
            for (final String listener : listeners) {
                if (!listener.contains("QualityCenterTestResultSynchronizer")) {
                    builder.append(listener);
                    builder.append(",");
                }
            }
            confOptions.setProperty("listener", builder.toString());
        }

        return confOptions;
    }
}

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
package eu.tsystems.mms.tic.testframework.connectors.util;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.MapUtils;
import java.util.LinkedList;
import java.util.Properties;
import org.apache.maven.surefire.providerapi.AbstractProvider;
import org.apache.maven.surefire.providerapi.ProviderParameters;
import org.apache.maven.surefire.report.ReporterFactory;
import org.apache.maven.surefire.suite.RunResult;
import org.apache.maven.surefire.testset.TestRequest;
import org.apache.maven.surefire.testset.TestSetFailedException;

/**
 * Abstract class encapsulating common tasks for surefire providers of testerra.
 *
 * @author sepr
 */
public abstract class AbstractCommonProvider extends AbstractProvider implements Loggable {

    static {
        TesterraCommons.init();
    }

    /**
     * The class loader used to find the test classes.
     */
    protected ClassLoader testClassLoader;

    /**
     * Parameters provided by surefire.
     */
    protected ProviderParameters providerParameters;

    /**
     * The type mapping to Quality Center.
     */
    protected SyncType syncType;

    /**
     * Enables or disables the synchronization to QC.
     */
    protected boolean syncToProvider;

    /**
     * Properties provided by surefire
     */
    protected Properties providerProperties;

    /**
     * The TestRequest provided by surefire.
     */
    protected TestRequest testRequest;

    /**
     * File name to be used for provider properties.
     */
    protected String propertiesFile;

    /**
     * String literal 'test', cause it appears very often in this class.
     */
    private static final String TEST = "test";

    /**
     * String literal 'test_', cause it appears very often in this class.
     */
    private static final String TEST2 = "test_";

    /**
     * Public constructor. Creates a new <code>QualityCenterSurefireProvider</code> object.
     *
     * @param parameters The parameters given by surefire.
     */
    public AbstractCommonProvider(final ProviderParameters parameters) {
        this.providerParameters = parameters;
        this.testClassLoader = parameters.getTestClassLoader();
        this.providerProperties = MapUtils.mapToProperties(parameters.getProviderProperties());
        this.testRequest = parameters.getTestRequest();
    }

    /**
     * Don't know why this here is needed. For Tests?
     */
    public AbstractCommonProvider() {
    }

    /**
     * Calculates the tests to run, runs the tests and returns the result.
     *
     * @param forkTestSet Ignored. Calculation of tests to run is performed separately.
     *
     * @return The result from test run.
     *
     * @throws TestSetFailedException thrown if Testset could not be executed.
     */
    @Override
    public RunResult invoke(final Object forkTestSet) throws TestSetFailedException {
        return invokeInternal(forkTestSet);
    }

    /**
     * Impl of invoke method.
     *
     * @param forkTestSet Ignored. Calculation of tests to run is performed separately.
     *
     * @return The result from test run.
     *
     * @throws TestSetFailedException thrown if Testset could not be executed.
     */
    private RunResult invokeInternal(final Object forkTestSet) throws TestSetFailedException {

        final ReporterFactory reporterFactory = providerParameters.getReporterFactory();
        providerProperties.put("reportsDirectory", providerParameters.getReporterConfiguration().getReportsDirectory().getAbsolutePath());

        if (syncType == SyncType.ANNOTATION) {
            if (SyncUtils.isTestNGXmlSuite(testRequest)) {
                TestngTestExecutor.execute(providerProperties, testRequest.getSuiteXmlFiles(), reporterFactory, syncToProvider);
            } else {
                log().error("No Suite XML was set! You must set a suite.xml in the pom " + "or use the Standard Surefire Junit/TestNG Provider! ");
            }
        }

        return reporterFactory.close();
    }

    /**
     * Simply returns an empty list because tests are calculated when invoking the method <code>invoke</code>.
     *
     * @return Empty list, as mentioned earlier.
     */
    @Override
    public Iterable<Class<?>> getSuites() {
        return new LinkedList<>();
    }


    /**
     * Implementing providers must tell the path to a config file, containing property TestsToRunFile.
     *
     * @return Resource name of property file.
     */
    public abstract String getPropertiesFile();

    public void setSyncType(final SyncType syncType) {
        this.syncType = syncType;
    }
}

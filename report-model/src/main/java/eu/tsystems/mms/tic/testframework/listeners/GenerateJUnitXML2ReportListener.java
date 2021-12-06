/*
 * Testerra
 *
 * (C) 2021,  Martin Großmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.listeners;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.ExecutionFinishEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.events.TestStatusUpdateEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.collections.Maps;
import org.testng.collections.Sets;
import org.testng.internal.Utils;
import org.testng.reporters.XMLConstants;
import org.testng.reporters.XMLStringBuffer;
import org.testng.util.TimeUtils;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.regex.Pattern;

/**
 * Generates an JUnit XML report.
 * <p>
 * This file is taken from https://github.com/cbeust/testng/blob/master/testng-core/src/main/java/org/testng/reporters/JUnitXMLReporter.java
 * and customized to Testerra listener
 *
 * @author <a href='mailto:the[dot]mindstorm[at]gmail[dot]com'>Alex Popescu</a>
 * @author mgn
 */
public class GenerateJUnitXML2ReportListener implements
        Loggable,
        ExecutionFinishEvent.Listener,
        MethodEndEvent.Listener,
        TestStatusUpdateEvent.Listener {

    private static final Pattern ENTITY = Pattern.compile("&[a-zA-Z]+;.*");
    private static final Pattern LESS = Pattern.compile("<");
    private static final Pattern GREATER = Pattern.compile(">");
    private static final Pattern SINGLE_QUOTE = Pattern.compile("'");
    private static final Pattern QUOTE = Pattern.compile("\"");
    private static final Map<String, Pattern> ATTR_ESCAPES = Maps.newHashMap();

    private static final String XML_RESULT_FILENAME = "TEST-Result.xml";

    private ITestContext testngTestContext = null;

    private Report report = TesterraListener.getReport();

    static {
        ATTR_ESCAPES.put("&lt;", LESS);
        ATTR_ESCAPES.put("&gt;", GREATER);
        ATTR_ESCAPES.put("&apos;", SINGLE_QUOTE);
        ATTR_ESCAPES.put("&quot;", QUOTE);
    }

    private int m_numFailed = 0;
    private Queue<ITestResult> m_allTests = new ConcurrentLinkedDeque<>();
    private Queue<ITestResult> m_configIssues = new ConcurrentLinkedDeque<>();

    @Subscribe
    @Override
    public void onMethodEnd(MethodEndEvent event) {
        if (event.getMethodContext().isConfigMethod()) {
            ITestResult iTestResult = event.getMethodContext().getTestNgResult().get();
            m_configIssues.add(iTestResult);
        }

        // TestNG ITestContext contains information about whole test run. Why its part of MethodContext?
        if (this.testngTestContext == null) {
            this.testngTestContext = event.getTestContext();
        }
    }

    @Subscribe
    @Override
    public void onTestStatusUpdate(TestStatusUpdateEvent event) {

        Optional<Method> methodFromEvent = this.getMethodFromEvent(event);
        if (methodFromEvent.isPresent()) {

            Status status = event.getMethodContext().getStatus();
            ITestResult iTestResult = event.getMethodContext().getTestNgResult().get();
            Method method = methodFromEvent.get();

            switch (status) {
                case PASSED:
                case REPAIRED:
                case RECOVERED:
                    m_allTests.add(iTestResult);
                    break;
                case FAILED:
                case FAILED_EXPECTED:
                    m_allTests.add(iTestResult);
                    m_numFailed++;
                    break;
                case NO_RUN:
                case RETRIED:
                    // do nothing
                    break;
                case SKIPPED:
                    m_allTests.add(iTestResult);
                    break;
                default:
                    log().debug(String.format("Method state %s of %s cannot handle.", status, method.getName()));
            }
        }
    }

    @Subscribe
    @Override
    public void onExecutionFinish(ExecutionFinishEvent event) {
        generateReport(testngTestContext);
        resetAll();
    }

    private Optional<Method> getMethodFromEvent(TestStatusUpdateEvent event) {
        return event.getMethodContext().getTestNgResult()
                .map(iTestResult -> iTestResult.getMethod().getConstructorOrMethod().getMethod());
    }

    /**
     * generate the XML report given what we know from all the test results
     *
     * @param context The test context
     */
    protected void generateReport(ITestContext context) {

        XMLStringBuffer document = new XMLStringBuffer();
        document.addComment("Generated by " + getClass().getName());

        Properties attrs = new Properties();
        attrs.setProperty(XMLConstants.ATTR_ERRORS, "0");
        attrs.setProperty(XMLConstants.ATTR_FAILURES, Integer.toString(m_numFailed));
        attrs.setProperty(
                XMLConstants.ATTR_IGNORED, Integer.toString(context.getExcludedMethods().size()));
        try {
            attrs.setProperty(XMLConstants.ATTR_HOSTNAME, InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            // ignore
        }
        Set<String> packages = getPackages(context);
        if (packages.size() > 0) {
            attrs.setProperty(XMLConstants.ATTR_NAME, context.getCurrentXmlTest().getName());
            //        attrs.setProperty(XMLConstants.ATTR_PACKAGE, packages.iterator().next());
        }

        attrs.setProperty(XMLConstants.ATTR_TESTS, Integer.toString(m_allTests.size()));
        attrs.setProperty(
                XMLConstants.ATTR_TIME,
                Double.toString(
                        (context.getEndDate().getTime() - context.getStartDate().getTime()) / 1000.0));

        attrs.setProperty(XMLConstants.ATTR_TIMESTAMP, formattedTime());

        document.push(XMLConstants.TESTSUITE, attrs);

        createElementFromTestResults(document, m_configIssues);
        createElementFromTestResults(document, m_allTests);
        createElementFromIgnoredTests(document, context);

        document.pop();

        Utils.writeUtf8File(report.getReportDirectory(Report.XML_FOLDER_NAME).getAbsolutePath(), XML_RESULT_FILENAME, document.toXML());
    }

    static String formattedTime() {
        return TimeUtils.formatTimeInLocalOrSpecifiedTimeZone(
                System.currentTimeMillis(), "yyyy-MM-dd'T'HH:mm:ss z");
    }

    private synchronized void createElementFromTestResults(
            XMLStringBuffer document, Collection<ITestResult> results) {
        for (ITestResult tr : results) {
            createElement(document, tr);
        }
    }

    private synchronized void createElementFromIgnoredTests(
            XMLStringBuffer doc, ITestContext context) {
        Collection<ITestNGMethod> methods = context.getExcludedMethods();
        for (ITestNGMethod method : methods) {
            Properties properties = getPropertiesFor(method, 0);
            doc.push(XMLConstants.TESTCASE, properties);
            doc.addEmptyElement(XMLConstants.ATTR_IGNORED);
            doc.pop();
        }
    }

    private Properties getPropertiesFor(ITestNGMethod method, long elapsedTimeMillis) {
        Properties attrs = new Properties();
        String name = Utils.detailedMethodName(method, false);
        attrs.setProperty(XMLConstants.ATTR_NAME, name);
        attrs.setProperty(XMLConstants.ATTR_CLASSNAME, method.getRealClass().getName());
        attrs.setProperty(XMLConstants.ATTR_TIME, Double.toString(((double) elapsedTimeMillis) / 1000));
        return attrs;
    }

    private Set<String> getPackages(ITestContext context) {
        Set<String> result = Sets.newHashSet();
        for (ITestNGMethod m : context.getAllTestMethods()) {
            Package pkg = m.getRealClass().getPackage();
            if (pkg != null) {
                result.add(pkg.getName());
            }
        }
        return result;
    }

    private void createElement(XMLStringBuffer doc, ITestResult tr) {

        long elapsedTimeMillis = tr.getEndMillis() - tr.getStartMillis();
        Properties attrs = getPropertiesFor(tr.getMethod(), elapsedTimeMillis);
        if (tr.getMethod().isTest()) {
            attrs.setProperty(XMLConstants.ATTR_NAME, tr.getName());
        }

        if ((ITestResult.FAILURE == tr.getStatus()) || (ITestResult.SKIP == tr.getStatus())) {
            doc.push(XMLConstants.TESTCASE, attrs);

            if (ITestResult.FAILURE == tr.getStatus()) {
                createFailureElement(doc, tr);
            } else if (ITestResult.SKIP == tr.getStatus()) {
                createSkipElement(doc);
            }

            doc.pop();
        } else {
            doc.addEmptyElement(XMLConstants.TESTCASE, attrs);
        }
    }

    private void createFailureElement(XMLStringBuffer doc, ITestResult tr) {
        Properties attrs = new Properties();
        Throwable t = tr.getThrowable();
        if (t != null) {
            attrs.setProperty(XMLConstants.ATTR_TYPE, t.getClass().getName());
            String message = t.getMessage();
            if ((message != null) && (message.length() > 0)) {
                attrs.setProperty(XMLConstants.ATTR_MESSAGE, encodeAttr(message)); // ENCODE
            }
            doc.push(XMLConstants.FAILURE, attrs);
            doc.addCDATA(Utils.shortStackTrace(t, false));
            doc.pop();
        } else {
            doc.addEmptyElement(XMLConstants.FAILURE); // THIS IS AN ERROR
        }
    }

    private void createSkipElement(XMLStringBuffer doc) {
        doc.addEmptyElement(XMLConstants.SKIPPED);
    }

    private String encodeAttr(String attr) {
        String result = replaceAmpersand(attr, ENTITY);
        for (Map.Entry<String, Pattern> e : ATTR_ESCAPES.entrySet()) {
            result = e.getValue().matcher(result).replaceAll(e.getKey());
        }

        return result;
    }

    private String replaceAmpersand(String str, Pattern pattern) {
        int start = 0;
        int idx = str.indexOf('&', start);
        if (idx == -1) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        while (idx != -1) {
            result.append(str.substring(start, idx));
            if (pattern.matcher(str.substring(idx)).matches()) {
                // do nothing it is an entity;
                result.append("&");
            } else {
                result.append("&amp;");
            }
            start = idx + 1;
            idx = str.indexOf('&', start);
        }
        result.append(str.substring(start));

        return result.toString();
    }

    /**
     * Reset all member variables for next test.
     */
    private void resetAll() {
        m_allTests = new ConcurrentLinkedDeque<>();
        m_configIssues = new ConcurrentLinkedDeque<>();
        m_numFailed = 0;
    }

}

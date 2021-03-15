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

import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A helper class, creates a virtual xml suite file with specific project information.
 *
 * @author mrgi, sepr
 */
public class XmlSuiteWriter {

    /**
     * A Map containing the test set methods.
     */
    private final Map<String, List<Method>> testSetMethods;

    /**
     * The Suite to create.
     */
    private final XmlSuite suite;

    /**
     * The parallel attribute to set
     */
    private final String parallel;

    /**
     * Public constructor. Creates a new <code>XmlSuiteWriter</code> object.
     *
     * @param testSetMethods A map containing the methods to be executed.
     * @param parallel       The parallel attribute.
     */
    public XmlSuiteWriter(final Map<String, List<Method>> testSetMethods, final String parallel) {
        this.testSetMethods = testSetMethods;
        this.parallel = parallel;
        this.suite = new XmlSuite();

    }

    /**
     * Creates a virtual testNG xml suite containing the test classes and methods which should run.
     */
    public void createXmlSuite() {
        createXmlSuiteIntern();
    }

    /**
     * Creates a virtual testNG xml suite containing the test classes and methods which should run.
     */
    private void createXmlSuiteIntern() {
        suite.setName("TesterraSuite");
        for (final Map.Entry<String, List<Method>> entry : testSetMethods.entrySet()) {
            final List<Class<?>> classesToRun = new ArrayList<Class<?>>();
            final List<XmlClass> allClasses = new ArrayList<XmlClass>();
            final XmlTest test = new XmlTest(suite);
            test.setName(entry.getKey());

            //            test.setParallel(parallel);
            test.setParallel(XmlSuite.ParallelMode.getValidParallel(parallel));

            final List<Method> testMethods = entry.getValue();
            for (final Method testMethod : testMethods) {
                if (!(classesToRun.contains(testMethod.getDeclaringClass()))) {
                    classesToRun.add(testMethod.getDeclaringClass());
                }
            }
            for (final Class<?> testClass : classesToRun) {
                final XmlClass xmlClass = new XmlClass();
                final List<XmlInclude> thisClassMethods = new ArrayList<XmlInclude>();
                xmlClass.setClass(testClass);
                xmlClass.setName(testClass.getName());
                for (final Method method : testMethods) {
                    if (xmlClass.getName().equals(method.getDeclaringClass().getName())) {
                        thisClassMethods.add(new XmlInclude(method.getName()));
                    }
                }
                xmlClass.setIncludedMethods(thisClassMethods);
                allClasses.add(xmlClass);
            }
            test.setClasses(allClasses);
        }
    }

    /**
     * .
     *
     * @return .
     */
    public XmlSuite getXmlSuite() {
        return suite;
    }
}

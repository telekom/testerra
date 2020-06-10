/*
 * Testerra
 *
 * (C) 2020,  Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.execution.testng.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IMethodInstance;
import org.testng.ITestContext;

import java.util.List;

/**
 * Abstract implementation for Method-Filter running in {@link eu.tsystems.mms.tic.testframework.report.TesterraListener} with a default set of class members.
 * <p>
 * Date: 01.04.2020
 * Time: 13:18
 *
 * @author Eric Kubenka
 */
public abstract class TestMethodInterceptWorker implements Worker {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestMethodInterceptWorker.class);
    protected List<IMethodInstance> iMethodInstanceList;
    protected ITestContext iTestContext;

    /**
     * Runs the filter and returns a filtered {@link List} of {@link IMethodInstance}
     */
    public abstract List<IMethodInstance> run();

    /**
     * Initilialze a TestMethodFilterWorker with its defined fields.
     *
     * @param list         {@link List} of {@link IMethodInstance}
     * @param iTestContext {@link ITestContext}
     */
    public void init(final List<IMethodInstance> list, final ITestContext iTestContext) {
        this.iMethodInstanceList = list;
        this.iTestContext = iTestContext;
    }
}

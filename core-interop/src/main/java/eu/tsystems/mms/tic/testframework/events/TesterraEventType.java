/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.events;

/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 14.10.13
 * Time: 23:10
 * To change this template use File | Settings | File Templates.
 */
public enum TesterraEventType implements ITesterraEventType {

    /**
     * Called on start of every test method annotated by TestNG @Test annotation, but before the execution of registered Before Method Worker and after all TestNG configuration methods like @BeforeMethod.
     */
    TEST_METHOD_START,
    /**
     * Called at the end of every test method annotated by TestNG @Test annotation, but before the execution of registered After Method Worker and the execution fo all TestNG configuration methods like @AfterMethod.
     */
    TEST_METHOD_END,

    CONFIGURATION_METHOD_START,
    CONFIGURATION_METHOD_END,

    /**
     * Called on start of every test method annotated by TestNG @Test annotation and every test configuration method annotated by TestNG @BeforeMethod or similar, but before the execution of registered Before Method Worker
     */
    TEST_START,
    /**
     * Called at the end of test run to trigger report generation and other output worker.
     */
    TEST_RUN_END,
    /**
     * Called at the abortion of test run by unclear circumstances.
     */
    TEST_RUN_ABORT,

    @Deprecated
    RETRYING_METHOD,

    FIRST_FAILED_TEST,
    TEST_WITH_FILTERED_THROWABLE,

    SYNC_METHOD_RESULT,

    @Deprecated
    TAKE_SCREENSHOTS,

    CONTEXT_UPDATE,

    GENERATE_REPORT,
    GENERATE_METHOD_REPORT

}

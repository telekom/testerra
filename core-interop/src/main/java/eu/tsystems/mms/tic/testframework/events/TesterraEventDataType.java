/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.events;

import eu.tsystems.mms.tic.testframework.report.model.context.SynchronizableContext;

/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 14.10.13
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */
public enum TesterraEventDataType implements ITesterraEventDataType {

    TIMESTAMP(Long.class),
    DURATION(Long.class),
    TEST_START_TIMESTAMP(Long.class),
    VERSION(String.class),
    EXECUTING_SELENIUM_HOST(String.class),
    METHOD_NAME(String.class),
    CONFIGURATION_METHOD_ANNOTATION(String.class),
    BROWSER(String.class),
    PARAMETER(String.class),

    /**
     * Test result status. Int value from ITestResult.
     */
    TESTRESULT_STATUS(Integer.class),

    // and some more ??
    TEST_METHODS_SUCCESSFUL(Long.class),
    TEST_METHODS_ERROR(Long.class),
    TEST_METHODS_SKIPPED(Long.class),

    IInvokedMethod(org.testng.IInvokedMethod.class),
    ITestResult(org.testng.ITestResult.class),

    CONTEXT(SynchronizableContext.class),

    WITH_PARENT(Boolean.class),
    RECURSIVE(Boolean.class);

    private Class typeClass;

    TesterraEventDataType(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public Class getTypeClass() {
        return typeClass;
    }

}

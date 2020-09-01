/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import java.util.List;
import org.testng.IMethodInstance;
import org.testng.ITestContext;

/**
 * This event gets fired before executing test methods.
 * Use this event to filter methods to be executed by updating {@link #setMethodInstanceList(List)}
 */
public class InterceptMethodsEvent {
    public interface Listener {
        void onInterceptMethods(InterceptMethodsEvent event);
    }
    private List<IMethodInstance> methodInstanceList;
    private ITestContext testContext;
    public List<IMethodInstance> getMethodInstanceList() {
        return methodInstanceList;
    }

    public InterceptMethodsEvent setMethodInstanceList(List<IMethodInstance> methodInstanceList) {
        this.methodInstanceList = methodInstanceList;
        return this;
    }

    public ITestContext getTestContext() {
        return testContext;
    }

    public InterceptMethodsEvent setTestContext(ITestContext testContext) {
        this.testContext = testContext;
        return this;
    }
}

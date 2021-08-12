/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.execution.testng;

import com.google.inject.Inject;
import eu.tsystems.mms.tic.testframework.testing.TestController;

/**
 * This wrapper wraps assertions to the current {@link Assertion} implementation
 * configured by {@link TestController.Overrides#setAssertionImpl(Assertion)}
 */
public class DefaultAssertionWrapper extends AbstractAssertion {

    private final TestController.Overrides overrides;

    @Inject
    protected DefaultAssertionWrapper(TestController.Overrides overrides) {
        this.overrides = overrides;
    }

    @Override
    public void fail(Error error) {
        this.overrides.getAssertionImpl().fail(error);
    }
}

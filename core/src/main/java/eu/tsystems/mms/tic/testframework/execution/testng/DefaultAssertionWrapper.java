/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
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
package eu.tsystems.mms.tic.testframework.execution.testng;

import com.google.inject.Inject;

/**
 * Passes assertions to the current implementation.
 * @author Mike Reiche
 */
public class DefaultAssertionWrapper extends AbstractAssertion implements Assertion {
    private final AssertionFactory assertionFactory;
    @Inject
    protected DefaultAssertionWrapper(AssertionFactory assertionFactory) {
        this.assertionFactory = assertionFactory;
    }
    @Override
    public void fail(AssertionError error) {
        this.assertionFactory.create().fail(error);
    }
}

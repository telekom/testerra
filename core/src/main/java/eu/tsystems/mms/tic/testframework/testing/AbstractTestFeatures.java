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
package eu.tsystems.mms.tic.testframework.testing;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertionFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.IAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.IPageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.PageOverrides;

/**
 * Abstract test features for Tests and Pages
 * @author Mike Reiche
 */
public abstract class AbstractTestFeatures {

    protected static final IPageFactory pageFactory = Testerra.ioc().getInstance(IPageFactory.class);
    private static final AssertionFactory assertionFactory = Testerra.ioc().getInstance(AssertionFactory.class);
    protected static final PageOverrides pageOverrides = Testerra.ioc().getInstance(PageOverrides.class);
    protected static IAssertion assertion = assertionFactory.create();

    protected void collectAssertions(Runnable runnable) {
        Class<? extends IAssertion> prevClass = assertionFactory.setDefault(CollectedAssertion.class);
        assertion = assertionFactory.create();
        runnable.run();
        assertionFactory.setDefault(prevClass);
        assertion = assertionFactory.create();
    }

    protected void nonFunctionalAssertions(Runnable runnable) {
        Class<? extends IAssertion> prevClass = assertionFactory.setDefault(NonFunctionalAssertion.class);
        assertion = assertionFactory.create();
        runnable.run();
        assertionFactory.setDefault(prevClass);
        assertion = assertionFactory.create();
    }

    protected void withTimeout(int seconds, Runnable runnable) {
        pageOverrides.setElementTimeoutInSeconds(seconds);
        runnable.run();
        pageOverrides.removeElementTimeoutInSeconds();
    }
}

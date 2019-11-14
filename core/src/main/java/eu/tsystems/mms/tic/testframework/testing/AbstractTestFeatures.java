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

import com.google.inject.Injector;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertionFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.IPageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.PageOverrides;

/**
 * Abstract test features for Tests and Pages
 * @author Mike Reiche
 */
public abstract class AbstractTestFeatures {

    protected static final Injector ioc = Testerra.ioc();
    private static final AssertionFactory assertionFactory = ioc.getInstance(AssertionFactory.class);
    private static final PageOverrides pageOverrides = ioc.getInstance(PageOverrides.class);
    protected static final IPageFactory pageFactory = ioc.getInstance(IPageFactory.class);
    /**
     * Instance for wrapping static class {@link AssertCollector}
     */
    @Deprecated
    protected final Assertion AssertCollector = ioc.getInstance(CollectedAssertion.class);
    /**
     * Instance for wrapping static class {@link NonFunctionalAssert}
     */
    @Deprecated
    protected final Assertion NonFunctionalAssert = ioc.getInstance(NonFunctionalAssertion.class);

    /**
     * @todo There is currently no way to make that final.
     */
    protected Assertion Assert = assertionFactory.create();

    protected void collectAssertions(Runnable runnable) {
        Class<? extends Assertion> prevClass = assertionFactory.setDefault(CollectedAssertion.class);
        Assert = assertionFactory.create();
        runnable.run();
        assertionFactory.setDefault(prevClass);
        Assert = assertionFactory.create();
    }

    protected void nonFunctionalAssertions(Runnable runnable) {
        Class<? extends Assertion> prevClass = assertionFactory.setDefault(NonFunctionalAssertion.class);
        Assert = assertionFactory.create();
        runnable.run();
        assertionFactory.setDefault(prevClass);
        Assert = assertionFactory.create();
    }

    protected void withElementTimeout(int seconds, Runnable runnable) {
        pageOverrides.setElementTimeoutInSeconds(seconds);
        runnable.run();
        pageOverrides.removeElementTimeoutInSeconds();
    }
}

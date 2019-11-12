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
package eu.tsystems.mms.tic.testframework.pageobjects.factory;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.IPageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.PageVariables;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Use {@link IPageFactory} instead
 */
@Deprecated
public final class PageFactory {

    private static IPageFactory pageFactory = Testerra.ioc().getInstance(IPageFactory.class);

    private static final ThreadLocal<CircularFifoBuffer> LOOP_DETECTION_LOGGER = new ThreadLocal<>();
    private static final int NR_OF_LOOPS = PropertyManager.getIntProperty(TesterraProperties.PAGE_FACTORY_LOOPS, 20);

    public static abstract class ErrorHandler {

        /**
         * Run the things you want to do when a page object could not be instantiated. Throw a new throwable by yourself!
         * @param driver .
         * @param throwableFromPageFactory .
         */
        public abstract void run(WebDriver driver, Throwable throwableFromPageFactory);
    }

    private static ErrorHandler errorHandler = null;

    @Deprecated
    public static void setGlobalPagesPrefix(String prefix) {
        pageFactory.setGlobalPagePrefix(prefix);
    }

    @Deprecated
    public static void setThreadLocalPagesPrefix(String threadLocalPagesPrefix) {
        pageFactory.setThreadLocalPagePrefix(threadLocalPagesPrefix);
    }

    @Deprecated
    public static void clearThreadLocalPagesPrefix() {
        pageFactory.removeThreadLocalPagePrefix();
    }

    /**
     * @deprecated False-Check overrides are deprecated
     */
    @Deprecated
    public static <T extends Page> T checkNot(Class<T> pageClass, WebDriver driver) {
        return loadPO(pageClass, driver, null, false);
    }

    /**
     * @deprecated False-Check overrides are deprecated
     */
    @Deprecated
    public static <T extends Page, U extends PageVariables> T checkNot(Class<T> pageClass, WebDriver driver, U pageVariables) {
        return loadPO(pageClass, driver, pageVariables, false);
    }

    public static <T extends Page> T create(Class<T> pageClass, WebDriver driver) {
        return pageFactory.createPage(pageClass, driver);
    }

    /**
     * @deprecated Passing page variables is deprecated
     */
    @Deprecated
    public static <T extends Page, U extends PageVariables> T create(Class<T> pageClass, WebDriver driver, U pageVariables) {
        if (pageVariables != null) {
            return loadPO(pageClass, driver, pageVariables, true);
        } else {
            return create(pageClass, driver);
        }
    }

    /**
     * @deprecated Passing page variables and False-Check overrides are deprecated
     */
    @Deprecated
    private static <T extends Page, U extends PageVariables> T loadPO(Class<T> pageClass, WebDriver driver, U pageVariables, boolean positiveCheck) {
        if (pageVariables instanceof Page) {
            throw new TesterraRuntimeException("You cannot hand over a page to a page. This is a bad design and also may produce looping. " +
                    "You can make page compositions with a) static modules (Page xyzPage = PageFactory.create(...) inside a page class) " +
                    "or b) dynamic modules (public XyzPage xyz() {return PageFactory.create(...)} ).");
        }

        /*
        find matching implementing class
         */
        pageClass = pageFactory.findBestMatchingClass(pageClass, driver);

        /*
        create object
         */
        final String msg = "Could not create instance of page class ";
        T t;
        try {
            try {
                Constructor<T> constructor;
                constructor = pageClass.getConstructor(WebDriver.class, pageVariables.getClass());
                t = constructor.newInstance(driver, pageVariables);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new TesterraRuntimeException(msg + pageClass.getSimpleName(), e);
            }

            // check page
            if (positiveCheck) {
                t.checkPage();
            }
            else {
                t.checkPage(true, false);
            }
        }
        catch (Throwable overAllThrowable) {
            if (errorHandler != null) {
                // should throw a new RuntimeException or Error ...
                try {
                    errorHandler.run(driver, overAllThrowable);
                } catch (Throwable e) {
                    // modify test method container
                    final String message = e.getMessage();

                    MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
                    if (methodContext != null) {
                        methodContext.errorContext().setThrowable(message, e, true);
                    }

                    throw e;
                }
                // ... if not, fall through
            }
            throw overAllThrowable;
        }

        /*
        Loop detection
         */
        CircularFifoBuffer buffer = LOOP_DETECTION_LOGGER.get();
        if (buffer == null) {
            CircularFifoBuffer fifoBuffer = new CircularFifoBuffer(NR_OF_LOOPS);
            LOOP_DETECTION_LOGGER.set(fifoBuffer);
            buffer = LOOP_DETECTION_LOGGER.get();
        }
        // add this page type to the buffer
        buffer.add(t);

        // detect
        if (buffer.size() == NR_OF_LOOPS) {
            // when the buffer is filled...
            List<String> classesInQueue = new ArrayList<>();
            for (Object o : buffer) {
                String classname = o.getClass().getName();
                // put each classname of the buffer entries into the list
                if (!classesInQueue.contains(classname)) {
                    classesInQueue.add(classname);
                }
            }

            // if this list is size 1, then there is only 1 page type loaded in NR_OF_LOOPS load actions (recorded by the buffer)
            if (classesInQueue.size() == 1) {
                // NR_OF_LOOPS times this one class has been loaded in this thread
                throw new TesterraRuntimeException("PageFactory create loop detected loading: " + classesInQueue.get(0));
            }
        }

        return t;
    }

    @Deprecated
    public static void clearCache() {
        ClassFinder.clearCache();
    }

    public static void setErrorHandler(ErrorHandler errorHandler) {
        PageFactory.errorHandler = errorHandler;
    }
}

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
package eu.tsystems.mms.tic.testframework.pageobjects.factory;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.PageVariables;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public final class PageFactory {

    /*
    TODO:
    RhjghPfghfh_Min_299px
    PfgfPage_500px_999px
    RhjghPfghfh_500px_Max

    Pnsns_Res_dklsk

     */

    private static String GLOBAL_PAGES_PREFIX = null;
    private static ThreadLocal<String> THREAD_LOCAL_PAGES_PREFIX = new ThreadLocal<>();

    private static final ThreadLocal<CircularFifoBuffer> LOOP_DETECTION_LOGGER = new ThreadLocal<>();

    private static final int NR_OF_LOOPS = PropertyManager.getIntProperty(TesterraProperties.PAGE_FACTORY_LOOPS, 20);

    private PageFactory() {

    }

    public static void setGlobalPagesPrefix(String prefix) {
        GLOBAL_PAGES_PREFIX = prefix;
    }

    public static void setThreadLocalPagesPrefix(String threadLocalPagesPrefix) {
        THREAD_LOCAL_PAGES_PREFIX.set(threadLocalPagesPrefix);
    }

    public static void clearThreadLocalPagesPrefix() {
        THREAD_LOCAL_PAGES_PREFIX.remove();
    }

    public static <T extends Page> T checkNot(Class<T> pageClass, WebDriver driver) {
        return loadPO(pageClass, driver, null, false);
    }

    /**
     * @deprecated Injecting {@link PageVariables} is an anti pattern and this feature will be removed soon.
     * Please see documentation for details.
     */
    @Deprecated
    public static <T extends Page, U extends PageVariables> T checkNot(Class<T> pageClass, WebDriver driver, U pageVariables) {
        return loadPO(pageClass, driver, pageVariables, false);
    }

    public static <T extends Page> T create(Class<T> pageClass, WebDriver driver) {
        return loadPO(pageClass, driver, null, true);
    }

    /**
     * @deprecated Injecting {@link PageVariables} is an anti pattern and this feature will be removed soon.
     * Please see documentation for details.
     */
    @Deprecated
    public static <T extends Page, U extends PageVariables> T create(Class<T> pageClass, WebDriver driver, U pageVariables) {
        return loadPO(pageClass, driver, pageVariables, true);
    }

    private static <T extends Page, U extends PageVariables> T loadPO(Class<T> pageClass, WebDriver driver, U pageVariables, boolean positiveCheck) {
        if (pageVariables instanceof Page) {
            throw new RuntimeException("You cannot hand over a page to a page. This is a bad design and also may produce looping. " +
                    "You can make page compositions with a) static modules (Page xyzPage = PageFactory.create(...) inside a page class) " +
                    "or b) dynamic modules (public XyzPage xyz() {return PageFactory.create(...)} ).");
        }

        /*
        find matching implementing class
         */
        String pagesPrefix = GLOBAL_PAGES_PREFIX;
        if (StringUtils.isNotEmpty(THREAD_LOCAL_PAGES_PREFIX.get())) {
            pagesPrefix = THREAD_LOCAL_PAGES_PREFIX.get();
        }
        pageClass = ClassFinder.getBestMatchingClass(pageClass, driver, pagesPrefix);

        /*
        create object
         */
        final String msg = "Could not create instance of page class ";
        T t;
        try {
            try {
                Constructor<T> constructor;
                if (pageVariables != null) {
                    constructor = pageClass.getConstructor(WebDriver.class, pageVariables.getClass());
                    t = constructor.newInstance(driver, pageVariables);
                } else {
                    constructor = pageClass.getConstructor(WebDriver.class);
                    t = constructor.newInstance(driver);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(msg + pageClass.getSimpleName(), e);
            }

            // check page
            if (positiveCheck) {
                t.checkPage();
            } else {
                t.checkPage(true, false);
            }
        } catch (Throwable overAllThrowable) {
            // modify test method container
//            final String message = overAllThrowable.getMessage();
//
//            MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
//            if (methodContext != null) {
//                methodContext.getErrorContext().setThrowable(message, overAllThrowable, true);
//            }

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
                throw new RuntimeException("PageFactory create loop detected loading: " + classesInQueue.get(0));
            }
        }

        return t;
    }

    public static void clearLoopDetectionBuffer() {
        LOOP_DETECTION_LOGGER.get().clear();
    }

    public static void clearCache() {
        ClassFinder.clearCache();
    }
}

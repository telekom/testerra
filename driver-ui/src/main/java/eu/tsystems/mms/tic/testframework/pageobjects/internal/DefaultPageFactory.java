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
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.exceptions.PageFactoryException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.Component;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultPageFactory implements PageFactory, MethodEndEvent.Listener, Loggable {

    @Deprecated
    private String GLOBAL_PAGES_PREFIX = null;
    @Deprecated
    private final ThreadLocal<String> THREAD_LOCAL_PAGES_PREFIX = new ThreadLocal<>();

    private static final ThreadLocal<CircularFifoBuffer> LOOP_DETECTION_LOGGER = new ThreadLocal<>();
    private static final int NR_OF_LOOPS = Properties.PAGE_FACTORY_LOOPS.asLong().intValue();

    @Override
    public PageFactory setGlobalPagesPrefix(String pagePrefix) {
        GLOBAL_PAGES_PREFIX = pagePrefix;
        return this;
    }

    @Override
    public PageFactory setThreadLocalPagesPrefix(String pagePrefix) {
        THREAD_LOCAL_PAGES_PREFIX.set(pagePrefix);
        return this;
    }

    @Override
    public PageFactory clearThreadLocalPagesPrefix() {
        THREAD_LOCAL_PAGES_PREFIX.remove();
        return this;
    }

    public DefaultPageFactory() {
        // For clear PageFactory loop detection buffer
        EventBus eventBus = Testerra.getEventBus();
        eventBus.register(this);
    }

    @Override
    public <T extends Page> T createPage(Class<T> pageClass, WebDriver webDriver) {
        return createPageWithCheckRule(pageClass, webDriver, CheckRule.DEFAULT);
    }

    private String getConfiguredPrefix() {
        String pagesPrefix = GLOBAL_PAGES_PREFIX;
        if (StringUtils.isNotEmpty(THREAD_LOCAL_PAGES_PREFIX.get())) {
            pagesPrefix = THREAD_LOCAL_PAGES_PREFIX.get();
        }
        return (pagesPrefix != null ? pagesPrefix : "");
    }

    @Override
    public <T extends Component> T createComponent(Class<T> componentClass, UiElement rootElement) {
        try {
            Constructor<T> constructor = componentClass.getConstructor(UiElement.class);
            T component = constructor.newInstance(rootElement);
            ((AbstractPage) component).checkUiElements();
            return component;
        } catch (Throwable throwable) {
            throw new PageFactoryException(componentClass, rootElement.getWebDriver(), throwable);
        }
    }

    @Override
    public <T extends Page> T createPageWithCheckRule(Class<T> pageClass, WebDriver webDriver, CheckRule checkRule) {
        T page;
        try {
            String pageClassString = String.format("%s.%s%s", pageClass.getPackage().getName(), getConfiguredPrefix(), pageClass.getSimpleName());
            pageClass = (Class<T>) Class.forName(pageClassString);

            Constructor<T> constructor = pageClass.getConstructor(WebDriver.class);
            page = constructor.newInstance(webDriver);
            ((AbstractPage) page).checkUiElements(checkRule);
        } catch (Throwable throwable) {
            throw new PageFactoryException(pageClass, webDriver, throwable);
        }
        this.runLoopDetection(pageClass);
        return page;
    }

    private void runLoopDetection(Class pageClass) {
        CircularFifoBuffer buffer = LOOP_DETECTION_LOGGER.get();
        if (buffer == null) {
            CircularFifoBuffer fifoBuffer = new CircularFifoBuffer(NR_OF_LOOPS);
            LOOP_DETECTION_LOGGER.set(fifoBuffer);
            buffer = LOOP_DETECTION_LOGGER.get();
        }

        buffer.add(pageClass);

        // Check if complete buffer contains only one class
        if (buffer.size() == NR_OF_LOOPS) {
            List<Class> list = (List<Class>) buffer.stream().distinct().collect(Collectors.toList());

            // if this list is size 1, then there is only 1 page type loaded in NR_OF_LOOPS load actions (recorded by the buffer)
            if (list.size() == 1) {
                // NR_OF_LOOPS times this one class has been loaded in this thread
                throw new RuntimeException("PageFactory create loop detected loading: " + list.get(0));
            }
        }
    }

    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent event) {
        this.clearLoopDetectionBuffer();
    }

    public static void clearLoopDetectionBuffer() {
        if (LOOP_DETECTION_LOGGER.get() != null) {
            LOOP_DETECTION_LOGGER.get().clear();
        }
    }
}

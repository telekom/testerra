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
 package eu.tsystems.mms.tic.testframework.webdrivermanager;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.internal.utils.DriverStorage;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.webdriver.DefaultWebDriverManager;
import eu.tsystems.mms.tic.testframework.webdriver.IWebDriverFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverFactory.wrapRawWebDriverWithEventFiringWebDriver;

/**
 * @todo Migrate to {@link DefaultWebDriverManager}
 */
@Deprecated
public final class WebDriverSessionsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverSessionsManager.class);

    private static final Map<String, IWebDriverFactory> WEB_DRIVER_FACTORIES = new HashMap<>();

    public static final String EXCLUSIVE_PREFIX = "EXCLUSIVE_";

    public static final Map<Date, Throwable> SESSION_STARTUP_ERRORS = new LinkedHashMap<>();

    private static final Map<String, WebDriver> EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP = new ConcurrentHashMap<>();
    private static final Map<String, WebDriver> THREAD_SESSION_KEY_WEBDRIVER_MAP = new ConcurrentHashMap<>();
    private static final Map<WebDriver, Long> WEBDRIVER_THREAD_ID_MAP = new ConcurrentHashMap<>();
    private static final Map<WebDriver, SessionContext> WEBDRIVER_SESSIONS_CONTEXTS_MAP = new ConcurrentHashMap<>();

    private static final String FULL_SESSION_KEY_SPLIT_MARKER = "___";

    private WebDriverSessionsManager() {

    }

    private static String getThreadSessionKey(String sessionKey) {
        Thread currentThread = Thread.currentThread();
        return currentThread.getId() + FULL_SESSION_KEY_SPLIT_MARKER + sessionKey;
    }

    static {
        /**
         * Getting multi binder set programmatically
         * @see {https://groups.google.com/forum/#!topic/google-guice/EUnNStmrhOk}
         */
        Set<IWebDriverFactory> webDriverFactories = Testerra.getInjector().getInstance(Key.get(new TypeLiteral<Set<IWebDriverFactory>>(){}));
        webDriverFactories.forEach(webDriverFactory -> webDriverFactory.getSupportedBrowsers().forEach(browser -> WEB_DRIVER_FACTORIES.put(browser, webDriverFactory)));
    }

    static void storeWebDriverSession(AbstractWebDriverRequest webDriverRequest, WebDriver eventFiringWebDriver, SessionContext sessionContext) {
        final String sessionKey = webDriverRequest.getSessionKey();
        final String threadSessionKey = getThreadSessionKey(sessionKey);
        THREAD_SESSION_KEY_WEBDRIVER_MAP.put(threadSessionKey, eventFiringWebDriver);

        final long threadId = Thread.currentThread().getId();
        WEBDRIVER_THREAD_ID_MAP.put(eventFiringWebDriver, threadId);

        /*
        storing driver into driver storage, for whatever reason
         */
        if (Testerra.Properties.REUSE_DATAPROVIDER_DRIVER_BY_THREAD.asBool()) {
            String methodName = ExecutionContextUtils.getMethodNameFromCurrentTestResult();
            if (methodName != null) {
                String threadName = Thread.currentThread().getId() + "";
                LOGGER.debug("Saving driver in " + DriverStorage.class.getSimpleName() + " for : " + methodName + ": " + threadName);
                DriverStorage.saveDriverForTestMethod(eventFiringWebDriver, threadName, methodName);
            }
        }

        /*
        store driver to session context relation
         */
        WEBDRIVER_SESSIONS_CONTEXTS_MAP.put(WebDriverUtils.getLowestWebDriver(eventFiringWebDriver), sessionContext);
    }

    private static void unlinkFromThread(String sessionKey, WebDriver eventFiringWebDriver) {
        String threadSessionKey = getThreadSessionKey(sessionKey);
        THREAD_SESSION_KEY_WEBDRIVER_MAP.remove(threadSessionKey, eventFiringWebDriver);

        final long threadId = Thread.currentThread().getId();
        WEBDRIVER_THREAD_ID_MAP.remove(eventFiringWebDriver, threadId);

        /*
        storing driver into driver storage, for whatever reason
         */
        if (Testerra.Properties.REUSE_DATAPROVIDER_DRIVER_BY_THREAD.asBool()) {
            String methodName = ExecutionContextUtils.getMethodNameFromCurrentTestResult();
            if (methodName != null) {
                String threadName = Thread.currentThread().getId() + "";
                LOGGER.info("Removing driver in " + DriverStorage.class.getSimpleName() + " for : " + methodName + ": " + threadName);
                DriverStorage.removeSpecificDriver(methodName);
            }
        }

        /*
        Log something about the session handling maps
         */
        String msg = "Removed WebDriver session: " + sessionKey;
        msg += "\n Remaining sessions: ";
        int i = 0;
        for (WebDriver webDriver : WEBDRIVER_THREAD_ID_MAP.keySet()) {
            Long tid = WEBDRIVER_THREAD_ID_MAP.get(webDriver);
            String key = getSessionKey(webDriver);
            if (key == null) {
                key = "!!unknown!!";
            }
            msg += "\n  " + key + " in thread " + tid;
            i++;
        }
        msg += "\n => " + i + " sessions (map: " + THREAD_SESSION_KEY_WEBDRIVER_MAP.size() + ")";
        LOGGER.debug(msg);
    }

    /**
     * Introduce an own webdriver object. Selenium session will be released in this case.
     *
     * @param driver     .
     * @param sessionKey .
     */
    static void introduceWebDriver(final String sessionKey, final WebDriver driver) {
        if (!(driver instanceof RemoteWebDriver)) {
            throw new IllegalArgumentException(
                    "The driver object of the argument must be an instance of RemoteWebDriver");
        }
        LOGGER.info("Introducing webdriver object");
        EventFiringWebDriver eventFiringWebDriver = wrapRawWebDriverWithEventFiringWebDriver(driver);

        UnspecificWebDriverRequest request = new UnspecificWebDriverRequest();
        request.setSessionKey(sessionKey);

        // create new session context
        SessionContext sessionContext = new SessionContext(request);

        // store to method context
        ExecutionContextController.getCurrentMethodContext().addSessionContext(sessionContext);
        storeWebDriverSession(request, eventFiringWebDriver, sessionContext);
    }

    private static final List<Consumer<WebDriver>> beforeQuitActions = new LinkedList<>();
    private static final List<Consumer<WebDriver>> afterQuitActions = new LinkedList<>();
    private static final List<Consumer<WebDriver>> WEBDRIVER_STARTUP_HANDLERS = new LinkedList<>();

    public static void registerWebDriverBeforeShutdownHandler(Consumer<WebDriver> beforeQuit) {
        beforeQuitActions.add(beforeQuit);
    }

    public static void registerWebDriverAfterShutdownHandler(Consumer<WebDriver> afterQuit) {
        afterQuitActions.add(afterQuit);
    }

    public static void registerWebDriverAfterStartupHandler(Consumer<WebDriver> afterStart) {
        WEBDRIVER_STARTUP_HANDLERS.add(afterStart);
    }

    public static void unregisterWebDriverAfterStartupHandler(Consumer<WebDriver> afterStart) {
        WEBDRIVER_STARTUP_HANDLERS.remove(afterStart);
    }

    private static String createSessionIdentifier(WebDriver webDriver, String sessionKey) {
        return String.format("%s (sessionKey=%s)", webDriver.getClass().getSimpleName(), sessionKey);
    }

    public static void shutdownWebDriver(WebDriver webDriver) {
        String sessionKey = getSessionKey(webDriver);
        String sessionIdentifier = createSessionIdentifier(webDriver, sessionKey);

        beforeQuitActions.forEach(webDriverConsumer -> {
            try {
                webDriverConsumer.accept(webDriver);
            } catch (Exception e) {
                LOGGER.error("Failed executing before shutdown handler", e);
            }
        });
        LOGGER.info("Shutting down " + sessionIdentifier);
        WebDriverManagerUtils.quitWebDriverSession(webDriver);

        afterQuitActions.forEach(webDriverConsumer -> {
            try {
                webDriverConsumer.accept(webDriver);
            } catch (Exception e) {
                LOGGER.error("Failed executing after shutdown handler", e);
            }
        });
        unlinkFromThread(sessionKey, webDriver);
        WEBDRIVER_SESSIONS_CONTEXTS_MAP.remove(WebDriverUtils.getLowestWebDriver(webDriver));
    }


    static void shutdownAllThreadSessions() {
        getWebDriversFromCurrentThread().forEach(WebDriverSessionsManager::shutdownWebDriver);
    }

    public static Stream<WebDriver> getWebDriversFromCurrentThread() {
        long threadId = Thread.currentThread().getId();
        return getWebDriversFromThread(threadId);
    }

    static void shutdownAllSessions() {
        THREAD_SESSION_KEY_WEBDRIVER_MAP.values().forEach(WebDriverSessionsManager::shutdownWebDriver);
        EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.values().forEach(WebDriverSessionsManager::shutdownWebDriver);

        // This should not be necessary but we do it anyway
        THREAD_SESSION_KEY_WEBDRIVER_MAP.clear();
        WEBDRIVER_THREAD_ID_MAP.clear();
        EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.clear();
        WEBDRIVER_SESSIONS_CONTEXTS_MAP.clear();
    }

    /**
     * Returns true if any session is active.
     *
     * @return .
     */
    static boolean hasAnySessionActive() {
        return hasSessionActiveInThisThread();
    }

    static boolean hasSessionActiveInThisThread() {
        long threadId = Thread.currentThread().getId();
        return getWebDriversFromThread(threadId).findAny().isPresent();
    }

    static synchronized String makeSessionExclusive(final WebDriver eventFiringWebDriver) {
        if (!(eventFiringWebDriver instanceof EventFiringWebDriver)) {
            throw new RuntimeException("Nah, your WebDriver is not an EventFiringWebDriver.");
        }

        if (EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.containsValue(eventFiringWebDriver)) {
            LOGGER.error("Session already set exclusive.");
            return null;
        }

        SessionContext sessionContext = getSessionContext(eventFiringWebDriver).get();
        String sessionKey = sessionContext.getSessionKey();
        unlinkFromThread(sessionKey, eventFiringWebDriver);
        /*
        Add session to exclusive map.
         */
        String exclusiveSessionKey = EXCLUSIVE_PREFIX + UUID.randomUUID().toString();
        EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.put(exclusiveSessionKey, eventFiringWebDriver);

        /*
        introduce session context to execution context
         */
        sessionContext.setSessionKey(exclusiveSessionKey);
        ExecutionContext currentExecutionContext = ExecutionContextController.getCurrentExecutionContext();
        currentExecutionContext.addExclusiveSessionContext(sessionContext);
        // fire sync
        Testerra.getEventBus().post(new ContextUpdateEvent().setContext(sessionContext));

        LOGGER.info("Promoted " + createSessionIdentifier(eventFiringWebDriver, sessionKey) + " to " + createSessionIdentifier(eventFiringWebDriver, exclusiveSessionKey));
        return exclusiveSessionKey;
    }

    static void shutdownExclusiveSession(final String key) {
        final WebDriver driver = EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.get(key);
        if (driver != null) {
            shutdownWebDriver(driver);
            EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.remove(key);
        }
    }

    /**
     * @deprecated Use {@link #getSessionContext(WebDriver)} instead
     */
    static String getSessionKey(WebDriver webDriver) {
        return getSessionContext(webDriver).map(SessionContext::getSessionKey).orElse("no session");
    }

    static Stream<WebDriver> getWebDriversFromThread(final long threadId) {
        return WEBDRIVER_THREAD_ID_MAP.entrySet().stream().filter(entry -> entry.getValue() == threadId).map(Map.Entry::getKey);
    }


    public static WebDriver getWebDriver(AbstractWebDriverRequest webDriverRequest) {
        /*
        get session key
         */
        if (StringUtils.isEmpty(webDriverRequest.getSessionKey())) {
            webDriverRequest.setSessionKey(WebDriverManager.DEFAULT_SESSION_KEY);
        }

        if (StringUtils.isEmpty(webDriverRequest.getBrowser())) {
            webDriverRequest.setBrowser(WebDriverManager.getConfig().getBrowser());
        }

        if (StringUtils.isEmpty(webDriverRequest.getBrowserVersion())) {
            webDriverRequest.setBrowserVersion(WebDriverManager.getConfig().getBrowserVersion());
        }

        String browser = webDriverRequest.getBrowser();
        String sessionKey = webDriverRequest.getSessionKey();
        /*
        Check for exclusive session
         */
        if (sessionKey.startsWith(EXCLUSIVE_PREFIX)) {
            // returning exclusive session
            if (EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.containsKey(sessionKey)) {
                return EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.get(sessionKey);
            } else {
                throw new SystemException("No Session for key: " + sessionKey);
            }
        }

        String fullSessionKey = getThreadSessionKey(sessionKey);
        WebDriver existingWebDriver = THREAD_SESSION_KEY_WEBDRIVER_MAP.get(fullSessionKey);

        /*
        session already exists?
         */
        if (existingWebDriver != null) {
            return existingWebDriver;
        }

        /*
         **** STARTING NEW SESSION ****
         */

        /*
        decide which session manager to use
         */
        if (WEB_DRIVER_FACTORIES.containsKey(browser)) {
            IWebDriverFactory webDriverFactory = WEB_DRIVER_FACTORIES.get(browser);

            /*
            create session context and link to method context
             */
            SessionContext sessionContext = new SessionContext(webDriverRequest);
            MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
            if (methodContext != null) {
                methodContext.addSessionContext(sessionContext);
            }
            ExecutionContextController.setCurrentSessionContext(sessionContext);

            /*
            setup new session
             */
            WebDriver newWebDriver = webDriverFactory.createWebDriver(webDriverRequest, sessionContext);
            Testerra.getEventBus().post(new ContextUpdateEvent().setContext(sessionContext));
            String sessionIdentifier = createSessionIdentifier(newWebDriver, sessionKey);
            /*
            run the handlers
             */
            WEBDRIVER_STARTUP_HANDLERS.forEach(webDriverConsumer -> {
                try {
                    webDriverConsumer.accept(newWebDriver);
                } catch (Exception e) {
                    LOGGER.error("Failed executing handler after starting up " + sessionIdentifier, e);
                }
            });
            return newWebDriver;
        } else {
            throw new SystemException("No webdriver factory registered for browser: '" + browser + "'");
        }
    }

    @Deprecated
    static void registerWebDriverFactory(IWebDriverFactory webDriverFactory, String... browsers) {
        LOGGER.debug("Register " + webDriverFactory.getClass().getSimpleName() + " for browsers " + String.join(", ", browsers));

        for (String browser : browsers) {
            WEB_DRIVER_FACTORIES.put(browser, webDriverFactory);
        }
    }

    public static boolean isExclusiveSession(WebDriver webDriver) {
        return EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.containsValue(webDriver);
    }

    public static Optional<SessionContext> getSessionContext(WebDriver webDriver) {
        return Optional.ofNullable(WEBDRIVER_SESSIONS_CONTEXTS_MAP.get(WebDriverUtils.getLowestWebDriver(webDriver)));
    }

    public static Optional<String> getRequestedBrowser(WebDriver webDriver) {
        return getSessionContext(webDriver).map(SessionContext::getWebDriverRequest).map(AbstractWebDriverRequest::getBrowser);
    }

    public static IWebDriverFactory getWebDriverFactory(String browser) {
        return WEB_DRIVER_FACTORIES.getOrDefault(browser, null);
    }

    public static Stream<SessionContext> readSessionContexts() {
        return WEBDRIVER_SESSIONS_CONTEXTS_MAP.values().stream();
    }

    public static Stream<WebDriver> readWebDrivers() {
        return WEBDRIVER_SESSIONS_CONTEXTS_MAP.keySet().stream();
    }
}

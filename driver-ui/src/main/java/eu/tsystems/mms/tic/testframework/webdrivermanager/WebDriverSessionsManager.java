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
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.webdriver.DefaultWebDriverManager;
import eu.tsystems.mms.tic.testframework.webdriver.IWebDriverFactory;
import java.util.Collections;
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

    private static final Map<String, WebDriver> ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, WebDriver> ALL_EVENTFIRING_WEBDRIVER_SESSIONS = Collections.synchronizedMap(new HashMap<>());
    private static final Map<WebDriver, Long> ALL_EVENTFIRING_WEBDRIVER_SESSIONS_WITH_THREADID = Collections.synchronizedMap(new HashMap<>());

    /*
    stores: webDriverSessionId = sessionContext
     */
    private static final Map<String, SessionContext> ALL_EVENTFIRING_WEBDRIVER_SESSIONS_CONTEXTS = Collections.synchronizedMap(new HashMap<>());

    private static final String FULL_SESSION_KEY_SPLIT_MARKER = "___";

    static final Map<EventFiringWebDriver, WebDriverRequest> DRIVER_REQUEST_MAP = new ConcurrentHashMap<>();

    private WebDriverSessionsManager() {

    }

    private static String getFullSessionKey(String sessionKey) {
        Thread currentThread = Thread.currentThread();
        return currentThread.getId() + FULL_SESSION_KEY_SPLIT_MARKER + sessionKey;
    }

    static {
        /**
         * Getting multi binder set programmatically
         * @see {https://groups.google.com/forum/#!topic/google-guice/EUnNStmrhOk}
         */
        Set<IWebDriverFactory> webDriverFactories = Testerra.injector.getInstance(Key.get(new TypeLiteral<Set<IWebDriverFactory>>(){}));
        webDriverFactories.forEach(webDriverFactory -> webDriverFactory.getSupportedBrowsers().forEach(browser -> WEB_DRIVER_FACTORIES.put(browser, webDriverFactory)));
    }

    static void storeWebDriverSession(WebDriverRequest webDriverRequest, WebDriver eventFiringWebDriver, SessionContext sessionContext) {
        final String sessionKey = webDriverRequest.getSessionKey();
        final String fullSessionKey = getFullSessionKey(sessionKey);
        ALL_EVENTFIRING_WEBDRIVER_SESSIONS.put(fullSessionKey, eventFiringWebDriver);

        final long threadId = Thread.currentThread().getId();
        ALL_EVENTFIRING_WEBDRIVER_SESSIONS_WITH_THREADID.put(eventFiringWebDriver, threadId);

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
        final String sessionId = WebDriverUtils.getSessionId(eventFiringWebDriver);
        if (sessionId != null) {
            ALL_EVENTFIRING_WEBDRIVER_SESSIONS_CONTEXTS.put(sessionId, sessionContext);
            sessionContext.setRemoteSessionId(sessionId);
            LOGGER.trace("Stored SessionContext " + sessionContext + " for session " + sessionId);
        } else {
            LOGGER.error("Could not store SessionContext, could not get SessionId");
        }

        // store the request
        DRIVER_REQUEST_MAP.put((EventFiringWebDriver) eventFiringWebDriver, webDriverRequest);

    }

    static void removeWebDriverSession(String sessionKey, WebDriver eventFiringWebDriver) {
        String fullSessionKey = getFullSessionKey(sessionKey);
        ALL_EVENTFIRING_WEBDRIVER_SESSIONS.remove(fullSessionKey, eventFiringWebDriver);

        final long threadId = Thread.currentThread().getId();
        ALL_EVENTFIRING_WEBDRIVER_SESSIONS_WITH_THREADID.remove(eventFiringWebDriver, threadId);

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
        for (WebDriver webDriver : ALL_EVENTFIRING_WEBDRIVER_SESSIONS_WITH_THREADID.keySet()) {
            Long tid = ALL_EVENTFIRING_WEBDRIVER_SESSIONS_WITH_THREADID.get(webDriver);
            String key = getSessionKey(webDriver);
            if (key == null) {
                key = "!!unknown!!";
            }
            msg += "\n  " + key + " in thread " + tid;
            i++;
        }
        msg += "\n => " + i + " sessions (map: " + ALL_EVENTFIRING_WEBDRIVER_SESSIONS.size() + ")";
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

        // create new session context
        SessionContext sessionContext = new SessionContext(sessionKey, "external");

        // store to method context
        ExecutionContextController.getCurrentMethodContext().addSessionContext(sessionContext);

        UnspecificWebDriverRequest r = new UnspecificWebDriverRequest();
        r.setSessionKey(sessionKey);
        storeWebDriverSession(r, eventFiringWebDriver, sessionContext);
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
        removeWebDriverSession(sessionKey, webDriver);

        afterQuitActions.forEach(webDriverConsumer -> {
            try {
                webDriverConsumer.accept(webDriver);
            } catch (Exception e) {
                LOGGER.error("Failed executing after shutdown handler", e);
            }
        });
    }


    static void shutdownAllThreadSessions() {
        for (WebDriver eventFiringWebDriver : getWebDriversFromCurrentThread()) {
            shutdownWebDriver(eventFiringWebDriver);
        }
    }

    public static List<WebDriver> getWebDriversFromCurrentThread() {
        long threadId = Thread.currentThread().getId();
        return getWebDriversFromThread(threadId);
    }

    static void shutdownAllSessions() {
        for (String key : ALL_EVENTFIRING_WEBDRIVER_SESSIONS.keySet()) {
            WebDriver eventFiringWebDriver = ALL_EVENTFIRING_WEBDRIVER_SESSIONS.get(key);
            shutdownWebDriver(eventFiringWebDriver);
        }

        for (String key : ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.keySet()) {
            WebDriver eventFiringWebDriver = ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.get(key);
            shutdownWebDriver(eventFiringWebDriver);
        }

        ALL_EVENTFIRING_WEBDRIVER_SESSIONS.clear();
        ALL_EVENTFIRING_WEBDRIVER_SESSIONS_WITH_THREADID.clear();
        ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.clear();
    }

    /**
     * Returns true if any session is active.
     *
     * @return .
     */
    static boolean hasAnySessionActive() {
        String fullSessionKey = getFullSessionKey("dummy");
        String fullSessionKeyPartForCurrentThread = fullSessionKey.split(FULL_SESSION_KEY_SPLIT_MARKER)[0];
        for (String key : ALL_EVENTFIRING_WEBDRIVER_SESSIONS.keySet()) {
            if (key.startsWith(fullSessionKeyPartForCurrentThread)) {
                return true;
            }
        }
        return false;
    }

    static boolean hasSessionActiveInThisThread() {
        long threadId = Thread.currentThread().getId();
        List<WebDriver> drivers = getWebDriversFromThread(threadId);
        return drivers.size() > 0;
    }

    static synchronized String makeSessionExclusive(final WebDriver eventFiringWebDriver) {
        if (!(eventFiringWebDriver instanceof EventFiringWebDriver)) {
            throw new RuntimeException("Nah, your WebDriver is not an EventFiringWebDriver.");
        }

        if (ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.containsValue(eventFiringWebDriver)) {
            LOGGER.error("Session already set exclusive.");
            return null;
        }

        /*
        Find session
         */
        String sessionKey = getSessionKey(eventFiringWebDriver);

        /*
        Add session to exclusive map.
         */
        String exclusiveSessionKey = EXCLUSIVE_PREFIX + UUID.randomUUID().toString();
        ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.put(exclusiveSessionKey, eventFiringWebDriver);

        /*
        introduce session context to execution context
         */
        String sessionId = WebDriverUtils.getSessionId(eventFiringWebDriver);
        SessionContext sessionContext = ALL_EVENTFIRING_WEBDRIVER_SESSIONS_CONTEXTS.get(sessionId);
        sessionContext.setSessionKey(exclusiveSessionKey);
        ExecutionContext currentExecutionContext = ExecutionContextController.getCurrentExecutionContext();
        currentExecutionContext.addExclusiveSessionContext(sessionContext);
        // fire sync
        Testerra.getEventBus().post(new ContextUpdateEvent().setContext(sessionContext));

        /*
        Delete session from session maps.
         */
        removeWebDriverSession(sessionKey, eventFiringWebDriver);

        LOGGER.info("Promoted " + createSessionIdentifier(eventFiringWebDriver, sessionKey) + " to " + createSessionIdentifier(eventFiringWebDriver, exclusiveSessionKey));
        return exclusiveSessionKey;
    }

    static void shutdownExclusiveSession(final String key) {
        final WebDriver driver = ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.get(key);
        if (driver != null) {
            shutdownWebDriver(driver);
            ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.remove(key);
        }
    }

    static String getSessionKey(WebDriver webDriver) {
        String sessionKey;
        Optional<Map.Entry<String, WebDriver>> optionalWebDriverEntry = ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.entrySet().stream().filter(entry -> entry.getValue() == webDriver).findFirst();
        if (optionalWebDriverEntry.isPresent()) {
            sessionKey = optionalWebDriverEntry.get().getKey();
        } else {
            optionalWebDriverEntry = ALL_EVENTFIRING_WEBDRIVER_SESSIONS.entrySet().stream().filter(entry -> entry.getValue() == webDriver).findFirst();
            if (optionalWebDriverEntry.isPresent()) {
                sessionKey = optionalWebDriverEntry.get().getKey().split(FULL_SESSION_KEY_SPLIT_MARKER)[1];
            } else {
                sessionKey = "no session";
            }
        }
        return sessionKey;
    }

    static List<WebDriver> getWebDriversFromThread(final long threadId) {
        // !! but this has to be a list of eventfiring webdrivers
        final List<WebDriver> drivers = new LinkedList<>();
        for (WebDriver eventFiringWebDriver : ALL_EVENTFIRING_WEBDRIVER_SESSIONS_WITH_THREADID.keySet()) {
            Long threadIdToCheck = ALL_EVENTFIRING_WEBDRIVER_SESSIONS_WITH_THREADID.get(eventFiringWebDriver);
            if (threadIdToCheck == threadId) {
                drivers.add(eventFiringWebDriver);
            }
        }
        return drivers;
    }


    public static WebDriver getWebDriver(WebDriverRequest webDriverRequest) {
        /*
        get session key
         */
        if (!webDriverRequest.hasSessionKey()) {
            webDriverRequest.setSessionKey(WebDriverManager.DEFAULT_SESSION_KEY);
        }

        if (!webDriverRequest.hasBrowser()) {
            webDriverRequest.setBrowser(WebDriverManager.getConfig().getBrowser());
        }

        if (!webDriverRequest.hasBrowserVersion()) {
            webDriverRequest.setBrowserVersion(WebDriverManager.getConfig().getBrowserVersion());
        }

        String browser = webDriverRequest.getBrowser();
        String sessionKey = webDriverRequest.getSessionKey();
        /*
        Check for exclusive session
         */
        if (sessionKey.startsWith(EXCLUSIVE_PREFIX)) {
            // returning exclusive session
            if (ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.containsKey(sessionKey)) {
                return ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.get(sessionKey);
            } else {
                throw new SystemException("No Session for key: " + sessionKey);
            }
        }

        String fullSessionKey = getFullSessionKey(sessionKey);
        WebDriver existingWebDriver = ALL_EVENTFIRING_WEBDRIVER_SESSIONS.get(fullSessionKey);

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
            SessionContext sessionContext = new SessionContext(sessionKey, webDriverFactory.getClass().getSimpleName());
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
        return ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.containsValue(webDriver);
    }

    public static SessionContext getSessionContext(String sessionId) {
        return ALL_EVENTFIRING_WEBDRIVER_SESSIONS_CONTEXTS.get(sessionId);
    }

    public static IWebDriverFactory getWebDriverFactory(String browser) {
        return WEB_DRIVER_FACTORIES.getOrDefault(browser, null);
    }
}

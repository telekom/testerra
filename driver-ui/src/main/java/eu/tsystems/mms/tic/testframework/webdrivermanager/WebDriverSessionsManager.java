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

import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.execution.worker.finish.WebDriverSessionHandler;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.utils.DriverStorage;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverFactory.wrapRawWebDriverWithEventFiringWebDriver;

public final class WebDriverSessionsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverSessionsManager.class);

    private static final Map<String, WebDriverFactory> WEB_DRIVER_FACTORIES = new HashMap<>();

    public static final String EXCLUSIVE_PREFIX = "EXCLUSIVE_";

    public static final Map<Date, Throwable> SESSION_STARTUP_ERRORS = new LinkedHashMap<>();

    private static final Map<String, WebDriver> ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, WebDriver> ALL_EVENTFIRING_WEBDRIVER_SESSIONS = Collections.synchronizedMap(new HashMap<>());
    private static final Map<WebDriver, String> ALL_EVENTFIRING_WEBDRIVER_SESSIONS_INVERSE = Collections.synchronizedMap(new HashMap<>());
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

    static void storeWebDriverSession(WebDriverRequest webDriverRequest, WebDriver eventFiringWebDriver, SessionContext sessionContext) {
        final String sessionKey = webDriverRequest.sessionKey;
        final String fullSessionKey = getFullSessionKey(sessionKey);
        ALL_EVENTFIRING_WEBDRIVER_SESSIONS.put(fullSessionKey, eventFiringWebDriver);
        ALL_EVENTFIRING_WEBDRIVER_SESSIONS_INVERSE.put(eventFiringWebDriver, fullSessionKey);

        final long threadId = Thread.currentThread().getId();
        ALL_EVENTFIRING_WEBDRIVER_SESSIONS_WITH_THREADID.put(eventFiringWebDriver, threadId);

        /*
        storing driver into driver storage, for whatever reason
         */
        if (Flags.REUSE_DATAPROVIDER_DRIVER_BY_THREAD) {
            String methodName = ExecutionContextUtils.getMethodNameFromCurrentTestResult();
            if (methodName != null) {
                String threadName = Thread.currentThread().getId() + "";
                LOGGER.info("Saving driver in " + DriverStorage.class.getSimpleName() + " for : " + methodName + ": " + threadName);
                DriverStorage.saveDriverForTestMethod(eventFiringWebDriver, threadName, methodName);
            }
        }

        /*
        store driver to session context relation
         */
        final String sessionId = WebDriverUtils.getSessionId(eventFiringWebDriver);
        if (sessionId != null) {
            ALL_EVENTFIRING_WEBDRIVER_SESSIONS_CONTEXTS.put(sessionId, sessionContext);
            LOGGER.trace("Stored SessionContext " + sessionContext + " for session " + sessionId);
        } else {
            LOGGER.error("Could not store SessionContext, could not get SessionId");
        }

        // store the request
        DRIVER_REQUEST_MAP.put((EventFiringWebDriver) eventFiringWebDriver, webDriverRequest);

    }

    static void removeWebDriverSession(String sessionId, WebDriver eventFiringWebDriver, String fullSessionKeyOrNull) {
        String fullSessionKey = fullSessionKeyOrNull;
        if (fullSessionKey == null) {
            fullSessionKey = getFullSessionKey(sessionId);
        }
        ALL_EVENTFIRING_WEBDRIVER_SESSIONS.remove(fullSessionKey, eventFiringWebDriver);
        ALL_EVENTFIRING_WEBDRIVER_SESSIONS_INVERSE.remove(eventFiringWebDriver, fullSessionKey);

        final long threadId = Thread.currentThread().getId();
        ALL_EVENTFIRING_WEBDRIVER_SESSIONS_WITH_THREADID.remove(eventFiringWebDriver, threadId);

        /*
        storing driver into driver storage, for whatever reason
         */
        if (Flags.REUSE_DATAPROVIDER_DRIVER_BY_THREAD) {
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
        String msg = "Removed WebDriver session: " + sessionId + " => " + fullSessionKey;
        msg += "\n Remaining sessions: ";
        int i = 0;
        for (WebDriver webDriver : ALL_EVENTFIRING_WEBDRIVER_SESSIONS_WITH_THREADID.keySet()) {
            Long tid = ALL_EVENTFIRING_WEBDRIVER_SESSIONS_WITH_THREADID.get(webDriver);
            String key = ALL_EVENTFIRING_WEBDRIVER_SESSIONS_INVERSE.get(webDriver);
            if (key == null) {
                key = "!!unknown!!";
            }
            msg += "\n  " + key + " in thread " + tid;
            i++;
        }
        msg += "\n => " + i + " sessions (map: " + ALL_EVENTFIRING_WEBDRIVER_SESSIONS.size() + " mapInv: " + ALL_EVENTFIRING_WEBDRIVER_SESSIONS_INVERSE.size() + ")";
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
        ExecutionContextController.getCurrentMethodContext().sessionContexts.add(sessionContext);

        UnspecificWebDriverRequest r = new UnspecificWebDriverRequest();
        r.sessionKey = sessionKey;
        storeWebDriverSession(r, eventFiringWebDriver, sessionContext);
    }

    static final List<WebDriverSessionHandler> beforeQuitActions = new LinkedList<>();
    static final List<Runnable> afterQuitActions = new LinkedList<>();

    public static void registerWebDriverShutDownHandler(WebDriverSessionHandler beforeQuit) {
        WebDriverSessionsManager.beforeQuitActions.add(beforeQuit);
    }

    public static void registerWebDriverShutDownHandler(Runnable afterQuit) {
        WebDriverSessionsManager.afterQuitActions.add(afterQuit);
    }

    static void shutDownAllThreadSessions() {
        long threadId = Thread.currentThread().getId();
        List<WebDriver> webDriversFromThread = getWebDriversFromThread(threadId);

        for (WebDriver eventFiringWebDriver : webDriversFromThread) {
            String sessionKey = getSessionKey(eventFiringWebDriver);
            LOGGER.info(String.format("Shutting down %s (session key=%s)", eventFiringWebDriver.getClass().getSimpleName(), sessionKey));

            beforeQuitActions.forEach(webDriverSessionHandler -> {
                try {
                    webDriverSessionHandler.run(eventFiringWebDriver);
                } catch (Exception e) {
                    LOGGER.error("Error executing " + webDriverSessionHandler, e);
                }
            });

            // quit it
            WebDriverManagerUtils.quitWebDriverSession(eventFiringWebDriver);

            // remove links
            removeWebDriverSession(sessionKey, eventFiringWebDriver, null);

            afterQuitActions.forEach(runnable -> {
                try {
                    runnable.run();
                } catch (Exception e) {
                    LOGGER.error("Error executing WD shutdown afterQuit action " + runnable, e);
                }
            });

        }
    }

    static void shutDownAllSessions() {
        for (String key : ALL_EVENTFIRING_WEBDRIVER_SESSIONS.keySet()) {
            LOGGER.info("Quitting webdriver session: " + key);
            WebDriver eventFiringWebDriver = ALL_EVENTFIRING_WEBDRIVER_SESSIONS.get(key);

            WebDriverManagerUtils.quitWebDriverSession(eventFiringWebDriver);
        }

        for (String key : ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.keySet()) {
            LOGGER.info("Quitting exclusive ebdriver session: " + key);
            WebDriver eventFiringWebDriver = ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.get(key);

            WebDriverManagerUtils.quitWebDriverSession(eventFiringWebDriver);
        }

        ALL_EVENTFIRING_WEBDRIVER_SESSIONS.clear();
        ALL_EVENTFIRING_WEBDRIVER_SESSIONS_INVERSE.clear();

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
            throw new TesterraRuntimeException("Nah, your WebDriver is not an EventFiringWebDriver.");
        }

        if (ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.containsValue(eventFiringWebDriver)) {
            LOGGER.error("Session already set exclusive.");
            return null;
        }

        /*
        Find session
         */
        String fullSessionKey = ALL_EVENTFIRING_WEBDRIVER_SESSIONS_INVERSE.get(eventFiringWebDriver);
        String sessionKey = fullSessionKey.split(FULL_SESSION_KEY_SPLIT_MARKER)[1];

        /*
        Add session to exclusive map.
         */
        String uuid = EXCLUSIVE_PREFIX + UUID.randomUUID().toString();
        ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.put(uuid, eventFiringWebDriver);

        /*
        introduce session context to execution context
         */
        String sessionId = WebDriverUtils.getSessionId(eventFiringWebDriver);
        SessionContext sessionContext = ALL_EVENTFIRING_WEBDRIVER_SESSIONS_CONTEXTS.get(sessionId);
        ExecutionContextController.getCurrentExecutionContext().exclusiveSessionContexts.add(sessionContext);
        sessionContext.parentContext = ExecutionContextController.getCurrentExecutionContext();
        // fire sync
        TesterraListener.getEventBus().post(new ContextUpdateEvent().setContext(sessionContext));

        /*
        Delete session from session maps.
         */
        removeWebDriverSession(sessionKey, eventFiringWebDriver, fullSessionKey);

        LOGGER.info("Created exclusive session: " + uuid);
        return uuid;
    }

    static void shutdownExclusiveSession(final String key) {
        final WebDriver driver = ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.get(key);
        if (driver != null) {
            LOGGER.info("Shutting down exclusive session: " + key);
            WebDriverManagerUtils.quitWebDriverSession(driver);
            ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.remove(key);
        }
    }

    static String getSessionKey(WebDriver eventFiringWebDriver) {
        if (eventFiringWebDriver == null) {
            return "no session";
        }

        String fullSessionKey = ALL_EVENTFIRING_WEBDRIVER_SESSIONS_INVERSE.get(eventFiringWebDriver);
        if (fullSessionKey != null) {
            return fullSessionKey.split(FULL_SESSION_KEY_SPLIT_MARKER)[1];
        }

        return "external";
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

    private static final List<WebDriverSessionHandler> WEBDRIVER_STARTUP_HANDLERS = new LinkedList<>();

    static void addWebDriverStartUpHandler(WebDriverSessionHandler webDriverSessionHandler) {
        WEBDRIVER_STARTUP_HANDLERS.add(webDriverSessionHandler);
    }

    public static WebDriver getWebDriver(WebDriverRequest webDriverRequest) {
        /*
        get session key
         */
        String sessionKey = WebDriverManager.DEFAULT_SESSION_KEY;
        if (!StringUtils.isStringEmpty(webDriverRequest.sessionKey)) {
            sessionKey = webDriverRequest.sessionKey;
        } else {
            webDriverRequest.sessionKey = sessionKey;
        }

        /**
         * Browser global setting.
         */
        String browser = WebDriverManager.config().browser();
        String browserVersion = WebDriverManager.config().browserVersion();

        if (webDriverRequest.browser != null) {
            browser = webDriverRequest.browser;
        } else {
            webDriverRequest.browser = browser;
        }

        if (webDriverRequest.browserVersion != null) {
            browserVersion = webDriverRequest.browserVersion;
        } else {
            webDriverRequest.browserVersion = browserVersion;
        }

        /*
        Check for exclusive session
         */
        if (sessionKey.startsWith(EXCLUSIVE_PREFIX)) {
            // returning exclusive session
            if (ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.containsKey(sessionKey)) {
                return ALL_EXCLUSIVE_EVENTFIRING_WEBDRIVER_SESSIONS.get(sessionKey);
            } else {
                throw new TesterraSystemException("Session not useable anymore: " + sessionKey);
            }
        }

        String fullSessionKey = getFullSessionKey(sessionKey);
        WebDriver eventFiringWebDriver = ALL_EVENTFIRING_WEBDRIVER_SESSIONS.get(fullSessionKey);

        /*
        session already exists?
         */
        if (eventFiringWebDriver != null) {
            return eventFiringWebDriver;
        }

        /*
         **** STARTING NEW SESSION ****
         */

        /*
        decide which session manager to use
         */
        if (WEB_DRIVER_FACTORIES.containsKey(browser)) {
            WebDriverFactory webDriverFactory = WEB_DRIVER_FACTORIES.get(browser);

            /*
            create session context and link to method context
             */
            SessionContext sessionContext = new SessionContext(sessionKey, webDriverFactory.getClass().getSimpleName());
            webDriverRequest.sessionContext = sessionContext;
            MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
            if (methodContext != null) {
                methodContext.sessionContexts.add(sessionContext);
            }
            sessionContext.parentContext = methodContext;
            ExecutionContextController.setCurrentSessionContext(sessionContext);

            /*
            setup new session
             */
            eventFiringWebDriver = webDriverFactory.getWebDriver(webDriverRequest, sessionContext);
            TesterraListener.getEventBus().post(new ContextUpdateEvent().setContext(sessionContext));

            /*
            run the handlers
             */
            for (WebDriverSessionHandler handler : WEBDRIVER_STARTUP_HANDLERS) {
                try {
                    handler.run(eventFiringWebDriver);
                } catch (Exception e) {
                    LOGGER.error("Error executing webdriver startup handler", e);
                }
            }
            return eventFiringWebDriver;
        } else {
            throw new TesterraSystemException("No webdriver factory registered for browser " + browser);
        }
    }

    static void registerWebDriverFactory(WebDriverFactory webDriverFactory, String... browsers) {
        LOGGER.debug("Register " + webDriverFactory.getClass().getSimpleName() + " for browsers " + String.join(", ", browsers));

        for (String browser : browsers) {
            WEB_DRIVER_FACTORIES.put(browser, webDriverFactory);
        }
    }

    public static SessionContext getSessionContext(String sessionId) {
        return ALL_EVENTFIRING_WEBDRIVER_SESSIONS_CONTEXTS.get(sessionId);
    }
}

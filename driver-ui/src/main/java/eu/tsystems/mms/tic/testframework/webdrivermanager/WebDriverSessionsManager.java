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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.internal.utils.DriverStorage;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextUtils;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;
import eu.tsystems.mms.tic.testframework.useragents.BrowserInformation;
import eu.tsystems.mms.tic.testframework.utils.DefaultCapabilityUtils;
import eu.tsystems.mms.tic.testframework.utils.ObjectUtils;
import eu.tsystems.mms.tic.testframework.webdriver.WebDriverFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @todo Migrate to {@link DefaultWebDriverManager}
 */
@Deprecated
public final class WebDriverSessionsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverSessionsManager.class);

    private static final Map<String, WebDriverFactory> WEB_DRIVER_FACTORIES = new HashMap<>();

    public static final Map<Date, Throwable> SESSION_STARTUP_ERRORS = new LinkedHashMap<>();

    private static final Map<String, EventFiringWebDriver> EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP = new ConcurrentHashMap<>();
    private static final Map<String, EventFiringWebDriver> THREAD_SESSION_KEY_WEBDRIVER_MAP = new ConcurrentHashMap<>();
    private static final Map<EventFiringWebDriver, Long> WEBDRIVER_THREAD_ID_MAP = new ConcurrentHashMap<>();
    static final Map<EventFiringWebDriver, SessionContext> WEBDRIVER_SESSIONS_CONTEXTS_MAP = new ConcurrentHashMap<>();
    private static final Queue<Consumer<WebDriver>> beforeQuitActions = new ConcurrentLinkedQueue<>();
    private static final Queue<Consumer<WebDriver>> afterQuitActions = new ConcurrentLinkedQueue<>();
    private static final Queue<Consumer<WebDriver>> WEBDRIVER_STARTUP_HANDLERS = new ConcurrentLinkedQueue<>();

    private static final String FULL_SESSION_KEY_SPLIT_MARKER = "___";
    private static final Set<WebDriverFactory> webDriverFactories = Testerra.getInjector().getInstance(Key.get(new TypeLiteral<Set<WebDriverFactory>>() {
    }));
    static final Queue<Consumer<WebDriverRequest>> webDriverRequestConfigurators = new ConcurrentLinkedQueue<>();
    private static final IExecutionContextController executionContextController = Testerra.getInjector().getInstance(IExecutionContextController.class);

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
        webDriverFactories.stream()
                .sorted(Comparator.comparing(f -> f.getClass().getSimpleName()))
                .forEach(webDriverFactory -> {
                    webDriverFactory.getSupportedBrowsers().forEach(browser -> WEB_DRIVER_FACTORIES.put(browser, webDriverFactory));
                });
    }

    private static void storeWebDriverSession(EventFiringWebDriver eventFiringWebDriver, SessionContext sessionContext) {
        WebDriverRequest webDriverRequest = sessionContext.getWebDriverRequest();
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
            String threadName = Thread.currentThread().getId() + "";
            LOGGER.debug("Saving driver in " + DriverStorage.class.getSimpleName() + " for : " + methodName + ": " + threadName);
            DriverStorage.saveDriverForTestMethod(eventFiringWebDriver, threadName, methodName);
        }

        /*
        store driver to session context relation
         */
        WEBDRIVER_SESSIONS_CONTEXTS_MAP.put(eventFiringWebDriver, sessionContext);
    }

    private static void unlinkFromThread(String sessionKey, EventFiringWebDriver eventFiringWebDriver) {
        final String sessionIdentifier = createSessionIdentifier(eventFiringWebDriver, sessionKey);
        LOGGER.trace("Unlink from thread: " + sessionIdentifier);
        String threadSessionKey = getThreadSessionKey(sessionKey);
        THREAD_SESSION_KEY_WEBDRIVER_MAP.remove(threadSessionKey, eventFiringWebDriver);

        final long threadId = Thread.currentThread().getId();
        WEBDRIVER_THREAD_ID_MAP.remove(eventFiringWebDriver, threadId);

        executionContextController.clearCurrentSessionContext();

        /*
        storing driver into driver storage, for whatever reason
         */
        if (Testerra.Properties.REUSE_DATAPROVIDER_DRIVER_BY_THREAD.asBool()) {
            String methodName = ExecutionContextUtils.getMethodNameFromCurrentTestResult();
            String threadName = Thread.currentThread().getId() + "";
            LOGGER.info("Removing driver in " + DriverStorage.class.getSimpleName() + " for : " + methodName + ": " + threadName);
            DriverStorage.removeSpecificDriver(methodName);
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
            msg += "\n  " + key + " in thread " + tid;
            i++;
        }
        msg += "\n => " + i + " sessions (map: " + THREAD_SESSION_KEY_WEBDRIVER_MAP.size() + ")";
        LOGGER.debug(msg);
    }

    /**
     * Introduce an own webdriver object. Selenium session will be released in this case.
     *
     * @param webDriver .
     * @param sessionKey .
     */
    static void introduceWebDriver(final String sessionKey, WebDriver webDriver) {
        if (!(webDriver instanceof RemoteWebDriver)) {
            throw new IllegalArgumentException(
                    "The driver object of the argument must be an instance of RemoteWebDriver");
        }

        LOGGER.info("Introducing webdriver object");
        //EventFiringWebDriver eventFiringWebDriver = wrapRawWebDriverWithEventFiringWebDriver(driver);

        UnspecificWebDriverRequest request = new UnspecificWebDriverRequest();
        request.setSessionKey(sessionKey);

        // create new session context
        SessionContext sessionContext = new SessionContext(request);
        EventFiringWebDriver eventFiringWebDriver = wrapWebDriver(webDriver, sessionContext);

        // store to method context
        executionContextController.getCurrentMethodContext().ifPresent(methodContext -> {
            methodContext.addSessionContext(sessionContext);
        });
        storeWebDriverSession(eventFiringWebDriver, sessionContext);
    }

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
        EventFiringWebDriver eventFiringWebDriver = checkForWrappedWebDriver(webDriver);
        String sessionKey = getSessionKey(eventFiringWebDriver);
        String sessionIdentifier = createSessionIdentifier(eventFiringWebDriver, sessionKey);

        beforeQuitActions.forEach(webDriverConsumer -> {
            try {
                LOGGER.trace("Call before shutdown handler");
                webDriverConsumer.accept(eventFiringWebDriver);
            } catch (Exception e) {
                LOGGER.error("Failed executing before shutdown handler", e);
            }
        });
        LOGGER.info("Shutting down " + sessionIdentifier);
        WebDriverManagerUtils.quitWebDriverSession(eventFiringWebDriver);

        afterQuitActions.forEach(webDriverConsumer -> {
            try {
                LOGGER.trace("Call after shutdown handler");
                webDriverConsumer.accept(eventFiringWebDriver);
            } catch (Exception e) {
                LOGGER.error("Failed executing after shutdown handler", e);
            }
        });
        unlinkFromThread(sessionKey, eventFiringWebDriver);
        WEBDRIVER_SESSIONS_CONTEXTS_MAP.remove(eventFiringWebDriver);
        if (sessionKey.startsWith(SessionContext.EXCLUSIVE_PREFIX)) {
            EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.remove(sessionKey);
        }
        LOGGER.debug("Shut down: " + sessionIdentifier);
    }

    static void shutdownAllThreadSessions() {
        getWebDriversFromCurrentThread().forEach(WebDriverSessionsManager::shutdownWebDriver);
    }

    public static Stream<EventFiringWebDriver> getWebDriversFromCurrentThread() {
        long threadId = Thread.currentThread().getId();
        return getWebDriversFromThread(threadId);
    }

    public static Stream<EventFiringWebDriver> readExclusiveWebDrivers() {
        return EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.values().stream();
    }

    static void shutdownAllSessions() {
        THREAD_SESSION_KEY_WEBDRIVER_MAP.values().forEach(WebDriverSessionsManager::shutdownWebDriver);
        EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.values().forEach(WebDriverSessionsManager::shutdownWebDriver);

        // This should not be necessary but we do it anyway
//        THREAD_SESSION_KEY_WEBDRIVER_MAP.clear();
//        WEBDRIVER_THREAD_ID_MAP.clear();
//        EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.clear();
//        WEBDRIVER_SESSIONS_CONTEXTS_MAP.clear();
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

    static synchronized String makeSessionExclusive(final WebDriver webDriver) {
        EventFiringWebDriver eventFiringWebDriver = checkForWrappedWebDriver(webDriver);

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
        String exclusiveSessionKey = SessionContext.EXCLUSIVE_PREFIX + UUID.randomUUID().toString();
        EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.put(exclusiveSessionKey, eventFiringWebDriver);

        /*
        introduce session context to execution context
         */
        sessionContext.setSessionKey(exclusiveSessionKey);
        sessionContext.getWebDriverRequest().setShutdownAfterTest(false);
        sessionContext.getWebDriverRequest().setShutdownAfterTestFailed(false);
        executionContextController.getExecutionContext().addExclusiveSessionContext(sessionContext);
        Testerra.getEventBus().post(new ContextUpdateEvent().setContext(sessionContext));

        LOGGER.info("Promoted " + createSessionIdentifier(webDriver, sessionKey) + " to " + createSessionIdentifier(webDriver, exclusiveSessionKey));
        return exclusiveSessionKey;
    }

    public static void shutdownSessionKey(final String key) {
        if (EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.containsKey(key)) {
            shutdownWebDriver(EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.get(key));
            EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.remove(key);
        } else if (THREAD_SESSION_KEY_WEBDRIVER_MAP.containsKey(key)) {
            shutdownWebDriver(THREAD_SESSION_KEY_WEBDRIVER_MAP.get(key));
        }
    }

    /**
     * @deprecated Use {@link #getSessionContext(WebDriver)} instead
     */
    static String getSessionKey(WebDriver webDriver) {
        return getSessionContext(webDriver).map(SessionContext::getSessionKey).orElse("no session");
    }

    static Stream<EventFiringWebDriver> getWebDriversFromThread(final long threadId) {
        return WEBDRIVER_THREAD_ID_MAP.entrySet().stream().filter(entry -> entry.getValue() == threadId).map(Map.Entry::getKey);
    }

    public static EventFiringWebDriver getWebDriver(final WebDriverRequest webDriverRequest) {
        String sessionKey = webDriverRequest.getSessionKey();
        EventFiringWebDriver existingWebDriver = null;
        /*
        Check for exclusive session
         */
        if (sessionKey.startsWith(SessionContext.EXCLUSIVE_PREFIX)) {
            // returning exclusive session
            if (EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.containsKey(sessionKey)) {
                existingWebDriver = EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.get(sessionKey);
            } else {
                throw new SystemException("No Session for key: " + sessionKey);
            }
        } else {
            String fullSessionKey = getThreadSessionKey(sessionKey);
            existingWebDriver = THREAD_SESSION_KEY_WEBDRIVER_MAP.get(fullSessionKey);
        }

        /*
        session already exists?
         */
        if (existingWebDriver != null) {
            /*
            Link sessionContext to methodContext if not exist
            e.g. Session was created in setup method and reused in test method or exclusive session is used in current test.
            */
            if(sessionKey.startsWith(SessionContext.EXCLUSIVE_PREFIX)) {
                // Link an exclusive sessionContext
                executionContextController.getExecutionContext().readExclusiveSessionContexts()
                        .filter(sessionContext -> sessionContext.getSessionKey().equals(sessionKey))
                        .findFirst()
                        .ifPresent(sessionContext ->
                                executionContextController.getCurrentMethodContext().ifPresent(currentMethodContext ->
                                        currentMethodContext.addSessionContext(sessionContext)
                                ));
            } else {
                // Link an normal sessionContext
                executionContextController.getCurrentSessionContext().ifPresent(currentSessionContext ->
                        executionContextController.getCurrentMethodContext().ifPresent(currentMethodContext ->
                                currentMethodContext.addSessionContext(currentSessionContext)
                        ));
            }
            return existingWebDriver;
        }

        /*
         **** STARTING NEW SESSION ****
         */

        /*
        decide which session manager to use
         */
        String browser = webDriverRequest.getBrowser();

        if (StringUtils.isBlank(browser)) {
            throw new SystemException(String.format("No browser configured. Please define one in %s.setBrowser() or property '%s'", WebDriverRequest.class.getSimpleName(),
                    IWebDriverManager.Properties.BROWSER_SETTING));
        }

        if (WEB_DRIVER_FACTORIES.containsKey(browser)) {
            WebDriverFactory webDriverFactory = WEB_DRIVER_FACTORIES.get(browser);

             /*
            create session context and link to method context
             */
            final WebDriverRequest finalWebDriverRequest = webDriverFactory.prepareWebDriverRequest(webDriverRequest);
            webDriverRequestConfigurators.forEach(handler -> handler.accept(finalWebDriverRequest));

            SessionContext sessionContext = new SessionContext(finalWebDriverRequest);
            executionContextController.getCurrentMethodContext().ifPresent(methodContext -> {
                methodContext.addSessionContext(sessionContext);
            });
            executionContextController.setCurrentSessionContext(sessionContext);

            logRequest(finalWebDriverRequest, sessionContext);

            /*
            setup new session
             */
            StopWatch sw = new StopWatch();
            sw.start();
            WebDriver newRawWebDriver = webDriverFactory.createWebDriver(finalWebDriverRequest, sessionContext);
            sw.stop();

            if (!sessionContext.getActualBrowserName().isPresent()) {
                BrowserInformation browserInformation = WebDriverManagerUtils.getBrowserInformation(newRawWebDriver);
                sessionContext.setActualBrowserName(browserInformation.getBrowserName());
                sessionContext.setActualBrowserVersion(browserInformation.getBrowserVersion());
            }

            if (newRawWebDriver instanceof RemoteWebDriver) {
                SessionId sessionId = ((RemoteWebDriver) newRawWebDriver).getSessionId();
                sessionContext.setRemoteSessionId(sessionId.toString());
            } else {
                sessionContext.setRemoteSessionId(sessionContext.getId());
            }

            LOGGER.info(String.format(
                    "Started %s (sessionKey=%s, node=%s, userAgent=%s) in %s",
                    newRawWebDriver.getClass().getSimpleName(),
                    sessionContext.getSessionKey(),
                    sessionContext.getNodeUrl().map(Object::toString).orElse("(unknown)"),
                    sessionContext.getActualBrowserName().orElse("(unknown)") + ":" + sessionContext.getActualBrowserVersion().orElse("(unknown)"),
                    sw
            ));
            EventFiringWebDriver eventFiringWebDriver = wrapWebDriver(newRawWebDriver, sessionContext);
            storeWebDriverSession(eventFiringWebDriver, sessionContext);

            webDriverFactory.setupNewWebDriverSession(eventFiringWebDriver, sessionContext);
            Testerra.getEventBus().post(new ContextUpdateEvent().setContext(sessionContext));
            String sessionIdentifier = createSessionIdentifier(newRawWebDriver, sessionKey);
            /*
            run the handlers
             */
            WEBDRIVER_STARTUP_HANDLERS.forEach(webDriverConsumer -> {
                try {
                    webDriverConsumer.accept(eventFiringWebDriver);
                } catch (Exception e) {
                    LOGGER.error("Failed executing handler after starting up " + sessionIdentifier, e);
                }
            });
            return eventFiringWebDriver;
        } else {
            throw new SystemException(String.format("No %s registered for browser '%s'", WebDriverFactory.class.getSimpleName(), browser));
        }
    }

    private static void logRequest(WebDriverRequest request, SessionContext sessionContext) {
        Map<String, Object> cleanedCapsMap = new DefaultCapabilityUtils().clean(request.getCapabilities());
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        LOGGER.info(String.format(
                "New %s (sessionKey=%s, server=%s) with capabilities:\n%s",
                request.getClass().getSimpleName(),
                sessionContext.getSessionKey(),
                (request.getServerUrl().isPresent() ? request.getServerUrl().get() : "local"),
                gson.toJson(cleanedCapsMap)
        ));
        LOGGER.debug(String.format("Starting (sessionKey=%s) here", sessionContext.getSessionKey()), new Throwable());
    }

    @Deprecated
    static void registerWebDriverFactory(WebDriverFactory webDriverFactory, String... browsers) {
        LOGGER.debug("Register " + webDriverFactory.getClass().getSimpleName() + " for browsers " + String.join(", ", browsers));

        for (String browser : browsers) {
            WEB_DRIVER_FACTORIES.put(browser, webDriverFactory);
        }
    }

    private static EventFiringWebDriver checkForWrappedWebDriver(WebDriver webDriver) {
        if (!(webDriver instanceof EventFiringWebDriver)) {
            throw new IllegalArgumentException(webDriver.getClass().getSimpleName() + " is no instance of " + EventFiringWebDriver.class.getSimpleName());
        }
        return (EventFiringWebDriver) webDriver;
    }

    public static boolean isExclusiveSession(WebDriver webDriver) {
        webDriver = checkForWrappedWebDriver(webDriver);
        return EXCLUSIVE_SESSION_KEY_WEBDRIVER_MAP.containsValue(webDriver);
    }

    public static Optional<SessionContext> getSessionContext(WebDriver webDriver) {
        webDriver = checkForWrappedWebDriver(webDriver);
        return Optional.ofNullable(WEBDRIVER_SESSIONS_CONTEXTS_MAP.get(webDriver));
    }

    public static Optional<EventFiringWebDriver> getWebDriver(SessionContext sessionContext) {
        return WEBDRIVER_SESSIONS_CONTEXTS_MAP.entrySet().stream()
                .filter(entry -> entry.getValue() == sessionContext)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public static Optional<String> getRequestedBrowser(WebDriver webDriver) {
        return getSessionContext(webDriver).map(SessionContext::getWebDriverRequest).map(WebDriverRequest::getBrowser);
    }

    public static WebDriverFactory getWebDriverFactory(String browser) {
        if (WEB_DRIVER_FACTORIES.containsKey(browser)) {
            return WEB_DRIVER_FACTORIES.get(browser);
        } else {
            throw new RuntimeException("No " + WebDriverFactory.class.getSimpleName() + " registered for browser: " + browser);
        }
    }

    public static Stream<SessionContext> readSessionContexts() {
        return WEBDRIVER_SESSIONS_CONTEXTS_MAP.values().stream();
    }

    public static Stream<EventFiringWebDriver> readWebDrivers() {
        return WEBDRIVER_SESSIONS_CONTEXTS_MAP.keySet().stream();
    }

    private static EventFiringWebDriver wrapWebDriver(WebDriver webDriver, SessionContext sessionContext) {
        /*
        wrap the driver with the proxy
         */
        /*
         * Watch out when wrapping the driver here. Any more wraps than EventFiringWebDriver will break at least
         * the MobileDriverAdapter. This is because we need to compare the lowermost implementation of WebDriver in this case.
         * It can be made more robust, if we always can retrieve the storedSessionId of the WebDriver, given a WebDriver object.
         * For more info, please ask @rnhb
         */
        try {
            WebDriverProxy webDriverProxy = new WebDriverProxy(webDriver, sessionContext);
            Class[] interfaces = ObjectUtils.getAllInterfacesOf(webDriver);
            webDriver = ObjectUtils.simpleProxy(WebDriver.class, webDriverProxy, interfaces);
        } catch (Exception e) {
            LOGGER.error("Could not create proxy for raw webdriver", e);
        }

        return new EventFiringWebDriver(webDriver);
    }
}

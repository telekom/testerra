/*
 * Created on 19.09.2019
 */
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.execution.worker.finish.WebDriverSessionHandler;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebDriverKeepAliveSequence
 * <p>
 * Date: 19.09.2019
 * Time: 12:50
 *
 * @author Eric Kubenka <Eric.Kubenka@t-systems.com>
 */
public class WebDriverKeepAliveSequence extends Timer.Sequence<WebDriverKeepAliveSequence.KeepAliveState> implements Loggable {

    private static CopyOnWriteArrayList<WebDriverKeepAliveSequence> currentKeepAliveSessions = new CopyOnWriteArrayList<>();

    static final int GLOBAL_KEEP_ALIVE_TIMEOUT_IN_SECONDS = 3600;

    /**
     * State list.
     */
    public enum KeepAliveState {
        REMOVED_BY_USER,
        REMOVED_BY_TIMEOUT,
        REMOVED_BY_DRIVER_SHUTDOWN
    }

    private boolean remove = false;
    private boolean forceRemove = false;

    private final WebDriver driver;

    /**
     * Initialize an empty {@link WebDriverKeepAliveSequence} - This will not trigger any action.
     * Just a helper to avoid static remove-methods.
     */
    WebDriverKeepAliveSequence() {
        driver = null;
    }

    /**
     * Initialize a WebDriverKeepAliveSequence that can be used by {@link Timer#executeSequence(Timer.Sequence)}
     *
     * @param driver {@link WebDriver}
     */
    WebDriverKeepAliveSequence(final WebDriver driver) {

        // store driver
        this.driver = driver;

        final WebDriver webDriverForUseInSequence = driver;

        // add ShutDownHandler that kills this Sequence.
        WebDriverSessionsManager.registerWebDriverShutDownHandler(new WebDriverSessionHandler() {
            @Override
            public void run(WebDriver driver) {
                if (webDriverForUseInSequence == driver) {
                    forceRemove = true;
                }
            }
        });

        // add to active sessions
        currentKeepAliveSessions.add(this);
        log().info(String.format("Initialized a WebDriverKeepAliveSequence for driver with session id %s and session key %s.",
                WebDriverUtils.getSessionId(this.driver),
                WebDriverManagerUtils.getSessionKey(this.driver)));
    }

    /**
     * Removes active {@link WebDriverKeepAliveSequence} if present
     *
     * @param driver {@link WebDriver}
     */
    void removeKeepAliveForDriver(final WebDriver driver) {

        currentKeepAliveSessions
                .stream()
                .filter(webDriverKeepAliveSequence -> webDriverKeepAliveSequence.driver == driver)
                .findAny()
                .ifPresent(webDriverKeepAliveSequence -> webDriverKeepAliveSequence.remove = true);
    }

    @Override
    public void run() {

        if (remove) {

            setPassState(true);
            setReturningObject(KeepAliveState.REMOVED_BY_USER);
            log().info("WebDriverKeepAliveSequence canceled by user.");
            currentKeepAliveSessions.remove(this);
            return;
        }

        if (forceRemove) {

            setPassState(true);
            setReturningObject(KeepAliveState.REMOVED_BY_DRIVER_SHUTDOWN);
            log().info("WebDriverKeepAliveSequence canceled because driver shut down.");
            currentKeepAliveSessions.remove(this);
            return;
        }

        driver.getTitle();
        setPassState(false);
        setReturningObject(KeepAliveState.REMOVED_BY_TIMEOUT);
    }
}

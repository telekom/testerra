package eu.tsystems.mms.tic.testframework.webdrivermanager;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

public interface WebDriverRequest extends Cloneable {
    String DEFAULT_SESSION_KEY = "default";
    String getSessionKey();
    String getBrowser();
    String getBrowserVersion();
    Map<String, Object> getCapabilities();

    boolean getShutdownAfterTest();
    boolean getShutdownAfterTestFailed();
    boolean getShutdownAfterExecution();
    void setShutdownAfterTest(boolean shutdownAfterTest);
    void setShutdownAfterTestFailed(boolean shutdownAfterTestFailed);
    void setShutdownAfterExecution(boolean shutdownAfterExecution);

    Optional<URL> getServerUrl();
    WebDriverRequest clone() throws CloneNotSupportedException;
}

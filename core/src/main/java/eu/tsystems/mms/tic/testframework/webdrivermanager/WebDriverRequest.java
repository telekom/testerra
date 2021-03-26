package eu.tsystems.mms.tic.testframework.webdrivermanager;

import java.io.Serializable;
import java.util.Map;

public interface WebDriverRequest extends Serializable {
    String DEFAULT_SESSION_KEY = "default";
    String getSessionKey();
    String getBrowser();
    String getBrowserVersion();
    boolean getShutdownAfterTest();
    boolean getShutdownAfterTestFailed();
    boolean getShutdownAfterExecution();
    Map<String, Object> getCapabilities();
}

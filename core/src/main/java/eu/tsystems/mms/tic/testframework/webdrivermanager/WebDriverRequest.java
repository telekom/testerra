package eu.tsystems.mms.tic.testframework.webdrivermanager;

import java.io.Serializable;

public interface WebDriverRequest extends Serializable {
    String DEFAULT_SESSION_KEY = "default";
    String getSessionKey();
    String getBrowser();
    String getBrowserVersion();
}

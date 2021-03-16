package eu.tsystems.mms.tic.testframework.webdrivermanager;

import java.io.Serializable;

public interface WebDriverRequest extends Serializable {
    String getSessionKey();
    String getBrowser();
    String getBrowserVersion();
}

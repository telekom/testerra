package eu.tsystems.mms.tic.testframework.webdrivermanager;

import java.io.Serializable;
import java.net.URL;
import java.util.Optional;

public interface WebDriverRequest extends Serializable {
    String getSessionKey();
    String getBrowser();
    String getBrowserVersion();
    Optional<URL> getServerUrl();
    WebDriverRequest clone();
}

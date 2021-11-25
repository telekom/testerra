package eu.tsystems.mms.tic.testframework.webdrivermanager;

import java.net.URL;
import java.util.Optional;

public interface WebDriverRequest extends Cloneable {
    String getSessionKey();
    String getBrowser();
    String getBrowserVersion();
    Optional<URL> getServerUrl();
    WebDriverRequest clone() throws CloneNotSupportedException;
}

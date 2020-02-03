package eu.tsystems.mms.tic.testframework.testing;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverManager;

/**
 * Interface for injecting the a {@link IWebDriverManager}
 * @author Mike Reiche
 */
public interface WebDriverManagerProvider {
    IWebDriverManager webDriverManager = Testerra.injector.getInstance(IWebDriverManager.class);
}

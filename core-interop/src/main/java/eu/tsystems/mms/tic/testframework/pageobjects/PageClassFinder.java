package eu.tsystems.mms.tic.testframework.pageobjects;

import org.openqa.selenium.WebDriver;

public interface PageClassFinder {
    <T extends PageObject> Class<T> getBestMatchingClass(Class<T> baseClass, WebDriver driver, String prefix);
    void clearCache();
}

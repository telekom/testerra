package io.testerra.report.test.pages.pretest;

import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import org.openqa.selenium.WebDriver;

public class UniversalPage extends Page {
    public UniversalPage(WebDriver webDriver) {
        super(webDriver);
    }

    public UiElementFinder getFinder() {
        return super.getFinder();
    }
}

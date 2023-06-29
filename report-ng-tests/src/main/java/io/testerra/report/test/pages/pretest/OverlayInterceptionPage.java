package io.testerra.report.test.pages.pretest;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OverlayInterceptionPage extends Page {

    @Check
    private UiElement button = find(By.id("btn"));

    public OverlayInterceptionPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void clickButton() {
        button.click();
    }
}

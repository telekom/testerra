package io.testerra.report.test.pages.pretest;

import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created on 2022-09-08
 *
 * @author mgn
 */
public class NonExistingPage extends Page {
    private UiElement nonExisting = find(By.id("foo"));

    public NonExistingPage(WebDriver webDriver) {
        super(webDriver);
    }
}

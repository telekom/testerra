package io.testerra.report.test.pages.pretest.CheckPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PrioritizedErrorMessageCheckPage extends Page {

    public PrioritizedErrorMessageCheckPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Check(prioritizedErrorMessage = "Custom error message - pretest")
    final UiElement prioritizedErrorMessageCheckedUiElement = find(By.xpath("//non_existing_element"));
}

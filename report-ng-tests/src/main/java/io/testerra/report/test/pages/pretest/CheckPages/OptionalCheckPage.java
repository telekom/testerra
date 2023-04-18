package io.testerra.report.test.pages.pretest.CheckPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OptionalCheckPage extends Page {

    public OptionalCheckPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Check(optional = true)
    final UiElement optionalCheckedUiElement = find(By.xpath("//non_existing_element"));
}

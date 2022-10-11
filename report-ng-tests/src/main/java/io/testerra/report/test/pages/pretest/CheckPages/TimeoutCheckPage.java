package io.testerra.report.test.pages.pretest.CheckPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TimeoutCheckPage extends Page {

    public TimeoutCheckPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Check(timeout = 20)
    final UiElement timeoutCheckedUiElement = find(By.xpath("//div[@class='box'][text()='new text node']"));
}

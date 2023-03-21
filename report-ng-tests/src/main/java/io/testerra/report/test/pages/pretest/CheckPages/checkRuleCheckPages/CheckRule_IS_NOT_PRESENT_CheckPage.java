package io.testerra.report.test.pages.pretest.CheckPages.checkRuleCheckPages;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckRule_IS_NOT_PRESENT_CheckPage extends Page {
    public CheckRule_IS_NOT_PRESENT_CheckPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Check(checkRule = CheckRule.IS_NOT_PRESENT)
    final UiElement checkRuleUiElement = find(By.xpath("//div//h1"));
}

package io.testerra.report.test.pages.pretest.CheckPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CollectCheckPage extends Page {

    public CollectCheckPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Check(collect = true)
    final UiElement collectCheckedUiElement_01 = find(By.xpath("//non_existing_element_01"));

    @Check(collect = true)
    final UiElement collectCheckedUiElement_02 = find(By.xpath("//non_existing_element_02"));

    @Check(collect = true)
    final UiElement collectCheckedUiElement_03 = find(By.xpath("//body"));
}

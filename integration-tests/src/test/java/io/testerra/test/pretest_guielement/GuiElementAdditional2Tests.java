package io.testerra.test.pretest_guielement;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created on 08.03.2022
 *
 * @author mgn
 */
public class GuiElementAdditional2Tests extends AbstractTestSitesTest {

    @Test
    public void testGuiElement_Create_SensibleData() {

        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement input = new GuiElement(driver, By.id("1")).sensibleData();

        Assert.assertTrue(input.isDisplayed());
        Assert.assertTrue(input.hasSensibleData());

        input.type("testT02_SensibleData");
        // pageobjects.GuiElement - type "*****************" on By.id: 1
//        LogAssertUtils.assertEntryInLogFile("pageobjects.GuiElement - type \"*****************\" on By.id: 1");
//        LogAssertUtils.assertEntryNotInLogFile("pageobjects.GuiElement - type \"testT02_SensibleData\" on By.id: 1");
    }

}

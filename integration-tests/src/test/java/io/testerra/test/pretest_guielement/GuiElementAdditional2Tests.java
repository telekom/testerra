package io.testerra.test.pretest_guielement;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
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
    public void testGuiElement_Create_SensitiveData() {

        final WebDriver driver = getWebDriver();
        GuiElement input = new GuiElement(driver, By.id("1")).sensitiveData();
        input.assertThat().displayed().is(true);
        Assert.assertTrue(input.hasSensitiveData());

        input.type("testT02_SensitiveData");
        input.sendKeys("testT02_SensitiveDataSendKeys");

        // Check that the sensitiveData flag is inherited by list()
        GuiElement listItem = (GuiElement) input.list().first();
        Assert.assertTrue(listItem.hasSensitiveData());
        listItem.type("testT02_SensitiveDataByList");
    }

}

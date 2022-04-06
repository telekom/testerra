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
    public void testGuiElement_Create_SensibleData() {

        final WebDriver driver = getWebDriver();
        GuiElement input = new GuiElement(driver, By.id("1")).sensibleData();
        input.assertThat().displayed().is(true);
        Assert.assertTrue(input.hasSensibleData());

        input.type("testT02_SensibleData");
    }

}

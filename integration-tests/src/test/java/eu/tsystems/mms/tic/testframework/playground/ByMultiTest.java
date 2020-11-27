package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.location.ByMulti;
import eu.tsystems.mms.tic.testframework.pageobjects.location.MultiDimSelector;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class ByMultiTest extends AbstractTestSitesTest {

    private WebDriver driver;

    private final String xpath = "//h1[contains(text(), 'Cross Frame Drag and Drop Example')]";
    private Rectangle rect;

    @Override
    protected TestPage getTestPage() {
        return TestPage.DRAG_AND_DROP_OVER_FRAMES;
    }

    @BeforeMethod
    public void init() {
        driver = WebDriverManager.getWebDriver();

        WebElement webElement = driver.findElement(By.xpath(xpath));
        rect = webElement.getRect();
    }

    @Test
    public void testT01_CorrectXpath() {
        String selector = ByMulti.serialize(new MultiDimSelector(xpath, rect));

        GuiElement guiElement = new GuiElement(driver, new ByMulti(driver, selector));
        Assert.assertTrue(guiElement.isDisplayed());
        Assert.assertEquals(guiElement.getText(), "Cross Frame Drag and Drop Example");
    }

    @Test
    public void testT01_WrongXpath() {
        String wrongXpath = "wrong_xpath";
        String wrongSelector = ByMulti.serialize(new MultiDimSelector(wrongXpath, rect));

        GuiElement guiElement = new GuiElement(driver, new ByMulti(driver, wrongSelector));
        Assert.assertTrue(guiElement.isDisplayed());
        Assert.assertEquals(guiElement.getText(), "Cross Frame Drag and Drop Example");
    }
}

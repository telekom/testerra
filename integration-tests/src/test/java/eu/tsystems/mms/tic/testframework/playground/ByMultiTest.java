package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.location.ByMulti;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;


public class ByMultiTest extends AbstractTestSitesTest {

    private WebDriver driver;

    private JSONObject json;

    @Override
    protected TestPage getTestPage() {
        return TestPage.DRAG_AND_DROP_OVER_FRAMES;
    }

    @BeforeMethod
    public void init() {
        driver = WebDriverManager.getWebDriver();

        String xpath = "//h1[contains(text(), 'Cross Frame Drag and Drop Example')]";

        WebElement webElement = driver.findElement(By.xpath(xpath));

        Rectangle rect = webElement.getRect();

        json = new JSONObject();
        json.put("xpath", xpath);
        json.put("bb", Arrays.asList(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
        System.out.println(json.toString());
    }

    @Test
    public void testT00_Compression() {
        String selector = ByMulti.serialize(json);

        System.out.println("The selector:\n" + selector);

        JSONObject json2 = ByMulti.deserialize(selector);
        Assert.assertEquals(json.getString("xpath"), json2.getString("xpath"));
    }

    @Test
    public void testT01_CorrectXpath() {
        String selector = ByMulti.serialize(json);

        GuiElement guiElement = new GuiElement(driver, new ByMulti(selector));
        Assert.assertTrue(guiElement.isDisplayed());
        Assert.assertEquals(guiElement.getText(), "Cross Frame Drag and Drop Example");
    }
}

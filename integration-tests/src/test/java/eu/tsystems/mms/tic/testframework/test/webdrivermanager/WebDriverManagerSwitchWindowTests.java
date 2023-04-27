package eu.tsystems.mms.tic.testframework.test.webdrivermanager;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.exceptions.UiElementAssertionError;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.UiElementFinderFactoryProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

/**
 * Created on 2023-01-31
 *
 * @author mgn
 */
public class WebDriverManagerSwitchWindowTests extends AbstractTestSitesTest implements UiElementFinderFactoryProvider, AssertProvider {

    protected TestPage getTestPage() {
        return TestPage.SWITCH_WINDOW;
    }

    @Test
    public void testT01_SwitchToWindowWithTitle() {
        WebDriver webDriver = this.getWebDriver();
        UiElement link1 = getFinder(webDriver).find(By.id("link1"));
        UiElement header2 = getFinder(webDriver).find(By.xpath("//h1[text() = 'Window 2']"));
        final String handle1 = webDriver.getWindowHandle();

        link1.click();
        WEB_DRIVER_MANAGER.switchToWindowTitle("Window 2");
        header2.assertThat().present(true);
        ASSERT.assertNotEquals(webDriver.getWindowHandle(), handle1);
    }

    @Test
    public void testT02_SwitchToWindowWithUrl() {
        WebDriver webDriver = this.getWebDriver();
        UiElement link1 = getFinder(webDriver).find(By.id("link1"));
        UiElement header2 = getFinder(webDriver).find(By.xpath("//h1[text() = 'Window 2']"));

        link1.click();
        boolean result = WEB_DRIVER_MANAGER.switchToWindow(driver -> driver.getCurrentUrl().contains("window2.html"));
        ASSERT.assertTrue(result);
        header2.assertThat().present(true);
    }

    @Test
    public void testT03_SwitchToSameWindow() {
        WebDriver webDriver = this.getWebDriver();
        UiElement link1 = getFinder(webDriver).find(By.id("link1"));
        final String handle1 = webDriver.getWindowHandle();

        link1.click();
        WEB_DRIVER_MANAGER.switchToWindowTitle("Window 1");
        link1.assertThat().present(true);
        ASSERT.assertEquals(webDriver.getWindowHandle(), handle1);
    }

    @Test
    public void testT04_UseElementsWithoutSwitch() {
        WebDriver webDriver = this.getWebDriver();
        UiElement link1 = getFinder(webDriver).find(By.id("link1"));
        UiElement link2 = getFinder(webDriver).find(By.id("link2"));
        final String handle1 = webDriver.getWindowHandle();

        link1.click();
        link2.click();
        ASSERT.assertEquals(webDriver.getWindowHandle(), handle1);
        ASSERT.assertEquals(webDriver.getWindowHandles().size(), 3);
    }

    @Test
    public void testT05_DelayedNewWindow() {
        WebDriver webDriver = this.getWebDriver();
        UiElement link3 = getFinder(webDriver).find(By.id("link3"));
        UiElement header2 = getFinder(webDriver).find(By.xpath("//h1[text() = 'Window 2']"));
        final String handle1 = webDriver.getWindowHandle();
        link3.click();

        CONTROL.waitFor(6, () -> WEB_DRIVER_MANAGER.switchToWindowTitle(webDriver, "Window 2"));
        header2.assertThat().present(true);
        ASSERT.assertNotEquals(webDriver.getWindowHandle(), handle1);
    }

    @Test
    public void testT06_DelayedNewWindowWithPredicate() {
        WebDriver webDriver = this.getWebDriver();
        UiElement link3 = getFinder(webDriver).find(By.id("link3"));
        UiElement header2 = getFinder(webDriver).find(By.xpath("//h1[text() = 'Window 2']"));
        final String handle1 = webDriver.getWindowHandle();
        link3.click();

        CONTROL.waitFor(6, () -> {
            boolean result = WEB_DRIVER_MANAGER.switchToWindow(webDriver, driver -> driver.getTitle().contains("Window 2"));
            ASSERT.assertTrue(result);
        });
        header2.assertThat().present(true);
        ASSERT.assertNotEquals(webDriver.getWindowHandle(), handle1);
    }

    @Test(
            expectedExceptions = UiElementAssertionError.class,
            expectedExceptionsMessageRegExp = "Expected that UiElement\\(By.xpath: //h1\\[text\\(\\) = 'Window 2'\\]\\) present is true"
    )
    public void testT10_AccessElementWithoutSwitch() {
        WebDriver webDriver = this.getWebDriver();
        UiElement link1 = getFinder(webDriver).find(By.id("link1"));
        UiElement header2 = getFinder(webDriver).find(By.xpath("//h1[text() = 'Window 2']"));

        link1.click();
        header2.assertThat().present(true);
    }

    @Test(
            expectedExceptions = RuntimeException.class,
            expectedExceptionsMessageRegExp = "Window title \"Wrong title\" not found"
    )
    public void testT11_SwitchToWindowWithWrongTitle() {
        WebDriver webDriver = this.getWebDriver();
        UiElement link1 = getFinder(webDriver).find(By.id("link1"));

        link1.click();
        WEB_DRIVER_MANAGER.switchToWindowTitle("Wrong title");
    }

    @Test
    public void testT12_SwitchToWindowWithWrongTitlePredicate() {
        WebDriver webDriver = this.getWebDriver();
        UiElement link1 = getFinder(webDriver).find(By.id("link1"));
        UiElement header1 = getFinder(webDriver).find(By.xpath("//h1[text() = 'Window 1']"));
        final String handle1 = webDriver.getWindowHandle();

        link1.click();
        boolean result = WEB_DRIVER_MANAGER.switchToWindow(webDriver, driver -> driver.getTitle().contains("Wrong title"));
        ASSERT.assertFalse(result);
        header1.assertThat().present(true);
        ASSERT.assertEquals(webDriver.getWindowHandle(), handle1);
    }

    private UiElementFinder getFinder(WebDriver driver) {
        return UI_ELEMENT_FINDER_FACTORY.create(driver);
    }

}

/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.IGuiElement.variations;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.core.test.TestPageObject;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * Test of IGuiElement methods
 *
 * @author rnhb
 */
public abstract class AbstractGuiElementTest extends AbstractTestSitesTest {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    static {
        POConfig.setUiElementTimeoutInSeconds(3);
        PropertyManager.getGlobalProperties().setProperty(TesterraProperties.CLOSE_WINDOWS_AFTER_TEST_METHODS, "false");
    }

    @AfterTest(alwaysRun = true)
    public void resetWDCloseWindowsMode() {
        PropertyManager.getGlobalProperties().setProperty(TesterraProperties.CLOSE_WINDOWS_AFTER_TEST_METHODS, "true");
    }

    /**
     * testpage of the website
     */
    protected TestPage testPage;

    /**
     * Initialize the WebDriver as local and with a testPage
     */
    @BeforeMethod(alwaysRun = true)
    protected void initDriverAndWebsite(Method method) {
        testPage = getTestPage();
        WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(testPage.getUrl());
    }

    @AfterSuite(alwaysRun = true)
    private void closeBrowsers() {
        WebDriverManager.forceShutdownAllThreads();
    }

    /**
     * abstract methods to get specific IGuiElement (e.g. FrameAwareInternalGuiElementDecorator) in a specific state
     * (e.g. present, not present)
     */
    public IGuiElement getAnyElement() {
        return getAnyElementByXpath();
    }

    public IGuiElement getNotExistingElement() {
        return getGuiElementBy(By.id("notExistingId"));
    }

    public IGuiElement getAnyElementByXpath() {
        return getGuiElementBy(By.xpath("//*[@id='11']"));
    }

    public IGuiElement getAnyElementByClassName() {
        return getGuiElementBy(By.className("className"));
    }

    public IGuiElement getAnyElementByLinkText() {
        return getGuiElementBy(By.linkText("Open again"));
    }

    public IGuiElement getAnyElementByPartialLinkText() {
        return getGuiElementBy(By.partialLinkText("Open"));
    }

    public IGuiElement getAnyElementByName() {
        return getGuiElementBy(By.name("radioBtn"));
    }

    public IGuiElement getDisplayedElement() {
        return getGuiElementBy(By.id("11"));
    }

    public IGuiElement getNotDisplayedElement() {
        return getGuiElementBy(By.id("notDisplayedElement"));
    }

    public IGuiElement getSelectableElement() {
        return getGuiElementBy(By.id("3"));
    }

    public IGuiElement getNotSelectableElement() {
        return getGuiElementBy(By.id("11"));
    }

    public IGuiElement getClickableElement() {
        return getGuiElementBy(By.xpath("//input[@type='submit']"));
    }

    public IGuiElement getEnabledElement() {
        return getGuiElementBy(By.id("16"));
    }

    public IGuiElement getElementWithText() {
        return getGuiElementBy(By.id("11"));
    }

    public IGuiElement getElementWithAttribute() {
        return getGuiElementBy(By.id("6"));
    }

    public IGuiElement getParent1() {
        return getGuiElementBy(By.xpath("//div[1]"));
    }

    public IGuiElement getParent2() {
        return getGuiElementBy(By.xpath("//div[2]"));
    }

    public IGuiElement getDisabledElement() {
        return getGuiElementBy(By.id("7"));
    }

    public IGuiElement getTextBoxElement() {
        return getGuiElementBy(By.id("5"));
    }

    public IGuiElement getLoggerTableElement() {
        return getGuiElementBy(By.id("99"));
    }

    public IGuiElement getTableElement() {
        return getGuiElementBy(By.id("100"));
    }

    public IGuiElement getMultiSelect() {
        return getGuiElementBy(By.xpath("//select[1]"));
    }

    public IGuiElement getSingleSelect() {
        return getGuiElementBy(By.xpath("//select[2]"));
    }

    public IGuiElement getTimeOutInput() {
        return getGuiElementBy(By.id("inputMillis"));
    }

    public IGuiElement getShowWithTimeOutButton() {
        return getGuiElementBy(By.id("showText"));
    }

    public IGuiElement getHideWithTimeOutButton() {
        return getGuiElementBy(By.id("hideText"));
    }

    public IGuiElement getChangeTextByJSButton() {
        return getGuiElementBy(By.id("changeText"));
    }

    public IGuiElement getInsertTextByJSButton() {
        return getGuiElementBy(By.id("insertText"));
    }

    public IGuiElement getDynamicTextElement() {
        return getGuiElementBy(By.id("switch"));
    }

    public IGuiElement getInsertedTextElement() {
        return getGuiElementBy(By.id("dynText"));
    }

    public IGuiElement getEnableRDButton() {
        return getGuiElementBy(By.id("enableRdButton"));
    }

    public IGuiElement getTimeOutDIV() {
        return getGuiElementBy(By.id("waiterDIV"));
    }

    public IGuiElement getAddAttributeWithTimeOutButton() {
        return getGuiElementBy(By.id("addAttributeToRDButton"));
    }

    public IGuiElement getDisableRDButton() {
        return getGuiElementBy(By.id("disableRDButton"));
    }

    public IGuiElement getRadio() {
        return getGuiElementBy(By.id("enabledSwitchRDButton"));
    }

    public IGuiElement getCheckBox() {
        return getGuiElementBy(By.id("9"));
    }

    public IGuiElement getSelectRadioButtonMitVerzoegerungButton() {
        return getGuiElementBy(By.id("SelectRDButton"));
    }

    public IGuiElement getDeselectRadioButtonMitVerzoegerungButton() {
        return getGuiElementBy(By.id("DeselectRDButton"));
    }

    public abstract IGuiElement getGuiElementBy(By locator);

    public TestPageObject getPageObject() {
        return new TestPageObject(WebDriverManager.getWebDriver());
    }

    protected abstract TestPage getTestPage();

}

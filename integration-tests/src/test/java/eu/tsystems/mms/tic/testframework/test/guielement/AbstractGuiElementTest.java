/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package eu.tsystems.mms.tic.testframework.test.guielement;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.BasePage;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locator;
import eu.tsystems.mms.tic.testframework.pageobjects.LocatorFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.UiElementFinderFactoryProvider;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverUtils;
import org.openqa.selenium.By;

/**
 * Test of GuiElement methods
 *
 * @author rnhb
 */
public abstract class AbstractGuiElementTest extends AbstractTestSitesTest implements UiElementFinderFactoryProvider, LocatorFactoryProvider, Loggable {

    protected static final DesktopWebDriverUtils desktopWebDriverUtils = new DesktopWebDriverUtils();

    /**
     * abstract methods to get specific GuiElement (e.g. FrameAwareInternalGuiElementDecorator) in a specific state
     * (e.g. present, not present)
     */
    public GuiElement getAnyElement() {
        return getAnyElementByXpath();
    }

    public GuiElement getNotExistingElement() {
        return getGuiElementBy(By.id("notExistingId"));
    }

    public GuiElement getAnyElementByXpath() {
        return getGuiElementBy(By.xpath("//*[@id='11']"));
    }

    public GuiElement getAnyElementByClassName() {
        return getGuiElementBy(By.className("className"));
    }

    public GuiElement getAnyElementByLinkText() {
        return getGuiElementBy(By.linkText("Open again"));
    }

    public GuiElement getAnyElementByPartialLinkText() {
        return getGuiElementBy(By.partialLinkText("Open"));
    }

    public GuiElement getAnyElementByName() {
        return getGuiElementBy(By.name("radioBtn"));
    }

    public GuiElement getDisplayedElement() {
        return getGuiElementBy(By.id("11"));
    }

    public GuiElement getNotDisplayedElement() {
        return getGuiElementBy(By.id("notDisplayedElement"));
    }

    public GuiElement getSelectableElement() {
        return getGuiElementBy(By.id("3"));
    }

    public GuiElement getNotSelectableElement() {
        return getGuiElementBy(By.id("11"));
    }

    public GuiElement getClickableElement() {
        return getGuiElementBy(By.xpath("//input[@type='submit']"));
    }

    public GuiElement getEnabledElement() {
        return getGuiElementBy(By.id("16"));
    }

    public GuiElement getElementWithText() {
        return getGuiElementBy(By.id("11"));
    }

    public GuiElement getElementWithAttribute() {
        return getGuiElementBy(By.id("6"));
    }

    public GuiElement getParent1() {
        return getGuiElementBy(By.xpath("//div[1]"));
    }

    public GuiElement getParent2() {
        return getGuiElementBy(By.xpath("//div[2]"));
    }

    public GuiElement getDisabledElement() {
        return getGuiElementBy(By.id("7"));
    }

    public GuiElement getTextBoxElement() {
        return getGuiElementBy(By.id("5"));
    }

    public GuiElement getLoggerTableElement() {
        return getGuiElementBy(By.id("99"));
    }

    public GuiElement getTableElement() {
        return getGuiElementBy(By.id("100"));
    }

    public GuiElement getMultiSelect() {
        return getGuiElementBy(By.xpath("//select[1]"));
    }

    public GuiElement getSingleSelect() {
        return getGuiElementBy(By.xpath("//select[2]"));
    }

    public GuiElement getTimeOutInput() {
        return getGuiElementBy(By.id("inputMillis"));
    }

    public GuiElement getShowWithTimeOutButton() {
        return getGuiElementBy(By.id("showText"));
    }

    public GuiElement getHideWithTimeOutButton() {
        return getGuiElementBy(By.id("hideText"));
    }

    public GuiElement getChangeTextByJSButton() {
        return getGuiElementBy(By.id("changeText"));
    }

    public GuiElement getInsertTextByJSButton() {
        return getGuiElementBy(By.id("insertText"));
    }

    public GuiElement getDynamicTextElement() {
        return getGuiElementBy(By.id("switch"));
    }

    public GuiElement getInsertedTextElement() {
        return getGuiElementBy(By.id("dynText"));
    }

    public GuiElement getEnableRDButton() {
        return getGuiElementBy(By.id("enableRdButton"));
    }

    public GuiElement getTimeOutDIV() {
        return getGuiElementBy(By.id("waiterDIV"));
    }

    public GuiElement getAddAttributeWithTimeOutButton() {
        return getGuiElementBy(By.id("addAttributeToRDButton"));
    }

    public GuiElement getDisableRDButton() {
        return getGuiElementBy(By.id("disableRDButton"));
    }

    public GuiElement getRadio() {
        return getGuiElementBy(By.id("enabledSwitchRDButton"));
    }

    public GuiElement getCheckBox() {
        return getGuiElementBy(By.id("9"));
    }

    public GuiElement getSelectRadioButtonMitVerzoegerungButton() {
        return getGuiElementBy(By.id("SelectRDButton"));
    }

    public GuiElement getDeselectRadioButtonMitVerzoegerungButton() {
        return getGuiElementBy(By.id("DeselectRDButton"));
    }

    /**
     * @deprecated Use {@link BasePage#getFinder()}} instead
     */
    @Deprecated
    public GuiElement getGuiElementBy(By locator) {
        return getGuiElementBy(LOCATE.by(locator));
    }

    @Deprecated
    public abstract GuiElement getGuiElementBy(Locator locator);
}

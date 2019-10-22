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
/*
 * Created on 04.01.2013
 *
 * Copyright(c) 2012 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.logging.LogLevel;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.GuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.GuiElementCoreFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.GuiElementWaitFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AssertableBinaryValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AssertableQuantifiedValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AssertableValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertDescriptionDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableBinaryValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableQuantifiedValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.DelayActionsGuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacadeLoggingDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFace;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.StandardGuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GuiElement is the access point for most tests and is an extension of WebElement.
 * <p>
 * Authors: pele, rnhb
 */
public class GuiElement implements IGuiElement {
    /**
     * logger. What a surprise. Glad this JavaDoc is here.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiElement.class);

    private boolean forcedStandardAsserts = false;

    @Deprecated
    public GuiElementAssert nonFunctionalAssert;

    private GuiElementAssert functionalAssert;

    private GuiElementAssert functionalStandardAssert;
    private GuiElementAssert functionalAssertCollector;
    private GuiElementAssert nonFunctionalAssertReplacement;
    /**
     * Facade for all parts of the lowest GuiElement
     */
    private GuiElementFacade guiElementFacade;

    private GuiElementCore guiElementCore;
    private GuiElementWait guiElementWait;
    private Locate locator;
    private final GuiElementData guiElementData;

    private GuiElement(GuiElementData guiElementData, int index) {
        this.guiElementData = guiElementData.copy();
        this.guiElementData.index = index;
        if (!StringUtils.isEmpty(guiElementData.name)) {
            this.guiElementData.name = guiElementData.name + "_" + index;
        }
        buildInternals(guiElementData.webDriver, guiElementData.by);

    }

    /**
     * Constructor with explicit web driver session.
     *
     * @param driver Actual driver object.
     * @param by     By locator.
     */
    public GuiElement(final WebDriver driver, final By by) {
        this(driver, by, null);
    }

    public GuiElement(final WebDriver driver, final Locate locator) {
        this(driver, locator, null);
    }

    /**
     * Constructor with frames and explicit webDriver session.
     *
     * @param locator     By locator.
     * @param frames frames containing the element
     * @param driver Driver Object.
     */
    public GuiElement(
        final WebDriver driver,
        final Locate locator,
        final IGuiElement... frames
    ) {
        this(driver, locator.getBy(), frames);
        this.locator = locator;
    }

    public GuiElement(
        final WebDriver driver,
        final By by,
        final IGuiElement... frames
    ) {
        FrameLogic frameLogic = null;
        if (frames != null && frames.length > 0) {
            frameLogic = new FrameLogic(driver, frames);
        }
        guiElementData = new GuiElementData(driver, "", frameLogic, by, this);
        buildInternals(driver, by);
        this.locator = Locate.by(by);
    }

    public Locate getLocator() {
        return locator;
    }

    private void buildInternals(WebDriver driver, By by) {
        // Create core depending on requested Browser
        WebDriverRequest webDriverRequest = WebDriverManager.getRelatedWebDriverRequest(driver);
        String currentBrowser = webDriverRequest.browser;
        guiElementData.browser = currentBrowser;

        GuiElementCoreFactory coreFactory = TesterraCommons.ioc().getInstance(GuiElementCoreFactory.class);
        guiElementCore = coreFactory.create(currentBrowser, by, driver, guiElementData, null);

        GuiElementWaitFactory waitFactory = TesterraCommons.ioc().getInstance(GuiElementWaitFactory.class);
        guiElementWait = waitFactory.create(guiElementCore, this.guiElementData);

        GuiElementAssertFactory assertFactory = TesterraCommons.ioc().getInstance(GuiElementAssertFactory.class);
        functionalStandardAssert = assertFactory.create(true, false, guiElementCore, guiElementWait, this.guiElementData);
        functionalAssertCollector = assertFactory.create(true, true, guiElementCore, guiElementWait, this.guiElementData);
        nonFunctionalAssert = assertFactory.create(false, false, guiElementCore, guiElementWait, this.guiElementData);
        nonFunctionalAssertReplacement = nonFunctionalAssert;

        initDefaultAssert();
    }

    private void initDefaultAssert() {
        if (!forcedStandardAsserts && Flags.GUIELEMENT_DEFAULT_ASSERT_IS_COLLECTOR) {
            functionalAssert = functionalAssertCollector;
        } else {
            functionalAssert = functionalStandardAssert;
        }

        guiElementFacade = getFacade(guiElementCore, guiElementWait, functionalAssert);
    }

    public void forceStandardAsserts() {
        forcedStandardAsserts = true;
        initDefaultAssert();
    }

    public void resetDefaultAsserts() {
        forcedStandardAsserts = false;
        initDefaultAssert();
    }

    @Deprecated
    private GuiElementFacade getFacade(GuiElementCore guiElementCore, GuiElementWait guiElementWait, GuiElementAssert guiElementAssert) {
        GuiElementFacade guiElementFacade;
        guiElementFacade = new StandardGuiElementFacade(guiElementCore, guiElementWait, guiElementAssert);
        guiElementFacade = new GuiElementFacadeLoggingDecorator(guiElementFacade, guiElementData);
        guiElementFacade = new GuiElementFace(guiElementFacade, guiElementData);

        int delayAfterAction = PropertyManager.getIntProperty(TesterraProperties.DELAY_AFTER_GUIELEMENT_ACTION_MILLIS);
        int delayBeforeAction = PropertyManager.getIntProperty(TesterraProperties.DELAY_BEFORE_GUIELEMENT_ACTION_MILLIS);
        if (delayAfterAction > 0 || delayBeforeAction > 0) {
            guiElementFacade = new DelayActionsGuiElementFacade(guiElementFacade, delayBeforeAction, delayAfterAction, guiElementData);
        }
        return guiElementFacade;
    }

    /**
     * After retrieving all WebElements of the given locator, this method can be used to filter the WebElements.
     * Different filters can be applied in conjunction (and).
     *
     * @param filters Filters to be applied
     *
     * @return The same GuiElement
     * @deprecated Use TesterraBy instead
     */
    @Deprecated
    public GuiElement withWebElementFilter(WebElementFilter... filters) {
        if (filters != null) {
            Collections.addAll(locator.getFilters(), filters);
        }
        return this;
    }

    /**
     * Add a Decorator to log every action performed on this element with addition to a short description of it.
     *
     * @param description A very short description of this GuiElement, for example "Continue Shopping Button"
     *
     * @deprecated use setName() instead.
     */
    @Deprecated
    public GuiElement setDescription(String description) {
        guiElementData.name = description;
        return this;
    }

    /**
     * Get sub element by locator. Using this executes a find on the parent element and the parent.findElement for the
     * given locator. It does not wait for the subelement if the parent has been found!
     *
     * @param by Locator of new element.
     *
     * @return GuiElement
     */
    public IGuiElement getSubElement(By by) {
        return getSubElement(by, null);
    }

    /**
     * Get sub element by locator. Using this executes a find on the parent element and the parent.findElement for the
     * given locator. It does not wait for the subelement if the parent has been found!
     *
     * @param by   Locator of new element.
     * @param description Description for GuiElement
     *
     * @return GuiElement
     */
    @Deprecated
    public IGuiElement getSubElement(By by, String description) {
        return guiElementFacade.getSubElement(by, description);
    }

    @Override
    public IGuiElement getSubElement(Locate locator) {
        return guiElementFacade.getSubElement(locator);
    }

    @Override
    public WebElement getWebElement() {
        return guiElementFacade.getWebElement();
    }

    @Override
    public By getBy() {
        return guiElementFacade.getBy();
    }

    @Override
    public GuiElement scrollToElement() {
        guiElementFacade.scrollToElement();
        return this;
    }

    @Override
    public GuiElement scrollToElement(int yOffset) {
        guiElementFacade.scrollToElement(yOffset);
        return this;
    }

    @Override
    public IGuiElement select() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.select();
        guiElementData.resetLogLevel();
        return this;
    }

    /**
     * Select/Deselect a selectable element. If null is passed, no action will be performed.
     *
     * @param select true/false/null.
     */
    @Override
    public GuiElement select(Boolean select) {
        guiElementData.setLogLevel(LogLevel.INFO);
        if (select == null) {
            LOGGER.info("Select option is null. Selecting/Deselecting nothing.");
        }
        if (select) {
            guiElementFacade.select();
        } else {
            guiElementFacade.deselect();
        }
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public IGuiElement deselect() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.deselect();
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public IGuiElement type(String text) {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.type(text);
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public IGuiElement click() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.click();
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public IGuiElement clickJS() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.clickJS();
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public IGuiElement clickAbsolute() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.clickAbsolute();
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public IGuiElement mouseOverAbsolute2Axis() {
        guiElementFacade.mouseOverAbsolute2Axis();
        return this;
    }

    @Override
    public IGuiElement submit() {
        guiElementFacade.submit();
        return this;
    }

    @Override
    public IGuiElement sendKeys(CharSequence... charSequences) {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.sendKeys(charSequences);
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public IGuiElement clear() {
        guiElementFacade.clear();
        return this;
    }

    @Override
    public String getTagName() {
        return guiElementFacade.getTagName();
    }

    @Override
    public String getAttribute(String attributeName) {
        return guiElementFacade.getAttribute(attributeName);
    }

    @Override
    public boolean isSelected() {
        return guiElementFacade.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return guiElementFacade.isEnabled();
    }

    @Override
    public String getText() {
        return guiElementFacade.getText();
    }

    @Override
    public boolean isDisplayed() {
        return guiElementFacade.isDisplayed();
    }

    @Override
    public boolean isVisible(final boolean complete) {
        return guiElementFacade.isVisible(complete);
    }

    @Override
    public boolean isDisplayedFromWebElement() {
        return guiElementFacade.isDisplayedFromWebElement();
    }

    /**
     * Checks if the element is selectable.
     * <p>
     * WARNING: To determine if the element is truly selectable, a selection or deselection will be done and reverted.
     * Keep this in mind.
     *
     * @return true, if the element is selectable.
     */
    @Override
    public boolean isSelectable() {
        return guiElementFacade.isSelectable();
    }

    @Override
    public Point getLocation() {
        return guiElementFacade.getLocation();
    }

    @Override
    public Dimension getSize() {
        return guiElementFacade.getSize();
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        return guiElementFacade.getCssValue(cssIdentifier);
    }

    @Override
    public IGuiElement mouseOver() {
        guiElementFacade.mouseOver();
        return this;
    }

    @Override
    public IGuiElement mouseOverJS() {
        guiElementFacade.mouseOverJS();
        return this;
    }

    @Override
    public boolean isPresent() {
        return guiElementFacade.isPresent();
    }

    @Override
    public Select getSelectElement() {
        return guiElementFacade.getSelectElement();
    }

    @Override
    public List<String> getTextsFromChildren() {
        return guiElementFacade.getTextsFromChildren();
    }

    @Override
    public boolean anyFollowingTextNodeContains(String contains) {
        return guiElementFacade.anyFollowingTextNodeContains(contains);
    }

    public WebDriver getWebDriver() {
        return guiElementData.webDriver;
    }

    @Override
    public GuiElementCore doubleClick() {
        guiElementFacade.doubleClick();
        return this;
    }

    public int getTimeoutInSeconds() {
        return guiElementData.getTimeoutInSeconds();
    }

    public void setTimeoutInSeconds(int timeoutInSeconds) {
        guiElementData.setTimeoutInSeconds(timeoutInSeconds);
    }

    public void restoreDefaultTimeout() {
        int timeoutInSeconds = POConfig.getUiElementTimeoutInSeconds();
        guiElementData.setTimeoutInSeconds(timeoutInSeconds);
    }

    @Override
    public GuiElementCore highlight() {
        guiElementFacade.highlight();
        return this;
    }

    @Override
    public GuiElementCore swipe(int offsetX, int offSetY) {
        guiElementFacade.swipe(offsetX, offSetY);
        return this;
    }

    @Override
    public int getLengthOfValueAfterSendKeys(String textToInput) {
        return guiElementFacade.getLengthOfValueAfterSendKeys(textToInput);
    }

    @Override
    public int getNumberOfFoundElements() {
        return guiElementFacade.getNumberOfFoundElements();
    }

    @Override
    public IGuiElement rightClick() {
        guiElementFacade.rightClick();
        return this;
    }

    @Override
    public IGuiElement rightClickJS() {
        guiElementFacade.rightClickJS();
        return this;
    }

    @Override
    public IGuiElement doubleClickJS() {
        guiElementFacade.doubleClickJS();
        return this;
    }

    @Override
    public File takeScreenshot() {
        return guiElementFacade.takeScreenshot();
    }

    /**
     * Get Frame Login object. It encapsulates the correct order of switching the frames containing this GuiElement.
     *
     * @return Frame Logic object or null.
     */
    public FrameLogic getFrameLogic() {
        return guiElementData.frameLogic;
    }

    /**
     * Get the parent element of this element, from getSubElement().
     *
     * @return parent object or null if this element has no parent
     */
    public GuiElementCore getParent() {
        return guiElementData.parent;
    }

    /**
     * Sets an element as parent.
     *
     * @param parent Object that should act as parent.
     */
    public GuiElement setParent(GuiElementCore parent) {
        guiElementData.parent = parent;
        return this;
    }

    @Deprecated
    public List<WebElementFilter> getWebElementFilters() {
        return locator.getFilters();
    }

    @Override
    public String toString() {
        return guiElementData.toString();
    }

    @Deprecated
    public WebDriver getDriver() {
        return getWebDriver();
    }

    public boolean hasSensibleData() {
        return guiElementData.sensibleData;
    }

    public GuiElement sensibleData() {
        guiElementData.sensibleData = true;
        return this;
    }

    @Override
    public GuiElement setName(String name) {
        guiElementData.name = name;
        return this;
    }

    @Override
    public String getName() {
        return guiElementData.name;
    }

    /**
     * Provides access to all functional assert methods.
     *
     * @return GuiElementAssert object for functional assertions
     */
    public GuiElementAssert asserts() {
        return functionalAssert;
    }

    /**
     * Provides access to all assert methods. If an assertion fails, the assertDescription will be given as cause,
     * instead of the technical cause like an isDisplayed error.
     *
     * @param errorMessage Cause returned on assertion error.
     *
     * @return GuiElementAssert object for functional assertions
     */
    public GuiElementAssert asserts(String errorMessage) {
        GuiElementAssertDescriptionDecorator guiElementAssertDescriptionDecorator
                = new GuiElementAssertDescriptionDecorator(errorMessage, functionalAssert);
        return guiElementAssertDescriptionDecorator;
    }

    /**
     * Provides access to all non-functional assert methods.
     *
     * @return GuiElementAssert object for non-functional assertions
     */
    public GuiElementAssert nonFunctionalAsserts() {
        return nonFunctionalAssertReplacement;
    }

    /**
     * Provides access to all non-functional assert methods. If an assertion fails, the assertDescription will be
     * given as cause, instead of the technical cause like an isDisplayed error.
     *
     * @param errorMessage Cause returned on assertion error.
     *
     * @return GuiElementAssert object for non-functional assertions
     */
    public GuiElementAssert nonFunctionalAsserts(String errorMessage) {
        GuiElementAssertDescriptionDecorator guiElementAssertDescriptionDecorator
                = new GuiElementAssertDescriptionDecorator(errorMessage, nonFunctionalAssertReplacement);
        return guiElementAssertDescriptionDecorator;
    }

    /**
     * Provides access to all functional assert methods.
     *
     * @return GuiElementAssert object for functional assertions
     */
    public GuiElementAssert assertCollector() {
        return functionalAssertCollector;
    }

    /**
     * Provides access to all assert methods. If an assertion fails, the assertDescription will be given as cause,
     * instead of the technical cause like an isDisplayed error.
     *
     * @param errorMessage Cause returned on assertion error.
     *
     * @return GuiElementAssert object for functional assertions
     */
    public GuiElementAssert assertCollector(String errorMessage) {
        GuiElementAssertDescriptionDecorator guiElementAssertDescriptionDecorator
                = new GuiElementAssertDescriptionDecorator(errorMessage, functionalAssertCollector);
        return guiElementAssertDescriptionDecorator;
    }

    public List<IGuiElement> getList() {
        int numberOfFoundElements = getNumberOfFoundElements();
        List<IGuiElement> guiElements = new ArrayList<>(numberOfFoundElements);
        for (int i = 0; i < numberOfFoundElements; i++) {
            IGuiElement guiElement = new GuiElement(guiElementData, i);
            guiElements.add(guiElement);
        }
        return guiElements;
    }

    public LogLevel getLogLevel() {
        return guiElementData.getLogLevel();
    }

    public void setLogLevel(LogLevel logLevel) {
        guiElementData.setLogLevel(logLevel);
    }

    public GuiElement shadowRoot() {
        guiElementData.shadowRoot = true;
        return this;
    }

    /**
     * Provides access to all wait methods
     */
    public GuiElementWait waits() {
        return guiElementWait;
    }

    @Override
    public IAssertableValue text() {
        return new AssertableValue(getText(), Property.TEXT.toString(), this);
    }

    @Override
    public IAssertableValue value() {
        return value(Attribute.VALUE);
    }

    @Override
    public IAssertableValue value(Attribute attribute) {
        return new AssertableValue(getAttribute(attribute.toString()), String.format("@%s", Property.ATTRIBUTE), this);
    }

    @Override
    public IAssertableBinaryValue present() {
        return new AssertableBinaryValue(waits().waitForIsPresent(), Property.PRESENT.toString(), this);
    }

    @Override
    public IAssertableBinaryValue<Boolean> visible(boolean complete) {
        return new AssertableBinaryValue(isVisible(true), Property.VISIBLE.toString(), this);
    }

    @Override
    public IAssertableBinaryValue displayed() {
        return new AssertableBinaryValue(isDisplayed(), Property.DISPLAYED.toString(), this);
    }

    @Override
    public IAssertableBinaryValue<Boolean> enabled() {
        return new AssertableBinaryValue(isDisplayed(), Property.DISPLAYED.toString(), this);
    }

    @Override
    public IAssertableBinaryValue<Boolean> selected() {
        return new AssertableBinaryValue<>(isSelected(), Property.SELECTED.toString(), this);
    }

    @Override
    public IAssertableQuantifiedValue<Boolean> layout() {
        return new AssertableQuantifiedValue<>(1, Property.LAYOUT.toString(), this);
    }
}

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
import eu.tsystems.mms.tic.testframework.constants.GuiElementType;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.logging.LogLevel;
import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Checkable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.ConfiguredAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ConfigurableGuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertDescriptionDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertExecutionLogDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertHighlightDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PerformanceTestGuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCoreFrameAwareDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCoreSequenceDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementStatusCheck;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementStatusCheckFrameAwareDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.creation.GuiElementCoreFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.DelayActionsGuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacadeLoggingDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFace;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.StandardGuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.StandardGuiElementWait;
import eu.tsystems.mms.tic.testframework.utils.ArrayUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GuiElement is the access point for most tests and is an extension of WebElement.
 * <p>
 * Authors: pele, rnhb
 */
public class GuiElement implements
        Checkable,
        GuiElementCore,
        GuiElementWait,
        Nameable {

    private static final Map<String, GuiElementCoreFactory> coreFactories = new HashMap<>();

    public static void registerGuiElementCoreFactory(GuiElementCoreFactory guiElementCoreFactory, String... browsers) {
        LOGGER.info("Registering " + guiElementCoreFactory.getClass().getSimpleName() + " for browsers " + ArrayUtils.join(browsers, ","));

        for (String browser : browsers) {
            coreFactories.put(browser, guiElementCoreFactory);
        }
    }

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
        this(driver, by, "");
    }

    /**
     * Constructor with explicit web driver session and description.
     *
     * @param name   .
     * @param driver Actual driver object.
     * @param by     By locator.
     *
     * @deprecated name is automatically read from field name
     */
    @Deprecated
    public GuiElement(final WebDriver driver, final By by, final String name) {
        this(driver, by, name, new GuiElement[0]);
    }

    public GuiElement(final WebDriver driver, final By by, final GuiElement... guiElements) {
        this(driver, by, "", guiElements);
    }

    /**
     * Constructor with frames and explicit webDriver session.
     *
     * @param by     By locator.
     * @param frames frames containing the element
     * @param driver Driver Object.
     *
     * @deprecated name is automatically read from field name
     */
    @Deprecated
    public GuiElement(WebDriver driver, By by, String name, GuiElement... frames) {
        FrameLogic frameLogic = null;
        if (frames != null && frames.length > 0) {
            frameLogic = new FrameLogic(driver, frames);
        }
        guiElementData = new GuiElementData(driver, name, frameLogic, by, this);
        buildInternals(driver, by);
    }

    private void buildInternals(WebDriver driver, By by) {
        // Create core depending on requested Browser
        WebDriverRequest webDriverRequest = WebDriverManager.getRelatedWebDriverRequest(driver);
        String currentBrowser = webDriverRequest.browser;
        guiElementData.browser = currentBrowser;

        if (coreFactories.containsKey(currentBrowser)) {
            GuiElementCoreFactory guiElementCoreFactory = coreFactories.get(currentBrowser);
            guiElementCore = guiElementCoreFactory.create(by, driver, this.guiElementData);
        } else {
            throw new TesterraSystemException("No GuiElementCoreFactory registered for " + currentBrowser);
        }
        GuiElementStatusCheck guiElementStatusCheck = guiElementCore;

        if (this.guiElementData.hasFrameLogic()) {
            // if frames are set, the waiter should use frame switches when executing its sequences
            guiElementStatusCheck = new GuiElementStatusCheckFrameAwareDecorator(guiElementStatusCheck, this.guiElementData);
            guiElementCore = new GuiElementCoreFrameAwareDecorator(guiElementCore, this.guiElementData);
        }

        // guielement wait uses the core and not the facade
        guiElementWait = new StandardGuiElementWait(guiElementStatusCheck, this.guiElementData);

        // Wrap the core with sequence decorator, such that its methods are executed with sequence
        guiElementCore = new GuiElementCoreSequenceDecorator(guiElementCore, this.guiElementData);

        functionalStandardAssert = getGuiElementAssert(true, false, guiElementCore, guiElementWait, this.guiElementData);
        functionalAssertCollector = getGuiElementAssert(true, true, guiElementCore, guiElementWait, this.guiElementData);

        nonFunctionalAssert = getGuiElementAssert(false, false, guiElementCore, guiElementWait, this.guiElementData);
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

    private GuiElementAssert getGuiElementAssert(boolean functional, boolean collected,
                                                 GuiElementCore guiElementCore,
                                                 GuiElementWait guiElementWait,
                                                 GuiElementData guiElementData) {
        GuiElementType guiElementType = GuiElementType.sequence;
        GuiElementAssert guiElementAssert;
        switch (guiElementType) {
            case perf:
                guiElementAssert = new PerformanceTestGuiElementAssert();
                break;
            default:
                ConfiguredAssert configuredAssert = new ConfiguredAssert(functional, collected);
                guiElementAssert = new ConfigurableGuiElementAssert(guiElementCore, guiElementWait, configuredAssert, guiElementData);
                guiElementAssert = new GuiElementAssertHighlightDecorator(guiElementAssert, guiElementData);
                guiElementAssert = new GuiElementAssertExecutionLogDecorator(guiElementAssert, guiElementData);
        }
        return guiElementAssert;
    }

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
     */
    public GuiElement withWebElementFilter(WebElementFilter... filters) {
        if (filters != null) {
            Collections.addAll(guiElementData.webElementFilters, filters);
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
     * @param byLocator Locator of new element.
     *
     * @return GuiElement
     */
    public GuiElement getSubElement(By byLocator) {
        return getSubElement(byLocator, null);
    }

    /**
     * Get sub element by locator. Using this executes a find on the parent element and the parent.findElement for the
     * given locator. It does not wait for the subelement if the parent has been found!
     *
     * @param byLocator   Locator of new element.
     * @param description Description for GuiElement
     *
     * @return GuiElement
     */
    public GuiElement getSubElement(By byLocator, String description) {
        return guiElementFacade.getSubElement(byLocator, description);
    }

    /**
     * @return true if name is not empty or null
     *
     * @deprecated should not be public, will be removed
     */
    @Deprecated
    public boolean hasDescription() {
        return !StringUtils.isStringEmpty(guiElementData.name);
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
    public void scrollToElement() {
        guiElementFacade.scrollToElement();
    }

    @Override
    public void scrollToElement(int yOffset) {
        guiElementFacade.scrollToElement(yOffset);
    }

    @Override
    public void select() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.select();
        guiElementData.resetLogLevel();
    }

    /**
     * Select/Deselect a selectable element. If null is passed, no action will be performed.
     *
     * @param select true/false/null.
     */
    public void select(Boolean select) {
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
    }

    @Override
    public void deselect() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.deselect();
        guiElementData.resetLogLevel();
    }

    @Override
    public void type(String text) {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.type(text);
        guiElementData.resetLogLevel();
    }

    @Override
    public void click() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.click();
        guiElementData.resetLogLevel();
    }

    @Override
    public void clickJS() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.clickJS();
        guiElementData.resetLogLevel();
    }

    @Override
    public void clickAbsolute() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.clickAbsolute();
        guiElementData.resetLogLevel();
    }

    @Override
    public void mouseOverAbsolute2Axis() {
        guiElementFacade.mouseOverAbsolute2Axis();
    }

    @Override
    public void submit() {
        guiElementFacade.submit();
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.sendKeys(charSequences);
        guiElementData.resetLogLevel();
    }

    @Override
    public void clear() {
        guiElementFacade.clear();
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
    public List<WebElement> findElements(By byLocator) {
        return guiElementFacade.findElements(byLocator);
    }

    @Override
    public WebElement findElement(By byLocator) {
        return guiElementFacade.findElement(byLocator);
    }

    @Override
    public boolean isDisplayed() {
        return guiElementFacade.isDisplayed();
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
    public Rectangle getRect() {
        return null;
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        return guiElementFacade.getCssValue(cssIdentifier);
    }

    @Override
    public void mouseOver() {
        guiElementFacade.mouseOver();
    }

    @Override
    public void mouseOverJS() {
        guiElementFacade.mouseOverJS();
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

    /**
     * @deprecated Remove this, you don't need it. The GuiElement refreshes itself in case of an error.
     */
    @Deprecated
    public void refresh() {
        guiElementFacade.refresh();
    }

    @Override
    public boolean anyFollowingTextNodeContains(String contains) {
        return guiElementFacade.anyFollowingTextNodeContains(contains);
    }

    public WebDriver getWebDriver() {
        return guiElementData.webDriver;
    }

    @Override
    public void doubleClick() {
        guiElementFacade.doubleClick();
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
    public void highlight() {
        guiElementFacade.highlight();
    }

    @Override
    public void swipe(int offsetX, int offSetY) {
        guiElementFacade.swipe(offsetX, offSetY);
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
    public void rightClick() {
        guiElementFacade.rightClick();
    }

    @Override
    public void rightClickJS() {
        guiElementFacade.rightClickJS();
    }

    @Override
    public void doubleClickJS() {
        guiElementFacade.doubleClickJS();
    }

    @Override
    public File takeScreenshot() {
        return guiElementFacade.takeScreenshot();
    }

    @Override
    @Deprecated
    public boolean waitForIsPresent() {
        return guiElementFacade.waitForIsPresent();
    }

    @Override
    @Deprecated
    public boolean waitForIsNotPresent() {
        return guiElementFacade.waitForIsNotPresent();
    }

    @Override
    @Deprecated
    public boolean waitForIsEnabled() {
        return guiElementFacade.waitForIsEnabled();
    }

    @Override
    @Deprecated
    public boolean waitForIsDisabled() {
        return guiElementFacade.waitForIsDisabled();
    }

    @Override
    @Deprecated
    public boolean waitForAnyFollowingTextNodeContains(String contains) {
        return guiElementFacade.waitForAnyFollowingTextNodeContains(contains);
    }

    @Override
    @Deprecated
    public boolean waitForIsDisplayed() {
        return guiElementFacade.waitForIsDisplayed();
    }

    @Override
    @Deprecated
    public boolean waitForIsNotDisplayed() {
        return guiElementFacade.waitForIsNotDisplayed();
    }

    @Override
    @Deprecated
    public boolean waitForIsDisplayedFromWebElement() {
        return guiElementFacade.waitForIsDisplayedFromWebElement();
    }

    @Override
    @Deprecated
    public boolean waitForIsNotDisplayedFromWebElement() {
        return guiElementFacade.waitForIsNotDisplayedFromWebElement();
    }

    @Override
    @Deprecated
    public boolean waitForIsSelected() {
        return guiElementFacade.waitForIsSelected();
    }

    @Override
    @Deprecated
    public boolean waitForIsNotSelected() {
        return guiElementFacade.waitForIsNotSelected();
    }

    @Override
    @Deprecated
    public boolean waitForText(String text) {
        return guiElementFacade.waitForText(text);
    }

    @Override
    @Deprecated
    public boolean waitForTextContains(String... text) {
        return guiElementFacade.waitForTextContains(text);
    }

    @Override
    @Deprecated
    public boolean waitForTextContainsNot(String... text) {
        return guiElementFacade.waitForTextContainsNot(text);
    }

    @Override
    @Deprecated
    public boolean waitForAttribute(String attributeName) {
        return guiElementFacade.waitForAttribute(attributeName);
    }

    @Override
    @Deprecated
    public boolean waitForAttribute(String attributeName, String value) {
        return guiElementFacade.waitForAttribute(attributeName, value);
    }

    @Override
    @Deprecated
    public boolean waitForAttributeContains(String attributeName, String value) {
        return guiElementFacade.waitForAttributeContains(attributeName, value);
    }

    @Override
    @Deprecated
    public boolean waitForAttributeContainsNot(String attributeName, String value) {
        return guiElementFacade.waitForAttributeContainsNot(attributeName, value);
    }

    @Override
    @Deprecated
    public boolean waitForCssClass(final String className) {
        return guiElementFacade.waitForCssClassIsPresent(className);
    }

    @Override
    @Deprecated
    public boolean waitForCssClassIsPresent(final String className) {
        return guiElementFacade.waitForCssClassIsPresent(className);
    }

    @Override
    @Deprecated
    public boolean waitForCssClassIsNotPresent(final String className) {
        return guiElementFacade.waitForCssClassIsNotPresent(className);
    }

    @Override
    @Deprecated
    public boolean waitForIsSelectable() {
        return guiElementFacade.waitForIsSelectable();
    }

    @Override
    @Deprecated
    public boolean waitForIsNotSelectable() {
        return guiElementFacade.waitForIsNotSelectable();
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
    public void setParent(GuiElementCore parent) {
        guiElementData.parent = parent;
    }

    public List<WebElementFilter> getWebElementFilters() {
        return guiElementData.webElementFilters;
    }

    @Override
    public String toString() {
        return guiElementData.toString();
    }

    public WebDriver getDriver() {
        return guiElementData.webDriver;
    }

    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return guiElementFacade.getScreenshotAs(target);
    }

    public boolean hasSensibleData() {
        return guiElementData.sensibleData;
    }

    public GuiElement sensibleData() {
        guiElementData.sensibleData = true;
        return this;
    }

    @Override
    public void setName(String name) {
        guiElementData.name = name;
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

    public List<GuiElement> getList() {
        int numberOfFoundElements = getNumberOfFoundElements();
        List<GuiElement> guiElements = new ArrayList<>(numberOfFoundElements);
        for (int i = 0; i < numberOfFoundElements; i++) {
            GuiElement guiElement = new GuiElement(guiElementData, i);
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
}

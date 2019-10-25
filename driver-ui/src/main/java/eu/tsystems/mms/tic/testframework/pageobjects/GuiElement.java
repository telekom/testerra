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
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.logging.LogLevel;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
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
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GuiElement is the access point for most tests and is an extension of WebElement.
 * <p>
 * Authors: pele, rnhb
 */
public class GuiElement implements GuiElementFacade, Loggable {
    private boolean forcedStandardAsserts = false;

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
        final GuiElementFacade... frames
    ) {
        this(locator, driver, frames);
    }

    public GuiElement(
        final WebDriver driver,
        final By by,
        final GuiElementFacade... frames
    ) {
        this(Locate.by(by), driver, frames);
    }

    public GuiElement(
        final Locate locator,
        final WebDriver driver,
        final GuiElementFacade... frames
    ) {
        IFrameLogic frameLogic = null;
        if (frames != null && frames.length > 0) {
            frameLogic = new FrameLogic(driver, frames);
        }
        final By by = locator.getBy();
        guiElementData = new GuiElementData(driver, "", frameLogic, by, this);
        buildInternals(driver, by);
        this.locator = locator;
    }

    public GuiElement(
        final Locate locator,
        final WebDriver driver,
        final GuiElementFacade parent
    ) {
        if (!(parent instanceof GuiElement)) {
            throw new TesterraSystemException(String.format("Parent element is no implementation of %s", GuiElement.class));
        }

        // Use FrameLogic from parent
        GuiElement parentGuiElement = (GuiElement)parent;
        IFrameLogic frameLogic = null;
        if (parentGuiElement.guiElementData.frameLogic != null) {
            frameLogic = parentGuiElement.guiElementData.frameLogic;
        }

        final By by = locator.getBy();
        guiElementData = new GuiElementData(driver, "", frameLogic, by, this);
        guiElementData.parent = parentGuiElement.guiElementCore;
        buildInternals(driver, by);
        this.locator = locator;
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
        guiElementCore = coreFactory.create(currentBrowser, by, driver, guiElementData);

        GuiElementWaitFactory waitFactory = TesterraCommons.ioc().getInstance(GuiElementWaitFactory.class);
        guiElementWait = waitFactory.create(guiElementCore, guiElementData);

        GuiElementAssertFactory assertFactory = TesterraCommons.ioc().getInstance(GuiElementAssertFactory.class);
        functionalStandardAssert = assertFactory.create(true, false, guiElementCore, guiElementWait, guiElementData);
        functionalAssertCollector = assertFactory.create(true, true, guiElementCore, guiElementWait, guiElementData);
        nonFunctionalAssertReplacement = assertFactory.create(false, false, guiElementCore, guiElementWait, guiElementData);;

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

    /**
     * We cannot use the GuiElementFactory for decorating the facade here,
     * since GuiElement is not always created by it's according factory.
     * You can move this code to DefaultGuiElementFactory when no more 'new GuiElement()' calls exists.
     * But this may not happen before 2119.
     */
    private GuiElementFacade getFacade(GuiElementCore guiElementCore, GuiElementWait guiElementWait, GuiElementAssert guiElementAssert) {
        GuiElementFacade guiElementFacade;
        //guiElementFacade = new StandardGuiElementFacade(guiElementCore, guiElementWait, guiElementAssert);
        guiElementFacade = new GuiElementFacadeLoggingDecorator(this, guiElementData);
        //guiElementFacade = new GuiElementFace(guiElementFacade, guiElementData);

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
    public GuiElementFacade withWebElementFilter(WebElementFilter... filters) {
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
    public GuiElementFacade setDescription(String description) {
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
    public GuiElement getSubElement(By by) {
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
    public GuiElement getSubElement(By by, String description) {
        return (GuiElement)guiElementCore.getSubElement(by, description);
    }

    @Override
    public GuiElement getSubElement(Locate locator) {
        return (GuiElement)guiElementFacade.getSubElement(locator);
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
    public GuiElementFacade scrollToElement(int yOffset) {
        guiElementFacade.scrollToElement(yOffset);
        return this;
    }

    @Override
    public GuiElementFacade select() {
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
    public GuiElementFacade select(final Boolean select) {
        guiElementData.setLogLevel(LogLevel.INFO);
        if (select == null) {
            log().info("Select option is null. Selecting/Deselecting nothing.");
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
    public GuiElementFacade deselect() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.deselect();
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public GuiElementFacade type(String text) {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementCore.type(text);
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public GuiElementFacade click() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.click();
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public GuiElementFacade clickJS() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.clickJS();
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public GuiElementFacade clickAbsolute() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.clickAbsolute();
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public GuiElementFacade mouseOverAbsolute2Axis() {
        guiElementFacade.mouseOverAbsolute2Axis();
        return this;
    }

    @Override
    public GuiElementFacade submit() {
        guiElementFacade.submit();
        return this;
    }

    @Override
    public GuiElementFacade sendKeys(CharSequence... charSequences) {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.sendKeys(charSequences);
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public GuiElementFacade clear() {
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
    public GuiElementFacade mouseOver() {
        guiElementFacade.mouseOver();
        return this;
    }

    @Override
    public GuiElementFacade mouseOverJS() {
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
    public GuiElementFacade doubleClick() {
        guiElementFacade.doubleClick();
        return this;
    }

    public int getTimeoutInSeconds() {
        return guiElementData.getTimeoutInSeconds();
    }

    public GuiElementFacade setTimeoutInSeconds(int timeoutInSeconds) {
        guiElementData.setTimeoutInSeconds(timeoutInSeconds);
        return this;
    }

    public GuiElementFacade restoreDefaultTimeout() {
        int timeoutInSeconds = POConfig.getUiElementTimeoutInSeconds();
        guiElementData.setTimeoutInSeconds(timeoutInSeconds);
        return this;
    }

    @Override
    public GuiElementFacade highlight() {
        guiElementFacade.highlight();
        return this;
    }

    @Override
    public GuiElementFacade swipe(int offsetX, int offSetY) {
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
    public GuiElementFacade rightClick() {
        guiElementFacade.rightClick();
        return this;
    }

    @Override
    public GuiElementFacade rightClickJS() {
        guiElementFacade.rightClickJS();
        return this;
    }

    @Override
    public GuiElementFacade doubleClickJS() {
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
    @Override
    public IFrameLogic getFrameLogic() {
        return guiElementData.frameLogic;
    }

    /**
     * Get the parent element of this element, from getSubElement().
     *
     * @return parent object or null if this element has no parent
     */
    @Deprecated
    public GuiElementCore getParent() {
        return guiElementData.parent;
    }

    /**
     * Sets an element as parent.
     *
     * @param parent Object that should act as parent.
     */
    @Deprecated
    public GuiElementFacade setParent(GuiElementCore parent) {
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
    public GuiElementFacade setName(String name) {
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

    @Override
    public List<GuiElementFacade> getList() {
        int numberOfFoundElements = getNumberOfFoundElements();
        List<GuiElementFacade> guiElements = new ArrayList<>(numberOfFoundElements);
        for (int i = 0; i < numberOfFoundElements; i++) {
            GuiElementFacade guiElement = new GuiElement(guiElementData, i);
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

    public GuiElementFacade shadowRoot() {
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
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        return new AssertableValue( this, getText(), methodName);
    }

    @Override
    public IAssertableValue value() {
        return value(Attribute.VALUE);
    }

    @Override
    public IAssertableValue value(Attribute attribute) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        return new AssertableValue(this, getAttribute(attribute.toString()), methodName);
    }

    @Override
    public IAssertableBinaryValue present() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        return new AssertableBinaryValue(this,waits().waitForIsPresent(), methodName);
    }

    @Override
    public IAssertableBinaryValue visible(boolean complete) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        return new AssertableBinaryValue(this,isVisible(true), String.format("%s(complete: %s)", methodName, complete));
    }

    @Override
    public IAssertableBinaryValue displayed() {
        /*Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(!checkForEnabled); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                boolean isEnabled = guiElementStatusCheck.isEnabled();
                boolean sequenceStatus = isEnabled == checkForEnabled;
                setPassState(sequenceStatus);
                setReturningObject(sequenceStatus);
            }
        };*/
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        return new AssertableBinaryValue(this, isDisplayed(), methodName);
    }

    @Override
    public IAssertableBinaryValue enabled() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        return new AssertableBinaryValue(this,isEnabled(), methodName);
    }

    @Override
    public IAssertableBinaryValue selected() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        return new AssertableBinaryValue(this, isSelected(), methodName);
    }

    @Override
    public IAssertableQuantifiedValue screenshot(final String imageName) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        return new AssertableQuantifiedValue(this, 1, methodName);
    }

    @Override
    public GuiElementFacade scrollTo() {
        return scrollToElement();
    }

    @Override
    public GuiElementFacade scrollTo(final int yOffset) {
        return scrollToElement(yOffset);
    }
}

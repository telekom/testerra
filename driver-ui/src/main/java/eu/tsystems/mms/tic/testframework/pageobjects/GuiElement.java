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
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultInstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultNonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.logging.LogLevel;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AssertionProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertDescriptionDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IBinaryPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IImagePropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IStringPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCoreFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.DefaultGuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.DelayActionsGuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacadeLoggingDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWaitFactory;
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
public class GuiElement implements IGuiElement, Loggable {

    protected static final PropertyAssertionFactory propertyAssertionFactory = TesterraCommons.ioc().getInstance(PropertyAssertionFactory.class);

    /**
     * This is the default functional assertion for GuiElements,
     * which always contains a {@link DefaultFunctionalAssertion}.
     * See {@link GuiElementAssertFactory} for details.
     */
    private GuiElementAssert functionalAssert;

    /**
     * This contains always an {@link DefaultInstantAssertion}
     */
    private GuiElementAssert instantAssert;

    /**
     * This contains always an {@link DefaultNonFunctionalAssertion}
     */
    private GuiElementAssert nonFunctionalAssert;

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
        this(locator, driver, frames);
    }

    public GuiElement(
        final WebDriver driver,
        final By by,
        final IGuiElement... frames
    ) {
        this(Locate.by(by), driver, frames);
    }

    public GuiElement(
        final Locate locator,
        final WebDriver driver,
        final IGuiElement... frames
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

        guiElementFacade = createFacade(guiElementCore);
    }

    /**
     * We cannot use the GuiElementFactory for decorating the facade here,
     * since GuiElement is not always created by it's according factory
     * and implementing a GuiElementFacadeFactory is useless.
     * You can move this code to DefaultGuiElementFactory when no more 'new GuiElement()' calls exists.
     * But this may not happen before 2119.
     */
    private GuiElementFacade createFacade(GuiElementCore guiElementCore) {
        GuiElementFacade guiElementFacade;
        guiElementFacade = new DefaultGuiElementFacade(guiElementCore);
        guiElementFacade = new GuiElementFacadeLoggingDecorator(guiElementFacade, guiElementData);
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
    public IGuiElement scrollToElement(int yOffset) {
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
    public IGuiElement select(final Boolean select) {
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
    public IGuiElement deselect() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.deselect();
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public IGuiElement type(String text) {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementCore.type(text);
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
    public IGuiElement doubleClick() {
        guiElementFacade.doubleClick();
        return this;
    }

    public int getTimeoutInSeconds() {
        return guiElementData.getTimeoutInSeconds();
    }

    public IGuiElement setTimeoutInSeconds(int timeoutInSeconds) {
        guiElementData.setTimeoutInSeconds(timeoutInSeconds);
        return this;
    }

    public IGuiElement restoreDefaultTimeout() {
        int timeoutInSeconds = POConfig.getUiElementTimeoutInSeconds();
        guiElementData.setTimeoutInSeconds(timeoutInSeconds);
        return this;
    }

    @Override
    public IGuiElement highlight() {
        guiElementFacade.highlight();
        return this;
    }

    @Override
    public IGuiElement swipe(int offsetX, int offSetY) {
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
    public IGuiElement setParent(GuiElementCore parent) {
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
    public IGuiElement setName(String name) {
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
        if (functionalAssert==null) {
            /**
             * Configure {@link GuiElementAssert} with {@link InstantAssertion} when {@link Flags.GUIELEMENT_DEFAULT_ASSERT_IS_COLLECTOR} != true
             */
            if (!Flags.GUIELEMENT_DEFAULT_ASSERT_IS_COLLECTOR) {
                functionalAssert = instantAsserts();
            } else {
                final GuiElementAssertFactory assertFactory = TesterraCommons.ioc().getInstance(GuiElementAssertFactory.class);
                functionalAssert = assertFactory.create(true, false, guiElementCore, guiElementWait, guiElementData);
            }
        }
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
                = new GuiElementAssertDescriptionDecorator(errorMessage, asserts());
        return guiElementAssertDescriptionDecorator;
    }

    /**
     * Provides access to all non-functional assert methods.
     *
     * @return GuiElementAssert object for non-functional assertions
     */
    public GuiElementAssert nonFunctionalAsserts() {
        if (nonFunctionalAssert==null) {
            final GuiElementAssertFactory assertFactory = TesterraCommons.ioc().getInstance(GuiElementAssertFactory.class);
            nonFunctionalAssert = assertFactory.create(false, false, guiElementCore, guiElementWait, guiElementData);
        }
        return nonFunctionalAssert;
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
                = new GuiElementAssertDescriptionDecorator(errorMessage, nonFunctionalAsserts());
        return guiElementAssertDescriptionDecorator;
    }

    public GuiElementAssert instantAsserts() {
        if (instantAssert == null) {
            final GuiElementAssertFactory assertFactory = TesterraCommons.ioc().getInstance(GuiElementAssertFactory.class);
            instantAssert = assertFactory.create(true, true, guiElementCore, guiElementWait, guiElementData);
        }
        return instantAssert;
    }

    /**
     * Provides access to all functional assert methods.
     *
     * @return GuiElementAssert object for functional assertions
     * @deprecated Use {@link #asserts()} instead
     */
    @Deprecated
    public GuiElementAssert assertCollector() {
        return asserts();
    }

    /**
     * Provides access to all assert methods. If an assertion fails, the assertDescription will be given as cause,
     * instead of the technical cause like an isDisplayed error.
     *
     * @param errorMessage Cause returned on assertion error.
     *
     * @return GuiElementAssert object for functional assertions
     * @deprecated Use {@link #asserts()} instead
     */
    @Deprecated
    public GuiElementAssert assertCollector(String errorMessage) {
        GuiElementAssertDescriptionDecorator guiElementAssertDescriptionDecorator
                = new GuiElementAssertDescriptionDecorator(errorMessage, asserts());
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

    public IGuiElement shadowRoot() {
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
    public IStringPropertyAssertion<String> tagName() {
        final IGuiElement self = this;
        return propertyAssertionFactory.string(new AssertionProvider<String>() {
            @Override
            public String actual() {
                return getTagName();
            }

            @Override
            public String subject() {
                return String.format("%s.tagName", self);
            }
        });
    }

    @Override
    public IStringPropertyAssertion<String> text() {
        final IGuiElement self = this;
        return propertyAssertionFactory.string(new AssertionProvider<String>() {
            @Override
            public String actual() {
                return getText();
            }

            @Override
            public String subject() {
                return String.format("%s.text", self);
            }
        });
    }

    @Override
    public IStringPropertyAssertion<String> value() {
        return value(Attribute.VALUE);
    }

    @Override
    public IStringPropertyAssertion<String> value(final Attribute attribute) {
        final IGuiElement self = this;
        return propertyAssertionFactory.string(new AssertionProvider<String>() {
            @Override
            public String actual() {
                return getAttribute(attribute.toString());
            }

            @Override
            public String subject() {
                return String.format("%s.value(attribute: %s)", self, attribute);
            }
        });
    }

    @Override
    public IBinaryPropertyAssertion<Boolean> present() {
        final IGuiElement self = this;
        return propertyAssertionFactory.binary(new AssertionProvider<Boolean>() {
            @Override
            public Boolean actual() {
                return isPresent();
            }

            @Override
            public String subject() {
                return String.format("%s.present", self);
            }
        });
    }

    @Override
    public IBinaryPropertyAssertion<Boolean> visible(boolean complete) {
        final IGuiElement self = this;
        return propertyAssertionFactory.binary(new AssertionProvider<Boolean>() {
            @Override
            public Boolean actual() {
                return isVisible(complete);
            }

            @Override
            public String subject() {
                return String.format("%s.visible(complete: %s)", self, complete);
            }
        });
    }

    @Override
    public IBinaryPropertyAssertion<Boolean> displayed() {
        final IGuiElement self = this;
        return propertyAssertionFactory.binary(new AssertionProvider<Boolean>() {
            @Override
            public Boolean actual() {
                return isDisplayed();
            }

            @Override
            public String subject() {
                return String.format("%s.displayed", self);
            }
        });
    }

    @Override
    public IBinaryPropertyAssertion<Boolean> enabled() {
        final IGuiElement self = this;
        return propertyAssertionFactory.binary(new AssertionProvider<Boolean>() {
            @Override
            public Boolean actual() {
                return isEnabled();
            }

            @Override
            public String subject() {
                return String.format("%s.enabled", self);
            }
        });
    }

    @Override
    public IBinaryPropertyAssertion<Boolean> selected() {
        final IGuiElement self = this;
        return propertyAssertionFactory.binary(new AssertionProvider<Boolean>() {
            @Override
            public Boolean actual() {
                return isSelected();
            }

            @Override
            public String subject() {
                return String.format("%s.selected", self);
            }
        });
    }

    @Override
    public IImagePropertyAssertion screenshot() {
        final IGuiElement self = this;
        return propertyAssertionFactory.image(new AssertionProvider<File>() {
            @Override
            public File actual() {
                return guiElementCore.takeScreenshot();
            }

            @Override
            public String subject() {
                return String.format("%s.screenshot", self);
            }
        });
    }

    @Override
    public IGuiElement scrollTo() {
        return scrollToElement();
    }

    @Override
    public IGuiElement scrollTo(final int yOffset) {
        /**
         * We have to negate the yOffset here
         * because {@link #scrollToElement(int)} substracts the offset
         */
        return scrollToElement(yOffset*-1);
    }
}

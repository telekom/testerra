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
 *     Peter Lehmann
 *     pele
 */
/*
 * Created on 04.01.2013
 *
 * Copyright(c) 2012 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.logging.LogLevel;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Hierarchy;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AssertionProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultRectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertDescriptionDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantifiedPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.RectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertion;
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
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * GuiElement is the access point for most tests and is an extension of WebElement.
 * <p>
 * Authors: pele, rnhb
 */
public class GuiElement implements
    IGuiElement,
    Loggable,
    Hierarchy<IGuiElement>
{

    protected static final PropertyAssertionFactory propertyAssertionFactory = Testerra.ioc().getInstance(PropertyAssertionFactory.class);

    private GuiElementAssert defaultAssert;
    private GuiElementAssert instantAssert;
    private GuiElementAssert collectableAssert;
    private GuiElementAssert nonFunctionalAssert;

    /**
     * Facade for all parts of the lowest GuiElement
     */
    private GuiElementFacade guiElementFacade;
    private GuiElementCore guiElementCore;
    private GuiElementWait guiElementWait;
    private Locate locate;
    private final GuiElementData guiElementData;
    protected Object parent;

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
     */
    @Deprecated
    public GuiElement(WebDriver driver, By by) {
        this(driver, by, null);
    }

    @Deprecated
    public GuiElement(WebDriver driver, Locate locate) {
        this(driver, locate, null);
    }

    @Deprecated
    public GuiElement(IPage page, Locate locate) {
        this(page.getWebDriver(), locate, null);
        Page pageImpl = (Page)page;
        this.setTimeoutInSeconds(pageImpl.getElementTimeoutInSeconds());
        setParent(page);
    }

    /**
     * Constructor with frames and explicit webDriver session.
     */
    @Deprecated
    public GuiElement(
        WebDriver driver,
        Locate locate,
        IGuiElement... frames
    ) {
        IFrameLogic frameLogic = null;
        if (frames != null && frames.length > 0) {
            frameLogic = new FrameLogic(driver, frames);
        }
        By by = locate.getBy();
        guiElementData = new GuiElementData(driver, "", frameLogic, by, this);
        buildInternals(driver, by);
        this.locate = locate;
    }

    /**
     * Default Constructor for optional FrameLogic
     */
    @Deprecated
    public GuiElement(
        WebDriver driver,
        By by,
        IGuiElement... frames
    ) {
        this(driver, Locate.by(by), frames);
    }

    /**
     * Constructor with ancestor GuiElement
     */
    @Deprecated
    public GuiElement(
        Locate locate,
        IGuiElement ancestor
    ) {
        // Use FrameLogic from parent
        GuiElement ancestorGuiElement = (GuiElement)ancestor;
        IFrameLogic frameLogic = null;
        if (ancestorGuiElement.guiElementData.frameLogic != null) {
            frameLogic = ancestorGuiElement.guiElementData.frameLogic;
        }

        String abstractLocatorString = locate.getBy().toString();
        if (abstractLocatorString.toLowerCase().contains("xpath")) {
            int i = abstractLocatorString.indexOf(":") + 1;
            String xpath = abstractLocatorString.substring(i).trim();
            String prevXPath = xpath;
            // Check if locator does not start with dot, ignoring a leading parenthesis for choosing the n-th element
            if (xpath.startsWith("/")) {
                xpath = xpath.replaceFirst("/", "./");
                log().warn(String.format("Replaced absolute xpath locator \"%s\" to relative: \"%s\"", prevXPath, xpath));
                locate = Locate.by(By.xpath(xpath));
            } else if (!xpath.startsWith(".")) {
                xpath = "./" + xpath;
                log().warn(String.format("Added relative xpath locator for children to \"%s\": \"%s\"", prevXPath, xpath));
                locate = Locate.by(By.xpath(xpath));
            }
        }

        By by = locate.getBy();
        guiElementData = new GuiElementData(ancestor.getWebDriver(), "", frameLogic, by, this);
        guiElementData.parent = ancestorGuiElement.guiElementCore;
        setParent(ancestorGuiElement);
        buildInternals(ancestor.getWebDriver(), by);
        this.locate = locate;
    }

    @Override
    public Locate getLocate() {
        return locate;
    }

    private void buildInternals(WebDriver driver, By by) {
        // Create core depending on requested Browser
        WebDriverRequest webDriverRequest = WebDriverManager.getRelatedWebDriverRequest(driver);
        String currentBrowser = webDriverRequest.browser;
        guiElementData.browser = currentBrowser;

        GuiElementCoreFactory coreFactory = Testerra.ioc().getInstance(GuiElementCoreFactory.class);
        guiElementCore = coreFactory.create(currentBrowser, by, driver, guiElementData);

        GuiElementWaitFactory waitFactory = Testerra.ioc().getInstance(GuiElementWaitFactory.class);
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

        int delayAfterAction = Properties.DELAY_AFTER_ACTION_MILLIS.asLong().intValue();
        int delayBeforeAction = Properties.DELAY_BEFORE_ACTION_MILLIS.asLong().intValue();
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
            Collections.addAll(locate.getFilters(), filters);
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
    @Deprecated
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

    public GuiElement getSubElement(Locate locator) {
        return (GuiElement)guiElementFacade.getSubElement(locator);
    }

    @Override
    public WebElement getWebElement() {
        return guiElementFacade.getWebElement();
    }

    public By getBy() {
        return guiElementFacade.getBy();
    }

    public GuiElement scrollToElement() {
        guiElementFacade.scrollToElement();
        return this;
    }

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

    public IGuiElement clickAbsolute() {
        guiElementData.setLogLevel(LogLevel.INFO);
        guiElementFacade.clickAbsolute();
        guiElementData.resetLogLevel();
        return this;
    }

    public IGuiElement mouseOverAbsolute2Axis() {
        guiElementFacade.mouseOverAbsolute2Axis();
        return this;
    }

    public IGuiElement submit() {
        guiElementFacade.submit();
        return this;
    }

    @Override
    public IGuiElement sendKeys(CharSequence... charSequences) {
        int cpm = Properties.USER_INPUT_CHARACTERS_PER_MINUTE.asLong().intValue();
        if (cpm > 0) {
            return userSendKeys(cpm, charSequences);
        } else {
            guiElementData.setLogLevel(LogLevel.INFO);
            guiElementFacade.sendKeys(charSequences);
            guiElementData.resetLogLevel();
        }
        return this;
    }

    private IGuiElement userSendKeys(int cpm, CharSequence... charSequences) {
        float cps = cpm/60;
        if (cps <= 0) cps = 1;
        int cpsSleepMs = Math.round(1000/cps);
        final WebElement webElement = guiElementCore.findWebElement();
        for (CharSequence charSequence : charSequences) {
            charSequence.codePoints().forEach(codePoint -> {
                webElement.sendKeys(new String(Character.toChars(codePoint)));
                TimerUtils.sleepSilent(cpsSleepMs);
            });
        }
        return this;
    }

    @Override
    public IGuiElement clear() {
        guiElementFacade.clear();
        return this;
    }

    public String getTagName() {
        return guiElementFacade.getTagName();
    }

    public String getAttribute(String attributeName) {
        return guiElementFacade.getAttribute(attributeName);
    }

    public boolean isSelected() {
        return guiElementFacade.isSelected();
    }

    public boolean isEnabled() {
        return guiElementFacade.isEnabled();
    }

    public String getText() {
        return guiElementFacade.getText();
    }

    public boolean isDisplayed() {
        return guiElementFacade.isDisplayed();
    }

    public boolean isVisible(final boolean complete) {
        return guiElementFacade.isVisible(complete);
    }

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

    public boolean isPresent() {
        return guiElementFacade.isPresent();
    }

    public Select getSelectElement() {
        return guiElementFacade.getSelectElement();
    }

    public List<String> getTextsFromChildren() {
        return guiElementFacade.getTextsFromChildren();
    }

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

    /**
     * @deprecated This method should not be public
     */
    @Deprecated
    public int getTimeoutInSeconds() {
        return guiElementData.getTimeoutInSeconds();
    }

    /**
     * @deprecated This method should not be public
     */
    @Deprecated
    public IGuiElement setTimeoutInSeconds(int timeoutInSeconds) {
        guiElementData.setTimeoutInSeconds(timeoutInSeconds);
        return this;
    }

    /**
     * @deprecated This method should not be public
     */
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

    public int getLengthOfValueAfterSendKeys(String textToInput) {
        return guiElementFacade.getLengthOfValueAfterSendKeys(textToInput);
    }

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
     * Sets the abstract parent
     * @param parent {@link IGuiElement} or {@link IPage}
     */
    @Override
    public IGuiElement setParent(Object parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Retrieves the parent
     * @return Can be {@link IGuiElement} or {@link IPage}
     */
    @Override
    public Object getParent() {
        return parent;
    }

    @Deprecated
    public List<WebElementFilter> getWebElementFilters() {
        return locate.getFilters();
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
    @Deprecated
    public GuiElementAssert asserts() {
        if (defaultAssert == null) {
            if (IGuiElement.Properties.DEFAULT_ASSERT_IS_COLLECTOR.asBool()) {
                defaultAssert = assertCollector();
            } else {
                defaultAssert = instantAsserts();
            }
        }
        return defaultAssert;
    }

    /**
     * Provides access to all assert methods. If an assertion fails, the assertDescription will be given as cause,
     * instead of the technical cause like an isDisplayed error.
     *
     * @param errorMessage Cause returned on assertion error.
     *
     * @return GuiElementAssert object for functional assertions
     */
    @Deprecated
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
    @Deprecated
    public GuiElementAssert nonFunctionalAsserts() {
        if (nonFunctionalAssert==null) {
            GuiElementAssertFactory assertFactory = Testerra.ioc().getInstance(GuiElementAssertFactory.class);
            NonFunctionalAssertion assertion = Testerra.ioc().getInstance(NonFunctionalAssertion.class);
            nonFunctionalAssert = assertFactory.create(assertion, guiElementCore, guiElementWait, guiElementData);
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
    @Deprecated
    public GuiElementAssert nonFunctionalAsserts(String errorMessage) {
        GuiElementAssertDescriptionDecorator guiElementAssertDescriptionDecorator
                = new GuiElementAssertDescriptionDecorator(errorMessage, nonFunctionalAsserts());
        return guiElementAssertDescriptionDecorator;
    }

    @Deprecated
    public GuiElementAssert instantAsserts() {
        if (instantAssert == null) {
            GuiElementAssertFactory assertFactory = Testerra.ioc().getInstance(GuiElementAssertFactory.class);
            InstantAssertion assertion = Testerra.ioc().getInstance(InstantAssertion.class);
            instantAssert = assertFactory.create(assertion, guiElementCore, guiElementWait, guiElementData);
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
        if (collectableAssert==null) {
            GuiElementAssertFactory assertFactory = Testerra.ioc().getInstance(GuiElementAssertFactory.class);
            CollectedAssertion assertion = Testerra.ioc().getInstance(CollectedAssertion.class);
            collectableAssert = assertFactory.create(assertion, guiElementCore, guiElementWait, guiElementData);
        }
        return collectableAssert;
    }

    /**
     * Provides access to all assert methods. If an assertion fails, the assertDescription will be given as cause,
     * instead of the technical cause like an isDisplayed error.
     *
     * @param errorMessage Cause returned on assertion error.
     *
     * @return GuiElementAssert object for functional assertions
     */
    @Deprecated
    public GuiElementAssert assertCollector(String errorMessage) {
        GuiElementAssertDescriptionDecorator guiElementAssertDescriptionDecorator
                = new GuiElementAssertDescriptionDecorator(errorMessage, assertCollector());
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
    public StringPropertyAssertion<String> tagName() {
        final IGuiElement self = this;
        return propertyAssertionFactory.string(new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return guiElementCore.findWebElement().getTagName();
            }

            @Override
            public String getSubject() {
                return String.format("%s.tagName", self);
            }
        });
    }

    @Override
    public StringPropertyAssertion<String> text() {
        final IGuiElement self = this;
        return propertyAssertionFactory.string(new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return guiElementCore.findWebElement().getText();
            }

            @Override
            public String getSubject() {
                return String.format("%s.text", self);
            }
        });
    }

    @Override
    public StringPropertyAssertion<String> value() {
        return value(Attribute.VALUE);
    }

    @Override
    public StringPropertyAssertion<String> value(final Attribute attribute) {
        return value(attribute.toString());
    }

    @Override
    public StringPropertyAssertion<String> value(String attribute) {
        final IGuiElement self = this;
        return propertyAssertionFactory.string(new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return guiElementCore.findWebElement().getAttribute(attribute);
            }

            @Override
            public String getSubject() {
                return String.format("%s.value(attribute: %s)", self, attribute);
            }
        });
    }

    @Override
    public StringPropertyAssertion<String> css(String property) {
        final IGuiElement self = this;
        return propertyAssertionFactory.string(new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return getCssValue(property);
            }

            @Override
            public String getSubject() {
                return String.format("%s.css(property: %s)", self, property);
            }
        });
    }

    @Override
    public BinaryPropertyAssertion<Boolean> present() {
        final IGuiElement self = this;
        return propertyAssertionFactory.binary(new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                try {
                    return guiElementCore.findWebElement()!=null;
                } catch (ElementNotFoundException e) {
                    return false;
                }
            }

            @Override
            public String getSubject() {
                return String.format("%s.present", self);
            }
        });
    }

    @Override
    public BinaryPropertyAssertion<Boolean> visible(boolean complete) {
        final IGuiElement self = this;
        return propertyAssertionFactory.binary(new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return guiElementCore.isVisible(complete);
            }

            @Override
            public String getSubject() {
                return String.format("%s.visible(complete: %s)", self, complete);
            }
        });
    }

    @Override
    public BinaryPropertyAssertion<Boolean> displayed() {
        final IGuiElement self = this;
        return propertyAssertionFactory.binary(new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return guiElementCore.findWebElement().isDisplayed();
            }

            @Override
            public String getSubject() {
                return String.format("%s.displayed", self);
            }
        });
    }

    @Override
    public BinaryPropertyAssertion<Boolean> enabled() {
        final IGuiElement self = this;
        return propertyAssertionFactory.binary(new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return guiElementCore.findWebElement().isEnabled();
            }

            @Override
            public String getSubject() {
                return String.format("%s.enabled", self);
            }
        });
    }

    @Override
    public BinaryPropertyAssertion<Boolean> selected() {
        final IGuiElement self = this;
        return propertyAssertionFactory.binary(new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return guiElementCore.findWebElement().isSelected();
            }

            @Override
            public String getSubject() {
                return String.format("%s.selected", self);
            }
        });
    }

    @Override
    public RectAssertion bounds() {
        final IGuiElement self = this;
        return new DefaultRectAssertion(null, new AssertionProvider<Rectangle>() {
            @Override
            public Rectangle getActual() {
                return guiElementCore.findWebElement().getRect();
            }

            @Override
            public String getSubject() {
                return String.format("%s.bounds", self);
            }
        });
    }

    @Override
    public QuantifiedPropertyAssertion<Integer> numberOfElements() {
        final IGuiElement self = this;
        return propertyAssertionFactory.quantified(new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                return getNumberOfFoundElements();
            }

            @Override
            public String getSubject() {
                return String.format("%s.numberOfElements", self);
            }
        });
    }

    @Override
    public ImageAssertion screenshot() {
        final IGuiElement self = this;
        final AtomicReference<File> screenshot = new AtomicReference<>();
        screenshot.set(guiElementCore.takeScreenshot());
        return propertyAssertionFactory.image(new AssertionProvider<File>() {
            @Override
            public File getActual() {
                return screenshot.get();
            }

            @Override
            public void failed(PropertyAssertion assertion) {
                screenshot.set(guiElementCore.takeScreenshot());
            }

            @Override
            public String getSubject() {
                return String.format("%s.screenshot", self);
            }
        });
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

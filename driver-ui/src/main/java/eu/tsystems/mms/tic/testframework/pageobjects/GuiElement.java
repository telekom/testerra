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
import eu.tsystems.mms.tic.testframework.pageobjects.internal.HasParent;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AssertionProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultBinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultQuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultRectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultStringAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertDescriptionDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.RectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.AbstractGuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCoreFrameAwareDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCoreSequenceDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.DefaultGuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.DelayActionsGuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacadeLoggingDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWaitFactory;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * GuiElement is the access point for most tests and is an extension of WebElement.
 * <p>
 * Authors: pele, rnhb
 */
public class GuiElement implements
    IGuiElement,
    Loggable
{
    private static final PropertyAssertionFactory propertyAssertionFactory = Testerra.injector.getInstance(PropertyAssertionFactory.class);
    private static final GuiElementFactory guiElementFactory = Testerra.injector.getInstance(GuiElementFactory.class);

    private GuiElementAssert defaultAssert;
    private GuiElementAssert instantAssert;
    private GuiElementAssert collectableAssert;
    private GuiElementAssert nonFunctionalAssert;

    public final GuiElementCore core;
    public final GuiElementData guiElementData;

    /**
     * Facade for all parts of the lowest GuiElement
     */
    private GuiElementFacade decoratedFacade;
    private GuiElementCore decoratedCore;
    private GuiElementWait decoratedWait;

    protected HasParent parent;
    private int iteratorIndex = 0;
    private int iteratorSize = 0;

    /**
     * Elementary constructor
     */
    private GuiElement(GuiElementData data) {
        guiElementData = data;
        guiElementData.setGuiElement(this);
        IWebDriverFactory factory = WebDriverSessionsManager.getWebDriverFactory(guiElementData.getBrowser());
        core = factory.createCore(guiElementData);
    }

    /**
     * Constructor for list elements of {@link #element(int)}
     * Elements created by this constructor are identical to it's parent,
     * but with a different element index.
     */
    private GuiElement(GuiElement guiElement, int index) {
        this(new GuiElementData(guiElement.guiElementData, index));
        setParent(guiElement.getParent());
        createDecoratorFacades();
    }

    /**
     * Constructor for {@link GuiElementFactory#createFromParent(IGuiElement, Locate)}
     * This is the internal standard constructor for elements with parent {@link GuiElementCore} implementations.
     */
    public GuiElement(GuiElementCore core) {
        this.core = core;
        AbstractGuiElementCore realCore = (AbstractGuiElementCore)core;
        guiElementData = realCore.guiElementData;
        guiElementData.setGuiElement(this);
        createDecoratorFacades();
    }

    /**
     * Constructor for {@link GuiElementFactory#createWithPage(PageObject, Locate)}
     */
    public GuiElement(PageObject page, Locate locate) {
        this(page.getWebDriver(), locate, null);
        Page realPage = (Page)page;
        setTimeoutInSeconds(realPage.getElementTimeoutInSeconds());
        setParent(page);
    }

    /**
     * Constructor for {@link GuiElementFactory#createWithFrames(Locate, IGuiElement...)}
     */
    public GuiElement(
        WebDriver driver,
        Locate locate,
        IGuiElement... frames
    ) {
        this(new GuiElementData(driver, locate));
        guiElementData.setGuiElement(this);
        if (frames != null && frames.length > 0) {
            guiElementData.setFrameLogic(new FrameLogic(driver, frames));
        }
        createDecoratorFacades();
    }

    @Deprecated
    public GuiElement(WebDriver driver, By by) {
        this(driver, by, null);
    }

    @Deprecated
    public GuiElement(WebDriver driver, Locate locate) {
        this(driver, locate, null);
    }

    @Deprecated
    public GuiElement(
        WebDriver driver,
        By by,
        IGuiElement... frames
    ) {
        this(driver, Locate.by(by), frames);
    }

    @Override
    public Locate getLocate() {
        return guiElementData.getLocate();
    }

    private void createDecoratorFacades() {
        decoratedCore = core;

        if (guiElementData.hasFrameLogic()) {
            // if frames are set, the waiter should use frame switches when executing its sequences
            decoratedCore = new GuiElementCoreFrameAwareDecorator(decoratedCore, guiElementData);
        }
        // Wrap the core with sequence decorator, such that its methods are executed with sequence
        decoratedCore = new GuiElementCoreSequenceDecorator(decoratedCore, guiElementData);

        GuiElementWaitFactory waitFactory = Testerra.injector.getInstance(GuiElementWaitFactory.class);
        decoratedWait = waitFactory.create(guiElementData);

        decoratedFacade = createFacade(decoratedCore);
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
            Collections.addAll(guiElementData.getLocate().getFilters(), filters);
        }
        return this;
    }

    /**
     * Add a Decorator to log every action performed on this element with addition to a short description of it.
     *
     * @param description A very short description of this GuiElement, for example "Continue Shopping Button"
     *
     * @deprecated use {@link #setName(String)} instead.
     */
    @Deprecated
    public GuiElement setDescription(String description) {
        guiElementData.setName(description);
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
        GuiElement subElement = getSubElement(Locate.by(by));
        if (description != null) subElement.setName(description);
        return subElement;
    }

    @Deprecated
    public GuiElement getSubElement(Locate locate) {
        return (GuiElement)find(locate);
    }

    @Override
    public WebElement getWebElement() {
        return decoratedFacade.getWebElement();
    }

    public By getBy() {
        return decoratedFacade.getBy();
    }

    public GuiElement scrollToElement() {
        decoratedFacade.scrollToElement();
        return this;
    }

    public IGuiElement scrollToElement(int yOffset) {
        decoratedFacade.scrollToElement(yOffset);
        return this;
    }

    @Override
    public IGuiElement select() {
        guiElementData.setLogLevel(LogLevel.INFO);
        decoratedFacade.select();
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
            decoratedFacade.select();
        } else {
            decoratedFacade.deselect();
        }
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public IGuiElement deselect() {
        guiElementData.setLogLevel(LogLevel.INFO);
        decoratedFacade.deselect();
        guiElementData.resetLogLevel();
        return this;
    }

    public IGuiElement type(String text) {
        guiElementData.setLogLevel(LogLevel.INFO);
        decoratedCore.type(text);
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public IGuiElement click() {
        guiElementData.setLogLevel(LogLevel.INFO);
        decoratedFacade.click();
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public IGuiElement clickJS() {
        guiElementData.setLogLevel(LogLevel.INFO);
        decoratedFacade.clickJS();
        guiElementData.resetLogLevel();
        return this;
    }

    public IGuiElement clickAbsolute() {
        guiElementData.setLogLevel(LogLevel.INFO);
        decoratedFacade.clickAbsolute();
        guiElementData.resetLogLevel();
        return this;
    }

    public IGuiElement mouseOverAbsolute2Axis() {
        decoratedFacade.mouseOverAbsolute2Axis();
        return this;
    }

    public IGuiElement submit() {
        decoratedFacade.submit();
        return this;
    }

    @Override
    public IGuiElement sendKeys(CharSequence... charSequences) {
        guiElementData.setLogLevel(LogLevel.INFO);
        decoratedFacade.sendKeys(charSequences);
        guiElementData.resetLogLevel();
        return this;
    }

    @Override
    public IGuiElement userSendKeys(int cpm, CharSequence... charSequences) {
        float cps = cpm/60;
        if (cps <= 0) cps = 1;
        int cpsSleepMs = Math.round(1000/cps);
        final WebElement webElement = core.findWebElement();
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
        decoratedFacade.clear();
        return this;
    }

    @Override
    public InteractiveGuiElement hover() {
        return mouseOver();
    }

    public String getTagName() {
        return decoratedFacade.getTagName();
    }

    public String getAttribute(String attributeName) {
        return decoratedFacade.getAttribute(attributeName);
    }

    public boolean isSelected() {
        return decoratedFacade.isSelected();
    }

    public boolean isEnabled() {
        return decoratedFacade.isEnabled();
    }

    public String getText() {
        return decoratedFacade.getText();
    }

    public boolean isDisplayed() {
        return decoratedFacade.isDisplayed();
    }

    public boolean isVisible(final boolean complete) {
        return decoratedFacade.isVisible(complete);
    }

    public boolean isDisplayedFromWebElement() {
        return decoratedFacade.isDisplayedFromWebElement();
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
        return decoratedFacade.isSelectable();
    }

    @Override
    public Point getLocation() {
        return decoratedFacade.getLocation();
    }

    @Override
    public Dimension getSize() {
        return decoratedFacade.getSize();
    }

    @Override
    public IGuiElement find(Locate locate) {
        return guiElementFactory.createFromParent(this, locate);
    }

    @Override
    public IGuiElement element(int position) {
        if (position < 1) position = 1;
        return new GuiElement(this, position-1);
    }

    public String getCssValue(String cssIdentifier) {
        return decoratedFacade.getCssValue(cssIdentifier);
    }

    public IGuiElement mouseOver() {
        decoratedFacade.mouseOver();
        return this;
    }

    public IGuiElement mouseOverJS() {
        decoratedFacade.mouseOverJS();
        return this;
    }

    public boolean isPresent() {
        return decoratedFacade.isPresent();
    }

    public Select getSelectElement() {
        return decoratedFacade.getSelectElement();
    }

    public List<String> getTextsFromChildren() {
        return decoratedFacade.getTextsFromChildren();
    }

    public boolean anyFollowingTextNodeContains(String contains) {
        TestableGuiElement<IGuiElement> textElements = anyElementContainsText(contains);
        return textElements.present().getActual();
    }

    /**
     * This method is no part of any interface, because we don't know if
     * we want to support this feature at the moment.
     */
    public TestableGuiElement<IGuiElement> anyElementContainsText(String text) {
        String textFinderXpath = String.format("//text()[contains(., '%s')]/..", text);
        return find(By.xpath(textFinderXpath));
    }

    @Override
    public WebDriver getWebDriver() {
        return guiElementData.getWebDriver();
    }

    @Override
    public IGuiElement doubleClick() {
        decoratedFacade.doubleClick();
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
        decoratedFacade.highlight();
        return this;
    }

    public IGuiElement swipe(int offsetX, int offSetY) {
        decoratedFacade.swipe(offsetX, offSetY);
        return this;
    }

    public int getLengthOfValueAfterSendKeys(String textToInput) {
        return decoratedFacade.getLengthOfValueAfterSendKeys(textToInput);
    }

    public int getNumberOfFoundElements() {
        return decoratedFacade.getNumberOfFoundElements();
    }

    @Override
    public IGuiElement rightClick() {
        decoratedFacade.rightClick();
        return this;
    }

    @Override
    public IGuiElement rightClickJS() {
        decoratedFacade.rightClickJS();
        return this;
    }

    @Override
    public IGuiElement doubleClickJS() {
        decoratedFacade.doubleClickJS();
        return this;
    }

    @Deprecated
    public File takeScreenshot() {
        return decoratedFacade.takeScreenshot();
    }

    /**
     * Get Frame Login object. It encapsulates the correct order of switching the frames containing this GuiElement.
     *
     * @return Frame Logic object or null.
     */
    @Override
    public IFrameLogic getFrameLogic() {
        return guiElementData.getFrameLogic();
    }

    /**
     * Sets the abstract parent
     * @param parent {@link IGuiElement} or {@link PageObject}
     */
    public IGuiElement setParent(HasParent parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Retrieves the parent
     * @return Can be {@link IGuiElement} or {@link PageObject}
     */
    @Override
    public HasParent getParent() {
        return parent;
    }

    @Deprecated
    public List<WebElementFilter> getWebElementFilters() {
        return guiElementData.getLocate().getFilters();
    }

    @Override
    public String toString() {
        return guiElementData.toString();
    }

    @Override
    public String toString(boolean details) {
        return guiElementData.toString(details);
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
        guiElementData.setName(name);
        return this;
    }

    @Override
    public String getName() {
        return guiElementData.getName();
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
            GuiElementAssertFactory assertFactory = Testerra.injector.getInstance(GuiElementAssertFactory.class);
            NonFunctionalAssertion assertion = Testerra.injector.getInstance(NonFunctionalAssertion.class);
            nonFunctionalAssert = assertFactory.create(guiElementData, assertion, decoratedWait);
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
            GuiElementAssertFactory assertFactory = Testerra.injector.getInstance(GuiElementAssertFactory.class);
            InstantAssertion assertion = Testerra.injector.getInstance(InstantAssertion.class);
            instantAssert = assertFactory.create(guiElementData, assertion, decoratedWait);
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
            GuiElementAssertFactory assertFactory = Testerra.injector.getInstance(GuiElementAssertFactory.class);
            CollectedAssertion assertion = Testerra.injector.getInstance(CollectedAssertion.class);
            collectableAssert = assertFactory.create(guiElementData, assertion, decoratedWait);
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

    @Deprecated
    public List<GuiElement> getList() {
        int numberOfFoundElements = getNumberOfFoundElements();
        List<GuiElement> guiElements = new ArrayList<>(numberOfFoundElements);
        for (int i = 0; i < numberOfFoundElements; i++) {
            IGuiElement guiElement = element(i+1);
            guiElements.add((GuiElement)guiElement);
        }
        return guiElements;
    }

    @Deprecated
    public LogLevel getLogLevel() {
        return guiElementData.getLogLevel();
    }

    @Deprecated
    public void setLogLevel(LogLevel logLevel) {
        guiElementData.setLogLevel(logLevel);
    }

    @Deprecated
    public IGuiElement shadowRoot() {
        guiElementData.shadowRoot = true;
        return this;
    }

    /**
     * Provides access to all wait methods
     */
    public GuiElementWait waits() {
        return decoratedWait;
    }

    @Override
    public StringAssertion<String> tagName() {
        final IGuiElement self = this;
        return propertyAssertionFactory.create(DefaultStringAssertion.class, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.findWebElement().getTagName();
            }

            @Override
            public String getSubject() {
                return String.format("%s.tagName", self);
            }
        });
    }

    @Override
    public StringAssertion<String> text() {
        final IGuiElement self = this;
        return propertyAssertionFactory.create(DefaultStringAssertion.class, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.findWebElement().getText();
            }

            @Override
            public String getSubject() {
                return String.format("%s.text", self);
            }
        });
    }

    @Override
    public StringAssertion<String> value() {
        return value(Attribute.VALUE);
    }

    @Override
    public StringAssertion<String> value(final Attribute attribute) {
        return value(attribute.toString());
    }

    @Override
    public StringAssertion<String> value(String attribute) {
        final IGuiElement self = this;
        return propertyAssertionFactory.create(DefaultStringAssertion.class, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.findWebElement().getAttribute(attribute);
            }

            @Override
            public String getSubject() {
                return String.format("%s.value(attribute: %s)", self, attribute);
            }
        });
    }

    @Override
    public StringAssertion<String> css(String property) {
        final IGuiElement self = this;
        return propertyAssertionFactory.create(DefaultStringAssertion.class, new AssertionProvider<String>() {
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
    public BinaryAssertion<Boolean> present() {
        final IGuiElement self = this;
        return propertyAssertionFactory.create(DefaultBinaryAssertion.class, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                try {
                    return core.findWebElement()!=null;
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
    public BinaryAssertion<Boolean> visible(boolean complete) {
        final IGuiElement self = this;
        return propertyAssertionFactory.create(DefaultBinaryAssertion.class, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isVisible(complete);
            }

            @Override
            public String getSubject() {
                return String.format("%s.visible(complete: %s)", self, complete);
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> displayed() {
        final IGuiElement self = this;
        BinaryAssertion<Boolean> prop = propertyAssertionFactory.create(DefaultBinaryAssertion.class, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                try {
                    return core.findWebElement().isDisplayed();
                } catch (ElementNotFoundException e) {
                    return false;
                }
            }

            @Override
            public String getSubject() {
                return String.format("%s.displayed", self);
            }
        });
        return prop;
    }

    @Override
    public BinaryAssertion<Boolean> enabled() {
        final IGuiElement self = this;
        return propertyAssertionFactory.create(DefaultBinaryAssertion.class, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.findWebElement().isEnabled();
            }

            @Override
            public String getSubject() {
                return String.format("%s.enabled", self);
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> selected() {
        final IGuiElement self = this;
        return propertyAssertionFactory.create(DefaultBinaryAssertion.class, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.findWebElement().isSelected();
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
        return propertyAssertionFactory.create(DefaultRectAssertion.class, new AssertionProvider<Rectangle>() {
            @Override
            public Rectangle getActual() {
                return core.findWebElement().getRect();
            }

            @Override
            public String getSubject() {
                return String.format("%s.bounds", self);
            }
        });
    }

    @Override
    public QuantityAssertion<Integer> numberOfElements() {
        final IGuiElement self = this;
        return propertyAssertionFactory.create(DefaultQuantityAssertion.class, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                try {
                    return core.findWebElements().size();
                } catch (ElementNotFoundException e) {
                    return 0;
                }
            }

            @Override
            public String getSubject() {
                return String.format("%s.numberOfElements", self);
            }
        });
    }

    @Override
    public TestableGuiElement waitFor() {
        propertyAssertionFactory.nextShouldWait();
        return this;
    }

    @Override
    public ImageAssertion screenshot() {
        final IGuiElement self = this;
        final AtomicReference<File> screenshot = new AtomicReference<>();
        screenshot.set(core.takeScreenshot());
        return propertyAssertionFactory.create(DefaultImageAssertion.class, new AssertionProvider<File>() {
            @Override
            public File getActual() {
                return screenshot.get();
            }

            @Override
            public void failed(PropertyAssertion assertion) {
                screenshot.set(core.takeScreenshot());
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

    @Override
    public Iterator<IGuiElement> iterator() {
        iteratorIndex = 0;
        iteratorSize = core.findWebElements().size();
        return this;
    }

    @Override
    public boolean hasNext() {
        return iteratorIndex < iteratorSize;
    }

    @Override
    public IGuiElement next() {
        return element(iteratorIndex++);
    }
}

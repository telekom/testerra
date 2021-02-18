/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.OptionalAssertion;
import eu.tsystems.mms.tic.testframework.internal.Nameable;
import eu.tsystems.mms.tic.testframework.internal.NameableChild;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.DefaultLocator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.LegacyGuiElementAssertWrapper;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultUiElementAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertDescriptionDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PerformanceTestGuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.UiElementAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.AbstractGuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCoreSequenceDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.DelayActionsGuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.UiElementLogger;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.DefaultGuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.webdriver.IWebDriverFactory;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * GuiElement is the access point for most tests and is an extension of WebElement.
 * <p>
 * Authors: pele, rnhb
 */
public class GuiElement implements UiElement, NameableChild<UiElement>, Loggable, WebDriverManagerProvider {
    /**
     * Factory required for {@link UiElementFinder}
     */
    private static final UiElementFactory uiElementFactory = Testerra.getInjector().getInstance(UiElementFactory.class);

    /**
     * Get lazy initialized by {@link #asserts()}
     * Is one of:
     *  {@link #collectableAssert}
     *  {@link #instantAssert}
     */
    private GuiElementAssert defaultAssert;
    /**
     * Gets lazy initialized by {@link #instantAsserts()}
     */
    private GuiElementAssert instantAssert;
    /**
     * Gets lazy initialized by {@link #assertCollector()}
     */
    private GuiElementAssert collectableAssert;
    /**
     * Gets lazy initialized by {@link #nonFunctionalAssert}
     */
    private GuiElementAssert nonFunctionalAssert;

    /**
     * This is the raw core implementation of {@link GuiElementCore}
     */
    private final GuiElementCore core;
    private DefaultUiElementAssertion assertions;
    private DefaultUiElementAssertion waits;

    /**
     * Contains the elements base information and the {@link GuiElementData} hierarchy only.
     */
    private final GuiElementData guiElementData;
    /**
     * This facade contains
     *  {@link #core}
     *  {@link GuiElementCoreSequenceDecorator}
     *  {@link UiElementLogger}
     *  {@link DelayActionsGuiElementFacade}
     */
    private GuiElementCore decoratedCore;
    private GuiElementWait decoratedWait;

    /**
     * Contains the {@link Nameable} hierarchy for {@link UiElement} and {@link PageObject}.
     */
    private Nameable parent;

    /**
     * Gets lazy initialized by {@link #list()}
     */
    private DefaultUiElementList list;

    /**
     * Elementary constructor
     */
    private GuiElement(GuiElementData data) {
        guiElementData = data;
        guiElementData.setGuiElement(this);
        IWebDriverFactory factory = WEB_DRIVER_MANAGER.getWebDriverFactoryForBrowser(WEB_DRIVER_MANAGER.getRequestedBrowser(guiElementData.getWebDriver()).orElse(null));
        this.core = factory.createCore(guiElementData);
    }

    /**
     * Package private constructor for list elements of {@link UiElementList}.
     * Elements created by this constructor are identical to it's parent,
     * but with a different element index.
     */
    GuiElement(GuiElement guiElement, int index) {
        this(new GuiElementData(guiElement.guiElementData, index));
        setParent(guiElement.getParent());
        createDecorators();
    }

    /**
     * Package private constructor for {@link UiElementFactory#createFromParent(UiElement, Locator)}
     * This is the internal standard constructor for elements with parent {@link GuiElementCore} implementations.
     */
    GuiElement(GuiElementCore core) {
        this.core = core;
        AbstractGuiElementCore realCore = (AbstractGuiElementCore)core;
        guiElementData = realCore.guiElementData;
        guiElementData.setGuiElement(this);
        createDecorators();
    }

    /**
     * @deprecated Use {@link UiElementFactory#createWithWebDriver(WebDriver, Locator)}} instead
     */
    @Deprecated
    public GuiElement(WebDriver driver, Locator locator) {
        this(new GuiElementData(driver, locator));
        guiElementData.setGuiElement(this);
        createDecorators();
    }

    /**
     * @deprecated Use {@link UiElementFactory#createWithWebDriver(WebDriver, Locator)}} instead
     */
    @Deprecated
    public GuiElement(WebDriver driver, By by) {
        this(driver, LOCATE.by(by));
    }

    public GuiElementCore getCore() {
        return this.core;
    }

    public GuiElementData getData() {
        return this.guiElementData;
    }

    @Override
    public Locator getLocator() {
        return guiElementData.getLocate();
    }

    /**
     * We cannot use the GuiElementFactory for decorating the facade here,
     * since GuiElement is not always created by it's according factory
     * and implementing a GuiElementFacadeFactory is useless.
     * You can move this code to DefaultGuiElementFactory when no more 'new GuiElement()' calls exists.
     */
    private void createDecorators() {
        // Wrap the core with sequence decorator, such that its methods are executed with sequence
        GuiElementCore sequenceCore = new GuiElementCoreSequenceDecorator(core);
        decoratedCore = new UiElementLogger(sequenceCore, this);

        int delayAfterAction = Properties.DELAY_AFTER_ACTION_MILLIS.asLong().intValue();
        int delayBeforeAction = Properties.DELAY_BEFORE_ACTION_MILLIS.asLong().intValue();
        if (delayAfterAction > 0 || delayBeforeAction > 0) {
            decoratedCore = new DelayActionsGuiElementFacade(decoratedCore, delayBeforeAction, delayAfterAction);
        }
    }

    /**
     * After retrieving all WebElements of the given locator, this method can be used to filter the WebElements.
     * Different filters can be applied in conjunction (and).
     *
     * @param filter Filters to be applied
     *
     * @return The same GuiElement
     * @deprecated Use {@link DefaultLocator} instead
     */
    @Deprecated
    public GuiElement withWebElementFilter(Predicate<WebElement> filter) {
        guiElementData.getLocate().filter(filter);
        return this;
    }

    /**
     * Add a Decorator to log every action performed on this element with addition to a short description of it.
     *
     * @param description A very short description of this GuiElement, for example "Continue Shopping Button"
     *
     * @deprecated Use {@link #setName(String)} instead
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
        GuiElement subElement = getSubElement(LOCATE.by(by));
        if (description != null) subElement.setName(description);
        return subElement;
    }

    @Deprecated
    public GuiElement getSubElement(Locator locator) {
        return (GuiElement)find(locator);
    }

    @Override
    public UiElement scrollIntoView(Point offset) {
        decoratedCore.scrollIntoView(offset);
        return this;
    }

    @Deprecated
    public By getBy() {
        return guiElementData.getLocate().getBy();
    }

    @Deprecated
    public UiElement scrollToElement() {
        decoratedCore.scrollToElement();
        return this;
    }
    @Deprecated
    public UiElement scrollToElement(int yOffset) {
        decoratedCore.scrollToElement(yOffset);
        return this;
    }

    @Override
    public UiElement select() {
        decoratedCore.select();
        return this;
    }

    @Override
    public UiElement deselect() {
        decoratedCore.deselect();
        return this;
    }

    @Override
    public UiElement type(String text) {
        decoratedCore.type(text);
        return this;
    }

    @Override
    public UiElement click() {
        decoratedCore.click();
        return this;
    }

    @Override
    public UiElement submit() {
        decoratedCore.submit();
        return this;
    }

    @Override
    public UiElement sendKeys(CharSequence... charSequences) {
        decoratedCore.sendKeys(charSequences);
        return this;
    }

//    @Override
//    public InteractiveUiElement asUser() {
//        if (this.userSimulator==null) {
//            this.userSimulator = new UserSimulator(this);
//        }
//        return this.userSimulator;
//    }

    /**
     * @deprecated
     * This method is no longer acceptable as a part of GuiElement.
     * Use {@link eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverUtils#clickAbsolute()} instead
     *
     */
    @Override
    public UiElement clear() {
        decoratedCore.clear();
        return this;
    }

    /**
     * @deprecated
     * This method is no longer acceptable as a part of GuiElement.
     * Use {@link eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverUtils#mouseOverAbsolute2Axis()} instead
     *
     */
    @Override
    public InteractiveUiElement hover() {
        return mouseOver();
    }

    @Deprecated
    public String getTagName() {
        return decoratedCore.getTagName();
    }

    @Deprecated
    public String getAttribute(String attributeName) {
        return decoratedCore.getAttribute(attributeName);
    }

    @Deprecated
    public boolean isSelected() {
        return decoratedCore.isSelected();
    }

    @Deprecated
    public boolean isEnabled() {
        return decoratedCore.isEnabled();
    }

    @Deprecated
    public String getText() {
        return decoratedCore.getText();
    }

    @Deprecated
    public boolean isDisplayed() {
        return decoratedCore.isDisplayed();
    }

    @Deprecated
    public boolean isVisible(final boolean complete) {
        return decoratedCore.isVisible(complete);
    }

    /**
     * Checks if the element is selectable.
     * <p>
     * WARNING: To determine if the element is truly selectable, a selection or deselection will be done and reverted.
     * Keep this in mind.
     *
     * @return true, if the element is selectable.
     */
    @Deprecated
    public boolean isSelectable() {
        return decoratedCore.isSelectable();
    }

    @Deprecated
    public Point getLocation() {
        return decoratedCore.getLocation();
    }

    @Deprecated
    public Dimension getSize() {
        return decoratedCore.getSize();
    }

    @Override
    public UiElement find(Locator locator) {
        return uiElementFactory.createFromParent(guiElementData.getGuiElement(), locator);
    }

    @Override
    public UiElementList<UiElement> list() {
        if (this.list == null) {
            this.list = new DefaultUiElementList(this);
        }
        return this.list;
    }

    /**
     * @deprecated
     * This method is no longer acceptable as a part of GuiElement.
     * Use {@link eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverUtils#mouseOverJS()} instead
     *
     */
    @Deprecated
    public String getCssValue(String cssIdentifier) {
        return decoratedCore.getCssValue(cssIdentifier);
    }
    @Deprecated
    public UiElement mouseOver() {
        decoratedCore.mouseOver();
        return this;
    }

    @Deprecated
    public boolean isPresent() {
        return decoratedCore.isPresent();
    }

    @Deprecated
    public List<String> getTextsFromChildren() {
        return decoratedCore.getTextsFromChildren();
    }

    @Deprecated
    public boolean anyFollowingTextNodeContains(String contains) {
        return anyElementContainsText(contains).waitFor().present(true);
    }

    /**
     * This method is no part of any interface, because we don't know if
     * we want to support this feature at the moment.
     * The better approach is to use {@link #find(XPath)}
     */
    @Deprecated
    public TestableUiElement anyElementContainsText(String text) {
        String textFinderXpath = String.format("//text()[contains(., '%s')]/..", text);
        return find(By.xpath(textFinderXpath));
    }

    @Override
    public WebDriver getWebDriver() {
        return guiElementData.getWebDriver();
    }

    /**
     * It's dangerous to use this method and rely on the given {@link WebElement}
     * because it can become stale during the execution.
     * Use {@link #findWebElement(Consumer)} instead.
     */
    @Deprecated
    public WebElement getWebElement() {
        return this.decoratedCore.getWebElement();
    }

    @Override
    public UiElement doubleClick() {
        decoratedCore.doubleClick();
        return this;
    }

    @Override
    public UiElement highlight(Color color) {
        decoratedCore.highlight(color);
        return this;
    }

    public UiElement swipe(int offsetX, int offSetY) {
        decoratedCore.swipe(offsetX, offSetY);
        return this;
    }

    @Deprecated
    public int getLengthOfValueAfterSendKeys(String textToInput) {
        return decoratedCore.getLengthOfValueAfterSendKeys(textToInput);
    }

    @Deprecated
    public int getNumberOfFoundElements() {
        return decoratedCore.getNumberOfFoundElements();
    }

    /**
     * @deprecated Use {@link #contextClick()} instead
     */
    @Deprecated
    public UiElement rightClick() {
        return this.contextClick();
    }

    /**
     * @deprecated
     * This method is no longer acceptable as a part of GuiElement.
     * Use {@link eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverUtils#doubleClickJS()} instead
     *
     */
    @Override
    public UiElement contextClick() {
        decoratedCore.contextClick();
        return this;
    }

    @Deprecated
    public File takeScreenshot() {
        return decoratedCore.takeScreenshot();
    }

    /**
     * Sets the abstract parent
     * @param parent {@link UiElement} or {@link PageObject}
     */
    public UiElement setParent(Nameable parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Retrieves the nameable parent.
     * @return Can be {@link UiElement} or {@link PageObject}
     */
    @Override
    public Nameable getParent() {
        return parent;
    }

    @Override
    public boolean hasName() {
        return guiElementData.hasName();
    }

    @Override
    public String toString() {
        return this.toString(false);
    }

    @Override
    public String toString(boolean detailed) {
        StringBuilder sb = new StringBuilder();
        this.traceAncestors(parent -> sb.append(parent.getName(detailed)).append(" -> "));
        sb.append(getName(detailed));
        return sb.toString();
    }

    @Deprecated
    public WebDriver getDriver() {
        return getWebDriver();
    }

    public boolean hasSensibleData() {
        return guiElementData.hasSensibleData();
    }

    public GuiElement sensibleData() {
        guiElementData.setHasSensibleData(true);
        return this;
    }

    @Override
    public GuiElement setName(String name) {
        guiElementData.setName(name);
        return this;
    }

    @Override
    public String getName(boolean detailed) {
        return guiElementData.getName(detailed);
    }

    /**
     * Provides access to all functional assert methods.
     *
     * @return GuiElementAssert object for functional assertions
     */
    @Deprecated
    public GuiElementAssert asserts() {
        if (defaultAssert == null) {
            if (UiElement.Properties.DEFAULT_ASSERT_IS_COLLECTOR.asBool()) {
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
     * @deprecated Use {@link #optionalAsserts()} instead
     */
    @Deprecated
    public GuiElementAssert nonFunctionalAsserts() {
        if (nonFunctionalAssert==null) {
            OptionalAssertion assertion = Testerra.getInjector().getInstance(OptionalAssertion.class);
            nonFunctionalAssert = createAssertDecorators(assertion);
        }
        return nonFunctionalAssert;
    }

    @Deprecated
    private GuiElementAssert createAssertDecorators(Assertion assertion) {
        GuiElementAssert guiElementAssert;
        if (Testerra.Properties.PERF_TEST.asBool()) {
            guiElementAssert = new PerformanceTestGuiElementAssert();
        } else {
            guiElementAssert = new LegacyGuiElementAssertWrapper(this, assertion);
        }
        return guiElementAssert;
    }

    /**
     * Provides access to all non-functional assert methods. If an assertion fails, the assertDescription will be
     * given as cause, instead of the technical cause like an isDisplayed error.
     *
     * @param errorMessage Cause returned on assertion error.
     *
     * @return GuiElementAssert object for non-functional assertions
     * @deprecated Use {@link #optionalAsserts(String)} instead
     */
    @Deprecated
    public GuiElementAssert nonFunctionalAsserts(String errorMessage) {
        GuiElementAssertDescriptionDecorator guiElementAssertDescriptionDecorator
                = new GuiElementAssertDescriptionDecorator(errorMessage, nonFunctionalAsserts());
        return guiElementAssertDescriptionDecorator;
    }

    @Deprecated
    private GuiElementAssert instantAsserts() {
        if (instantAssert == null) {
            InstantAssertion assertion = Testerra.getInjector().getInstance(InstantAssertion.class);
            instantAssert = createAssertDecorators(assertion);
        }
        return instantAssert;
    }

    /**
     * Provides access to optional assert methods
     */
    @Deprecated
    public GuiElementAssert optionalAsserts() { return nonFunctionalAsserts(); }

    /**
     * Provides access to optional assert methods
     */
    @Deprecated
    public GuiElementAssert optionalAsserts(String errorMessage) {
        return nonFunctionalAsserts(errorMessage);
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
            CollectedAssertion assertion = Testerra.getInjector().getInstance(CollectedAssertion.class);
            collectableAssert = createAssertDecorators(assertion);
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
        GuiElementAssertDescriptionDecorator guiElementAssertDescriptionDecorator = new GuiElementAssertDescriptionDecorator(errorMessage, assertCollector());
        return guiElementAssertDescriptionDecorator;
    }

    @Deprecated
    public List<GuiElement> getList() {
        List<GuiElement> guiElements = new ArrayList<>();
        list().forEach(guiElement -> guiElements.add((GuiElement)guiElement));
        return guiElements;
    }

    @Deprecated
    public GuiElement shadowRoot() {
        guiElementData.setHasShadowRoot(true);
        return this;
    }

    /**
     * Provides access to all wait methods
     */
    @Deprecated
    public GuiElementWait waits() {
        if (decoratedWait == null) {
            decoratedWait = new DefaultGuiElementWait(this);
        }
        return decoratedWait;
    }

    @Override
    public UiElementAssertion waitFor(int seconds) {
        if (this.waits == null) {
            this.waits = new DefaultUiElementAssertion(this, seconds);
        }
        return this.waits;
    }

    @Override
    public UiElementAssertion expect() {
        if (this.assertions == null) {
            this.assertions = new DefaultUiElementAssertion(this, true);
        }
        return this.assertions;
    }

    @Override
    public String createXPath() {
        GuiElementData guiElementData = this.guiElementData;
        StringBuilder sb = new StringBuilder();
        do {
            String elementPath = XPath.byToXPath(guiElementData.getLocate().getBy());
            if (guiElementData.getIndex() >= 0) {
                elementPath += "["+guiElementData.getIndex()+"]";
            }
            sb.insert(0, elementPath);
            guiElementData = guiElementData.getParent();
        } while (guiElementData != null && !guiElementData.isFrame());

        return sb.toString();
    }

    @Override
    public void findWebElement(Consumer<WebElement> consumer) {
        decoratedCore.findWebElement(consumer);
    }

    @Override
    public void screenshotToReport() {
        this.waitFor().screenshot(Report.Mode.ALWAYS);
    }

    @Deprecated
    public Select getSelectElement() {
        return new Select(getWebElement());
    }
}

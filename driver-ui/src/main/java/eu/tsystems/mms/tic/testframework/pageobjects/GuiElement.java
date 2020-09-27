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
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.NameableChild;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementAssertions;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementBase;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AbstractPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AssertionProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultBinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultGuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultQuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultRectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultStringAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertDescriptionDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertHighlightDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PerformanceTestGuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.RectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.AbstractGuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCoreSequenceDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.DelayActionsGuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.UiElementLogger;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.DefaultGuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * GuiElement is the access point for most tests and is an extension of WebElement.
 * <p>
 * Authors: pele, rnhb
 */
public class GuiElement implements UiElement, NameableChild<UiElement>, Loggable {
    /**
     * Factory required for {@link UiElementAssertions}
     */
    private static final PropertyAssertionFactory propertyAssertionFactory = Testerra.injector.getInstance(PropertyAssertionFactory.class);
    /**
     * Factory required for {@link UiElementFinder}
     */
    private static final UiElementFactory uiElementFactory = Testerra.injector.getInstance(UiElementFactory.class);

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
        IWebDriverFactory factory = WebDriverSessionsManager.getWebDriverFactory(guiElementData.getBrowser());
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
        this(driver, Locate.by(by));
    }

    public GuiElementCore getCore() {
        return this.core;
    }

    public GuiElementData getData() {
        return this.guiElementData;
    }

    @Override
    public Locator getLocate() {
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
        GuiElement subElement = getSubElement(Locate.by(by));
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

    @Override
    public UiElement clear() {
        decoratedCore.clear();
        return this;
    }

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
        guiElementData.setSensibleData(true);
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
     */
    @Deprecated
    public GuiElementAssert nonFunctionalAsserts() {
        if (nonFunctionalAssert==null) {
            NonFunctionalAssertion assertion = Testerra.injector.getInstance(NonFunctionalAssertion.class);
            nonFunctionalAssert = createAssertDecorators(core, guiElementData, assertion, waits());
        }
        return nonFunctionalAssert;
    }

    @Deprecated
    private GuiElementAssert createAssertDecorators(
            GuiElementCore core,
            GuiElementData data,
            Assertion assertion,
            GuiElementWait wait
    ) {
        GuiElementAssert guiElementAssert;
        if (Testerra.Properties.PERF_TEST.asBool()) {
            guiElementAssert = new PerformanceTestGuiElementAssert();
        } else {
            guiElementAssert = new DefaultGuiElementAssert(core, data, wait, assertion);
            guiElementAssert = new GuiElementAssertHighlightDecorator(guiElementAssert, data);
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
            InstantAssertion assertion = Testerra.injector.getInstance(InstantAssertion.class);
            instantAssert = createAssertDecorators(core, guiElementData, assertion, waits());
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
            CollectedAssertion assertion = Testerra.injector.getInstance(CollectedAssertion.class);
            collectableAssert = createAssertDecorators(core, guiElementData, assertion, waits());
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
        List<GuiElement> guiElements = new ArrayList<>();
        list().forEach(guiElement -> guiElements.add((GuiElement)guiElement));
        return guiElements;
    }

    @Deprecated
    public GuiElement shadowRoot() {
        guiElementData.setShadowRoot(true);
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
    public StringAssertion<String> tagName() {
        final UiElement self = this;
        DefaultStringAssertion<String> assertion = propertyAssertionFactory.create(DefaultStringAssertion.class, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.getTagName();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@tagName", self);
            }
        });
        return assertion;
    }

    @Override
    public StringAssertion<String> text() {
        final UiElement self = this;
        DefaultStringAssertion<String> assertion = propertyAssertionFactory.create(DefaultStringAssertion.class, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.getText();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@text", self.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public StringAssertion<String> value(String attribute) {
        final String finalAttribute = attribute;
        final UiElement self = this;
        DefaultStringAssertion<String> assertion = propertyAssertionFactory.create(DefaultStringAssertion.class, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.getAttribute(finalAttribute);
            }

            @Override
            public String getSubject() {
                return String.format("%s.@%s", self.toString(true), finalAttribute);
            }
        });
        return assertion;
    }

    @Override
    public StringAssertion<String> css(String property) {
        final UiElement self = this;
        DefaultStringAssertion<String> assertion = propertyAssertionFactory.create(DefaultStringAssertion.class, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return getCssValue(property);
            }

            @Override
            public String getSubject() {
                return String.format("%s.css(@%s)", self.toString(true), property);
            }
        });
        return assertion;
    }

    @Override
    public BinaryAssertion<Boolean> present() {
        final UiElement self = this;
        DefaultBinaryAssertion<Boolean> assertion = propertyAssertionFactory.create(DefaultBinaryAssertion.class, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isPresent();
//                try {
//                    frameAwareCore.findWebElement(webElement -> {});
//                    return true;
//                } catch (ElementNotFoundException e) {
//                    return false;
//                }
            }

            @Override
            public String getSubject() {
                return String.format("%s.@present", self.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public BinaryAssertion<Boolean> visible(boolean complete) {
        final UiElement self = this;
        DefaultBinaryAssertion<Boolean> assertion = propertyAssertionFactory.create(DefaultBinaryAssertion.class, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isVisible(complete);
            }

            @Override
            public String getSubject() {
                return String.format("%s.visible(complete: %s)", self.toString(true), complete);
            }
        });
        return assertion;
    }

    @Override
    public BinaryAssertion<Boolean> displayed() {
        final UiElement self = this;
        DefaultBinaryAssertion<Boolean> assertion = propertyAssertionFactory.create(DefaultBinaryAssertion.class, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isDisplayed();
            }
            @Override
            public String getSubject() {
                return String.format("%s.@displayed", self.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public BinaryAssertion<Boolean> enabled() {
        final UiElement self = this;
        DefaultBinaryAssertion<Boolean> assertion = propertyAssertionFactory.create(DefaultBinaryAssertion.class, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isEnabled();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@enabled", self.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public BinaryAssertion<Boolean> selected() {
        final UiElement self = this;
        DefaultBinaryAssertion<Boolean> assertion = propertyAssertionFactory.create(DefaultBinaryAssertion.class, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isSelected();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@selected", self.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public BinaryAssertion<Boolean> selectable() {
        final UiElement self = this;
        DefaultBinaryAssertion<Boolean> assertion = propertyAssertionFactory.create(DefaultBinaryAssertion.class, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isSelectable();
            }

            @Override
            public String getSubject() {
                return String.format("%s.selectable", self.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public RectAssertion bounds() {
        final UiElement self = this;
        DefaultRectAssertion assertion = propertyAssertionFactory.create(DefaultRectAssertion.class, new AssertionProvider<Rectangle>() {
            @Override
            public Rectangle getActual() {
                return core.getRect();
            }

            @Override
            public String getSubject() {
                return String.format("%s.bounds", self.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public QuantityAssertion<Integer> numberOfElements() {
        final UiElement self = this;
        DefaultQuantityAssertion assertion = propertyAssertionFactory.create(DefaultQuantityAssertion.class, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                try {
                    return core.getNumberOfFoundElements();
                } catch (ElementNotFoundException e) {
                    return 0;
                }
            }

            @Override
            public String getSubject() {
                return String.format("%s.@numberOfElements", self.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public UiElementAssertions waitFor() {
        propertyAssertionFactory.shouldWait();
        return this;
    }

    @Override
    public String createXPath() {
        Formatter formatter = Testerra.injector.getInstance(Formatter.class);
        ArrayList<String> xPathes = new ArrayList<>();
        Nameable element = this;
        while (element instanceof UiElementBase) {
            xPathes.add(0, formatter.byToXPath(((UiElementBase)element).getLocate().getBy()));
            element = element.getParent();
        }
        return String.join("", xPathes);
    }

    @Override
    public ImageAssertion screenshot() {
        final UiElement self = this;
        final AtomicReference<File> screenshot = new AtomicReference<>();
        screenshot.set(core.takeScreenshot());
        DefaultImageAssertion assertion = propertyAssertionFactory.create(DefaultImageAssertion.class, new AssertionProvider<File>() {
            @Override
            public File getActual() {
                return screenshot.get();
            }

            @Override
            public void failed(AbstractPropertyAssertion assertion) {
                screenshot.set(core.takeScreenshot());
            }

            @Override
            public String getSubject() {
                return String.format("%s.screenshot", self.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public void findWebElement(Consumer<WebElement> consumer) {
        decoratedCore.findWebElement(consumer);
    }
}

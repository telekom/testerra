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

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.internal.Nameable;
import eu.tsystems.mms.tic.testframework.internal.NameableChild;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.AbstractPage;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.DefaultUiElementFinder;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultPageAssertions;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PageAssertions;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import java.awt.Color;
import java.util.Random;
import org.openqa.selenium.WebDriver;

/**
 * Represents a full web page and provides advanced {@link PageObject} features:
 *      Supports finding elements by {@link #find(Locator)}
 *      Support for performance tests by {@link #perfTestExtras()}
 *      Support for text assertions by {@link #anyElementContainsText(String)}
 * @author Peter Lehmann
 * @author Mike Reiche
 * @todo Rename to AbstractPage
 */
public class Page extends AbstractPage implements TestablePage, Nameable<Page> {
    private WebDriver driver;
    private DefaultUiElementFinder finder;
    private PageAssertions assertions;
    private PageAssertions waits;

    public Page(WebDriver webDriver) {
        this.driver = webDriver;

        // performance test stop timer
        perfTestExtras();
    }

    /**
     * Pages should not have parents. Use {@link Component} instead.
     */
    @Override
    public Nameable getParent() {
        return null;
    }

    /**
     * A page has always a name
     */
    @Override
    public boolean hasName() {
        return true;
    }

    /**
     * There is usually no need to set the name of a page
     */
    @Override
    public Page setName(String name) {
        return this;
    }

    /**
     * Always return the class name of the page
     */
    @Override
    public String getName(boolean detailed) {
        return getClass().getSimpleName();
    }

    @Override
    public WebDriver getWebDriver() {
        return driver;
    }

    /**
     * Execute loadtestspecific
     */
    private void perfTestExtras() {
        StopWatch.stopPageLoad(getWebDriver(), this.getClass());

        if (Testerra.Properties.PERF_TEST.asBool()) {
            executeThinkTime();
        }
    }

    /**
     * executes think time
     */
    private void executeThinkTime() {
        // Thinktime in Properties
        int thinkTime = PropertyManager.getIntProperty(TesterraProperties.PERF_PAGE_THINKTIME_MS, 0);
        // timeOut for Threadsleep
        int timeToWait = 0;
        /*
        Zufallsabweichung für Thinktimes > 0
         */
        if (thinkTime > 0) {
            int randomDelta = 2000;
            Random r = new Random();
            int randomValue = r.nextInt(randomDelta * 2);

            timeToWait = thinkTime + (randomValue - randomDelta);
            if (timeToWait < 0) {
                timeToWait = 0;
            }
        }
        // wait Thinktime + Offset
        log().info("Waiting a time to think of " + timeToWait + " milliseconds");
        TimerUtils.sleep(timeToWait);
    }

    @Deprecated
    public boolean isTextPresent(String text) {
        WebDriver driver = getWebDriver();
        driver.switchTo().defaultContent();

        // check frames recursive
        boolean out = pIsTextPresentRecursive(false, text);

        // switch back to default frame
        driver.switchTo().defaultContent();
        return out;
    }

    @Deprecated
    public boolean isTextDisplayed(String text) {
        WebDriver driver = getWebDriver();
        driver.switchTo().defaultContent();

        // check frames recursive
        boolean out = pIsTextPresentRecursive(true, text);

        // switch back to default frame
        driver.switchTo().defaultContent();
        return out;
    }

    @Fails(validFor = "unsupportedBrowser=true")
    private boolean pIsTextPresentRecursive(final boolean isDisplayed, final String text) {
        UiElement textElement = (UiElement)anyElementContainsText(text);
        if (
            isDisplayed && textElement.waitFor().displayed().getActual()
            || textElement.waitFor().present().getActual()
        ) {
            WebDriver driver = getWebDriver();
            textElement.findWebElement(webElement -> {
                JSUtils.highlightWebElementStatic(driver, webElement, new Color(0, 255, 0));
            });
            return true;
        }

        return false;
    }

    /**
     * Waits for a text to be not present.
     *
     * @return boolean true if success == text is not present. false otherwise.
     */
    @Deprecated
    public boolean waitForIsNotTextPresentWithDelay(final String text, final int delayInSeconds) {
        TimerUtils.sleep(delayInSeconds * 1000);
        return waitForIsNotTextPresent(text);
    }

    protected UiElementFinder getFinder() {
        if (this.finder == null) {
            this.finder = new DefaultUiElementFinder(getWebDriver());
        }
        return this.finder;
    }

    @Override
    protected UiElement find(Locator locator) {
        UiElement element = getFinder().find(locator);
        if (element instanceof NameableChild) {
            ((NameableChild)element).setParent(this);
        }
        return element;
    }

    @Override
    protected UiElement findDeep(Locator locator) {
        UiElement element = getFinder().findDeep(locator);
        if (element instanceof NameableChild) {
            ((NameableChild)element).setParent(this);
        }
        return element;
    }

    /**
     * Waits for a text to be not present.
     *
     * @return boolean true if success == text is not present. false otherwise.
     */
    @Deprecated
    public boolean waitForIsNotTextDisplayedWithDelay(final String text, final int delayInSeconds) {
        TimerUtils.sleep(delayInSeconds * 1000);
        return waitForIsNotTextDisplayed(text);
    }

    @Override
    protected void pageLoaded() {
        if (PropertyManager.getBooleanProperty(TesterraProperties.SCREENSHOT_ON_PAGELOAD, false)) {
            this.screenshotToReport();
        }
    }

    /**
     * Waits for a text to be not present.
     *
     * @return boolean true if success == text is not present. false otherwise.
     */
    @Deprecated
    public boolean waitForIsNotTextPresent(final String text) {
        return anyElementContainsText(text).waitFor().present(false);
    }

    /**
     * Waits for a text to be not displayed.
     *
     * @return boolean true if success == text is not present. false otherwise.
     */
    @Deprecated
    public boolean waitForIsNotTextDisplayed(final String text) {
        return anyElementContainsText(text).waitFor().displayed(false);
    }

    @Deprecated
    public void assertIsTextPresent(String text, String description) {
        assertIsTextPresent(text);
    }

    @Deprecated
    public void assertIsTextDisplayed(String text, String description) {
        assertIsTextDisplayed(text);
    }

    @Deprecated
    public void assertIsTextPresent(String text) {
        anyElementContainsText(text).expect().present().is(true);
    }

    @Deprecated
    public void assertIsTextDisplayed(String text) {
        anyElementContainsText(text).expect().displayed().is(true);
    }

    @Deprecated
    public void assertIsNotTextPresent(String text) {
        anyElementContainsText(text).expect().present().is(false);
    }

    @Deprecated
    public void assertIsNotTextDisplayed(String text) {
        try {
            anyElementContainsText(text).expect().displayed().is(false);
        } catch (AssertionError error) {
            if (error.getCause() instanceof ElementNotFoundException) {
                // ignore this
            } else {
                throw error;
            }
        }
    }

    private TestableUiElement anyElementContainsText(String text) {
        return getFinder().findDeep(Locate.by(XPath.from("*").text().contains(text)));
    }

    @Override
    public PageAssertions waitFor() {
        if (this.waits == null) {
            this.waits = new DefaultPageAssertions(this, false);
        }
        return this.waits;
    }

    @Override
    public PageAssertions expect() {
        if (this.assertions == null) {
            this.assertions = new DefaultPageAssertions(this, true);
        }
        return this.assertions;
    }

    @Override
    public void screenshotToReport() {
        this.waitFor().screenshot(Report.Mode.ALWAYS);
    }
}

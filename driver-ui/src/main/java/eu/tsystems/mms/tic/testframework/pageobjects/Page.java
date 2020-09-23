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
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AbstractPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AssertionProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultScreenshotAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultStringAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ScreenshotAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Represents a full web page and provides advanced {@link PageObject} features:
 *      Supports finding elements by {@link #find(Locate)}
 *      Support for performance tests by {@link #perfTestExtras()}
 *      Support for frames by {@link #inFrame(UiElement)}
 *      Support for text assertions by {@link #anyElementContainsText(String)}
 * @author Peter Lehmann
 * @author Mike Reiche
 * @todo Rename to AbstractPage
 */
public abstract class Page extends AbstractPage implements TestablePage, Nameable<Page> {
    private static final PropertyAssertionFactory propertyAssertionFactory = Testerra.injector.getInstance(PropertyAssertionFactory.class);
    private WebDriver driver;

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

    private static class FrameFinder {
        private final UiElement frame;
        private FrameFinder(UiElement frame) {
            this.frame = frame;
        }
        public UiElement find(Locate locator) {
            return uiElementFactory.createWithFrames(locator, frame);
        }
    }

    protected FrameFinder inFrame(UiElement frame) {
        return new FrameFinder(frame);
    }

    public Page(WebDriver webDriver) {
        this.driver = webDriver;

        // performance test stop timer
        perfTestExtras();
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
        textElement.displayed();
        if (
            isDisplayed && textElement.displayed().getActual()
            || textElement.present().getActual()
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

    @Override
    protected UiElement find(eu.tsystems.mms.tic.testframework.pageobjects.Locate locate) {
        return uiElementFactory.createFromPage(this, locate);
    }

    protected void pCheckPage(final boolean findNot, final boolean checkCaller) {
        super.pCheckPage(findNot, checkCaller);
        if (PropertyManager.getBooleanProperty(TesterraProperties.SCREENSHOT_ON_PAGELOAD, false)) {
            this.screenshot().toReport();
        }
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

    /**
     * Waits for a text to be not present.
     *
     * @return boolean true if success == text is not present. false otherwise.
     */
    @Deprecated
    public boolean waitForIsNotTextPresent(final String text) {
        Timer timer = new Timer(1000, getElementTimeoutInSeconds() * 1000);
        final ThrowablePackedResponse<Boolean> response = timer.executeSequence(new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                final boolean textPresent = isTextPresent(text);
                final boolean passState = !textPresent;
                setPassState(passState);
                setReturningObject(passState);
            }
        });

        if (response.hasThrowable()) {
            log().error("waitForIsNotTextPresent ran into an error", response.getThrowable());
        }
        return response.getResponse();
    }

    /**
     * Waits for a text to be not displayed.
     *
     * @return boolean true if success == text is not present. false otherwise.
     */
    @Deprecated
    public boolean waitForIsNotTextDisplayed(final String text) {
        Timer timer = new Timer(1000, getElementTimeoutInSeconds() * 1000);
        final ThrowablePackedResponse<Boolean> response = timer.executeSequence(new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                final boolean textDisplayed = isTextDisplayed(text);
                final boolean passState = !textDisplayed;
                setPassState(passState);
                setReturningObject(passState);
            }
        });

        if (response.hasThrowable()) {
            log().error("waitForIsNotTextDisplayed ran into an error", response.getThrowable());
        }
        return response.getResponse();
    }

    @Deprecated
    public boolean waitForIsTextPresent(final String text) {
        Timer timer = new Timer(UiElement.Properties.ELEMENT_WAIT_INTERVAL_MS.asLong(), getElementTimeoutInSeconds() * 1000);
        ThrowablePackedResponse<Boolean> response = timer.executeSequence(new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                boolean textPresent = isTextPresent(text);
                setPassState(textPresent);
                setReturningObject(textPresent);
            }
        });
        return response.getResponse();
    }

    @Deprecated
    public boolean waitForIsTextDisplayed(final String text) {
        Timer timer = new Timer(UiElement.Properties.ELEMENT_WAIT_INTERVAL_MS.asLong(), getElementTimeoutInSeconds() * 1000);
        ThrowablePackedResponse<Boolean> response = timer.executeSequence(new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                boolean textPresent = isTextDisplayed(text);
                setPassState(textPresent);
                setReturningObject(textPresent);
            }
        });
        return response.getResponse();
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
        anyElementContainsText(text).present().is(true);
    }

    @Deprecated
    public void assertIsTextDisplayed(String text) {
        anyElementContainsText(text).displayed().is(true);
    }

    @Deprecated
    public void assertIsNotTextPresent(String text) {
        anyElementContainsText(text).present().is(false);
    }

    @Deprecated
    public void assertIsNotTextDisplayed(String text) {
        anyElementContainsText(text).displayed().is(false);
    }

    @Override
    public StringAssertion<String> title() {
        final Page self = this;
        return propertyAssertionFactory.create(DefaultStringAssertion.class, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return driver.getTitle();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@title", self.toString(true));
            }
        });
    }

    @Override
    public StringAssertion<String> url() {
        final Page self = this;
        return propertyAssertionFactory.create(DefaultStringAssertion.class, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return driver.getCurrentUrl();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@url", self.toString(true));
            }
        });
    }

    @Override
    public ScreenshotAssertion screenshot() {
        final Page self = this;
        final AtomicReference<Screenshot> atomicScreenshot = new AtomicReference<>();

        Screenshot screenshot = new Screenshot(self.toString());
        WebDriver driver = this.getWebDriver();
        UITestUtils.takeScreenshot(driver, screenshot);
        atomicScreenshot.set(screenshot);

        return propertyAssertionFactory.create(DefaultScreenshotAssertion.class, new AssertionProvider<Screenshot>() {
            @Override
            public Screenshot getActual() {
                return atomicScreenshot.get();
            }

            @Override
            public void failed(AbstractPropertyAssertion assertion) {
                // Take new screenshot only if failed
                UITestUtils.takeScreenshot(driver, atomicScreenshot.get());
            }

            @Override
            public String getSubject() {
                return self.toString(true);
            }
        });
    }

    @Override
    public TestableUiElement anyElementContainsText(String text) {
        String textFinderXpath = String.format("//text()[contains(., '%s')]/..", text);
        Locate locate = Locate.by(By.xpath(textFinderXpath));
        TestableUiElement textElements = find(locate);
        if (textElements.numberOfElements().getActual()>0) {
            return textElements;
        }
        WebDriver driver = this.getWebDriver();

        WebDriverRequest request = WebDriverManager.getRelatedWebDriverRequest(driver);
        if (Browsers.safari.equalsIgnoreCase(request.browser) || Browsers.phantomjs.equalsIgnoreCase(request.browser)) {
            String msg = "Recursive Page Scan does not work. Unsupported Browser.";
            log().error(msg);
            MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
            if (methodContext != null) {
                methodContext.addPriorityMessage(msg);
            }
            // don't return here, let it run into failure...
        }

        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        for (WebElement iframe : iframes) {
            driver.switchTo().frame(iframe);
            textElements = anyElementContainsText(text);
            if (textElements.numberOfElements().getActual()>0) {
               return textElements;
            }
            driver.switchTo().parentFrame();
        }

        List<WebElement> frames = driver.findElements(By.tagName("frame"));
        for (WebElement frame : frames) {
            driver.switchTo().frame(frame);
            textElements = anyElementContainsText(text);
            if (textElements.numberOfElements().getActual()>0) {
                return textElements;
            }
            driver.switchTo().parentFrame();
        }

        return textElements;
    }

    @Override
    public TestablePage waitFor() {
        propertyAssertionFactory.shouldWait();
        return this;
    }

}

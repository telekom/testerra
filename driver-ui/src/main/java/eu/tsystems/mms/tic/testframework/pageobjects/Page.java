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
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.GuiElementType;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.HasParent;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.FieldAction;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.FieldWithActionConfig;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.groups.GuiElementGroupAction;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.groups.GuiElementGroups;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AssertionProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultScreenshotAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultStringAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ScreenshotAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;
import eu.tsystems.mms.tic.testframework.report.IReport;
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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a full web page and provides advanced {@link PageObject} features:
 *      Supports finding elements by {@link #find(Locate)}
 *      Support for {@link #registerPageLoadHandler(PageLoadHandler)}
 *      Support for performance tests by {@link #perfTestExtras()}
 *      Support for frames by {@link #inFrame(IGuiElement)}
 *      Support for custom field actions {@link #addCustomFieldAction(FieldWithActionConfig, List, AbstractPage)}
 *      Support for text assertions by {@link #anyElementContainsText(String)}
 *      Support for {@link GuiElementGroups}
 * @author Peter Lehmann
 * @author Mike Reiche
 */
public abstract class Page extends AbstractPage implements TestablePage {
    private final GuiElementGroups guiElementGroups;
    private static List<PageLoadHandler> pageLoadHandlers = new LinkedList<>();
    private static final GuiElementFactory guiElementFactory = Testerra.injector.getInstance(GuiElementFactory.class);
    private static final PropertyAssertionFactory propertyAssertionFactory = Testerra.injector.getInstance(PropertyAssertionFactory.class);

    protected interface Finder {
        IGuiElement find(Locate locator);
        default IGuiElement findById(String id) {
            return find(Locate.by().id(id));
        }
        default IGuiElement findByQa(String qa) {
            return find(Locate.by().qa(qa));
        }
        default IGuiElement find(By by) {
            return find(Locate.by(by));
        }
    }

    @Override
    protected IGuiElement find(Locate locate) {
        return guiElementFactory.createWithPage(this, locate);
    }

    @Override
    public HasParent getParent() {
        return null;
    }

    @Override
    public String toString(boolean detailed) {
        return getClass().getSimpleName();
    }

    private static class FrameFinder implements Finder {
        private final IGuiElement frame;
        private FrameFinder(IGuiElement frame) {
            this.frame = frame;
        }
        public IGuiElement find(Locate locator) {
            return guiElementFactory.createWithFrames(locator, frame);
        }
    }

    protected Finder inFrame(IGuiElement frame) {
        return new FrameFinder(frame);
    }

    public Page(WebDriver webDriver) {
        this.driver = webDriver;

        // webdriver based waitForPageToLoad
        waitForPageToLoad();

        // performance test stop timer
        perfTestExtras();

        // call page load handlers
        for (PageLoadHandler pageLoadHandler : pageLoadHandlers) {
            pageLoadHandler.run(this);
        }

        // initialize ge groups
        guiElementGroups = new GuiElementGroups();
    }

    public static void registerPageLoadHandler(PageLoadHandler h) {
        pageLoadHandlers.add(h);
    }

    /**
     * Execute loadtestspecific
     */
    private void perfTestExtras() {
        StopWatch.stopPageLoad(driver, this.getClass());

        if (UITestUtils.getGuiElementType() == GuiElementType.perf) {
            executeThinkTime();
        }

        // activate the PERF_STOP_WATCH Flag if not already, to assure storing of pageLoadInfos only in case of perf test
        if (!Flags.PERF_STOP_WATCH_ACTIVE) {
            Flags.PERF_STOP_WATCH_ACTIVE = true;
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
        logger.info("Waiting a Thinktime of " + timeToWait + " milliseconds");
        TimerUtils.sleep(timeToWait);
    }

    @Override
    protected void handleDemoMode(WebDriver webDriver) {
        if (Testerra.Properties.DEMO_MODE.asBool() && webDriver != null) {
            JSUtils.turnOnDemoModeForCurrentPage(webDriver);
        }
    }

    @Override
    protected void addCustomFieldAction(FieldWithActionConfig field, List<FieldAction> fieldActions, AbstractPage declaringPage) {
        fieldActions.add(new GuiElementGroupAction(field.field, declaringPage, guiElementGroups));
    }

    @Override
    public void waitForPageToLoad() {

    }

    /**
     * Send F5 to the browser.
     */
    public Page refresh() {
        return refresh(false);
    }

    /**
     * taking screenshot from all open windows
     */
    @Deprecated
    public void takeScreenshot() {
        screenshot().toReport();
    }

    @Override
    protected void pCheckPage(boolean findNot, boolean fast, boolean checkCaller) {
        super.pCheckPage(findNot, fast, checkCaller);
        if (IReport.Properties.SCREENSHOT_ON_PAGELOAD.asBool()) {
            screenshot().toReport();
        }
    }

    /**
     * Send F5 to the browser.
     * @deprecated Use {@link #refresh()} and {@link #checkGuiElements(CheckRule)} instead
     */
    @Deprecated
    public Page refresh(boolean checkPage) {
        driver.navigate().refresh();
        if (checkPage) {
            pCheckPage(false, false, false);
        }
        return this;
    }

    @Deprecated
    public boolean isTextPresent(String text) {
        driver.switchTo().defaultContent();

        // check frames recursive
        boolean out = pIsTextPresentRecursive(false, text);

        // switch back to default frame
        driver.switchTo().defaultContent();
        return out;
    }

    @Deprecated
    public boolean isTextDisplayed(String text) {
        driver.switchTo().defaultContent();

        // check frames recursive
        boolean out = pIsTextPresentRecursive(true, text);

        // switch back to default frame
        driver.switchTo().defaultContent();
        return out;
    }

    @Fails(validFor = "unsupportedBrowser=true")
    private boolean pIsTextPresentRecursive(final boolean isDisplayed, final String text) {
        IGuiElement textElement = (IGuiElement)anyElementContainsText(text);
        textElement.displayed();
        if (
            isDisplayed && textElement.displayed().getActual()
            || textElement.present().getActual()
        ) {
            WebElement webElement = textElement.getWebElement();
            JSUtils.highlightWebElementStatic(driver, webElement, new Color(0, 255, 0));
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
        final Timer timer = new Timer();
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
            logger.error("waitForIsNotTextPresent ran into an error", response.getThrowable());
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
        final Timer timer = new Timer();
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
            logger.error("waitForIsNotTextDisplayed ran into an error", response.getThrowable());
        }
        return response.getResponse();
    }

    @Deprecated
    public boolean waitForIsTextPresent(final String text) {
        Timer timer = new Timer(IGuiElement.Properties.ELEMENT_WAIT_INTERVAL_MS.asLong(), elementTimeoutInSeconds * 1000);
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
        Timer timer = new Timer(IGuiElement.Properties.ELEMENT_WAIT_INTERVAL_MS.asLong(), elementTimeoutInSeconds * 1000);
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
        anyElementContainsText(text).present().isTrue();
    }

    @Deprecated
    public void assertIsTextDisplayed(String text) {
        anyElementContainsText(text).displayed().isTrue();
    }

    @Deprecated
    public void assertIsNotTextPresent(String text) {
        anyElementContainsText(text).present().isFalse();
    }

    @Deprecated
    public void assertIsNotTextDisplayed(String text) {
        anyElementContainsText(text).displayed().isFalse();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public GuiElementGroups getGuiElementGroups() {
        return guiElementGroups;
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
                return String.format("%s.title", self);
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
                return String.format("%s.url", self);
            }
        });
    }

    @Override
    public ScreenshotAssertion screenshot() {
        final Page self = this;
        final AtomicReference<Screenshot> atomicScreenshot = new AtomicReference<>();

        Screenshot screenshot = new Screenshot(self.toString());
        UITestUtils.takeScreenshot(driver, screenshot);
        atomicScreenshot.set(screenshot);

        return propertyAssertionFactory.create(DefaultScreenshotAssertion.class, new AssertionProvider<Screenshot>() {
            @Override
            public Screenshot getActual() {
                return atomicScreenshot.get();
            }

            @Override
            public void failed(PropertyAssertion assertion) {
                // Take new screenshot only if failed
                UITestUtils.takeScreenshot(driver, atomicScreenshot.get());
            }

            @Override
            public String getSubject() {
                return self.toString();
            }
        });
    }

    @Override
    public TestableGuiElement anyElementContainsText(String text) {
        String textFinderXpath = String.format("//text()[contains(., '%s')]/..", text);
        Locate locate = Locate.by(By.xpath(textFinderXpath));
        TestableGuiElement textElements = find(locate);
        if (textElements.numberOfElements().getActual()>0) {
            return textElements;
        }

        WebDriverRequest request = WebDriverManager.getRelatedWebDriverRequest(driver);
        if (Browsers.safari.equalsIgnoreCase(request.browser) || Browsers.phantomjs.equalsIgnoreCase(request.browser)) {
            String msg = "Recursive Page Scan does not work. Unsupported Browser.";
            logger.error(msg);
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

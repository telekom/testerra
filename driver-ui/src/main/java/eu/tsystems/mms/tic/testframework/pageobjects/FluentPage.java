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
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AssertionProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IImagePropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IStringPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImagePropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.net.URL;

/**
 * Page with fluent interface support.
 */
public abstract class FluentPage<SELF extends FluentPage<SELF>> extends Page {

    protected interface Finder {
        IGuiElement findOne(Locate locator);
        default IGuiElement findOneById(String id) {
            return findOne(Locate.by().id(id));
        }
        default IGuiElement findOneByQa(String qa) {
            return findOne(Locate.by().qa(qa));
        }
        default IGuiElement findOne(By by) {
            return findOne(Locate.by(by));
        }
    }

    private class FrameFind implements Finder {
        private IGuiElement frame;
        private FrameFind(IGuiElement frame) {
            this.frame = frame;
        }
        public IGuiElement findOne(Locate locator) {
            return guiElementFactory.createWithFrames(locator, frame);
        }
    }

    private class AncestorFind implements Finder {
        private IGuiElement ancestor;
        private AncestorFind(IGuiElement ancestor) {
            this.ancestor = ancestor;
        }
        public IGuiElement findOne(Locate locator) {
            return guiElementFactory.createFromAncestor(locator, ancestor);
        }
    }

    protected static final PropertyAssertionFactory propertyAssertionFactory = Testerra.ioc().getInstance(PropertyAssertionFactory.class);
    private static final GuiElementFactory guiElementFactory = Testerra.ioc().getInstance(GuiElementFactory.class);
    private static final IPageFactory pageFactory = Testerra.ioc().getInstance(IPageFactory.class);

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public FluentPage(WebDriver driver) {
        super(driver);
    }

    public IStringPropertyAssertion<String> title() {
        final Page self = this;
        return propertyAssertionFactory.string(new AssertionProvider<String>() {
            @Override
            public String actual() {
                return driver.getTitle();
            }

            @Override
            public Object subject() {
                return String.format("%s.title", self);
            }
        });
    }

    public IStringPropertyAssertion<String> url() {
        final Page self = this;
        return propertyAssertionFactory.string(new AssertionProvider<String>() {
            @Override
            public String actual() {
                return driver.getCurrentUrl();
            }

            @Override
            public Object subject() {
                return String.format("%s.url", self);
            }
        });
    }

    protected Finder inFrame(IGuiElement frame) {
        return new FrameFind(frame);
    }
    protected Finder forAncestor(IGuiElement ancestor) {
        return new AncestorFind(ancestor);
    }
    protected IGuiElement findOneById(final String id) {
        return findOne(Locate.by().id(id));
    }
    protected IGuiElement findOneByQa(final String qa) {
        return findOne(Locate.by().qa(qa));
    }
    protected IGuiElement findOne(final By by) {
        return findOne(Locate.by(by));
    }
    protected IGuiElement findOne(final Locate locator) {
        return guiElementFactory.create(locator, driver);
    }
    protected <T extends WebDriverRetainer> T createPage(final Class<T> pageClass) {
        return pageFactory.create(pageClass, driver);
    }
    protected <T extends WebDriverRetainer> T createComponent(final Class<T> pageClass, final IGuiElement guiElement) {
        return pageFactory.create(pageClass, guiElement);
    }

    /**
     * Fluent actions
     */
    protected abstract SELF self();

    @Override
    public SELF refresh() {
        super.refresh();
        return self();
    }

    public SELF call(final String urlString) {
        driver.navigate().to(urlString);
        return self();
    }

    public SELF call(final URL url) {
        driver.navigate().to(url);
        return self();
    }

    /**
     * Fluent properties
     */
    public IImagePropertyAssertion screenshot() {
        return new ImagePropertyAssertion(new AssertionProvider<File>() {
            @Override
            public File actual() {
                return UITestUtils.takeScreenshotAs(driver, OutputType.FILE);
            }

            @Override
            public Object subject() {
                return String.format("%s.screenshot", this);
            }
        });
    }

    /**
     * Deprecation APIs
     */

    @Override
    @Deprecated
    public SELF takeScreenshot() {
        super.takeScreenshot();
        return self();
    }

    @Override
    @Deprecated
    public Page refresh(boolean checkPage) {
        return super.refresh(checkPage);
    }

    @Override
    @Deprecated
    public void assertIsNotTextDisplayed(String text) {
        super.assertIsNotTextDisplayed(text);
    }

    @Override
    @Deprecated
    public void assertIsNotTextPresent(String text) {
        super.assertIsNotTextPresent(text);
    }

    @Override
    @Deprecated
    public void assertIsTextDisplayed(String text) {
        super.assertIsTextDisplayed(text);
    }

    @Override
    @Deprecated
    public void assertIsTextDisplayed(String text, String description) {
        super.assertIsTextDisplayed(text, description);
    }

    @Override
    @Deprecated
    public void assertIsTextPresent(String text) {
        super.assertIsTextPresent(text);
    }

    @Override
    @Deprecated
    public void assertIsTextPresent(String text, String description) {
        super.assertIsTextPresent(text, description);
    }

    @Override
    @Deprecated
    public void assertPageIsNotShown() {
        super.assertPageIsNotShown();
    }

    @Override
    @Deprecated
    public void assertPageIsShown() {
        super.assertPageIsShown();
    }

    @Override
    @Deprecated
    public boolean isTextDisplayed(String text) {
        return super.isTextDisplayed(text);
    }

    @Override
    @Deprecated
    public boolean isTextPresent(String text) {
        return super.isTextPresent(text);
    }

    @Override
    @Deprecated
    public boolean waitForIsNotTextDisplayed(String text) {
        return super.waitForIsNotTextDisplayed(text);
    }

    @Override
    @Deprecated
    public boolean waitForIsNotTextDisplayedWithDelay(String text, int delayInSeconds) {
        return super.waitForIsNotTextDisplayedWithDelay(text, delayInSeconds);
    }

    @Override
    @Deprecated
    public boolean waitForIsNotTextPresent(String text) {
        return super.waitForIsNotTextPresent(text);
    }

    @Override
    @Deprecated
    public boolean waitForIsNotTextPresentWithDelay(String text, int delayInSeconds) {
        return super.waitForIsNotTextPresentWithDelay(text, delayInSeconds);
    }

    @Override
    @Deprecated
    public boolean waitForIsTextDisplayed(String text) {
        return super.waitForIsTextDisplayed(text);
    }

    @Override
    @Deprecated
    public boolean waitForIsTextPresent(String text) {
        return super.waitForIsTextPresent(text);
    }

    @Override
    @Deprecated
    public void waitForPageToLoad() {
        super.waitForPageToLoad();
    }
}

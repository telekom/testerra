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
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AssertionProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IScreenshotAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IStringPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertion;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import org.openqa.selenium.WebDriver;

import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of new fluent Page Object API
 * @author Mike Reiche
 */
public abstract class FluentPage<SELF extends FluentPage<SELF>> extends AbstractFluentPage<SELF> implements Loggable {

    private static final GuiElementFactory guiElementFactory = Testerra.ioc().getInstance(GuiElementFactory.class);

    private static class FrameFinder implements Finder {
        private IGuiElement frame;
        private FrameFinder(IGuiElement frame) {
            this.frame = frame;
        }
        public IGuiElement find(Locate locator) {
            return guiElementFactory.createWithFrames(locator, frame);
        }
    }

    public FluentPage(WebDriver driver) {
        super(driver);
    }

    protected Finder inFrame(IGuiElement frame) {
        return new FrameFinder(frame);
    }

    @Override
    public SELF refresh() {
        super.refresh();
        return self();
    }

    public SELF call(String urlString) {
        driver.navigate().to(urlString);
        return self();
    }

    public SELF call(URL url) {
        driver.navigate().to(url);
        return self();
    }

    /**
     * Fluent properties
     */
    public IStringPropertyAssertion<String> title() {
        final Page self = this;
        return propertyAssertionFactory.string(new AssertionProvider<String>() {
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

    public IStringPropertyAssertion<String> url() {
        final Page self = this;
        return propertyAssertionFactory.string(new AssertionProvider<String>() {
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

    /**
     * Takes a screenshot of the current page
     */
    public IScreenshotAssertion screenshot() {
        final Page self = this;
        final AtomicReference<Screenshot> atomicScreenshot = new AtomicReference<>();

        Screenshot screenshot = new Screenshot(self.toString());
        UITestUtils.takeScreenshot(driver, screenshot);
        atomicScreenshot.set(screenshot);

        return propertyAssertionFactory.screenshot(new AssertionProvider<Screenshot>() {

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
}

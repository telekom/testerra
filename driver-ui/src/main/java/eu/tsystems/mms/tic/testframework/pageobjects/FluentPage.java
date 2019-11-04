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
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IStringPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertion;
import eu.tsystems.mms.tic.testframework.report.IReport;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of new fluent Page Object API
 * @author Mike Reiche
 */
public abstract class FluentPage<SELF extends FluentPage<SELF>> extends AbstractFluentPage<SELF> implements Loggable {

    private static final GuiElementFactory guiElementFactory = Testerra.ioc().getInstance(GuiElementFactory.class);
    protected static final IReport report = Testerra.ioc().getInstance(IReport.class);

    private static class FrameFinder implements Finder {
        private IGuiElement frame;
        private FrameFinder(IGuiElement frame) {
            this.frame = frame;
        }
        public IGuiElement findOne(Locate locator) {
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

    /**
     * @todo Implement toReport feature
     * @return
     */
    public IImageAssertion screenshot(boolean toReport) {
        final Page self = this;
        final AtomicReference<Screenshot> atomicScreenshot = new AtomicReference<>();
        atomicScreenshot.set(UITestUtils.takeScreenshot(driver));

        if (toReport) {
            report.addScreenshot(atomicScreenshot.get(), IReport.Mode.COPY);
        }

        return propertyAssertionFactory.image(new AssertionProvider<File>() {

            @Override
            public File actual() {
                return atomicScreenshot.get().getScreenshotFile();
            }

            @Override
            public void failed(PropertyAssertion assertion) {
                // Take new screenshot only if failed
                atomicScreenshot.set(UITestUtils.takeScreenshot(driver));
            }

            @Override
            public Object subject() {
                return String.format("%s.screenshot", self);
            }
        });
    }
}

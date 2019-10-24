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

import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.AssertableValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableValue;
import org.openqa.selenium.WebDriver;

import java.net.URL;

/**
 * tt. page objects base class.
 *
 * @author pele
 */
public abstract class FluentPage<SELF extends FluentPage<SELF>> extends Page {
    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public FluentPage(WebDriver driver) {
        super(driver);
    }

    /**
     * @Todo We need a self type here
     * @return
     */
    public IAssertableValue<String, SELF> title() {
        return new AssertableValue(driver.getTitle(), Property.TITLE.toString(), this);
    }

    public IAssertableValue<String, SELF> url() {
        return new AssertableValue(driver.getCurrentUrl(), Property.URL.toString(), this);
    }

    public SELF call(final String urlString) {
        driver.navigate().to(urlString);
        return self();
    }

    public SELF call(final URL url) {
        driver.navigate().to(url);
        return self();
    }

    protected abstract SELF self();

    /**
     * Fluent types
     */
    @Override
    public SELF refresh() {
        super.refresh();
        return self();
    }

    @Override
    public SELF takeScreenshot() {
        super.takeScreenshot();
        return self();
    }

    /**
     * Deprecations
     */
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

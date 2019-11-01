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
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IImagePropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertionFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * New fluent Page Object API
 * @author Mike Reiche
 */
public abstract class AbstractFluentPage<SELF extends AbstractFluentPage<SELF>> extends Page {

    protected static final PropertyAssertionFactory propertyAssertionFactory = Testerra.ioc().getInstance(PropertyAssertionFactory.class);
    private static final GuiElementFactory guiElementFactory = Testerra.ioc().getInstance(GuiElementFactory.class);
    private static final IPageFactory pageFactory = Testerra.ioc().getInstance(IPageFactory.class);

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

    private class AncestorFind implements Finder {
        private IGuiElement ancestor;
        private AncestorFind(IGuiElement ancestor) {
            this.ancestor = ancestor;
        }
        public IGuiElement findOne(Locate locator) {
            return guiElementFactory.createFromAncestor(locator, ancestor);
        }
    }

    public AbstractFluentPage(WebDriver driver) {
        super(driver);
    }

    protected Finder forAncestor(IGuiElement ancestor) {
        return new AncestorFind(ancestor);
    }
    protected IGuiElement findOneById(String id) {
        return findOne(Locate.by().id(id));
    }
    protected IGuiElement findOneByQa(String qa) {
        return findOne(Locate.by().qa(qa));
    }
    protected IGuiElement findOne(By by) {
        return findOne(Locate.by(by));
    }
    protected IGuiElement findOne(Locate locator) {
        return guiElementFactory.create(locator, this);
    }

    protected <T extends IPage> T createPage(final Class<T> pageClass) {
        return pageFactory.create(pageClass, driver);
    }
    protected <T extends IComponent> T createComponent(Class<T> pageClass, IGuiElement guiElement) {
        return pageFactory.createComponent(pageClass, guiElement);
    }

    /**
     * Fluent actions
     */
    protected abstract SELF self();

    /**
     * Fluent Overrides
     */

    public abstract IImagePropertyAssertion screenshot();

    /**
     * Deprecation APIs
     */

    @Override
    @Deprecated
    public Page refresh(boolean checkPage) {
        return super.refresh(checkPage);
    }

    @Override
    @Deprecated
    public void store() {
        super.store();
    }

    @Override
    @Deprecated
    public void forceGuiElementStandardAsserts() {
        super.forceGuiElementStandardAsserts();
    }

    @Override
    @Deprecated
    public void setElementTimeoutInSeconds(int newElementTimeout) {
        super.setElementTimeoutInSeconds(newElementTimeout);
    }

    @Override
    @Deprecated
    public int getElementTimeoutInSeconds() {
        return super.getElementTimeoutInSeconds();
    }

    @Override
    @Deprecated
    public void takeScreenshot() {
        super.takeScreenshot();
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

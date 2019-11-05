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
        IGuiElement find(Locate locator);
        default IGuiElement findOneById(String id) {
            return find(Locate.by().id(id));
        }
        default IGuiElement findOneByQa(String qa) {
            return find(Locate.by().qa(qa));
        }
        default IGuiElement findOne(By by) {
            return find(Locate.by(by));
        }
    }

    protected interface ComponentFinder extends Finder {
        <T extends IComponent> T createComponent(Class<T> componentClass);
    }

    private static class AncestorFinder implements ComponentFinder {
        private final IGuiElement ancestor;
        private final IPage parentPage;
        private AncestorFinder(IPage parentPage, IGuiElement ancestor) {
            this.parentPage = parentPage;
            this.ancestor = ancestor;
        }
        public IGuiElement find(Locate locator) {
            return guiElementFactory.createFromAncestor(locator, ancestor);
        }
        public <T extends IComponent> T createComponent(Class<T> componentClass) {
            return pageFactory.createComponent(componentClass, parentPage, ancestor);
        }
    }

    public AbstractFluentPage(WebDriver driver) {
        super(driver);
    }

    protected ComponentFinder withAncestor(IGuiElement ancestor) {
        return new AncestorFinder(this,ancestor);
    }
    protected IGuiElement findOneById(String id) {
        return find(Locate.by().id(id));
    }
    protected IGuiElement findOneByQa(String qa) {
        return find(Locate.by().qa(qa));
    }
    protected IGuiElement findOne(By by) {
        return find(Locate.by(by));
    }
    protected IGuiElement find(Locate locator) {
        return guiElementFactory.create(locator, this);
    }

    protected <T extends IPage> T createPage(final Class<T> pageClass) {
        return pageFactory.createPage(pageClass, driver);
    }

    /**
     * Fluent actions
     */
    protected abstract SELF self();
}

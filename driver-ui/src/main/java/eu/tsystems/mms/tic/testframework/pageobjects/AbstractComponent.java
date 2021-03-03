/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.internal.Nameable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.AbstractPage;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.UiElementBaseAssertion;
import java.awt.Color;
import java.util.function.Consumer;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Components are wrappers for HTML elements like WebComponents
 * that acts like a {@link UiElement} and {@link PageObject}
 * @author Mike Reiche
 */
public abstract class AbstractComponent<SELF extends AbstractComponent<SELF>> extends AbstractPage<SELF> implements Component<SELF>
{
    protected static final UiElementFactory uiElementFactory = Testerra.getInjector().getInstance(UiElementFactory.class);

    protected final UiElement rootElement;
    private String name;

    public AbstractComponent(UiElement rootElement) {
        this.rootElement = rootElement;
    }

    @Override
    public InteractiveUiElement highlight(Color color) {
        return rootElement.highlight(color);
    }

    @Override
    public String createXPath() {
        return rootElement.createXPath();
    }

    @Override
    public Locator getLocator() {
        return rootElement.getLocator();
    }

    @Override
    public InteractiveUiElement scrollIntoView(Point offset) {
        return rootElement.scrollIntoView(offset);
    }

    @Override
    public UiElementList<SELF> list() {
        return new DefaultComponentList<>((SELF)this);
    }

    @Override
    protected UiElement find(Locator locator) {
        GuiElement subElement = (GuiElement)uiElementFactory.createFromParent(rootElement, locator);
        /**
         * We change the parent from its {@link #rootElement} to this {@link Nameable}
         */
        subElement.setParent(this);
        return subElement;
    }

    @Override
    protected UiElement findDeep(Locator locator) {
        return find(locator);
    }

    @Override
    public WebDriver getWebDriver() {
        return rootElement.getWebDriver();
    }

    @Override
    public UiElementBaseAssertion waitFor() {
        return rootElement.waitFor();
    }

    @Override
    public UiElementBaseAssertion expect() {
        return rootElement.expect();
    }

    public SELF setName(String name) {
        this.name = name;
        return (SELF)this;
    }

    @Override
    public String getName(boolean detailed) {
        String name;

        if (!hasName()) name = getClass().getSimpleName();
        else name = this.name;

        if (detailed) {
            name += "("+rootElement.getName(detailed)+")";
        } else if (rootElement.hasName()) {
            name += "("+rootElement.getName()+")";
        }
        return name;
    }

    @Override
    public Nameable getParent() {
        return rootElement.getParent();
    }

    @Override
    public boolean hasName() {
        return name!=null;
    }

    @Override
    public void findWebElement(Consumer<WebElement> consumer) {
        rootElement.findWebElement(consumer);
    }

    @Override
    public void screenshotToReport() {
        rootElement.screenshotToReport();
    }
}

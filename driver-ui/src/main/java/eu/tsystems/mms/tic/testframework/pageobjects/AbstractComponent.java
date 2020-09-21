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

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicUiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.RectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;
import java.awt.Color;
import java.util.function.Consumer;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Components are wrappers for HTML elements like WebComponents
 * that acts like a {@link UiElement} and {@link PageObject}
 * @author Mike Reiche
 */
public abstract class AbstractComponent<SELF extends AbstractComponent<SELF>> extends AbstractPage implements Component<SELF>
{
    protected final UiElement rootElement;
    private String name;
    private DefaultComponentList<SELF> list;

    public AbstractComponent(UiElement rootElement) {
        this.rootElement = rootElement;
    }

    protected abstract SELF self();

    @Override
    public ImageAssertion screenshot() {
        return rootElement.screenshot();
    }

    @Override
    public BasicUiElement highlight(Color color) {
        return rootElement.highlight(color);
    }

    @Override
    public BasicUiElement highlight() {
        rootElement.highlight();
        return this;
    }

    @Override
    public String createXPath() {
        return rootElement.createXPath();
    }

    @Override
    public Locate getLocate() {
        return rootElement.getLocate();
    }

    @Override
    public BasicUiElement scrollIntoView(Point offset) {
        return rootElement.scrollIntoView(offset);
    }

    @Override
    public UiElementList<SELF> list() {
        if (this.list == null) {
            this.list = new DefaultComponentList<>(self());
        }
        return this.list;
    }

    @Override
    protected UiElement find(eu.tsystems.mms.tic.testframework.pageobjects.Locate locate) {
        GuiElement subElement = (GuiElement)uiElementFactory.createFromParent(rootElement, locate);
        subElement.setParent(this);
        return subElement;
    }

    @Override
    public WebDriver getWebDriver() {
        return rootElement.getWebDriver();
    }

    @Override
    public BinaryAssertion<Boolean> present() {
        return rootElement.present();
    }

    @Override
    public BinaryAssertion<Boolean> visible(boolean complete) {
        return rootElement.visible(complete);
    }

    @Override
    public StringAssertion<String> tagName() {
        return rootElement.tagName();
    }

    @Override
    public RectAssertion bounds() {
        return rootElement.bounds();
    }

    @Override
    public QuantityAssertion<Integer> numberOfElements() {
        return rootElement.numberOfElements();
    }

    @Override
    public BinaryAssertion<Boolean> displayed() {
        return rootElement.displayed();
    }

    public SELF setName(String name) {
        this.name = name;
        return self();
    }

    @Override
    public String getName(boolean detailed) {
        String name = this.name;
        if (name == null) name = getClass().getSimpleName();
        if (detailed) {
            name += "("+rootElement.getName(detailed)+")";
        }
        return name;
    }

    @Override
    public Nameable getParent() {
        return rootElement.getParent();
    }

    @Override
    public void findWebElement(Consumer<WebElement> consumer) {
        rootElement.findWebElement(consumer);
    }
}

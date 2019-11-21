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

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Hierarchy;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.RectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;
import org.openqa.selenium.WebElement;

/**
 * Components are wrappers for HTML elements like components
 * @author Mike Reiche
 */
public abstract class Component extends AbstractPage implements
    IComponent<Component>,
    Hierarchy<Component>
{
    protected final IGuiElement rootElement;
    private final Finder defaultFinder;
    private String name;
    private Object parent;

    public Component(IGuiElement rootElement) {
        this.rootElement = rootElement;
        defaultFinder = withAncestor(rootElement);
    }

    @Override
    public ImageAssertion screenshot() {
        return rootElement.screenshot();
    }

    @Override
    public BasicGuiElement scrollTo(int yOffset) {
        return rootElement.scrollTo(yOffset);
    }

    @Override
    public BasicGuiElement highlight() {
        return rootElement.highlight();
    }

    @Override
    public Locate getLocate() {
        return rootElement.getLocate();
    }

    @Override
    public WebElement getWebElement() {
        return rootElement.getWebElement();
    }

    protected IGuiElement find(Locate locate) {
        return defaultFinder.find(locate);
    }

    @Override
    @Deprecated
    public GuiElementAssert nonFunctionalAsserts() {
        return rootElement.nonFunctionalAsserts();
    }

    @Override
    @Deprecated
    public GuiElementAssert asserts() {
        return rootElement.asserts();
    }

    @Override
    @Deprecated
    public GuiElementAssert instantAsserts() {
        return rootElement.instantAsserts();
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
    public BinaryAssertion<Boolean> displayed() {
        return rootElement.displayed();
    }

    @Override
    public Component setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Component setParent(Object parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public Object getParent() {
        return parent;
    }

    @Override
    public String toString() {
        String toString="";
        if (parent!=null) {
            toString += parent+".";
        }
        if (name!=null) {
            toString += name;
        } else {
            toString += super.toString();
        }
        return toString;
    }
}

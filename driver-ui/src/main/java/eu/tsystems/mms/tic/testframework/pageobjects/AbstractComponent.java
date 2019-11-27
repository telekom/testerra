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
import eu.tsystems.mms.tic.testframework.pageobjects.internal.HasParent;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.FieldAction;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.FieldWithActionConfig;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.RectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;
import org.openqa.selenium.WebElement;

import java.util.Iterator;
import java.util.List;

/**
 * Components are wrappers for HTML elements like WebComponents
 * that acts like a {@link IGuiElement} and {@link PageObject}
 *      Supports finding component elements by {@link #find(Locate)}
 * @author Mike Reiche
 */
public abstract class AbstractComponent<SELF extends AbstractComponent<SELF>> extends AbstractPage implements Component<SELF>
{
    protected final IGuiElement rootElement;
    private String name;
    private HasParent parent;

    public AbstractComponent(IGuiElement rootElement) {
        GuiElement realRootElement = (GuiElement)rootElement;
        setParent(realRootElement.getParent());
        realRootElement.setParent(this);
        this.rootElement = rootElement;
    }

    protected abstract SELF self();

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

    @Override
    public SELF element(int position) {
        return createIteratedComponent(rootElement.element(position));
    }

    @Override
    protected IGuiElement find(Locate locate) {
        return rootElement.find(locate);
    }

    @Override
    protected void addCustomFieldAction(FieldWithActionConfig field, List<FieldAction> fieldActions, AbstractPage declaringPage) {
    }

    @Override
    public void waitForPageToLoad() {
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
    public TestableGuiElement waitFor() {
        return rootElement.waitFor();
    }

    @Override
    public QuantityAssertion<Integer> numberOfElements() {
        return rootElement.numberOfElements();
    }

    @Override
    public BinaryAssertion<Boolean> displayed() {
        return rootElement.displayed();
    }

    @Override
    public SELF setName(String name) {
        this.name = name;
        return self();
    }

    @Override
    public String getName() {
        return name;
    }

    public AbstractComponent setParent(HasParent parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public HasParent getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean detailed) {
        StringBuilder sb = new StringBuilder();
        if (parent!=null) {
            sb.append(parent.toString(detailed)).append(".");
        }
        if (name!=null) {
            sb.append(name);
        } else {
            sb.append(getClass().getSimpleName());
        }
        return sb.toString();
    }

    @Override
    public Iterator<SELF> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return rootElement.hasNext();
    }

    @Override
    public SELF next() {
        return createIteratedComponent(rootElement.next());
    }

    private SELF createIteratedComponent(IGuiElement rootElement) {
        SELF component = (SELF) createComponent(self().getClass(), rootElement);
        component.setParent(getParent());
        return component;
    }
}

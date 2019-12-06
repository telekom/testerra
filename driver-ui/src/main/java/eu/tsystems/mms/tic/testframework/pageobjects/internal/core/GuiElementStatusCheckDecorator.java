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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;

/**
 * @deprecated This decorator can be replaced by {@link BasicGuiElement}
 */
@Deprecated
public abstract class GuiElementStatusCheckDecorator implements GuiElementStatusCheck {
    private GuiElementStatusCheck decoratedGuiElementStatusCheck;

    public GuiElementStatusCheckDecorator(GuiElementStatusCheck decoratedGuiElementStatusCheck) {
        this.decoratedGuiElementStatusCheck = decoratedGuiElementStatusCheck;
    }

    protected abstract void beforeDelegation();

    protected abstract void afterDelegation();

    @Override
    public boolean isPresent() {
        beforeDelegation();
        boolean present = decoratedGuiElementStatusCheck.isPresent();
        afterDelegation();
        return present;
    }

    @Override
    public boolean isEnabled() {
        beforeDelegation();
        boolean enabled = decoratedGuiElementStatusCheck.isEnabled();
        afterDelegation();
        return enabled;
    }

    @Override
    public boolean isDisplayed() {
        beforeDelegation();
        boolean displayed = decoratedGuiElementStatusCheck.isDisplayed();
        afterDelegation();
        return displayed;
    }

    @Override
    public boolean isVisible(boolean complete) {
        beforeDelegation();
        boolean visible = decoratedGuiElementStatusCheck.isVisible(complete);
        afterDelegation();
        return visible;
    }

    @Override
    public boolean isSelected() {
        beforeDelegation();
        boolean selected = decoratedGuiElementStatusCheck.isSelected();
        afterDelegation();
        return selected;
    }

    @Override
    public String getText() {
        beforeDelegation();
        String text = decoratedGuiElementStatusCheck.getText();
        afterDelegation();
        return text;
    }

    @Override
    public String getAttribute(String attributeName) {
        beforeDelegation();
        String attributeValue = decoratedGuiElementStatusCheck.getAttribute(attributeName);
        afterDelegation();
        return attributeValue;
    }

    @Override
    public boolean isDisplayedFromWebElement() {
        beforeDelegation();
        boolean displayedFromWebElement = decoratedGuiElementStatusCheck.isDisplayedFromWebElement();
        afterDelegation();
        return displayedFromWebElement;
    }

    @Override
    public boolean isSelectable() {
        beforeDelegation();
        boolean selectable = decoratedGuiElementStatusCheck.isSelectable();
        afterDelegation();
        return selectable;
    }
}

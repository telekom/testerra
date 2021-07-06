/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import java.awt.Color;
import org.openqa.selenium.Point;

/**
 * Abstract decorator for a {@link GuiElementCoreActions}
 */
public abstract class AbstractGuiElementCoreActionsDecorator implements GuiElementCoreActions {

    protected final GuiElementCore decoratedCore;

    public AbstractGuiElementCoreActionsDecorator(GuiElementCore decoratedCore) {
        this.decoratedCore = decoratedCore;
    }

    protected void beforeDelegation(String method, Object ... params) {
    }

    protected void afterDelegation() {
    }

    @Override
    @Deprecated
    public void scrollToElement(int yOffset) {
        beforeDelegation("scrollToElement", yOffset);
        decoratedCore.scrollToElement(yOffset);
        afterDelegation();
    }

    @Override
    public void scrollIntoView(Point offset) {
        beforeDelegation("scrollIntoView", offset);
        decoratedCore.scrollIntoView(offset);
        afterDelegation();
    }

    @Override
    public void scrollToTop() {
        beforeDelegation("scrollToTop");
        decoratedCore.scrollToTop();
        afterDelegation();
    }

    @Override
    public void select() {
        beforeDelegation("select");
        decoratedCore.select();
        afterDelegation();
    }

    @Override
    public void deselect() {
        beforeDelegation("deselect");
        decoratedCore.deselect();
        afterDelegation();
    }

    @Override
    public void type(String text) {
        beforeDelegation("type", text);
        decoratedCore.type(text);
        afterDelegation();
    }

    @Override
    public void click() {
        beforeDelegation("click");
        decoratedCore.click();
        afterDelegation();
    }

    @Override
    public void contextClick() {
        beforeDelegation("contextClick");
        decoratedCore.contextClick();
        afterDelegation();
    }

    @Override
    public void submit() {
        beforeDelegation("submit");
        decoratedCore.submit();
        afterDelegation();
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        beforeDelegation("sendKeys", charSequences);
        decoratedCore.sendKeys(charSequences);
        afterDelegation();
    }

    @Override
    public void clear() {
        beforeDelegation("clear");
        decoratedCore.clear();
        afterDelegation();
    }

    @Override
    public void hover() {
        beforeDelegation("hover");
        decoratedCore.hover();
        afterDelegation();
    }

    @Override
    public void doubleClick() {
        beforeDelegation("doubleClick");
        decoratedCore.doubleClick();
        afterDelegation();
    }

    @Override
    public void highlight(Color color) {
        beforeDelegation("highlight", color);
        decoratedCore.highlight(color);
        afterDelegation();
    }

    @Override
    public void swipe(int offsetX, int offSetY) {
        beforeDelegation("swipe", offsetX, offSetY);
        decoratedCore.swipe(offsetX, offSetY);
        afterDelegation();
    }
}

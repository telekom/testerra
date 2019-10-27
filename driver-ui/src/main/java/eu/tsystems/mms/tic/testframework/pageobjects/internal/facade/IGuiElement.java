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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.facade;

import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IWebDriverRetainer;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IBinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IValueAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import org.openqa.selenium.By;

import java.util.List;

public interface IGuiElement extends
    GuiElementCore,
    Nameable,
    IWebDriverRetainer
{
    /**
     * New Features
     */
    IValueAssertion<String> text();
    IValueAssertion<String> value();
    IValueAssertion<String> value(final Attribute attribute);
    IBinaryAssertion<Boolean> present();
    IBinaryAssertion<Boolean> visible(final boolean complete);
    IBinaryAssertion<Boolean> displayed();
    IBinaryAssertion<Boolean> enabled();
    IBinaryAssertion<Boolean> selected();
    IImageAssertion screenshot();
    IGuiElement scrollTo();
    IGuiElement scrollTo(final int yOffset);

    /**
     * Unknown Features
     */
    int getTimeoutInSeconds();
    IGuiElement setTimeoutInSeconds(int timeoutInSeconds);
    IGuiElement restoreDefaultTimeout();

    IFrameLogic getFrameLogic();

    /**
     * Deprecated APIs
     */
    @Deprecated
    List<GuiElement> getList();

    @Override
    @Deprecated
    GuiElement getSubElement(final By by);

    @Override
    @Deprecated
    GuiElement getSubElement(final Locate locator);

    @Override
    @Deprecated
    IGuiElement scrollToElement(final int yOffset);

    @Override
    @Deprecated
    IGuiElement scrollToElement();

    @Override
    @Deprecated
    String getCssValue(final String cssIdentifier);

    @Deprecated
    GuiElementAssert asserts();

    @Deprecated
    GuiElementAssert asserts(String errorMessage);

    @Deprecated
    GuiElementWait waits();

    @Deprecated
    GuiElementAssert nonFunctionalAsserts();

    @Deprecated
    GuiElementAssert nonFunctionalAsserts(String errorMessage);

    @Override
    @Deprecated
    boolean isDisplayed();

    @Override
    @Deprecated
    boolean isPresent();

    @Override
    @Deprecated
    boolean isEnabled();

    /**
     * Fluent {@link IGuiElement} overrides
     */
    IGuiElement select(final Boolean select);

    @Override
    IGuiElement click();

    @Override
    IGuiElement clickJS();

    @Override
    IGuiElement doubleClick();

    @Override
    IGuiElement doubleClickJS();

    @Override
    IGuiElement rightClick();

    @Override
    IGuiElement rightClickJS();

    @Override
    IGuiElement setName(String name);

    @Override
    IGuiElement highlight();

    @Override
    IGuiElement swipe(final int offsetX, final int offSetY);

    @Override
    IGuiElement select();

    @Override
    IGuiElement deselect();

    @Override
    IGuiElement type(final String text);

    @Override
    IGuiElement sendKeys(final CharSequence... charSequences);

    @Override
    IGuiElement clear();
}

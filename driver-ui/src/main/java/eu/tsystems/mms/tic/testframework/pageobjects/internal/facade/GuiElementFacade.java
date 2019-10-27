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

public interface GuiElementFacade extends
    GuiElementCore,
    Nameable,
    IWebDriverRetainer
{
    IFrameLogic getFrameLogic();
    IValueAssertion<String> text();
    IValueAssertion<String> value();
    IValueAssertion<String> value(final Attribute attribute);
    IBinaryAssertion<Boolean> present();
    IBinaryAssertion<Boolean> visible(final boolean complete);
    IBinaryAssertion<Boolean> displayed();
    IBinaryAssertion<Boolean> enabled();
    IBinaryAssertion<Boolean> selected();
    IImageAssertion screenshot();
    GuiElementFacade scrollTo();
    GuiElementFacade scrollTo(final int yOffset);

    GuiElementFacade select(final Boolean select);

    int getTimeoutInSeconds();
    GuiElementFacade setTimeoutInSeconds(int timeoutInSeconds);
    GuiElementFacade restoreDefaultTimeout();

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
    GuiElementFacade scrollToElement(final int yOffset);

    @Override
    @Deprecated
    GuiElementFacade scrollToElement();

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
     * GuiElementCore to GuiElementFacade overrides
     */
    @Override
    GuiElementFacade click();

    @Override
    GuiElementFacade clickJS();

    @Override
    GuiElementFacade doubleClick();

    @Override
    GuiElementFacade doubleClickJS();

    @Override
    GuiElementFacade rightClick();

    @Override
    GuiElementFacade rightClickJS();

    @Override
    GuiElementFacade setName(String name);

    @Override
    GuiElementFacade highlight();

    @Override
    GuiElementFacade swipe(final int offsetX, final int offSetY);

    @Override
    GuiElementFacade select();

    @Override
    GuiElementFacade deselect();

    @Override
    GuiElementFacade type(final String text);

    @Override
    GuiElementFacade sendKeys(final CharSequence... charSequences);

    @Override
    GuiElementFacade clear();
}

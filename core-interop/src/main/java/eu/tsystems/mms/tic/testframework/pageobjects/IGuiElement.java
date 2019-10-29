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
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IBinaryPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IImagePropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IStringPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import org.openqa.selenium.By;

public interface IGuiElement extends
    GuiElementCore,
    Nameable<IGuiElement>,
    WebDriverRetainer
{
    /**
     * New Features
     */
    IStringPropertyAssertion<String> tagName();
    IStringPropertyAssertion<String> text();
    IStringPropertyAssertion<String> value();
    IStringPropertyAssertion<String> value(Attribute attribute);
    IBinaryPropertyAssertion<Boolean> present();
    IBinaryPropertyAssertion<Boolean> visible(boolean complete);
    IBinaryPropertyAssertion<Boolean> displayed();
    IBinaryPropertyAssertion<Boolean> enabled();
    IBinaryPropertyAssertion<Boolean> selected();
    IImagePropertyAssertion screenshot();
    IGuiElement scrollTo();

    /**
     * This method scrolls to the element with an given offset.
     * In difference to {@link #scrollToElement(int)} it adds the offset, not substracts it.
     */
    IGuiElement scrollTo(int yOffset);
    IFrameLogic getFrameLogic();

    /**
     * Deprecated APIs
     */
    @Override
    @Deprecated
    IGuiElement getSubElement(By by);

    @Override
    @Deprecated
    IGuiElement getSubElement(Locate locator);

    @Override
    @Deprecated
    IGuiElement scrollToElement(int yOffset);

    @Override
    @Deprecated
    IGuiElement scrollToElement();

    @Override
    @Deprecated
    String getCssValue(String cssIdentifier);

    @Override
    @Deprecated
    boolean isDisplayed();

    @Override
    @Deprecated
    boolean isPresent();

    @Override
    @Deprecated
    boolean isEnabled();

    @Override
    @Deprecated
    boolean isSelected();

    @Override
    @Deprecated
    boolean anyFollowingTextNodeContains(String contains);

    @Override
    @Deprecated
    String getAttribute(String attributeName);

    @Override
    @Deprecated
    By getBy();

    @Override
    @Deprecated
    int getLengthOfValueAfterSendKeys(String textToInput);

    @Override
    @Deprecated
    int getNumberOfFoundElements();

    @Override
    @Deprecated
    boolean isVisible(boolean complete);

    @Override
    @Deprecated
    String getTagName();

    @Override
    @Deprecated
    String getText();

    /**
     * Fluent {@link IGuiElement} overrides
     */
    IGuiElement select(Boolean select);

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
    IGuiElement highlight();

    @Override
    IGuiElement swipe(int offsetX, int offSetY);

    @Override
    IGuiElement select();

    @Override
    IGuiElement deselect();

    @Override
    IGuiElement type(String text);

    @Override
    IGuiElement sendKeys(CharSequence... charSequences);

    @Override
    IGuiElement clear();

    @Override
    IGuiElement mouseOver();

    @Override
    GuiElementCore mouseOverJS();
}

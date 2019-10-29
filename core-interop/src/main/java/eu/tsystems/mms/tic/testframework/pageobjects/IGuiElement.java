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
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;

public interface IGuiElement extends
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
    IStringPropertyAssertion<String> value(String attribute);
    IBinaryPropertyAssertion<Boolean> present();
    IBinaryPropertyAssertion<Boolean> visible(boolean complete);
    IBinaryPropertyAssertion<Boolean> displayed();
    IBinaryPropertyAssertion<Boolean> enabled();
    IBinaryPropertyAssertion<Boolean> selected();
    IImagePropertyAssertion screenshot();
    IGuiElement scrollTo();

    /**
     * This method scrolls to the element with an given offset.
     */
    IGuiElement scrollTo(int yOffset);
    IFrameLogic getFrameLogic();

    /**
     * Fluent {@link IGuiElement} overrides
     */
    IGuiElement select(Boolean select);

    IGuiElement click();

    IGuiElement clickJS();

    IGuiElement doubleClick();

    IGuiElement doubleClickJS();

    IGuiElement rightClick();

    IGuiElement rightClickJS();

    IGuiElement highlight();

    IGuiElement swipe(int offsetX, int offSetY);

    IGuiElement select();

    IGuiElement deselect();

    IGuiElement type(String text);

    IGuiElement sendKeys(CharSequence... charSequences);

    IGuiElement clear();

    IGuiElement mouseOver();

    IGuiElement mouseOverJS();
}

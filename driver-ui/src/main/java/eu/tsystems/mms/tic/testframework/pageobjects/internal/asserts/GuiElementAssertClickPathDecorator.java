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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.clickpath.ClickPath;
import eu.tsystems.mms.tic.testframework.pageobjects.clickpath.ClickPathElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import org.openqa.selenium.WebDriver;

/**
 * Created by rnhb on 26.10.2015.
 */
public class GuiElementAssertClickPathDecorator extends GuiElementAssertDecorator {

    private final GuiElementData guiElementData;
    private final WebDriver webDriver;

    public GuiElementAssertClickPathDecorator(GuiElementAssert decoratedAssert, GuiElementData guiElementData) {
        super(decoratedAssert);
        this.guiElementData = guiElementData;
        this.webDriver = guiElementData.webDriver;
    }

    @Override
    void beforeAssertion() {

    }

    @Override
    void afterAssertion(String message, AssertionError assertionErrorOrNull) {
        if (assertionErrorOrNull != null) {
            ClickPathElement clickPathElement = new ClickPathElement(ClickPathElement.CPEType.ERROR, message + " " + guiElementData);
            LOGGER.debug("actionInCatchBlock(): Adding element to ClickPath: " + clickPathElement);
            ClickPath.stack(clickPathElement, webDriver);
        }
    }
}

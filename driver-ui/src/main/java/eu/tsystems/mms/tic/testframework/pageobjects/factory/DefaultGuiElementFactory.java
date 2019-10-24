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
package eu.tsystems.mms.tic.testframework.pageobjects.factory;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DefaultGuiElementFactory implements
    GuiElementFactory,
    Loggable
{
    @Override
    public GuiElementFacade create(
        Locate locate,
        WebDriver webDriver,
        GuiElementFacade parent
    ) {
        String locatorToString = locate.getBy().toString();
        if (locatorToString.toLowerCase().contains("xpath")) {
            int i = locatorToString.indexOf(":") + 1;
            String locator = locatorToString.substring(i).trim();
            // Check if locator does not start with dot, ignoring a leading parenthesis for choosing the n-th element
            if (locator.startsWith("/")) {
                log().warn("Forced replacement of / to ./ at startTime of By.xpath locator, because / would not be relative: " + locator);
                locate = Locate.by(By.xpath(locator));
            } else if (!locator.startsWith(".") && !(locator.length() >= 2 && locator.startsWith("(") && locator.substring(1, 2).equals("."))) {
                log().warn("Apparently, getSubElement is called with an By.xpath locator that does not startTime with a dot. " +
                    "This will most likely lead to unexpected and potentially quiet errors. Locator is \"" +
                    locatorToString + "\".");
            }
        }

        return new GuiElement(locate, webDriver, parent);
    }

    @Override
    public GuiElementFacade create(Locate locator, WebDriver webDriver) {
        return new GuiElement(webDriver, locator);
    }
}

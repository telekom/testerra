/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.Aria;
import eu.tsystems.mms.tic.testframework.pageobjects.Locator;
import eu.tsystems.mms.tic.testframework.pageobjects.LocatorFactoryProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import eu.tsystems.mms.tic.testframework.utils.Condition;
import java.util.ArrayList;
import org.openqa.selenium.By;

/**
 * Aria based implementation of {@link UiElementLabelLocator}
 */
public class AriaElementLocator implements UiElementLabelLocator, LocatorFactoryProvider {

    @Override
    public Locator createLocator(String element, String label) {
        Condition condition = XPath.createAttributeCondition();
        Condition.Chain attributes;

        ArrayList<String> xpathes = new ArrayList<>();
        switch (element) {
            case Aria.BUTTON: {
                attributes = condition
                        .is(XPath.somethingContainsWord(".//text()", label))
                ;
                xpathes.add("//button["+attributes+"]");

                attributes = condition
                        .is(
                            condition.is(XPath.somethingIs("@type", "button"))
                            .or(XPath.somethingIs("@type","submit"))
                        )
                        .and(
                            condition.is(XPath.somethingContainsWord("@value", label))
                            .or(XPath.somethingContainsWord("@aria-label", label))
                        )
                ;
                xpathes.add("//input["+attributes+"]");

                attributes = condition
                        .is(XPath.somethingIs("@role", "button"))
                        .and(XPath.somethingContainsWord(".//text()", label))
                ;
                xpathes.add("//div["+attributes+"]");

                break;
            }
            case Aria.TEXTBOX: {
                attributes = condition
                        .is(
                            condition.is(XPath.somethingIsNot("@type", "button"))
                            .and(XPath.somethingIsNot("@type", "submit")))
                        .and(
                            condition.is()
                            .or(XPath.somethingContainsWord("@title", label))
                            .or(XPath.somethingContainsWord("@aria-label", label))
                            .or(XPath.somethingContainsWord("@placeholder", label))
                            .or(XPath.somethingContainsWord("@value", label))
                        );
                ;
                xpathes.add("//input["+attributes+"]");

                break;
            }

            case Aria.LINK: {
                attributes = condition
                        .is(XPath.somethingContainsWord(".//text()", label))
                        .or(XPath.somethingContainsWord("@title", label))
                ;
                xpathes.add("//a["+attributes+"]");
            }
        }

        return Locate.by(By.xpath("("+String.join("|", xpathes)+")"));
    }
}

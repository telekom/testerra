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

package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementLabelLocator;
import eu.tsystems.mms.tic.testframework.utils.Conditions;
import java.util.ArrayList;
import org.openqa.selenium.By;

/**
 * Default implementation of {@link UiElementLabelLocator}
 */
public class DefaultElementLabelLocator implements UiElementLabelLocator, LocatorFactoryProvider {

    @Override
    public Locator createLocator(String element, String label) {
        Conditions conditions = XPath.createAttributeConditions();
        Conditions.Chain attributes;

        ArrayList<String> xpathes = new ArrayList<>();
        switch (element) {
            case BUTTON: {
                attributes = conditions
                        .is(XPath.somethingContainsWord(".//text()", label))
                ;
                xpathes.add("//button["+attributes+"]");

                attributes = conditions
                        .is(
                            conditions.is(XPath.somethingIs("@type", "button"))
                            .or(XPath.somethingIs("@type","submit"))
                        )
                        .and(XPath.somethingContainsWord("@value", label))
                ;
                xpathes.add("//input["+attributes+"]");

                attributes = conditions
                        .is(XPath.somethingIs("@role", "button"))
                        .and(XPath.somethingContainsWord(".//text()", label))
                ;
                xpathes.add("//div["+attributes+"]");

                break;
            }
            case INPUT: {
                attributes = conditions
                        .is(
                            conditions.is(XPath.somethingIsNot("@type", "button"))
                            .and(XPath.somethingIsNot("@type", "submit")))
                        .and(
                            conditions.is(XPath.somethingContainsWord("@title", label))
                            .or(XPath.somethingContainsWord("@placeholder", label))
                            .or(XPath.somethingContainsWord("@value", label))
                        );
                ;
                xpathes.add("//input["+attributes+"]");

                break;
            }

            case LINK: {
                attributes = conditions
                        .is(XPath.somethingContainsWord(".//text()", label))
                        .or(XPath.somethingContainsWord("@title", label))
                ;
                xpathes.add("//a["+attributes+"]");
            }
        }

        return Locate.by(By.xpath("("+String.join("|", xpathes)+")"));
    }
}

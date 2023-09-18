/*
 * Testerra
 *
 * (C) 2023, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.Color;
import java.util.stream.Stream;

/**
 * Created on 2023-07-07
 *
 * @author mgn
 */
public interface UiElementHighlighter extends Loggable {

    String getJSSnippet();

    default void highlight(WebDriver driver, WebElement webElement, Color color) {
        long timeout = Testerra.Properties.DEMO_MODE_TIMEOUT.asLong();
        try {
            JSUtils.executeScriptWOCatch(
                    driver,
                    String.format(
                            "%s\n" +
                                    "ttHighlight(arguments[0], '%s', %d)",
                            JSUtils.readScriptResources(Stream.of(getJSSnippet())),
                            toHex(color),
                            timeout
                    ),
                    webElement
            );
        } catch (Exception e) {
            log().error("Unable to highlight WebElement: {}", e.getMessage());
        }
    }

    default String toHex(Color color) {
        return String.format("#%02x%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

}

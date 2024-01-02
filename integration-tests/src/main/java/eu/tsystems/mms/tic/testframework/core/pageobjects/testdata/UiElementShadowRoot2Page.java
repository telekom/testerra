/*
 * Testerra
 *
 * (C) 2023, Martin Großmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.core.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UiElementShadowRoot2Page extends Page {

    // shadow root elements
    public final UiElement shadowRootElement1 = find(By.id("shadow_host")).shadowRoot();
    // Nested shadow root
    public final UiElement shadowRootElement2 = shadowRootElement1.find(By.cssSelector("#nested_shadow_host")).shadowRoot();

    public UiElement text1 = shadowRootElement1.find(By.cssSelector("#shadow_content"));
    public UiElement textInput = shadowRootElement1.find(By.cssSelector("#textinput"));

    public UiElement nestedShadowContent = shadowRootElement2.find(By.cssSelector("#nested_shadow_content"));
    public UiElement text2 = nestedShadowContent.find(By.tagName("div"));

    public UiElementShadowRoot2Page(WebDriver driver) {
        super(driver);
    }
}

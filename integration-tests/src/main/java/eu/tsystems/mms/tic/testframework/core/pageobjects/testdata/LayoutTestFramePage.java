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
package eu.tsystems.mms.tic.testframework.core.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created on 2023-11-29
 *
 * @author mgn
 */
public class LayoutTestFramePage extends BasePage{

    public UiElement frame = find(By.id("iframe"));

    public UiElement layoutTestArticle = frame.find(LOCATE.byQa("section/layoutTestArticle"));

    public UiElement invisibleTestArticle = frame.find(LOCATE.byQa("section/invisibleTestArticle"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver The web driver.
     */
    public LayoutTestFramePage(WebDriver driver) {
        super(driver);
    }
}

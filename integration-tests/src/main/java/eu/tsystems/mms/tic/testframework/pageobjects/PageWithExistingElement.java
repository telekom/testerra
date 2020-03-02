/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by rnhb on 29.12.2015.
 */
public class PageWithExistingElement extends Page {

    @Check
    private GuiElement existingElement = new GuiElement(driver, By.id("1"));

    private GuiElement notExistingElement = new GuiElement(driver, By.id("NOT_EXISTING_WUWUWUWU"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public PageWithExistingElement(WebDriver driver) {
        super(driver);
        checkPage();
    }
}

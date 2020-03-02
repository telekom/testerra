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
 * Created by rnhb on 24.02.2016.
 */
public class PageWithGuiElementGroups extends Page {

    public static final String GROUP_1 = "group1";
    public static final String GROUP_2 = "group2";
    public static final String GROUP_3 = "group3";

    @Groups(groupNames = GROUP_1)
    GuiElement g1 = new GuiElement(driver, By.xpath(""));

    @Groups(groupNames = GROUP_3)
    GuiElement g2 = new GuiElement(driver, By.xpath(""));

    @Groups(groupNames = {GROUP_1, GROUP_2})
    GuiElement g3 = new GuiElement(driver, By.xpath(""));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public PageWithGuiElementGroups(WebDriver driver) {
        super(driver);
        checkPage();
    }
}

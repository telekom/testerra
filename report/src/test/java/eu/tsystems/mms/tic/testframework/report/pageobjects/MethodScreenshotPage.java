/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MethodScreenshotPage extends MethodDetailsPage {
    @Check
    private GuiElement screenShotInfoButton = mainFrame.getSubElement(By.xpath("//i[@class='bgwhite fa fa-info']"));

    private GuiElement screenShot = mainFrame.getSubElement(By.xpath("//div[@class='fotorama__html']"));

    public MethodScreenshotPage(WebDriver driver) {
        super(driver);
        checkPage();
    }
    public GuiElement getScreenShot() {
        return screenShot;
    }

}

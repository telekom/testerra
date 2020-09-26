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

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MethodStepsPage extends MethodDetailsPage {


    private final String INTERNAL_STEP_LOCATOR = "//a[@id='link_viewclass_";
    private final String BROWSER_LOG_INDICATOR = "Browser:";

    private GuiElement button1Point1 = mainFrame.getSubElement(By.xpath(INTERNAL_STEP_LOCATOR + "1_1']"));
    private GuiElement button2Point1 = mainFrame.getSubElement(By.xpath(INTERNAL_STEP_LOCATOR + "2_1']"));
    private GuiElement button3Point1 = mainFrame.getSubElement(By.xpath(INTERNAL_STEP_LOCATOR + "3_1']"));

    private final String EXTERNAL_STEP_LOCATOR = "//div[@class='list-group textleft listitems']//a[contains(text(),";

    private GuiElement testStep1Button = mainFrame.getSubElement(By.xpath(EXTERNAL_STEP_LOCATOR + "'Test-Step-1')]"));
    private GuiElement testStep2Button = mainFrame.getSubElement(By.xpath(EXTERNAL_STEP_LOCATOR + "'Test-Step-2')]"));
    private GuiElement testStep3Button = mainFrame.getSubElement(By.xpath(EXTERNAL_STEP_LOCATOR + "'Test-Step-3')]"));


    public MethodStepsPage(WebDriver driver) {

        super(driver);
        checkPage();
    }

    public GuiElement getButton1Point1() {
        return button1Point1;
    }

    public GuiElement getButton2Point1() {
        return button2Point1;
    }

    public GuiElement getButton3Point1() {
        return button3Point1;
    }

    public GuiElement getTestStep1Button() {
        return testStep1Button;
    }

    public GuiElement getTestStep2Button() {
        return testStep2Button;
    }

    public GuiElement getTestStep3Button() {
        return testStep3Button;
    }

    public List<GuiElement> getLoggingRowsByActionStep(int majorStep, int minorStep) {
        List<GuiElement> rows = mainFrame.getSubElement(By.xpath("//tr[contains(@class,'viewclass_" + majorStep + "_" + minorStep + "')]")).getList();
        return rows;
    }

}

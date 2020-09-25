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
 package eu.tsystems.mms.tic.testframework.core.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created with IntelliJ IDEA. User: pele Date: 07.01.13 Time: 14:15 To change this template use File | Settings | File
 * Templates.
 */
public class WebTestFramedPage extends Page {


    GuiElement inputFrame1 = new GuiElement(this.getWebDriver(), By.name("InputFrame1"));

    /** First text field on page. */
    @Check
    private final GuiElement input1 = inputFrame1.getSubElement(By.id("1"));
    /** A button on the page. */
    @Check(nonFunctional = true)
    private final GuiElement button1 = inputFrame1.getSubElement(By.id("4"));
    /** The output text field. */
    private final GuiElement textOutputField = new GuiElement(this.getWebDriver(), By.xpath("//p[@id='99']"));
    private final GuiElement textOutputFieldNotExisting = new GuiElement(this.getWebDriver(), By.xpath("//p[@id='notthere']"));

    /**
     * Default Page constructor.
     */
    public WebTestFramedPage(WebDriver driver) {
        super(driver);
        checkPage();
    }

    public void clickOnField() {
        textOutputField.click();
    }
    public void clickOnFieldNotExisting() {
        textOutputFieldNotExisting.click();
    }
}

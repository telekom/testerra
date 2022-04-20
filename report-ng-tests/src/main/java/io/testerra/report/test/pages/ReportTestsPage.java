/*
 * Testerra
 *
 * (C) 2022, Clemens Große, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.pages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportTestsPage extends AbstractReportPage {

    @Check
    public GuiElement testStatusSelect = pageContent.getSubElement(By.xpath(".//mdc-select[@label = 'Status']"));
    @Check
    public GuiElement testClassSelect = pageContent.getSubElement(By.xpath(".//mdc-select[@label = 'Class']"));
    @Check
    public GuiElement testSearchInput = pageContent.getSubElement(By.xpath(".//input[contains(@class, 'mdc-text-field__input')]"));

    public ReportTestsPage(WebDriver driver) {
        super(driver);
    }

    public void assertPageIsShown() {
        verifyReportPage(ReportPageType.TESTS);
    }
}

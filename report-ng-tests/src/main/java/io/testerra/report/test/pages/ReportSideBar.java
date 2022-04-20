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
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class ReportSideBar extends ReportHeader {

    @Check
    private final GuiElement sideBar = new GuiElement(getWebDriver(), By.xpath("//mdc-drawer"));
    @Check
    private final GuiElement sideBarDashBoard = sideBar.getSubElement(By.xpath(".//mdc-list-item[.//span[(text() = 'Dashboard')]]"));
    @Check
    private final GuiElement sideBarTests = sideBar.getSubElement(By.xpath(".//mdc-list-item[.//span[contains(text(), 'Tests')]]"));
    @Check
    private final GuiElement sideBarFailureAspects = sideBar.getSubElement(By.xpath(".//mdc-list-item[.//span[contains(text(), 'Failure Aspects')]]"));

    public ReportSideBar(WebDriver driver) {
        super(driver);
    }

    public <T extends AbstractReportPage> T gotoToReportPage(final ReportPageType reportPageType, final Class<T> reportPageClass) {
        switch (reportPageType) {
            case DASHBOARD:
                sideBarDashBoard.click();
                break;
            case TESTS:
                sideBarTests.click();
                break;
        }

        return PageFactory.create(reportPageClass, getWebDriver());
    }


    protected void verifyReportPage(final ReportPageType reportPageType) {
        switch (reportPageType) {
            case DASHBOARD:
                sideBarDashBoard.asserts().assertAttributeContains("class", "mdc-list-item--activated");
                sideBarTests.asserts().assertAttributeContainsNot("class", "mdc-list-item--activated");
                sideBarFailureAspects.asserts().assertAttributeContainsNot("class", "mdc-list-item--activated");
                break;
            case TESTS:
                sideBarTests.asserts().assertAttributeContains("class", "mdc-list-item--activated");
                sideBarDashBoard.asserts().assertAttributeContainsNot("class", "mdc-list-item--activated");
                sideBarFailureAspects.asserts().assertAttributeContainsNot("class", "mdc-list-item--activated");
                break;
            default:
                break;
        }
    }
}

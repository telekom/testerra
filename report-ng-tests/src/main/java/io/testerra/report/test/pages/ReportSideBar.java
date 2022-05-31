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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.testerra.report.test.pages.utils.RegExUtils;

public abstract class ReportSideBar extends ReportHeader {

    @Check
    private final GuiElement sideBar = new GuiElement(getWebDriver(), By.xpath("//mdc-drawer"));
    @Check
    private final GuiElement sideBarDashBoard = getSidebarElement(ReportPageType.DASHBOARD);
    @Check
    private final GuiElement sideBarTests = getSidebarElement(ReportPageType.TESTS);
    @Check
    private final GuiElement sideBarFailureAspects = getSidebarElement(ReportPageType.FAILURE_ASPECTS);
    @Check
    private final GuiElement sideBarLogs = getSidebarElement(ReportPageType.LOGS);
    @Check
    private final GuiElement sideBarThreads = getSidebarElement(ReportPageType.THREADS);

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
            case FAILURE_ASPECTS:
                sideBarFailureAspects.click();
                break;
            case LOGS:
                sideBarLogs.click();
                break;
            case THREADS:
                sideBarThreads.click();
                break;
        }

        return PageFactory.create(reportPageClass, getWebDriver());
    }

    public void verifyActiveSidebarCategory(final ReportPageType reportPageType) {
        List<GuiElement> sideBarElements = sideBar.getSubElement(By.xpath("/mdc-drawer-content/mdc-list-item")).getList();
        for(GuiElement sidebarElement : sideBarElements){
            if (sidebarElement.getText().toUpperCase().equals(reportPageType.name())){
                sidebarElement.asserts().assertAttributeContains("class", "mdc-list-item--activated");
            } else {
                sidebarElement.asserts().assertAttributeContainsNot("class", "mdc-list-item--activated");
            }
        }
    }

    public int getAmountOfTests() {
        final String testsTextOfSidebar = sideBarTests.getText();
        final String regExpResultOfString = RegExUtils.getRegExpResultOfString(RegExUtils.RegExp.DIGITS_ONLY, testsTextOfSidebar);

        return Integer.parseInt(regExpResultOfString);
    }

    private GuiElement getSidebarElement(ReportPageType reportPageType) {
        return sideBar.getSubElement(By.xpath(String.format(".//mdc-list-item[.//span[(text() = '%s')]]", reportPageType.title)));
    }
}

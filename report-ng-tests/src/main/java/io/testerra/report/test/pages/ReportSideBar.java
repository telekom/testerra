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
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import io.testerra.report.test.pages.utils.RegExUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class ReportSideBar extends ReportHeader {

    @Check
    private final UiElement sideBar = find(By.xpath("//mdc-drawer"));
    @Check
    private final UiElement sideBarDashBoard = sideBar.find(By.xpath(".//mdc-list-item[.//span[(text() = 'Dashboard')]]"));
    @Check
    private final UiElement sideBarTests = sideBar.find(By.xpath(".//mdc-list-item[.//span[contains(text(), 'Tests')]]"));
    @Check
    private final UiElement sideBarFailureAspects = sideBar.find(By.xpath(".//mdc-list-item[.//span[contains(text(), 'Failure Aspects')]]"));
    @Check
    private final UiElement sideBarLogs = sideBar.find(By.xpath(".//mdc-list-item[.//span[contains(text(), 'Logs')]]"));
    @Check
    private final UiElement sideBarThreads = sideBar.find(By.xpath(".//mdc-list-item[.//span[contains(text(), 'Threads')]]"));

    public ReportSideBar(WebDriver driver) {
        super(driver);
    }

    public <T extends AbstractReportPage> T gotoToReportPage(final ReportSidebarPageType reportSidebarPageType, final Class<T> reportPageClass) {
        switch (reportSidebarPageType) {
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

        return createPage(reportPageClass);
    }


    public void verifyReportPage(final ReportSidebarPageType reportSidebarPageType) {
        List<UiElement> sideBarElements = sideBar.find(By.xpath("/mdc-drawer-content/mdc-list-item")).list().stream().collect(Collectors.toList());
        for (UiElement sidebarElement : sideBarElements) {
            final boolean mdcListItemIsActivated = Objects.equals(sidebarElement.expect().text().getActual().toUpperCase(), reportSidebarPageType.name());
            sidebarElement.expect().attribute("class").contains("mdc-list-item--activated").is(mdcListItemIsActivated);
        }
    }

    public UiElement getSideBarTests() {
        return sideBarTests;
    }

    public int getAmountOfTests() {
        final String testsTextOfSidebar = sideBarTests.expect().text().getActual();
        String regExpResultOfString = RegExUtils.getRegExpResultOfString(RegExUtils.RegExp.DIGITS_ONLY, testsTextOfSidebar);
        return Integer.parseInt(regExpResultOfString);
    }
}

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
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.PreparedLocator;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import io.testerra.report.test.pages.utils.RegExUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class ReportSideBar extends Page {

    @Check
    protected final UiElement title = find(By.xpath("//div[contains(@class, 'MuiToolbar') and ./img]//h6"));

    @Check
    private final UiElement sideBar = find(By.xpath("//div[@data-testid = 'sidebar']"));
    private final PreparedLocator menuLinkLocator = LOCATE.prepare("//li[@data-testid = '%s']//a");
    @Check
    private final UiElement menuDashboardLink = sideBar.find(menuLinkLocator.with("menu-Dashboard"));
    @Check
    private final UiElement menuTestsLink = sideBar.find(menuLinkLocator.with("menu-Tests"));
    @Check
    private final UiElement menuFailureAspectsLink = sideBar.find(menuLinkLocator.with("menu-Failure Aspects"));
    @Check
    private final UiElement menuLogsLink = sideBar.find(menuLinkLocator.with("menu-Logs"));
//    @Check
    private final UiElement menuThreadsLink = sideBar.find(menuLinkLocator.with("menu-Threads"));
//    @Check
    private final UiElement menuHistoryLink = sideBar.find(menuLinkLocator.with("menu-History"));
//    @Check
    private final UiElement menuPrintReportLink = sideBar.find(menuLinkLocator.with("menu-Print Report"));
    @Check
    private final UiElement menuTimingsLink = sideBar.find(menuLinkLocator.with("menu-Timings"));


    public ReportSideBar(WebDriver driver) {
        super(driver);
    }

    public <T extends AbstractReportPage> T gotoToReportPage(final ReportSidebarPageType reportSidebarPageType, final Class<T> reportPageClass) {
        switch (reportSidebarPageType) {
            case DASHBOARD:
                menuDashboardLink.click();
                break;
            case TESTS:
                menuTestsLink.click();
                break;
            case FAILURE_ASPECTS:
                menuFailureAspectsLink.click();
                break;
            case LOGS:
                menuLogsLink.click();
                break;
            case THREADS:
                menuThreadsLink.click();
                break;
            case HISTORY:
                menuHistoryLink.click();
                break;
            case PRINT_REPORT:
                menuPrintReportLink.click();
        }

        return createPage(reportPageClass);
    }


//    public void verifyReportPage(final ReportSidebarPageType reportSidebarPageType) {
//        List<UiElement> sideBarElements = sideBar.find(By.xpath("/mdc-drawer-content/mdc-list-item")).list().stream().collect(Collectors.toList());
//        for (UiElement sidebarElement : sideBarElements) {
//            final boolean mdcListItemIsActivated = Objects.equals(sidebarElement.expect().text().getActual().toUpperCase(), reportSidebarPageType.name());
//            sidebarElement.expect().attribute("class").contains("mdc-list-item--activated").is(mdcListItemIsActivated);
//        }
//    }

    public UiElement getMenuTestsLink() {
        return menuTestsLink;
    }

    public int getAmountOfTests() {
        final String testsTextOfSidebar = menuTestsLink.expect().text().getActual();
        String regExpResultOfString = RegExUtils.getRegExpResultOfString(RegExUtils.RegExp.DIGITS_ONLY, testsTextOfSidebar);
        return Integer.parseInt(regExpResultOfString);
    }
}

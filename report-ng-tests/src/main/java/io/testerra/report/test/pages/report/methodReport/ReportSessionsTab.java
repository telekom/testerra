/*
 * Testerra
 *
 * (C) 2022, Marc Dietrich, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportSessionsTab extends AbstractReportMethodPage {

    @Check
    private final UiElement sessionCard = tabPagesContent.find(By.tagName("mdc-layout-grid-cell"));
    @Check
    private final UiElement headline = sessionCard.find(By.xpath("//*[contains(text(),'Session')]"));
    @Check
    private final UiElement id = sessionCard.find(By.xpath("/ul//span[contains(text(),'ID')]"));
    @Check
    private final UiElement userAgent = sessionCard.find(By.xpath("/ul//span[contains(text(),'User agent')]"));

    private final UiElement node = sessionCard.find(By.xpath("/ul//span[contains(text(),'Node')]"));
    @Check
    private final UiElement capabilityHeadline = sessionCard.find(By.xpath("/div[contains(text(),'Capabilities')]"));
    @Check
    private final UiElement capabilities = sessionCard.find(By.xpath("/div[contains(@class,'capabilities-view')]"));

    public ReportSessionsTab(WebDriver driver) {
        super(driver);
    }
}

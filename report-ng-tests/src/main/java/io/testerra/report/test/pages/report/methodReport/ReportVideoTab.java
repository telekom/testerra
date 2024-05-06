/*
 * Testerra
 *
 * (C) 2024, Selina Natschke, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

public class ReportVideoTab extends AbstractReportMethodPage {

    @Check
    private final UiElement videoCard = tabPagesContent.find(By.tagName("mdc-card"));
    private final UiElement headline = videoCard.find(By.xpath("//*[contains(@class, 'card-headline') and contains(text(),'Session')]"));
    @Check
    private final UiElement id = videoCard.find(By.xpath("//a[contains(@href, 'browser-info')]"));
    @Check
    private final UiElement browser = videoCard.find(By.xpath("//li[./span[contains(text(), 'Browser')]]"));
    @Check
    private final UiElement video = videoCard.find(By.xpath("//video"));

    public ReportVideoTab(WebDriver driver) {
        super(driver);
    }

    public void validateBrowser(String browser) {
        this.browser.assertThat().text().isContaining(browser);
    }

    public void checkSessionId(){
        this.id.assertThat().displayed();
    }

    public void checkVideo(){
        this.video.assertThat().displayed();
    }
}

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
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * This class represents the Page Object for the expandable menu in the right upper corner of a TesterraReportPage
 */
public class BurgerMenu extends AbstractReportPage {

    @Check
    private GuiElement exitPointsLink = mainFrame.getSubElement(By.id("ExitPoints"));
    private GuiElement logsLink = mainFrame.getSubElement(By.id("Logs"));
    private GuiElement timingsLink = mainFrame.getSubElement(By.id("Timings"));
    private GuiElement monitorLink = mainFrame.getSubElement(By.id("JVM Monitor"));



    /**
     * Constructor called bei PageFactory
     *
     * @param driver Webdriver to use for this Page
     */
    public BurgerMenu(WebDriver driver) {
        super(driver);
    }

    /**
     * Method to navigate to the ExitPointsPage
     *
     * @return
     */
    public ExitPointsPage openExitPointsPage() {
        exitPointsLink = exitPointsLink.getSubElement(By.xpath("./a"));
        exitPointsLink.click();
        return PageFactory.create(ExitPointsPage.class, this.getWebDriver());
    }

    /**
     * Method to navigate to the LogsPage
     *
     * @return
     */
    public LogsPage openLogsPage() {
        logsLink = logsLink.getSubElement(By.xpath("./a"));
        logsLink.click();
        return PageFactory.create(LogsPage.class, this.getWebDriver());
    }

    /**
     * Method to navigate to the TimingsPage
     *
     * @return
     */
    public TimingsPage openTimingsPage() {
        timingsLink = timingsLink.getSubElement(By.xpath("./a"));
        timingsLink.click();
        return PageFactory.create(TimingsPage.class, this.getWebDriver());
    }

    /**
     * Method to navigate to the MonitorPage
     *
     * @return
     */
    public MonitorPage openMonitorPage() {
        monitorLink = monitorLink.getSubElement(By.xpath("./a"));
        monitorLink.click();
        return PageFactory.create(MonitorPage.class, this.getWebDriver());
    }


}

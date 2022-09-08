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

public abstract class AbstractReportPage extends ReportSideBar {

    @Check
    protected final GuiElement pageContent = new GuiElement(getWebDriver(), By.tagName("mdc-layout-grid"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public AbstractReportPage(WebDriver driver) {
        super(driver);
    }

    /**
     * generic method selecting Element in a drop down list via provided label String
     * @param dropbox
     * @param label
     * @param clazz
     * @param <T>
     * @return clazz
     */
    protected <T extends AbstractReportPage> T selectDropBoxElement(GuiElement dropbox, String label, Class<T> clazz) {
        // open dropbox
        dropbox.click();

        // select element
        final GuiElement statusItem = dropbox.getSubElement(By.xpath(".//mdc-list-item[.//span[contains(text(), '" + label + "')]]"));
        statusItem.click();

        return PageFactory.create(clazz, getWebDriver());
    }
}

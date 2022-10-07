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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class AbstractReportPage extends ReportSideBar {

    @Check
    protected final UiElement pageContent = find(By.tagName("mdc-layout-grid"));

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
     *
     * @return clazz
     */
    protected <T extends AbstractReportPage> T selectDropBoxElement(UiElement dropbox, String label, Class<T> clazz) {
        // open dropbox
        dropbox.click();

        // select element
        final UiElement statusItem = dropbox.find(By.xpath(".//mdc-list-item[.//span[contains(text(), '" + label + "')]]"));
        statusItem.click();

        return createPage(clazz);
    }
}

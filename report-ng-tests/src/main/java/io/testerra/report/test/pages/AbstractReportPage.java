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
import org.testng.Assert;

import java.util.Optional;

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

    // for any reason, the following line should work, but does not:
    // specificDropBox.getSubElement(By.xpath("mdc-list-item[contains(text(), '<label>')]")).click();
    // following method is replaces the call above
    // TODO: use contains(., '<label>'): mdc-list-items has sub span, hence text() is not working
    public <T extends AbstractReportPage> T selectDropBoxElement(GuiElement dropbox, String label) {
        dropbox.click();
        Optional<GuiElement> optionalDropBoxSelection = dropbox.getSubElement(By.xpath("//mdc-list-item")).getList()
                .stream()
                .filter(i -> i.getText().contains(label))
                .findFirst();
        Assert.assertTrue(optionalDropBoxSelection.isPresent());
        optionalDropBoxSelection.get().click();
        return (T) PageFactory.create(this.getClass(), getWebDriver());
    }
}

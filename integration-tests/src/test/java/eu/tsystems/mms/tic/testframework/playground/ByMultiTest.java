/*
 * Testerra
 *
 * (C) 2021, Erik Schönherr, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.location.ByMulti;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;


public class ByMultiTest extends AbstractTestSitesTest {

    private WebDriver driver;

    private JSONObject json;

    @Override
    protected TestPage getTestPage() {
        return TestPage.DRAG_AND_DROP_OVER_FRAMES;
    }

    @BeforeMethod
    public void init() {
        driver = WebDriverManager.getWebDriver();

        String xpath = "//h1[contains(text(), 'Cross Frame Drag and Drop Example')]";

        WebElement webElement = driver.findElement(By.xpath(xpath));

        Rectangle rect = webElement.getRect();

        json = new JSONObject();
        json.put("xpath", xpath);
        json.put("bb", Arrays.asList(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
        System.out.println(json.toString());
    }

    @Test
    public void testT00_Compression() {
        String selector = ByMulti.serialize(json);

        System.out.println("The selector:\n" + selector);

        JSONObject json2 = ByMulti.deserialize(selector);
        Assert.assertEquals(json.getString("xpath"), json2.getString("xpath"));
    }

    @Test
    public void testT01_CorrectXpath() {
        String selector = ByMulti.serialize(json);

        GuiElement guiElement = new GuiElement(driver, new ByMulti(selector));
        Assert.assertTrue(guiElement.isDisplayed());
        Assert.assertEquals(guiElement.getText(), "Cross Frame Drag and Drop Example");
    }
}

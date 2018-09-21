/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.core.test.sikuli;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.sikuli.ImageElement;
import eu.tsystems.mms.tic.testframework.sikuli.SikuliBy;
import eu.tsystems.mms.tic.testframework.sikuli.SikuliWebDriver;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.net.URL;

/**
 * Created by pele on 27.08.2015.
 */
public class SikuliBasedWebTest extends AbstractTestSitesTest {

//    @Test
    public void testT01_ByImage() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();

        URL resourceURL = FileUtils.getResourceURL("sikuli/testimage.png");
        GuiElement guiElement = new GuiElement(driver, SikuliBy.image(driver, resourceURL));

        guiElement.click();
    }

//    @Test
    public void testT01_ByImage_Directly() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();

        URL resourceURL = FileUtils.getResourceURL("sikuli/iiswelcome.png");

        SikuliWebDriver sikuliWebDriver = (SikuliWebDriver) ((EventFiringWebDriver) driver).getWrappedDriver();
        ImageElement imageElement = sikuliWebDriver.findImageElement(resourceURL);
        imageElement.click();
    }

//    @Test
    public void testT03a_ByImage_InFrames_FindElementInFrame() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.DRAG_AND_DROP_OVER_FRAMES.getUrl();
        driver.get(url);

        URL resourceURL = FileUtils.getResourceURL("sikuli/ringo.png");
        GuiElement guiElement = new GuiElement(driver, SikuliBy.image(driver, resourceURL));

        guiElement.assertIsDisplayed();
        guiElement.assertAttributeContains("src", "ringo");
    }

//    @Test
    public void testT03b_ByImage_InFrames_FindElementInFrame_withObsoleteGuiElementFrame() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.DRAG_AND_DROP_OVER_FRAMES.getUrl();
        driver.get(url);

        GuiElement frame = new GuiElement(driver, By.id("draggableNodes"));

        URL resourceURL = FileUtils.getResourceURL("sikuli/ringo.png");
        GuiElement guiElement = new GuiElement(driver, SikuliBy.image(driver, resourceURL), frame);

        guiElement.assertIsDisplayed();
        guiElement.assertAttributeContains("src", "ringo");
    }

}

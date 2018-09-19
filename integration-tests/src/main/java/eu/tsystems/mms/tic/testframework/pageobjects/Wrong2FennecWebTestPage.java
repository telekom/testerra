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
/*
 * Created on 07.01.2013
 *
 * Copyright(c) 2013 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created with IntelliJ IDEA. User: pele Date: 07.01.13 Time: 14:15 To change this template use File | Settings | File
 * Templates.
 */
public class Wrong2FennecWebTestPage extends Page {

    /**
     * URL for the input-testpage
     *
     */
    public static final String URL = "http://192.168.60.239/WebsitesForTests/Input/input.html";

    /** First text field on page. */
    @Check
    private final GuiElement input1 = new GuiElement(driver, By.id("aaaaa1"), "Input1");
    /** A button on the page. */
    @Check
    private final GuiElement button1 = new GuiElement(driver, By.id("sssssss4"), "Button1");
    /** The output text field. */
    @Check
    private final GuiElement textOutputField = new GuiElement(driver, By.xpath("//p[@id='ssssssss99']"), "TextOutputField");
    /** Link to open site again. */
    @Check
    private final GuiElement openAgainLink = new GuiElement(driver, By.id("cccccc11"), "Open Again Link");

    /**
     * Default Page constructor.
     */
    public Wrong2FennecWebTestPage(WebDriver driver) {
        super(driver);
        checkPage();
    }

    @Override
    protected void checkPageErrorState(Throwable throwable) throws Throwable {
        logger.info("Yes, error here, but no exception is thrown");
    }

    /**
     * Test if button works as aspected.
     */
    public void assertFunctionalityOfButton1() {
        final String something = "some";
        input1.type(something);
        button1.click();
        textOutputField.assertAnyFollowingTextNodeContains(something);
    }

    /**
     * Click on link to open this page again.
     * 
     * @return Instance of the new page.
     */
    public Wrong2FennecWebTestPage reloadPage() {
        openAgainLink.click();
        return new Wrong2FennecWebTestPage(driver);
    }

    /**
     * Click on not existing element
     * 
     */
    public void gotoHell() {
        GuiElement guiElement = new GuiElement(driver, By.xpath("id('surely_not_existing')"));
        guiElement.click();
    }

    /**
     * Proof whether non existing element is present
     * 
     */
    public void nonfunctionalAssert() {
        GuiElement guiElement = new GuiElement(driver, By.xpath("id('surely_not_existing')"));
        guiElement.nonFunctionalAssert.assertIsPresent();
    }

}

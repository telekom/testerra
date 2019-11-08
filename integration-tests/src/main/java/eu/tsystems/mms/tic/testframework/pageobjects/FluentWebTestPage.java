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
public class FluentWebTestPage extends FluentPage<FluentWebTestPage> {

    private final IGuiElement input1 = findOneById("1");

    /** A button on the page. */
    @Check(nonFunctional = true)
    private final IGuiElement button1 = findOneById("4");

    //@Check
    //private IGuiElement specialElementFromVariable;

    @Check
    private final IGuiElement openAgainLink = findOneById("11");

    /** The output text field. */
    @Check
    private final IGuiElement textOutputField = findOneById("99");

    /**
     * Default Page constructor.
     */
    public FluentWebTestPage(WebDriver driver) {
        super(driver);

        //specialElementFromVariable = findOne(By.id("" + myVariables.number));
    }

    @Override
    protected FluentWebTestPage self() {
        return this;
    }

    /**
     * Test if button works as aspected.
     */
    public void assertFunctionalityOfButton1() {
        final String something = "some";
        input1.type(something);
        textOutputField.asserts().assertTextContains(something);
    }

    /**
     * Click on link to open this page again.
     *
     * @return Instance of the new page.
     */
    public FluentWebTestPage reloadPage() {
        openAgainLink.click();
        return new FluentWebTestPage(driver);
    }

    /**
     * Click on not existing element
     *
     */
    public void gotoHell() {
        IGuiElement guiElement = findOne(By.xpath("id('surely_not_existing')"));
        guiElement.click();
    }

    /**
     * Proof whether non existing element is present
     *
     */
    public void nonfunctionalAssert() {
        IGuiElement guiElement = findOne(By.xpath("id('surely_not_existing')"));
        guiElement.nonFunctionalAsserts().assertIsPresent();
    }

    public IGuiElement getGuiElementBy(By by) {
        return findOne(by);
    }
}

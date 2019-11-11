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

import eu.tsystems.mms.tic.testframework.pageobjects.components.InputForm;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.TestableGuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created with IntelliJ IDEA. User: pele Date: 07.01.13 Time: 14:15 To change this template use File | Settings | File
 * Templates.
 */
public class FluentWebTestPage extends FluentPage<FluentWebTestPage> {

    private final IGuiElement input1 = findById("1");

    /** A button on the page. */
    @Check(nonFunctional = true)
    private final IGuiElement button1 = findById("4");

    @Check
    private InputForm inputForm = withAncestor(find(By.className("className"))).createComponent(InputForm.class);

    //@Check
    //private IGuiElement specialElementFromVariable;

    @Check
    private final IGuiElement openAgainLink = findById("11");

    /** The output text field. */
    @Check
    private final IGuiElement textOutputField = findById("99");

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

    public InputForm inputForm() {
        return inputForm;
    }

    public TestableGuiElement notDisplayedElement() {
        return findById("notDisplayedElement");
    }

    public TestableGuiElement notVisibleElement() {
        return findById("notVisibleElement");
    }

    public TestableGuiElement nonExistentElement() {
        return findById("schnullifacks");
    }

    public IGuiElement getGuiElementBy(By by) {
        return find(by);
    }
}

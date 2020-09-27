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
 package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

public class DragAndDropJSTest extends AbstractDragAndDropTest {

    /**
     * @see {https://github.com/seleniumhq/selenium-google-code-issue-archive/issues/4202}
     */
    @Override
    protected void execute(WebDriver driver, GuiElement sourceGuiElement, GuiElement destinationGuiElement) {
        //MouseActions.dragAndDropJS(sourceGuiElement, destinationGuiElement);

        sourceGuiElement.findWebElement(sourceWebElement -> {
            Actions builder = new Actions(driver);
            builder.clickAndHold(sourceWebElement);
            Action dragAction = builder.build();
            dragAction.perform();

            destinationGuiElement.findWebElement(targetWebElement -> {
                builder.moveToElement(targetWebElement);
                builder.release(targetWebElement);
                Action dropAction = builder.build();
                dropAction.perform();
            });
        });
//
//        driver.switchTo().defaultContent();
//        Actions builder = new Actions(driver);
//        builder.clickAndHold(from);
//        Action action = builder.build();
//        action.perform();
//
//        driver.switchTo().frame("<to element frame id>");
//
//        builder.moveToElement(to);
//        builder.release(to);
//        action = builder.build();
//        action.perform();

    }

}

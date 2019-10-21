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
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.enums.DragAndDropOption;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.utils.MouseActions;
import org.openqa.selenium.WebDriver;

/**
 * Created by pele on 21.12.2015.
 */
public class DragAndDropCoordinatesTest extends AbstractDragAndDropTest {

    @Override
    protected void execute(WebDriver driver, IGuiElement sourceGuiElement, IGuiElement destinationGuiElement) {
        MouseActions.dragAndDrop(sourceGuiElement, destinationGuiElement, DragAndDropOption.DRAG_FROM_X_Y, DragAndDropOption.DROP_BY_RELATIVE_X_Y);
    }

}

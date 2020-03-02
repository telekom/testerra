/*
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
 *     Peter Lehmann
 *     pele
*/
package eu.tsystems.mms.tic.testframework.exceptions;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;

/**
 * Exception indicating an unexpected element was found on a page.
 *
 * @author pele
 */
public class UnexpectedElementFoundException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for this Exception.
     *
     * @param guiElement GuiElement that was found but not expected.
     */
    public UnexpectedElementFoundException(final GuiElement guiElement) {
        super("The element should NOT be shown: " + guiElement.toString());
    }
}

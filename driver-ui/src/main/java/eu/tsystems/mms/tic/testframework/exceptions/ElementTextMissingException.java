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
 * Created on 04.01.2013
 *
 * Copyright(c) 2012 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.exceptions;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;

/**
 * Exception stating that a GUI element misses some text.
 * 
 * @author pele
 */
public class ElementTextMissingException extends RuntimeException {

    /**
     * Default serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor, creating this exception based on the GUI Element and the missing text.
     * 
     * @param guiElement GuiElement that misses some text.
     * @param text Text that is missing.
     */
    public ElementTextMissingException(final GuiElement guiElement, final String text) {
        super("Element text is missing: " + text +
                "\n for element: " + guiElement);
    }

    /**
     * Constructor, creating this exception based on the GUI Element, the missing text and the actual text.
     * 
     * @param guiElement GuiElement that misses some text.
     * @param text Text that is missing.
     * @param actualText Text that is there instead.
     */
    public ElementTextMissingException(final GuiElement guiElement, final String text, final String actualText) {
        super("Element text is missing: " + text +
                "\n actual: " + actualText +
                "\n for element: " + guiElement);
    }

    /**
     * Default constructor, inherited from {@link RuntimeException}.
     * 
     * @param message Exception message.
     */
    public ElementTextMissingException(final String message) {
        super(message);
    }

    /**
     * Default constructor, inherited from {@link RuntimeException}.
     * 
     * @param message Message to append to exception.
     * @param cause Throwable that causes the exception.
     */
    public ElementTextMissingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Default constructor, inherited from {@link RuntimeException}.
     * 
     * @param cause Throwable that causes this Exception.
     */
    public ElementTextMissingException(final Throwable cause) {
        super(cause);
    }
}

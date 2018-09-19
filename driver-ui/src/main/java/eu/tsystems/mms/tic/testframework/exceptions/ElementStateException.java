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
 * Created by IntelliJ IDEA. User: pele Date: 27.01.12 Time: 14:17 To change this template use File | Settings | File
 * Templates.
 */
public class ElementStateException extends RuntimeException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param guiElement Create Exception from GuiElement.
     */
    public ElementStateException(final GuiElement guiElement) {
        super("Element " + guiElement + " nicht im erforderlichen Zustand");
    }

    /**
     * Constructor creating the exception if it's caused by a wrong state.
     * 
     * @param guiElement Element that causes the exception.
     * @param state Description of the wrong state.
     */
    public ElementStateException(final GuiElement guiElement, final String state) {
        super("Element " + guiElement + " nicht im erforderlichen Zustand " + state);
    }

    /**
     * Default constructor.
     */
    public ElementStateException() {
        super();
    }

    /**
     * Constructor creating this exception with a simple message.
     * 
     * @param message Message for Exception.
     */
    public ElementStateException(final String message) {
        super(message);
    }

    /**
     * Default constructor inherited by Exception class.
     * 
     * @param message Message for exception.
     * @param cause Throwable that causes this exception.
     */
    public ElementStateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Default constructor inherited by Exception class.
     * 
     * @param cause Exception that causes this Exception.
     */
    public ElementStateException(final Throwable cause) {
        super(cause);
    }
}

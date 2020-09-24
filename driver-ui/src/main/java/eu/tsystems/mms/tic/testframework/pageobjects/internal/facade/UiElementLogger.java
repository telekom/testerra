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
 package eu.tsystems.mms.tic.testframework.pageobjects.internal.facade;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.AbstractGuiElementCoreDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import java.util.Arrays;
import java.util.stream.Collectors;

public class UiElementLogger extends AbstractGuiElementCoreDecorator implements Loggable {

    private final GuiElement guiElement;

    public UiElementLogger(
            GuiElementCore decoratedCore,
            GuiElement guiElement
    ) {
        super(decoratedCore);
        this.guiElement = guiElement;
    }

    protected void beforeDelegation(String method, Object ... params) {
        log().info(method + "("+ Arrays.stream(params).map(Object::toString).collect(Collectors.joining(", "))+") on " + this.guiElement.toString(false));
    }

    @Override
    public void type(String text) {
        beforeDelegation("type", "\""+(guiElement.hasSensibleData()?"*****************":text)+"\"");
        decoratedCore.type(text);
        afterDelegation();
    }
}

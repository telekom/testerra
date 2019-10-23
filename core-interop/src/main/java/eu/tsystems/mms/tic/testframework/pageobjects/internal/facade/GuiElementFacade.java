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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.facade;

import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.IWebDriverRetainer;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableBinaryValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableQuantifiedValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;

import java.util.List;

public interface GuiElementFacade extends
    GuiElementCore,
    Nameable,
    IWebDriverRetainer
{
    IFrameLogic getFrameLogic();
    GuiElementAssert asserts();
    GuiElementAssert asserts(String errorMessage);
    GuiElementWait waits();
    IAssertableValue text();
    IAssertableValue value();
    IAssertableValue value(final Attribute attribute);
    GuiElementFacade select(final Boolean select);
    IAssertableBinaryValue<Boolean> present();
    IAssertableBinaryValue<Boolean> visible(final boolean complete);
    IAssertableBinaryValue<Boolean> displayed();
    IAssertableBinaryValue<Boolean> enabled();
    IAssertableBinaryValue<Boolean> selected();
    IAssertableQuantifiedValue<Boolean> layout();
    Locate getLocator();

    int getTimeoutInSeconds();
    void setTimeoutInSeconds(int timeoutInSeconds);
    void restoreDefaultTimeout();

    @Deprecated
    List<GuiElementFacade> getList();
}

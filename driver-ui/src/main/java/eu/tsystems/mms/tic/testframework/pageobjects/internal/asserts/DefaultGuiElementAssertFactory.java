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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;

@Deprecated
public class DefaultGuiElementAssertFactory implements GuiElementAssertFactory {

    @Override
    public GuiElementAssert create(
        GuiElementCore core,
        GuiElementData data,
        Assertion assertion,
        GuiElementWait wait
    ) {
        GuiElementAssert guiElementAssert;
        if (Testerra.Properties.PERF_TEST.asBool()) {
            guiElementAssert = new PerformanceTestGuiElementAssert();
        } else {
            guiElementAssert = new DefaultGuiElementAssert(core, data, wait, assertion);
            guiElementAssert = new GuiElementAssertHighlightDecorator(guiElementAssert, data);
        }
        return guiElementAssert;
    }
}

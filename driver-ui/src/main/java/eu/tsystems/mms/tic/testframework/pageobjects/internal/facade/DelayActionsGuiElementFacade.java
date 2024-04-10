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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.AbstractGuiElementCoreDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCoreActions;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;

/**
 * Delays all {@link GuiElementCoreActions}
 */
public class DelayActionsGuiElementFacade extends AbstractGuiElementCoreDecorator {

    private final static TestController.Overrides overrides = Testerra.getInjector().getInstance(TestController.Overrides.class);
    private final int beforeActionSleepTime;
    private final int afterActionSleepTime;

    public DelayActionsGuiElementFacade(GuiElementCore decoratedCore, int beforeActionSleepTime, int afterActionSleepTime) {
        super(decoratedCore);
        this.beforeActionSleepTime = beforeActionSleepTime;
        this.afterActionSleepTime = afterActionSleepTime;
    }

    protected void beforeDelegation(String method, Object... params) {
        if (beforeActionSleepTime > 0) {
            TimerUtils.sleep(beforeActionSleepTime);
        }
    }

    protected void afterDelegation() {
        if (overrides.hasDelayAfterAction()) {
            int delay = overrides.getDelayAfterAction();
            if (delay > 0) {
                TimerUtils.sleep(delay);
            }
        } else {
            if (afterActionSleepTime > 0) {
                TimerUtils.sleep(afterActionSleepTime);
            }
        }
    }
}

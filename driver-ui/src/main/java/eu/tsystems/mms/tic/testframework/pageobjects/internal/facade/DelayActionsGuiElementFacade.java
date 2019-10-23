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

import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import org.openqa.selenium.By;

/**
 * Created by rnhb on 09.02.2016.
 */
public class DelayActionsGuiElementFacade extends GuiElementFacadeDecorator {

    private final int beforeActionSleepTime;
    private final int afterActionSleepTime;

    public DelayActionsGuiElementFacade(GuiElementFacade decoratedFacade, int beforeActionSleepTime, int afterActionSleepTime, GuiElementData guiElementData) {
        super(decoratedFacade, guiElementData);
        this.beforeActionSleepTime = beforeActionSleepTime;
        this.afterActionSleepTime = afterActionSleepTime;
    }

    @Override
    protected void beforeActionDelegation(String message) {
        TimerUtils.sleep(beforeActionSleepTime);
    }

    @Override
    protected void afterActionDelegation() {
        TimerUtils.sleep(afterActionSleepTime);
    }

    @Override
    protected void beforeDelegation(String methodName, String message) {

    }

    @Override
    protected void afterDelegation(String result) {

    }
}

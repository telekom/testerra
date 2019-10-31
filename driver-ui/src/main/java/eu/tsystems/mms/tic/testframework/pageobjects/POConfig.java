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
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.enums.CheckRule;

@Deprecated
public final class POConfig {

    private static final PageConfig pageConfig = Testerra.ioc().getInstance(PageConfig.class);

    /** Private Constructor, cause this is a util class. */
    private POConfig() { }

    public static int getUiElementTimeoutInSeconds() {
        return pageConfig.getElementTimeoutInSeconds();
    }

    public static void setUiElementTimeoutInSeconds(int uiElementTimeoutInSeconds) {
        pageConfig.setElementTimeoutInSeconds(uiElementTimeoutInSeconds);
    }

    public static CheckRule getGuiElementCheckRule() {
        return pageConfig.getGuiElementCheckRule();
    }

    public static void setGuiElementCheckRule(CheckRule guiElementCheckRule) {
        pageConfig.setGuiElementCheckRule(guiElementCheckRule);
    }

    public static void setThreadLocalUiElementTimeoutInSeconds(int value) {
        pageConfig.setElementTimeoutInSeconds(value);
    }

    public static void removeThreadLocalUiElementTimeout() {
    }

    public static void executeWithExplicitUiElementTimeout(int value, Runnable runnable) {
        int timeoutBefore = pageConfig.getElementTimeoutInSeconds();
        pageConfig.setElementTimeoutInSeconds(value);
        runnable.run();
        pageConfig.setElementTimeoutInSeconds(timeoutBefore);
    }
}

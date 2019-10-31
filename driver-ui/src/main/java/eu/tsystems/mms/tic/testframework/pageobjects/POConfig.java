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

/**
 * @todo Create interface and make injectable or configure these things anywhere else
 */
public final class POConfig {

    /** Private Constructor, cause this is a util class. */
    private POConfig() { }

    /**
     * The element-timeout in seconds. Default: 8s.
     */
    private static int uiElementTimeoutInSeconds = Testerra.Properties.ELEMENT_TIMEOUT_SECONDS.asLong().intValue();

    private static final ThreadLocal<Integer> THREAD_LOCAL_TIMEOUT = new ThreadLocal<>();

    private static CheckRule guiElementCheckRule = CheckRule.valueOf(GuiElement.Properties.CHECK_RULE.asString());

    public static int getUiElementTimeoutInSeconds() {
        if (THREAD_LOCAL_TIMEOUT.get() != null) {
            return THREAD_LOCAL_TIMEOUT.get();
        }
        return uiElementTimeoutInSeconds;
    }

    public static void setUiElementTimeoutInSeconds(int uiElementTimeoutInSeconds) {
        POConfig.uiElementTimeoutInSeconds = uiElementTimeoutInSeconds;
    }

    public static CheckRule getGuiElementCheckRule() {
        return guiElementCheckRule;
    }

    public static void setGuiElementCheckRule(CheckRule guiElementCheckRule) {
        POConfig.guiElementCheckRule = guiElementCheckRule;
    }

    public static void setThreadLocalUiElementTimeoutInSeconds(int value) {
        THREAD_LOCAL_TIMEOUT.set(value);
    }

    @Deprecated
    public static void removeThreadLocalUiElementTimeout() {
        THREAD_LOCAL_TIMEOUT.remove();
    }

    @Deprecated
    public static void executeWithExplicitUiElementTimeout(int value, Runnable runnable) {
        Integer timeoutBefore = THREAD_LOCAL_TIMEOUT.get();
        THREAD_LOCAL_TIMEOUT.set(value);
        runnable.run();
        if (timeoutBefore == null) {
            THREAD_LOCAL_TIMEOUT.remove();
        }
        else {
            THREAD_LOCAL_TIMEOUT.set(timeoutBefore);
        }
    }
}

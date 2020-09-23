/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.testing.TestController;

/**
 * Allows temporary thread local {@link PageObject} related overrides.
 * This interface was originally created to replace the POConfig
 * and may be @deprecated since {@link TestController} for combining them both.
 * @author Mike Reiche
 */
@Deprecated
public interface PageOverrides {
    boolean hasTimeout();
    /**
     * @return Configured or default element timeout
     */
    int getTimeout();

    /**
     * Sets a new element timeout and returns the previously configured
     * @param seconds If < 0, the timeout configuration will be removed
     */
    int setTimeout(int seconds);

    boolean hasCheckRule();

    /**
     * @return Configured or default element check rule
     */
    CheckRule getCheckRule();

    /**
     * Sets a new check rule and returns the previously configured
     * @param checkRule If null, the checkrule configuration will be removed
     */
    CheckRule setCheckRule(CheckRule checkRule);
}

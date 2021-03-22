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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

/**
 * @deprecated Use {@link DesktopWebDriverUtils} instead
 */
@Deprecated
public interface UseJSAlternatives {
    /**
     * Clicks on a web element using javascript.
     *
     */
    @Deprecated
    void clickJS();

    /**
     * click
     *
     */
    @Deprecated
    void clickAbsolute();

    /**
     * hover mouse over 2 axis
     *
     */
    @Deprecated
    void mouseOverAbsolute2Axis();

    /**
     * Mouseover directly over js event.
     *
     */
    @Deprecated
    void mouseOverJS();

    @Deprecated
    void rightClickJS();

    @Deprecated
    void doubleClickJS();
}

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
 package eu.tsystems.mms.tic.testframework.core.pageobjects.testdata;

import org.openqa.selenium.WebDriver;

/**
 * Class for responsive page factory tests. This class is may be instantiated for browsers with a minimum viewport size
 * of 456px.
 */
public class TLPrefix2ResponsiveWebTestPage_801px_1234px extends PrefixResponsiveWebTestPage {
    /**
     * Constructor for existing sessions.
     *
     * @param driver The web driver.
     */
    public TLPrefix2ResponsiveWebTestPage_801px_1234px(WebDriver driver) {
        super(driver);
    }
}

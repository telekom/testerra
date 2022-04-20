/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems MMS GmbH, Deutsche Telekom AG
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

package io.testerra.report.test;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;

import org.testng.annotations.Test;

import io.testerra.report.test.pages.ReportDashBoardPage;
import io.testerra.report.test.pages.ReportPageType;
import io.testerra.report.test.pages.ReportTestsPage;

public class ReportUnderTest extends AbstractReportTest {

    @Test
    public void test_visitReport() {
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, WebDriverManager.getWebDriver(),
                PropertyManager.getProperty("reportDirectoryGeneratedTestStatus"));
        reportDashBoardPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);
    }

}

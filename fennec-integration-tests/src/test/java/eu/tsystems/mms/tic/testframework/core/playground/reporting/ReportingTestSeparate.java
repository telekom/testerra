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
 * Created on 07.12.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.report.utils.AbstractReportingTest;
import eu.tsystems.mms.tic.testframework.report.utils.ReportUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test ReportMethods
 * 
 * @author sepr
 */
public class ReportingTestSeparate extends AbstractReportingTest {

    /**
     * T01_ReportDirectoryNaming
     * <p/>
     * Description: T01 ReportDirectoryNaming
     */
    @Test
    public void testT01_ReportDirectoryNaming() {
        String reportDir;
        /*
         * Surefire reports dir must be empty for now.
         */
        // step 1: report
        reportDir = ReportUtils.getReportDir();
        Assert.assertTrue(reportDir.endsWith("target/surefire-reports/report/"), "Expected -report-, found: "
                + reportDir);

        // step 2: report1
        clearLoggingContainerReportDirectoryField();
        reportDir = ReportUtils.getReportDir();
        Assert.assertTrue(reportDir.endsWith("target\\surefire-reports\\report1\\"), "Expected -report1-, found: "
                + reportDir);

        // step 3: report2
        clearLoggingContainerReportDirectoryField();
        reportDir = ReportUtils.getReportDir();
        Assert.assertTrue(reportDir.endsWith("target\\surefire-reports\\report2\\"), "Expected -report2-, found: "
                + reportDir);
    }

}

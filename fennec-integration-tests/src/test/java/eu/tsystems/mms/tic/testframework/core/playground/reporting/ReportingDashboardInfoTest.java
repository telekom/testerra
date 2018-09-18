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
 * Created on 07.03.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Listeners;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
@Listeners(FennecListener.class)
public class ReportingDashboardInfoTest extends ReportingTest {

    static {
        Flags.FAILURE_CORRIDOR_ACTIVE = true;
    }

    @AfterClass
    public void setDashboardInfo() throws Exception {
        ReportInfo.getDashboardInfo().addInfo(3, "info with prio 3");
        ReportInfo.getDashboardInfo().addInfo(1, "info with prio 1");
        ReportInfo.getDashboardInfo().addInfo(1, "info with prio 1");

        ReportInfo.getDashboardWarning().addInfo(3, "warning with prio 3");
        ReportInfo.getDashboardWarning().addInfo(3, "warning with prio 3");
        ReportInfo.getDashboardWarning().addInfo(1, "warning with prio 1");
    }

}

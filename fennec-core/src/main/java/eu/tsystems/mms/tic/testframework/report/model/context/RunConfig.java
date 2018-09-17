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
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.FennecBuildInformation;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by pele on 21.12.2016.
 */
public final class RunConfig {

    public final String RUNCFG = PropertyManager.getProperty(FennecProperties.RUNCFG, "DEFAULT");
    public final FennecBuildInformation FennecBuildInformation = new FennecBuildInformation();

    public static String getModuleFolderName() {
        return new File(".").getAbsoluteFile().getParentFile().getName();
    }

    private String reportName = null;

    public void resetReportName() {
        String defaultName = "";
        try {
            defaultName = getModuleFolderName();
        } catch (Exception e) {
            // nothing
        }
        reportName = PropertyManager.getProperty(FennecProperties.REPORTNAME, defaultName);
    }

    public String getReportName() {
        if (reportName == null) {
            resetReportName();
        }
        return reportName;
    }

    @Override
    public String toString() {
        return "RunConfig{" +
                "RUNCFG='" + RUNCFG + '\'' +
                ", reportName='" + reportName + '\'' +
                '}';
    }
}

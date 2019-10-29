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
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.internal.TesterraBuildInformation;

import java.io.File;

/**
 * Created by pele on 21.12.2016.
 */
public final class RunConfig {

    public final String RUNCFG = (Testerra.Properties.DRY_RUN.asBool() ? Testerra.Properties.DRY_RUN : "") + PropertyManager.getProperty(TesterraProperties.RUNCFG, "DEFAULT");
    public final TesterraBuildInformation testerraBuildInformation = new TesterraBuildInformation();

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
        reportName = PropertyManager.getProperty(TesterraProperties.REPORTNAME, defaultName);
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

/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.internal.TesterraBuildInformation;
import eu.tsystems.mms.tic.testframework.internal.Flags;

import java.io.File;

public final class RunConfig {

    public final String RUNCFG = (Flags.DRY_RUN ? "DRY RUN " : "") + PropertyManager.getProperty(TesterraProperties.RUNCFG, "DEFAULT");
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

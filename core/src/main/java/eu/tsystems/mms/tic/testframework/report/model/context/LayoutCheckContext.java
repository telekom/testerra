/*
 * Testerra
 *
 * (C) 2020, René Habermann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Report;

import java.util.HashMap;
import java.util.Map;

public class LayoutCheckContext implements CustomContext, Cloneable, Loggable {
    public String image;
    public double distance;
    public Screenshot expectedScreenshot;
    public Screenshot actualScreenshot;
    public Screenshot distanceScreenshot;

    public ErrorContext errorContext;

    @Override
    public Map<String, Object> exportToReport(Report report) {
        Map<String, Object> exported = new HashMap<>();
        exported.put("image", this.image);
        exported.put("actualDistance", this.distance);
        exported.put("expectedScreenshotPath", report.getRelativePath(expectedScreenshot.getScreenshotFile()));
        exported.put("actualScreenshotPath", report.getRelativePath(actualScreenshot.getScreenshotFile()));
        exported.put("distanceScreenshotPath", report.getRelativePath(distanceScreenshot.getScreenshotFile()));
        return exported;
    }

    @Override
    public LayoutCheckContext clone() {
        try {
            return (LayoutCheckContext) super.clone();
        } catch (CloneNotSupportedException e) {
            log().debug("Cannot clone LayoutCheckContext", e);
            return this;
        }
    }
}

/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.internal;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

/**
 * Holds information about out the built version and other build information.
 *
 * @author mibu
 */
public class BuildInformation implements Serializable, Loggable {
    public final String buildJavaVersion;
    public final String buildOsName;
    public final String buildOsArch;
    public final String buildOsVersion;
    public final String buildUserName;
    public final String buildVersion;
    public final String buildTimestamp;

    public BuildInformation() {
        final Properties properties = new Properties();
        try {
            final InputStream propertiesInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("testerra-build.properties");
            if (propertiesInputStream != null) {
                properties.load(propertiesInputStream);
            }
        } catch (Exception e) {
            log().warn("No pre-set build information");
        }
        final String localBuild = "local build";
        this.buildJavaVersion = properties.getProperty("build.java.version", localBuild);
        this.buildOsName = properties.getProperty("build.os.name", localBuild);
        this.buildOsArch = properties.getProperty("build.os.arch", localBuild);
        this.buildOsVersion = properties.getProperty("build.os.version", localBuild);
        this.buildUserName = properties.getProperty("build.user.name", localBuild);
        this.buildTimestamp = properties.getProperty("build.timestamp", new Date().toString());
        this.buildVersion = properties.getProperty("build.version", localBuild);
    }
}

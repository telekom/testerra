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
package eu.tsystems.mms.tic.testframework.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

/**
 * Holds information about out the built version and other build information.
 *
 * @author mibu
 */
public class TesterraBuildInformation implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TesterraBuildInformation.class);

    /**
     * UID needed for serialization.
     */
    private static final long serialVersionUID = 7085358508783947090L;

    /**
     * The singleton instance.
     */
    private static TesterraBuildInformation instance = null;

    final String localBuild = "local build";
    public String buildJavaVersion = localBuild;
    public String buildOsName = localBuild;
    public String buildOsArch = localBuild;
    public String buildOsVersion = localBuild;
    public String buildUserName = localBuild;
    public String buildVersion = localBuild;
    public String buildTimestamp = "" + new Date();

    /**
     * Creates a new <code>TesterraBuildInformation</code> object by reading the data properties file.
     *
     * @return The test run configurations.
     */
    public static TesterraBuildInformation getInstance() {
        synchronized (TesterraBuildInformation.class) {
            if (instance == null) {
                instance = new TesterraBuildInformation();
                try {
                    final Properties properties = new Properties();
                    final InputStream propertiesInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("testerra-build.properties");

                    if (propertiesInputStream != null) {
                        properties.load(propertiesInputStream);
                    }

                    instance.buildJavaVersion = properties.getProperty("build.java.version", instance.buildJavaVersion);
                    instance.buildOsName = properties.getProperty("build.os.name", instance.buildOsName);
                    instance.buildOsArch = properties.getProperty("build.os.arch", instance.buildOsArch);
                    instance.buildOsVersion = properties.getProperty("build.os.version", instance.buildOsVersion);
                    instance.buildUserName = properties.getProperty("build.user.name", instance.buildUserName);
                    instance.buildTimestamp = properties.getProperty("build.timestamp", instance.buildTimestamp);
                    instance.buildVersion = properties.getProperty("build.version", instance.buildVersion);
                } catch (Exception e) {
                    LOGGER.debug("No pre-set build information");
                }
            }
        }

        return instance;
    }

}

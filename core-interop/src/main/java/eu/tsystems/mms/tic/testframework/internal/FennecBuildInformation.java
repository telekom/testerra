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
 * Created on 21.01.2014
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.internal;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;

/**
 * Holds information about out the fennec version and other build informations.
 * 
 * @author mibu
 */
public class FennecBuildInformation implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FennecBuildInformation.class);

    /**
     * UID needed for serialization.
     */
    private static final long serialVersionUID = 7085358508783947090L;

    /** The singleton instance. */
    private static FennecBuildInformation instance = null;

    final String localBuild = "local build";
    public String buildJavaVersion = localBuild;
    public String buildOsName = localBuild;
    public String buildOsArch = localBuild;
    public String buildOsVersion = localBuild;
    public String buildUserName = localBuild;
    public String fennecVersion = localBuild;
    public String buildTimestamp = "" + new Date();

    /**
     * Creates a new <code>FennecBuildInformation</code> object by reading the data properties file.
     * 
     * @return The test run configurations.
     */
    public static FennecBuildInformation getInstance() {
        synchronized (FennecBuildInformation.class) {
            if (instance == null) {
                instance = new FennecBuildInformation();
                try {
                    PropertyManager.loadProperties("fennec-build.properties");
                    instance.buildJavaVersion = PropertyManager.getProperty("build.java.version", instance.buildJavaVersion);
                    instance.buildOsName = PropertyManager.getProperty("build.os.name", instance.buildOsName);
                    instance.buildOsArch = PropertyManager.getProperty("build.os.arch", instance.buildOsArch);
                    instance.buildOsVersion = PropertyManager.getProperty("build.os.version", instance.buildOsVersion);
                    instance.buildUserName = PropertyManager.getProperty("build.user.name", instance.buildUserName);
                    instance.buildTimestamp = PropertyManager.getProperty("build.timestamp", instance.buildTimestamp);
                    instance.fennecVersion = PropertyManager.getProperty("build.fennec.version", instance.fennecVersion);
                } catch (Exception e) {
                    LOGGER.info("No pre-set build information");
                }
            }
        }

        return instance;
    }

}

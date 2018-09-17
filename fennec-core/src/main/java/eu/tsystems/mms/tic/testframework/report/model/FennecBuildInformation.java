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
package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;

import java.io.Serializable;

/**
 * Holds information about out the fennec version and other build informations.
 * 
 * @author mibu
 */
public class FennecBuildInformation implements Serializable {

    /**
     * UID needed for serialization.
     */
    private static final long serialVersionUID = 7085358508783947090L;

    /** The singleton instance. */
    private static FennecBuildInformation instance = null;

    /**
     * Property build.java.version.
     */
    private String buildJavaVersion;

    /**
     * Property build.os.name.
     */
    private String buildOsName;

    /**
     * Property build.os.arch.
     */
    private String buildOsArch;

    /**
     * Property build.os.version.
     */
    private String buildOsVersion;

    /**
     * Property build.user.name.
     */
    private String buildUserName;

    /**
     * Property fennec.version.
     */
    private String FennecVersion;

    /**
     * Property build.timestamp.
     */
    private String buildTimestamp;

    /**
     * Creates a new <code>FennecBuildInformation</code> object by reading the data properties file.
     * 
     * @return The test run configurations.
     */
    public static FennecBuildInformation getInstance() {
        synchronized (FennecBuildInformation.class) {
            if (instance == null) {
                instance = new FennecBuildInformation();
                PropertyManager.loadProperties("fennec-build.properties");
                instance.setBuildJavaVersion(PropertyManager.getProperty("build.java.version"));
                instance.setBuildOsName(PropertyManager.getProperty("build.os.name"));
                instance.setBuildOsArch(PropertyManager.getProperty("build.os.arch"));
                instance.setBuildOsVersion(PropertyManager.getProperty("build.os.version"));
                instance.setBuildUserName(PropertyManager.getProperty("build.user.name"));
                instance.setFennecVersion(PropertyManager.getProperty("fennec.version"));
                instance.setBuildTimestamp(PropertyManager.getProperty("build.timestamp"));
            }
        }

        return instance;
    }

    /**
     * gets build java version
     *
     * @return the buildJavaVersion
     */
    public String getBuildJavaVersion() {
        return buildJavaVersion;
    }

    /**
     * set build java version
     *
     * @param buildJavaVersion the buildJavaVersion to set
     */
    public void setBuildJavaVersion(String buildJavaVersion) {
        this.buildJavaVersion = buildJavaVersion;
    }

    /**
     * gets build os name
     *
     * @return the buildOsName
     */
    public String getBuildOsName() {
        return buildOsName;
    }

    /**
     * set build os name
     *
     * @param buildOsName the buildOsName to set
     */
    public void setBuildOsName(String buildOsName) {
        this.buildOsName = buildOsName;
    }

    /**
     * gets build os arch
     *
     * @return the buildOsArch
     */
    public String getBuildOsArch() {
        return buildOsArch;
    }

    /**
     * set build os arch
     *
     * @param buildOsArch the buildOsArch to set
     */
    public void setBuildOsArch(String buildOsArch) {
        this.buildOsArch = buildOsArch;
    }

    /**
     * gets build os version
     *
     * @return the buildOsVersion
     */
    public String getBuildOsVersion() {
        return buildOsVersion;
    }

    /**
     * set build os version
     *
     * @param buildOsVersion the buildOsVersion to set
     */
    public void setBuildOsVersion(String buildOsVersion) {
        this.buildOsVersion = buildOsVersion;
    }

    /**
     * gets build user name
     *
     * @return the buildUserName
     */
    public String getBuildUserName() {
        return buildUserName;
    }

    /**
     * set build user name
     *
     * @param buildUserName the buildUserName to set
     */
    public void setBuildUserName(String buildUserName) {
        this.buildUserName = buildUserName;
    }

    /**
     * gets fennec version
     *
     * @return the FennecVersion
     */
    public String getFennecVersion() {
        return FennecVersion;
    }

    /**
     * sets fennec version
     *
     * @param FennecVersion the FennecVersion to set
     */
    public void setFennecVersion(String FennecVersion) {
        this.FennecVersion = FennecVersion;
    }

    /**
     * gets the build timestamp
     *
     * @return the buildTimestamp
     */
    public String getBuildTimestamp() {
        return buildTimestamp;
    }

    /**
     * set build timestamp
     *
     * @param buildTimestamp the buildTimestamp to set
     */
    public void setBuildTimestamp(String buildTimestamp) {
        this.buildTimestamp = buildTimestamp;
    }

    /**
     * set instance
     *
     * @param instance the instance to set
     */
    public static void setInstance(FennecBuildInformation instance) {
        FennecBuildInformation.instance = instance;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}

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
 * Created on 26.03.2014
 *
 * Copyright(c) 2011 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.constants;

/**
 * property names of qcconnection.properties
 * 
 * @author sepr
 * 
 */
public final class QcProperties {

    /** Property to set url of the qc webservice */
    public static final String WEBSERVICEURL = "qc.webserviceURL";

    /** Property to set qc server to synchronize */
    public static final String SERVER = "qc.connection.server";

    /** Property to set qc domain to synchronize */
    public static final String DOMAIN = "qc.connection.domain";

    /** Property to set qc project to synchronize */
    public static final String PROJECT = "qc.connection.project";

    /** Property to set qc user to synchronize */
    public static final String USER = "qc.connection.user";

    /** Property to set qc password to synchronize */
    public static final String PASSWORD = "qc.connection.password";

    /** Mapping of field labels and internal names in qcconnection.properties */
    public static final String QCFIELDMAPPING = "qc.field.mapping.testrun";

    /** Property to set the file which contains QC TestSets to run. File should be in resources. */
    public static final String TESTSETSTORUNFILE = "qc.testSetsToRunFile";

    /** Property Key to set the sync type of the qcconnector. */
    public static final String SYNCTYPE = "qc.syncType";

    /** Property Key for package where qcconnector looks for tests. */
    public static final String TESTPACKAGE = "qc.test.package";

    /** Property Key for timeout of webservice requests */
    public static final String WEBSERVICETIMEOUT = "qc.webservice.timeout";

    /** Property Key for timeout of webservice requests */
    public static final String SYNCACTIVATED = "qc.syncActive";

    /** Property for ta_scriptname field, when qc.syncType=2 */
    public static final String SCRIPTNAMEFIELD = "qc.scriptnameField";

// TODO überarbeiten:
    /** QC upload screenshots global off. */
    public static final String QCUPLOADSCREENSHOTSOFF = "qc.upload.screenshots.off";
    /** Deprecated property indicating upload of automatic screenshots */
    public static final String QCUPLOADAUTOSCREENHOTOLD = "uploadAutomaticScreenshot";
    /** Property indicating to upload automatic screenshots to qc on failed tests */
    public static final String QCUPLOADAUTOSCREENSHOT = "qc.test.failed.upload.automatic.screenshot";
    /** Property indicating to upload all screenshots to qc on failed tests */
    public static final String QCUPLOADSCREENSHOTS = "qc.test.failed.upload.screenshots";
// TODO überarbeiten:
    /** Property indicating to upload screencast to qc */
    public static final String QCUPLOADSCREENCAST = "qc.test.failed.upload.screencast";
    /** Property indicating to upload automatic screenshots to qc on passed Tests */
    public static final String QCUPLOADSCREENSHOTSPASSED = "qc.test.passed.upload.screenshots";
    /** Property indicating to upload all screenshots to qc on passed Tests */
    public static final String QCUPLOADSCREENCASTPASSED = "qc.test.passed.upload.screencast";

    /** Property indicating to upload screencast video to qc */
    public static final String QC_UPLOAD_VIDEOS = "qc.upload.videos";
    /** Property indicating to upload screencast video to qc on failed Tests */
    public static final String QC_UPLOAD_VIDEOS_FAILED = "qc.test.failed.upload.videos";
    /** Property indicating to upload screencast video to qc on passed Tests */
    public static final String QC_UPLOAD_VIDEOS_SUCCESS = "qc.test.passed.upload.videos";

    /** Property to filter tests exectued by QCSurefireProvider */
    public static final String QCEXECUTIONFILTER = "qc.test.execution.filter";
    /** Property to set used qc server version. */
    public static final String VERSION = "qc.version";

    /**
     * Hide constructor
     */
    private QcProperties() {
    }

}

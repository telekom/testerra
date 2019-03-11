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
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.model.NodeInfo;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import eu.tsystems.mms.tic.testframework.utils.FileDownloader;
import eu.tsystems.mms.tic.testframework.utils.RESTUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public final class SelenoidFunctions {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelenoidFunctions.class);

    public static void downloadAndLinkVideo(NodeInfo executingNode, final String videoName, final MethodContext methodContext) throws IOException {
        final String remoteFile = getRemoteVideoFile(executingNode, videoName);
        // download
        File targetFile = new File(Report.VIDEO_DIRECTORY, videoName);
        FileDownloader.download(remoteFile, targetFile, null, 15000, false, null, null, true);

        if (methodContext != null) {
            // add to test method container
            Video video = new Video();
            video.filename = videoName;
            methodContext.videos.add(video);
        }
    }

    private static String getRemoteVideoFile(NodeInfo executingNode, String videoName) {
        return "http://" + executingNode.getHost() + ":" + executingNode.getPort() + "/video/" + videoName;
    }

    public static void deleteRemoteVideoFile(NodeInfo executingNode, final String videoName) {
        final String remoteFile = getRemoteVideoFile(executingNode, videoName);
        try {
            RESTUtils.requestDELETE(remoteFile);
        } catch (Exception e) {
            LOGGER.warn("Deleting remote video not successful", e);
        }
    }

    /**
     * Returns the effective selenoid node host and port from this driver session.
     *
     * @param driver
     * @return
     */
    public static String getExecutingNode(WebDriver driver) {
        WebDriverRequest request = WebDriverManager.getRelatedWebDriverRequest(driver);
        if (request instanceof DesktopWebDriverRequest) {
            DesktopWebDriverRequest desktopWebDriverRequest = (DesktopWebDriverRequest) request;
            String remoteServerUrl = desktopWebDriverRequest.seleniumServerURL;

            // get session id
            if (driver instanceof EventFiringWebDriver) {
                WebDriver wrappedDriver = ((EventFiringWebDriver) driver).getWrappedDriver();
                SessionId webDriverSessionId = ((RemoteWebDriver) wrappedDriver).getSessionId();
                String requestUrl = remoteServerUrl.replace("/wd/hub", "/host/" + webDriverSessionId);
                String result = RESTUtils.requestGET(requestUrl);
                JSONObject jsonObject = new JSONObject(result);

                /*
                Parsing from here: http://aerokube.com/ggr/latest/#_getting_host_by_session_id
                 */

                Object host = jsonObject.get("Name");
                Object port = jsonObject.get("Port");
                return host + ":" + port;
            }
            else {
                throw new FennecRuntimeException("Not a fennec managed driver");
            }
        }
        else {
            throw new FennecRuntimeException("Selenoid actions are not available with non-desktop drivers");
        }
    }

}

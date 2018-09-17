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
package eu.tsystems.mms.tic.testframework.remote;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import eu.tsystems.mms.tic.testframework.exceptions.fennecSystemException;
import eu.tsystems.mms.tic.testframework.model.NodeInfo;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by pele on 27.05.2016.
 */
public final class SupervisorRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SupervisorRestClient.class);

    private int port = 3333;
    private String host = "localhost";

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    private static final String SEC = "f836f307bb3ce081be5e22c2be2d1547";

    private String getURL() {
        return "http://" + host + ":" + port;
    }

    /**
     * Set host and port from webdriver session
     * This will only work for supervised nodes.
     * 
     * @param driver driver to get info from
     * @return {@link NodeInfo}
     */
    public NodeInfo setConnectionFromWebDriverSession(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver object is null");
        }

        NodeInfo info = WebDriverManagerUtils.getExecutingRemoteWebDriverNode(driver);
        if (info == null) {
            throw new fennecSystemException("Could not get host info from webdriver session");
        }
        if (info.isLocalMode()) {
            LOGGER.warn("Using a local driver session, supervisor rest client won't work here");
            return info;
        }

        setHost(info.getHost());
        setPort(info.getMTSPort());
        return info;
    }

    private String requestGET(String path) {
        WebResource resource = Client.create().resource(getURL() + path);
        String response = resource.get(String.class);
        return response;
    }

    private String requestPOST(String path) {
        return requestPOST(path, null);
    }

    private String requestPOST(String path, String parameters) {
        WebResource resource = Client.create().resource(getURL() + path);

        String response;
        if (parameters != null) {
            response = resource.post(String.class, parameters);
        } else {
            response = resource.post(String.class);
        }

        return response;
    }

    private ClientResponse requestPOSTOctet(String path, String parameters) {
        WebResource resource = Client.create().resource(getURL() + path);

        if (parameters != null) {
            return resource.post(ClientResponse.class, parameters);
        } else {
            return resource.post(ClientResponse.class);
        }
    }

    public int memUsageP() {
        String response = requestPOST("/ms/memUsageP", SEC);
        int percent = Integer.parseInt(response);
        return percent;
    }

    public String getFileList(String folder) {
        return requestPOST("/ms/getDownloadedFileList?folder=" + folder, SEC);
    }

    public ClientResponse downloadFile(String folder, String filename, File destinationFile) throws IOException {
        ClientResponse response = requestPOSTOctet("/ms/downloadFile?folder=" + folder + "&filename=" + filename, SEC);
        InputStream is = response.getEntityInputStream();

        byte[] bytes = IOUtils.toByteArray(is);
        is.close();
        LOGGER.info("Read " + bytes.length + " bytes from remote source");

        FileOutputStream fos = new FileOutputStream(destinationFile);
        fos.write(bytes);
        fos.flush();
        fos.close();
        LOGGER.info("Written " + bytes.length + " bytes to " + destinationFile);
        return response;
    }

    public ClientResponse downloadFileAndDeleteRemotely(String folder, String filename, File destinationFile) throws IOException {
        ClientResponse clientResponse = downloadFile(folder, filename, destinationFile);
        deleteFile(folder, filename);
        return clientResponse;
    }

    public boolean waitForFile(final String folder, final String filename, final int timeoutInMs) {
        Timer timer = new Timer(5000, timeoutInMs);
        ThrowablePackedResponse<Boolean> response = timer.executeSequence(new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setPassState(false);
                String fileList = getFileList(folder);
                if (!StringUtils.isStringEmpty(fileList)) {
                    String[] split = fileList.split("\n");
                    for (String f : split) {
                        if (filename.equals(f)) {
                            setReturningObject(true);
                            setPassState(true);
                        }
                    }
                }
            }
        });

        return response.getResponse();
    }

    public String deleteFile(String folder, String filename) {
        return requestPOST("/ms/deleteFile?folder=" + folder + "&filename=" + filename, SEC);
    }

    public String deleteFolder(String folder) {
        return requestPOST("/ms/deleteFolder?folder=" + folder, SEC);
    }

    public String createFolder(String folder) {
        return requestPOST("/ms/createFolder?folder=" + folder, SEC);
    }

    public static void main(String[] args) throws IOException {
        String uuid = "eb78f60c-2c12-41a0-9989-d6bf6ec4a228";

        SupervisorRestClient supervisorRestClient = new SupervisorRestClient();
        supervisorRestClient.setPort(3334);

        System.out.println("Q: " + uuid + " - " + supervisorRestClient.deleteFile(uuid, "huhu1"));
        //        System.out.println("Q: " + uuid + " - " +  downloadFile(uuid, "huhu1", new File("huhu1")));
        //        System.out.println("Q: " + uuid + " - " + getDownloadedFileList(uuid));
        //        System.out.println("MemUsage: " + memUsageP() + "%");
    }
}

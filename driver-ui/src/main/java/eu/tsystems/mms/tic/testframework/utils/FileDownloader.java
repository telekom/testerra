/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for downloading files to executing host
 * <p>
 * Date: 14.12.2015
 * Time: 07:35
 *
 * @author erku
 */
public class FileDownloader implements Loggable {

    private static int DEFAULT_TIMEOUT_MS = 10 * 1000;

    /**
     * Logging Instance
     */
    @Deprecated
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloader.class);

    /**
     * List of downloaded files. need for cleanup
     */
    private static final List<String> downloadList = new ArrayList<String>();

    /**
     * Determines download Location of this instance
     */
    private String downloadLocation = FileUtils.getTempDirectoryPath();

    /**
     * Determines if cookies should be imitated / mimed
     */
    private boolean imitateCookies = true;

    /**
     * Determines if all certificates should be trusted
     */
    private boolean trustAllCertificates = true;

    /**
     * SSL Socket Factory
     */
    //private SSLSocketFactory sslSocketFactory;
    private Proxy proxy = null;

    /**
     * Instantiate FileDownloader
     *
     * @param downloadLocation     String Download target location
     * @param imitateCookies       boolean Imitate cookies?
     * @param trustAllCertificates boolean Accept all certificates?
     */
    public FileDownloader(final String downloadLocation, final boolean imitateCookies,
                          boolean trustAllCertificates) {
        this.downloadLocation = downloadLocation;
        this.imitateCookies = imitateCookies;
        this.trustAllCertificates = trustAllCertificates;

        final URL systemHttpProxyUrl = ProxyUtils.getSystemHttpProxyUrl();
        if (systemHttpProxyUrl != null) {
            this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(systemHttpProxyUrl.getHost(), systemHttpProxyUrl.getPort()));
        }
    }

    /**
     * Instantiate FileDownloader
     *
     * @param downloadLocation String
     */
    public FileDownloader(final String downloadLocation) {
        this.downloadLocation = downloadLocation;
    }

    /**
     * Instantiate FileDownloader
     */
    public FileDownloader() {
    }

    /**
     * Deletes all downloads
     *
     * @deprecated Use {@link #cleanup()} instead
     */
    @Deprecated
    public static void deleteDownloads() {
        synchronized (downloadList) {
            for (String path : downloadList) {
                File file = FileUtils.getFile(path);
                if (!file.delete()) {
                    LOGGER.warn(String.format("File >%s< couldn't be deleted on cleanup. Please remove file manually.",
                            file.getAbsolutePath()));
                }
            }

            downloadList.clear();
        }
    }

    public FileDownloader cleanup() {
        deleteDownloads();
        return this;
    }

    public String getDownloadLocation() {
        return this.downloadLocation;
    }

    public FileDownloader setDownloadLocation(final String downloadLocation) {
        this.downloadLocation = downloadLocation;
        return this;
    }

    public boolean isTrustAllCertificates() {
        return this.trustAllCertificates;
    }

    public FileDownloader setTrustAllCertificates(final boolean trustAllCertificates) {
        this.trustAllCertificates = trustAllCertificates;
        return this;
    }

    public boolean isImitateCookies() {
        return this.imitateCookies;
    }

    /**
     * Imitate cookies (Default: true)
     *
     * @param value boolean
     */
    public FileDownloader setImitateCookies(boolean value) {
        this.imitateCookies = value;
        return this;
    }

    public File download(UiElement element) throws IOException {
        return new File(this.download(element, null));
    }

    /**
     * Download the file specified in the href/src attribute of a WebElement
     *
     * @param element        GuiElement
     * @param targetFileName String
     * @return String
     */
    public String download(UiElement element, String targetFileName) throws IOException {

        log().debug("Try to get href attribute of GuiElement");
        String link = element.waitFor().attribute(Attribute.HREF).getActual();
        if (link == null || link.length() == 0) {
            log().debug("No href attribute found. Try src attribute.");
            link = element.waitFor().attribute(Attribute.SRC).getActual();
        }

        if (link == null || link.length() == 0) {
            throw new SystemException("Neither href nor src attribute found on GuiElement.");
        }

        return this.download(element.getWebDriver(), link, targetFileName);
    }

    public File download(WebDriver driver, String urlString) throws IOException {
        String filePath = this.download(driver, urlString, null);
        return new File(filePath);
    }

    /**
     * Download file by URL
     *
     * @param driver         WebDriver
     * @param url            String
     * @param targetFileName String
     * @return String
     */
    public String download(WebDriver driver, String url, String targetFileName) throws IOException {
        return this.pDownload(driver, url, targetFileName, DEFAULT_TIMEOUT_MS);
    }

    public String download(WebDriver driver, String url, String targetFileName, int timeoutMS) throws IOException {
        return this.pDownload(driver, url, targetFileName, timeoutMS);
    }

    /**
     * Downloads the given file
     *
     * @param driver         WebDriver
     * @param urlString      String
     * @param targetFileName String
     * @return String
     */
    private String pDownload(
            WebDriver driver,
            String urlString,
            String targetFileName,
            int timeoutMS
    ) throws IOException {
        String cookieString = null;

        if (this.isImitateCookies() && driver != null) {
            cookieString = WebDriverUtils.getCookieString(driver);
        }

        this.ensureLocationExists();

        File targetFile = null;
        if (targetFileName != null) {
            targetFile = FileUtils.getFile(this.getDownloadLocation() + "/" + targetFileName);
        }
        URL url = new URL(urlString);
        return download(url, targetFile, proxy, timeoutMS, trustAllCertificates, null, cookieString, false);
    }

    /**
     * @deprecated Use instance {@link #download(WebDriver, String, String)} instead
     */
    @Deprecated
    public static String download(
            String urlString,
            File targetFile,
            Proxy proxy,
            int timeoutMS,
            boolean trustAll,
            SSLSocketFactory sslSocketFactory,
            String cookieString,
            boolean useSecondConnection
    ) throws IOException {
        URL url = new URL(urlString);
        FileDownloader downloader = new FileDownloader();
        return downloader.download(url, targetFile, proxy, timeoutMS, trustAll, sslSocketFactory, cookieString, useSecondConnection);
    }

    /**
     * Returns the absolute path to the target file as a String.
     *
     * @param url
     * @param targetFile
     * @param proxy
     * @param timeoutMS
     * @param trustAll
     * @param sslSocketFactory
     * @param cookieString
     * @return
     */
    private String download(
            URL url,
            File targetFile,
            Proxy proxy,
            int timeoutMS,
            boolean trustAll,
            SSLSocketFactory sslSocketFactory,
            String cookieString,
            boolean useSecondConnection
    ) throws IOException {
        log().debug("Downloading: " + url);

        String targetFileName = "";
        URLConnection connection = openConnection(url, proxy, timeoutMS, trustAll, cookieString, sslSocketFactory);
        if (connection instanceof HttpURLConnection) {
            targetFileName = readFileNameFromConnection((HttpURLConnection) connection);
        }
        InputStream inputStream = connection.getInputStream();

        if (useSecondConnection) {
            TimerUtils.sleep(3000, "FileDownloader flaky connections workaround");
            connection = openConnection(url, proxy, timeoutMS, trustAll, cookieString, sslSocketFactory);
            if (connection instanceof HttpURLConnection) {
                targetFileName = readFileNameFromConnection((HttpURLConnection) connection);
            }
            inputStream = connection.getInputStream();
        }

        if (targetFile == null) {
            if (targetFileName.isEmpty()) {
                targetFileName = FilenameUtils.getBaseName(url.getPath());
            }
            targetFile = FileUtils.getFile(this.getDownloadLocation() + "/" + targetFileName);
        }

        FileUtils.copyInputStreamToFile(inputStream, targetFile);

        log().info(String.format("Downloaded to: %s", targetFile.getAbsolutePath()));

        synchronized (downloadList) {
            downloadList.add(targetFile.getAbsolutePath());
        }
        return targetFile.getAbsolutePath();
    }

    private String readFileNameFromConnection(HttpURLConnection connection) {
        String fileName = "";

        String disposition = connection.getHeaderField("Content-Disposition");
        if (disposition != null) {
            int index = disposition.indexOf("filename=");
            if (index > 0) {
                fileName = disposition.substring(index + 10, disposition.length() - 1);
            }
        }
        return fileName;
    }

    private URLConnection openConnection(
            URL url,
            Proxy proxy,
            int timeoutMS,
            boolean trustAll,
            String cookieString,
            SSLSocketFactory sslSocketFactory
    ) throws IOException {
        URLConnection connection;
        if (proxy == null) {
            connection = url.openConnection();
        } else {
            connection = url.openConnection(proxy);
            log().debug("Using proxy " + proxy);
        }

        connection.setConnectTimeout(timeoutMS);
        connection.setReadTimeout(timeoutMS);

        if (trustAll && isHttpsUrl(url)) {
            log().debug("Trust all certificates on download is set to " + trustAll);
            connection = CertUtils.trustAllCerts((HttpsURLConnection) connection, sslSocketFactory);
        }

        if (cookieString != null) {
            log().debug("Imitating cookies");
            connection.setRequestProperty("Cookie", cookieString);
        }


        return connection;
    }

    private static boolean isHttpsUrl(final URL url) {
        return url.getProtocol().toLowerCase().equals("https");
    }

    /**
     * Ensure Location exists
     *
     * @return boolean
     */
    private boolean ensureLocationExists() {

        File downloadLoc = FileUtils.getFile(this.getDownloadLocation());
        return downloadLoc.exists() || downloadLoc.mkdirs();
    }

    public static void setDefaultTimeoutMs(int defaultTimeoutMs) {
        DEFAULT_TIMEOUT_MS = defaultTimeoutMs;
    }

    public FileDownloader setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }
}

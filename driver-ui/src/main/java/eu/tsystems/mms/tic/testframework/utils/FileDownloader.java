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
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.WebDriver;

/**
 * Utility class for downloading files to executing host.
 * <p>
 * Date: 14.12.2015
 * Time: 07:35
 *
 * @author erku
 */
public class FileDownloader implements Loggable {

    private static int DEFAULT_TIMEOUT_MS = 10 * 1000;

    /**
     * List of downloaded files. need for cleanup
     */
    private static final List<String> downloadList = new ArrayList<>();

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

    private Consumer<URLConnection> connectionConfigurator;

    /**
     * Instantiate FileDownloader
     *
     * @param downloadLocation     String Download target location
     * @param imitateCookies       boolean Imitate cookies?
     * @param trustAllCertificates boolean Accept all certificates?
     * @deprecated Use {@link #FileDownloader()} instead
     */
    public FileDownloader(
            String downloadLocation,
            boolean imitateCookies,
            boolean trustAllCertificates
    ) {
        this.downloadLocation = downloadLocation;
        this.imitateCookies = imitateCookies;
        this.trustAllCertificates = trustAllCertificates;
    }

    /**
     * Instantiate FileDownloader
     *
     * @param downloadLocation String
     * @deprecated
     */
    public FileDownloader(final String downloadLocation) {
        this.downloadLocation = downloadLocation;
    }

    public FileDownloader() {

    }

    /**
     * Sets the connection configurator.
     * When set, all the @deprecated features like {@link #setImitateCookies(boolean), {@link #setDefaultTimeoutMs(int)} and {@link #setTrustAllCertificates(boolean)}}
     * are disabled.
     */
    public FileDownloader setConnectionConfigurator(Consumer<URLConnection> connectionConfigurator) {
        this.connectionConfigurator = connectionConfigurator;
        return this;
    }

    /**
     * Deletes all downloads
     *
     * @deprecated Use {@link #cleanup()} instead
     */
    @Deprecated
    public static void deleteDownloads() {
        new FileDownloader().cleanup();
    }

    public FileDownloader cleanup() {
        synchronized (downloadList) {
            for (String path : downloadList) {
                File file = FileUtils.getFile(path);
                if (!file.delete()) {
                    log().warn(String.format("File >%s< couldn't be deleted on cleanup. Please remove file manually.",
                            file.getAbsolutePath()));
                }
            }

            downloadList.clear();
        }
        return this;
    }

    public String getDownloadLocation() {
        return this.downloadLocation;
    }

    public FileDownloader setDownloadLocation(final String downloadLocation) {
        this.downloadLocation = downloadLocation;
        return this;
    }

    /**
     * @deprecated Use {@link #setConnectionConfigurator(Consumer)} instead
     */
    public boolean isTrustAllCertificates() {
        return this.trustAllCertificates;
    }

    /**
     * @deprecated Use {@link #setConnectionConfigurator(Consumer)} instead
     */
    public FileDownloader setTrustAllCertificates(final boolean trustAllCertificates) {
        this.trustAllCertificates = trustAllCertificates;
        return this;
    }

    /**
     * @deprecated Use {@link #setConnectionConfigurator(Consumer)} instead
     */
    public boolean isImitateCookies() {
        return this.imitateCookies;
    }

    /**
     * @deprecated Use {@link #setConnectionConfigurator(Consumer)} instead
     * Imitate cookies (Default: true)
     *
     * @param value boolean
     */
    public FileDownloader setImitateCookies(boolean value) {
        this.imitateCookies = value;
        return this;
    }

    public File download(GuiElement element) throws IOException {
        return new File(this.download(element, null));
    }

    /**
     * Download the file specified in the href/src attribute of a WebElement
     *
     * @param element        GuiElement
     * @param targetFileName String
     * @return String
     */
    public String download(GuiElement element, String targetFileName) throws IOException {

        log().info("Try to get href attribute of GuiElement");
        String link = element.getAttribute("href");
        if (link == null || link.length() == 0) {
            log().info("No href attribute found. Try src attribute.");
            link = element.getAttribute("src");
        }

        if (link == null || link.length() == 0) {
            throw new SystemException("Neither href nor src attribute found on GuiElement.");
        }

        return this.download(element.getWebDriver(), link, targetFileName);
    }

    public File download(URL url) throws IOException {
        return this.download(url.toString());
    }

    public File download(String urlString) throws IOException {
        return this.download(urlString, null);
    }

    public File download(URL url, String targetFileName) throws IOException {
        return this.download(url.toString(), targetFileName);
    }

    public File download(String urlString, String targetFileName) throws IOException {
        return new File(this.pDownload(null, urlString, targetFileName, DEFAULT_TIMEOUT_MS));
    }

    /**
     * @deprecated Use {@link #setConnectionConfigurator(Consumer)} and {@link #download(String)} instead
     * @param driver
     * @param urlString
     * @return
     * @throws IOException
     */
    public File download(WebDriver driver, String urlString) throws IOException {
        String filePath = this.download(driver, urlString, null);
        return new File(filePath);
    }

    /**
     * @deprecated Use {@link #setConnectionConfigurator(Consumer)} and {@link #download(String, String)} instead
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

    /**
     * @deprecated Use {@link #setConnectionConfigurator(Consumer)} and {@link #download(String, String)} instead
     */
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

        log().info("Start downloading " + url);

        String targetFileName = "";
        URLConnection connection = openConnection(url, proxy, timeoutMS, trustAll, cookieString, sslSocketFactory);
        if (connection instanceof HttpURLConnection) {
            targetFileName = readFileNameFromConnection((HttpURLConnection) connection);
        }
        InputStream inputStream = connection.getInputStream();

        if (targetFile == null) {
            if (targetFileName.isEmpty()) {
                targetFileName = FilenameUtils.getBaseName(url.getPath());
            }
            targetFile = FileUtils.getFile(this.getDownloadLocation() + "/" + targetFileName);
        }

        log().info("Downloaded " + url + " to " + targetFile.getAbsolutePath());

        FileUtils.copyInputStreamToFile(inputStream, targetFile);

        synchronized (downloadList) {
            downloadList.add(targetFile.getAbsolutePath());
        }
        return targetFile.getAbsolutePath();
    }

    private String readFileNameFromConnection(HttpURLConnection connection) {
        String fileName = "";

        String disposition = connection.getHeaderField("Content-Disposition");
        if (disposition != null) {
            Pattern pattern = Pattern.compile("filename=\\\"?([^\\\"]+)\\\"?");
            Matcher matcher = pattern.matcher(disposition);
            if (matcher.find()) {
                return matcher.group(1);
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
            log().info("Using proxy " + proxy);
        }

        if (this.connectionConfigurator != null) {
            this.connectionConfigurator.accept(connection);
        } else {
            connection.setConnectTimeout(timeoutMS);
            connection.setReadTimeout(timeoutMS);

            if (trustAll && isHttpsUrl(url)) {
                log().info("Trust all certificates on download is set to " + trustAll);
                connection = CertUtils.trustAllCerts((HttpsURLConnection) connection, sslSocketFactory);
            }

            if (cookieString != null) {
                log().info("Imitating cookies");
                connection.setRequestProperty("Cookie", cookieString);
            }
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

    /**
     * @deprecated Use {@link #setConnectionConfigurator(Consumer)} instead
     */
    public static void setDefaultTimeoutMs(int defaultTimeoutMs) {
        DEFAULT_TIMEOUT_MS = defaultTimeoutMs;
    }

    public FileDownloader setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }
}

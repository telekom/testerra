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
 * Created on 14.12.2015
 *
 * Copyright(c) 1995 - 2007 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.exceptions.fennecSystemException;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for downloading files to executing host
 * <p>
 * Date: 14.12.2015
 * Time: 07:35
 *
 * @author erku
 */
public class FileDownloader {

    private static int DEFAULT_TIMEOUT_MS = 10 * 1000;

    /**
     * Logging Instance
     */
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
    private SSLSocketFactory sslSocketFactory;
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
     */
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

    public String getDownloadLocation() {
        return this.downloadLocation;
    }

    public void setDownloadLocation(final String downloadLocation) {
        this.downloadLocation = downloadLocation;
    }

    public boolean isTrustAllCertificates() {

        return this.trustAllCertificates;
    }

    public void setTrustAllCertificates(final boolean trustAllCertificates) {

        this.trustAllCertificates = trustAllCertificates;
    }

    public boolean isImitateCookies() {
        return this.imitateCookies;
    }

    /**
     * Imitate cookies (Default: true)
     *
     * @param value boolean
     */
    public void setImitateCookies(boolean value) {
        this.imitateCookies = value;
    }

    /**
     * Download the file specified in the href/src attribute of a WebElement
     *
     * @param element        GuiElement
     * @param targetFileName String
     * @return String
     */
    public String download(final GuiElement element, final String targetFileName) throws IOException {

        LOGGER.info("Try to get href attribute of GuiElement");
        String link = element.getAttribute("href");
        if (link == null || link.length() == 0) {
            LOGGER.info("No href attribute found. Try src attribute.");
            link = element.getAttribute("src");
        }

        if (link == null || link.length() == 0) {
            throw new fennecSystemException("Neither href nor src attribute found on GuiElement.");
        }

        return this.download(element.getDriver(), link, targetFileName);
    }

    /**
     * Download file by URL
     *
     * @param driver         WebDriver
     * @param url            String
     * @param targetFileName String
     * @return String
     */
    public String download(final WebDriver driver, final String url, String targetFileName) throws IOException {
        return this.pDownload(driver, url, targetFileName, DEFAULT_TIMEOUT_MS);
    }

    public String download(final WebDriver driver, final String url, String targetFileName, int timeoutMS) throws IOException {
        return this.pDownload(driver, url, targetFileName, timeoutMS);
    }

    /**
     * Downloads the given file
     *
     * @param driver         WebDriver
     * @param url            String
     * @param targetFileName String
     * @return String
     */
    private String pDownload(final WebDriver driver, final String url, final String targetFileName, int timeoutMS) throws IOException {
        String cookieString = null;

        if (this.isImitateCookies()) {
            cookieString = WebDriverUtils.getCookieString(driver);
        }

        this.ensureLocationExists();


        File targetFile = FileUtils.getFile(this.getDownloadLocation() + "/" + targetFileName);
        return download(url, targetFile, proxy, timeoutMS, trustAllCertificates, sslSocketFactory, cookieString, false);
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
    public static String download(final String url, final File targetFile, final Proxy proxy, final int timeoutMS,
                                  final boolean trustAll, final SSLSocketFactory sslSocketFactory,
                                  final String cookieString, boolean useSecondConnection) throws IOException {
        LOGGER.info("Downloading file " + url);
        URL fileToDownload = new URL(url);

        URLConnection connection = openConnection(fileToDownload, proxy, timeoutMS, trustAll, cookieString, sslSocketFactory);
        InputStream inputStream = connection.getInputStream();

        if (useSecondConnection) {
            TestUtils.sleep(3000, "FileDownloader flaky connections workaround");
            URLConnection connection2 = openConnection(fileToDownload, proxy, timeoutMS, trustAll, cookieString, sslSocketFactory);
            inputStream = connection2.getInputStream();
        }

        FileUtils.copyInputStreamToFile(inputStream, targetFile);

        LOGGER.info(String.format("Download to location >%s< successful", targetFile.getAbsolutePath()));

        synchronized (downloadList) {
            downloadList.add(targetFile.getAbsolutePath());
        }
        return targetFile.getAbsolutePath();
    }

    private static URLConnection openConnection(URL url, Proxy proxy, int timeoutMS, boolean trustAll,
                                                String cookieString, SSLSocketFactory sslSocketFactory) throws IOException {
        URLConnection connection;
        if (proxy == null) {
            connection = url.openConnection();
        } else {
            connection = url.openConnection(proxy);
            LOGGER.info("Using proxy " + proxy);
        }

        connection.setConnectTimeout(timeoutMS);
        connection.setReadTimeout(timeoutMS);

        if (trustAll && isHttpsUrl(url)) {
            LOGGER.info("Trust all certificates on download is set to " + trustAll);
            connection = CertUtils.trustAllCerts((HttpsURLConnection) connection, sslSocketFactory);
        }

        if (cookieString != null) {
            LOGGER.info("Imitating cookies");
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

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}

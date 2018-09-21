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
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.TestPage;
import eu.tsystems.mms.tic.testframework.utils.FileDownloader;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Tests for FileDownloader
 * <p/>
 * Date: 14.12.2015
 * Time: 08:04
 *
 * @author erku
 */
public class FileDownloaderTest extends AbstractTest {

    private WebDriver createWebDriver(boolean extraSession) {
        WebDriverRequest r = new DesktopWebDriverRequest();
        WebDriver driver;

        if (extraSession) {
            r.sessionKey = "test";
        }

        driver = WebDriverManager.getWebDriver(r);
        String url = TestPage.INPUT_TEST_PAGE.getUrl();
        driver.get(url);

        return driver;
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(Method method) {

        FileDownloader.deleteDownloads();
    }

    /**
     * Tests download and deletion
     *
     * @author erku
     */
    @Test()
    public void testT01_downloadFileAndDelete() throws IOException {

        final WebDriver driver = createWebDriver(false);

        FileDownloader downloader = new FileDownloader(FileUtils.getUserDirectoryPath(), true, true);

        String download = downloader.download(driver, TestPage.INPUT_TEST_PAGE.getUrl() + "#",
                "testT01_downloadFile.htm");
        File file = FileUtils.getFile(download);

        Assert.assertTrue(file.exists(), "File was downloaded correctly.");

        FileDownloader.deleteDownloads();
        Assert.assertFalse(file.exists(), "File deleted.");
    }

    /**
     * Test download of https
     *
     * @author erku
     */
    @Test
    public void testT02_downloadFileOfHttpsUrl() throws IOException {

        final WebDriver driver = createWebDriver(false);
        driver.get("https://google.de");

        FileDownloader downloader = new FileDownloader(FileUtils.getUserDirectoryPath(), true, true);

        String download = downloader.download(driver, "https://google.de","testT02_downloadFileOfHttpsUrl.htm");
        File file = FileUtils.getFile(download);

        Assert.assertTrue(file.exists(), "File was downloaded correctly.");
    }

    /**
     * Test download to long path
     *
     * @author erku
     */
    @Test()
    public void test03_downloadFileToLongLocation() throws IOException {

        final WebDriver driver = createWebDriver(false);
        FileDownloader downloader = new FileDownloader(FileUtils.getUserDirectoryPath() + "/foo/bar\\test", true, true);

        String download = downloader.download(driver, TestPage.INPUT_TEST_PAGE.getUrl() + "#",
                "test03_downloadFileToLongLocation.htm");
        File file = FileUtils.getFile(download);

        Assert.assertTrue(file.exists(), "File was downloaded correctly.");
    }

}

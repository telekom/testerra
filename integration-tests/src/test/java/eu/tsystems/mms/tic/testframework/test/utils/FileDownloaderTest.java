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

package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.utils.FileDownloader;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Tests for FileDownloader
 * <p/>
 * Date: 14.12.2015
 * Time: 08:04
 *
 * @author erku
 */
public class FileDownloaderTest extends AbstractTestSitesTest {

    @AfterMethod(alwaysRun = true)
    public void tearDown(Method method) {
        new FileDownloader().cleanup();
    }

    /**
     * Tests download and deletion
     *
     * @author erku
     */
    @Test()
    public void testT01_downloadFileAndDelete() throws IOException {

        final WebDriver driver = WebDriverManager.getWebDriver();
        FileDownloader downloader = new FileDownloader(FileUtils.getUserDirectoryPath(), true, true).setProxy(null);
        String download = downloader.download(driver, WebDriverManager.getWebDriver().getCurrentUrl(),
                "testT01_downloadFile.htm");
        File file = FileUtils.getFile(download);

        Assert.assertTrue(file.exists(), "File was downloaded correctly.");

        downloader.cleanup();
        Assert.assertFalse(file.exists(), "File deleted.");
    }

    /**
     * Test download of https
     *
     * @author erku
     */
    @Test
    public void testT02_downloadFileOfHttpsUrl() throws IOException {

        final WebDriver driver = WebDriverManager.getWebDriver();
        driver.get("https://google.de");

        FileDownloader downloader = new FileDownloader(FileUtils.getUserDirectoryPath(), true, true);

        String download = downloader.download(driver, "https://google.de", "testT02_downloadFileOfHttpsUrl.htm");
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

        final WebDriver driver = WebDriverManager.getWebDriver();
        FileDownloader downloader = new FileDownloader(FileUtils.getUserDirectoryPath() + "/foo/bar\\test", true, true).setProxy(null);

        String download = downloader.download(driver, WebDriverManager.getWebDriver().getCurrentUrl(),
                "test03_downloadFileToLongLocation.htm");
        File file = FileUtils.getFile(download);

        Assert.assertTrue(file.exists(), "File was downloaded correctly.");
    }

    @Test
    public void test04_readFileNameFromResponseHeader() throws IOException {
        WebDriver driver = WebDriverManager.getWebDriver();
        FileDownloader downloader = new FileDownloader();
        File file = downloader.download(driver, "https://upload.wikimedia.org/wikipedia/de/thumb/e/e1/Java-Logo.svg/800px-Java-Logo.svg.png");
        Assert.assertEquals(file.getName(), "800px-Java-Logo.svg");
    }

    @Test
    public void test05_readFileFromUrl() throws IOException {
        WebDriver driver = WebDriverManager.getWebDriver();
        FileDownloader downloader = new FileDownloader();
        File file = downloader.download(driver, "https://httpbin.org/image/png");
        Assert.assertEquals(file.getName(), "png");
    }

}

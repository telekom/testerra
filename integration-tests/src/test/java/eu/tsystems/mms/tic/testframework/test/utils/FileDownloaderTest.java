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
import eu.tsystems.mms.tic.testframework.utils.CertUtils;
import eu.tsystems.mms.tic.testframework.utils.DefaultConnectionConfigurator;
import eu.tsystems.mms.tic.testframework.utils.FileDownloader;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
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

    public DefaultConnectionConfigurator createConnectionConfigurator() {
        CertUtils certUtils = new CertUtils();
        certUtils.setTrustAllHosts(true);
        DefaultConnectionConfigurator defaultConnectionConfigurator = new DefaultConnectionConfigurator();
        defaultConnectionConfigurator.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        defaultConnectionConfigurator.useCertUtils(certUtils);
        return defaultConnectionConfigurator;
    }

    /**
     * Tests download and deletion
     *
     * @author erku
     */
    @Test()
    public void testT01_downloadFileAndDelete() throws IOException {

        final WebDriver driver = getClassExclusiveWebDriver();
        FileDownloader downloader = new FileDownloader(FileUtils.getUserDirectoryPath(), true, true).setProxy(null);
        downloader.setConnectionConfigurator(this.createConnectionConfigurator());
        String download = downloader.download(driver, driver.getCurrentUrl(), "testT01_downloadFile.htm");
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
        FileDownloader downloader = new FileDownloader(FileUtils.getUserDirectoryPath(), true, true);
        downloader.setConnectionConfigurator(this.createConnectionConfigurator());
        File file = downloader.download("https://google.de", "testT02_downloadFileOfHttpsUrl.htm");

        Assert.assertTrue(file.exists(), "File was downloaded correctly.");
    }

    /**
     * Test download to long path
     *
     * @author erku
     */
    @Test()
    public void test03_downloadFileToLongLocation() throws IOException {

        final WebDriver driver = getClassExclusiveWebDriver();
        FileDownloader downloader = new FileDownloader(FileUtils.getUserDirectoryPath() + "/foo/bar\\test", false, false).setProxy(null);
        downloader.setConnectionConfigurator(createConnectionConfigurator());

        File file = downloader.download(driver.getCurrentUrl(), "test03_downloadFileToLongLocation.htm");

        Assert.assertTrue(file.exists(), "File was downloaded correctly.");
    }

    @Test
    public void test04_readFileNameFromResponseHeader() throws IOException {
        FileDownloader downloader = new FileDownloader();
        DefaultConnectionConfigurator connectionConfigurator = createConnectionConfigurator();
        connectionConfigurator.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        downloader.setConnectionConfigurator(connectionConfigurator);
        File file = downloader.download( "https://images.pexels.com/users/avatars/616468/kira-schwarz-869.jpeg?auto=compress&fit=crop&h=256&w=256");
        Assert.assertEquals(file.getName(), "kira-schwarz-869.webp");
    }

    @Test
    public void test05_readFileFromUrl() throws IOException {
        FileDownloader downloader = new FileDownloader();
        File file = downloader.download("https://httpbin.org/image/png");
        Assert.assertEquals(file.getName(), "png");
    }
}
